/**
 * @author Dixon Martinez
 * 03/10/2012
 * DiscountInterest
 */
package org.mary.process;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

/**
 * * @author Dixon Martinez
 *
 */
public class Discount extends SvrProcess {

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
	 * @author Dixon Martinez - Carlos Parada Carga los datos en tabla temporal para ser lanzados en formato de impresion
	 * @param m_C_BPartner_ID
	 * @param m_C_Invoice_ID
	 * @param m_PayDate
	 */
	private void loadData(int m_C_BPartner_ID,int m_C_Invoice_ID, Timestamp m_PayDate){
		StringBuffer sql = new StringBuffer();
		
		PreparedStatement ps = null;
		
		sql.append("Insert Into T_CUST_DiscountInterest (AD_Client_ID,AD_Org_ID,AD_PInstance_ID,C_Invoice_ID,PendingDays,M_Product_ID,DueDate,DaysDue,M_PriceList_Version_ID)");
		sql.append("Select \n" +
				/*1*/"CI.AD_Client_ID,\n"+
				/*2*/"CI.AD_Org_ID,\n"+
				/*3*/getAD_PInstance_ID()+",\n"+		
				/*4*/"CI.C_Invoice_ID,\n"+				
				/*5*/"CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) - '"+m_PayDate.toString()+"' As PendingDays, \n"+
				/*6*/"CIL.M_Product_ID,\n"+				
				/*7*/"CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) AS DueDate, \n"+
				/*8*/"Cast(Extract(Day From '"+m_PayDate.toString()+"' - CI.DateInvoiced ) as Integer) As DaysDue, \n" +
				/*9*/"PL.M_PriceList_Version_ID \n"+
				"From C_Invoice CI  \n"+
				"Inner Join C_PaymentTerm CPT On CI.C_PaymentTerm_ID=CPT.C_PaymentTerm_ID \n"+				
				"Inner Join C_InvoiceLine CIL On CI.C_Invoice_ID = CIL.C_Invoice_ID \n"+
				"Inner Join (Select PL.M_PriceList_ID,PL.Name As PriceList_Name,PLV.Name as PriceList_Version_Name,PLV.M_PriceList_Version_ID,PLV.ValidFrom,PP.M_Product_ID,PP.PriceList,PP.XX_DiscountPercentage From M_PriceList PL Inner Join M_PriceList_Version PLV On PL.M_PriceList_ID=PLV.M_PriceList_ID Inner Join M_ProductPrice PP On PLV.M_PriceList_Version_ID=PP.M_PriceList_Version_ID) \n"+  
				"PL On CI.M_PriceList_ID = PL.M_PriceList_ID And CIL.M_Product_ID=PL.M_Product_ID \n"+
				"Inner Join M_Product MP On CIL.M_Product_ID=MP.M_Product_ID \n"+
				"Where \n"+ 
				"invoiceopen(CI.C_Invoice_ID, 0) <> 0 \n"+ 
				"And CI.IsPayScheduleValid <> 'Y' \n"+ 
				"And CI.DocStatus In ('CO', 'CL') \n"+
				(m_C_BPartner_ID!=-1 ?"And CI.C_BPartner_ID = "+m_C_BPartner_ID + " \n": "")+
				(m_C_Invoice_ID!=-1 ? "And CI.C_Invoice_ID = "+ m_C_Invoice_ID  + " \n": "") + 
				"And CI.IsSoTrx='Y' \n"+
				"And PL.M_PriceList_Version_ID = XX_ValidPricelist(PL.M_PriceList_ID,CI.DateInvoiced) \n" +
				"And CI.XX_DiscountApplied='N' \n" +
				"And CI.DateInvoiced<='"+m_PayDate.toString()+ "' \n"+
				"And CI.AD_Client_ID = "+getAD_Client_ID() + " \n"+
				//"And PL.M_PriceList_Version_ID In (Select M_PriceList_Version.M_PriceList_Version_ID From M_PriceList_Version Where M_PriceList_Version.M_PriceList_ID=PL.M_PriceList_ID And M_PriceList_Version.ValidFrom <=CI.DateInvoiced Order By M_PriceList_Version.ValidFrom Desc Limit 1) \n"+ //Error de Sintaxis cerca de Limit jeje a vaina loca
				"Order By CI.C_Invoice_ID");
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
	private Timestamp m_PayDate;// = Timestamp.valueOf("03/05/1990");	
}
