/**
 * @author Carlos Parada
 * 28/01/2013
 * Interest
 */
package org.mary.process;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

/**
 * * @author Carlos Parada
 *
 */
public class Interest extends SvrProcess {

	/* (non-Javadoc)
	 * @see org.compiere.process.SvrProcess#prepare()
	 */
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		for(ProcessInfoParameter parameter :getParameter()){
			String name = parameter.getParameterName();
			if(parameter.getParameter() == null){
				;
			}else if (name.toLowerCase().equals("c_bpartner_id"))
				m_C_BPartner_ID = parameter.getParameterAsInt();				
			else if(name.toLowerCase().equals("c_invoice_id"))
				m_C_Invoice_ID = parameter.getParameterAsInt();
			else if(name.toLowerCase().equals("c_tax_id"))
				m_C_Tax_ID = parameter.getParameterAsInt();
			else if(name.toLowerCase().equals("paydate"))
				m_PayDate = (Timestamp) parameter.getParameter();			
		}
	}

	/* (non-Javadoc)
	 * @see org.compiere.process.SvrProcess#doIt()
	 */
	@Override
	protected String doIt() throws Exception {
		// TODO Auto-generated method stub				
		loadData(m_C_BPartner_ID, m_C_Invoice_ID, m_PayDate);
		return null;	
	}
	/**
	 * @author Carlos Parada Carga los datos en tabla temporal para ser lanzados en formato de impresion
	 * @param m_C_BPartner_ID
	 * @param m_C_Invoice_ID
	 * @param m_PayDate
	 */
	private void loadData(int m_C_BPartner_ID,int m_C_Invoice_ID, Timestamp m_PayDate){
		StringBuffer sql = new StringBuffer();
		
		PreparedStatement ps = null;
		
		sql.append("Insert Into T_CUST_Interest (AD_PInstance_ID,AD_Client_ID,AD_Org_ID,C_Invoice_ID,DocumentNo,DateInvoiced,DueDate,C_PaymentTerm_ID,InvoiceOpen,CUST_Rate_ID,DaysDue,Interest,C_Tax_ID)");

		sql.append("Select \n" +
				getAD_PInstance_ID()+", \n" +
				"CI.AD_Client_ID, \n" +
				"CI.AD_Org_ID, \n" +
				"CI.C_Invoice_ID, \n" +
				"CI.DocumentNo, \n" +
				"CI.DateInvoiced, \n" +
				"CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) AS DueDate, \n" +
				"CI.C_PaymentTerm_ID, \n" +
				"InvoiceOpen(CI.C_Invoice_ID,0), \n" +
				"CP.CUST_Rate_ID, \n" +
				"Cast( \n" +
				"Date_Part('day', \n" +
				"/*Fecha Fin*/ \n" +
				"Case When '"+m_PayDate.toString()+ "' >= CP.ValidTo \n" +
				"Then CP.ValidTo \n" +
				"Else '"+m_PayDate.toString()+ "' \n" +
				"End \n" +
				"- \n" +
				"/*Fecha Inicio*/ \n" +
				"Case When CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) <= CP.ValidFrom \n" +
				"Then CP.ValidFrom \n" +
				"Else CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) \n" +
				"End) \n" +
				"As Integer) \n" +
				"As DaysDue \n" +
				", Round(Cast(\n" +
				"/*(--------Saldo Pendiente------- *  -Tasa Interes-) / --------------------------Días de Interes Anual---------------------------------------)*/ \n" +
				"((InvoiceOpen(CI.C_Invoice_ID,0) * CP.Rate) /(Select Cast(Value As Numeric(15,5)) From AD_SysConfig Where Name = 'XXInterestYearly')) \n" + 
				"/*----------------------------------Dias de Vencimiento en el mes----------------------------------*/ \n" +
				"* \n" +
				"Date_Part('day', \n" +
				"/*Fecha Fin*/ \n" +
				"Case When '"+m_PayDate.toString()+ "' >= CP.ValidTo \n" +
				"Then CP.ValidTo \n" +
				"Else '"+m_PayDate.toString()+ "' \n" +
				"End \n" +
				"- \n" +
				"/*Fecha Inicio*/ \n" +
				"Case When CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) <= CP.ValidFrom \n" +
				"Then CP.ValidFrom \n" +
				"Else CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) \n" +
				"End \n" +
				") \n" +
				"As Numeric),2) \n" +
				"AS Interest, \n" +
				(m_C_Tax_ID==-1?"null":m_C_Tax_ID) + " As C_Tax_ID \n" +
				"From \n" +
				"C_Invoice CI \n" +
				"Inner Join C_PaymentTerm CPT On CI.C_PaymentTerm_ID=CPT.C_PaymentTerm_ID, \n" +
				"CUST_Rate CP \n" +
				"Where \n" +
				"InvoiceOpen(CI.C_Invoice_ID,0) <>0 And \n" +
				"( \n" +
				"( \n" +
				"CP.ValidFrom Between \n" +
				"CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) \n" +
				"And \n" +
				"'"+m_PayDate.toString()+ "' \n" +
				"Or \n" +
				"CP.ValidTo Between \n" +
				"CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) \n" +
				"And \n" +
				"'"+m_PayDate.toString()+ "' \n" +
				"Or \n" +
				"CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) \n" +
				"Between CP.ValidFrom \n" + 
				"And \n" + 
				"CP.ValidTo \n" + 
				") \n" +
				"And \n" + 
				"'"+m_PayDate.toString()+ "'>CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) \n" +
				") \n" +	
				"And CI.AD_Client_ID = "+getAD_Client_ID() + " \n"+
				(m_C_BPartner_ID!=-1 ?"And CI.C_BPartner_ID = " + m_C_BPartner_ID + " \n": "")+
				(m_C_Invoice_ID!=-1 ? "And CI.C_Invoice_ID = " + m_C_Invoice_ID  + " \n": "") 
				);
	
		ps = DB.prepareStatement(sql.toString(),null);

		try {
			ps.execute();						
			ps.close();	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int m_C_BPartner_ID = -1;
	private int m_C_Invoice_ID =-1;
	private int m_C_Tax_ID =-1;
	private Timestamp m_PayDate;// = Timestamp.valueOf("03/05/1990");	
}
