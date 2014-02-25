/**
 * @author Carlos Parada
 * @Date 05/05/2012
 */
package org.sg.report;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.compiere.util.DB;

/**
 * @author Carlos Parada
 *
 */
public class MXX_ReceiptPayment {
	
	/**
	 * @author Carlos Parada
	 * @Date 23/03/2012
	 * Constructor Inicializa Las Variables del Reporte
	 */
	public MXX_ReceiptPayment(Integer m_AD_PInstance_ID,Integer m_C_Cash_ID) {
		// TODO Auto-generated constructor stub
		this.m_AD_PInstance_ID=m_AD_PInstance_ID;
		this.m_C_Cash_ID=m_C_Cash_ID;
	}

	/** @author Carlos Parada
	 * @throws SQLException 
	 * @Date 23/03/2012
	 * @Return boolean 
	 * Carga los datos del recibo de cobro en tabla temporal
	 */
	public boolean LoadDataReport() throws SQLException
	{
		if(InsertT_XX_ReceiptPayment()!=null)
			return true;
		else
			return false;	
	}
	/**@author Carlos Parada
	 * @date 26/03/2012
	 * @return Resultset
	 * Devuelve resultset con los los datos del recibo de cobro agrupado por socio de negocio
	 * */
	private ResultSet InsertT_XX_ReceiptPayment() throws SQLException
	{
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		getSql(sql);
		PreparedStatement ps = DB.prepareStatement(sql.toString(), null);
		ps.execute();
		rs=ps.getResultSet();
		
		ps.close();
		return rs;
	}
	/**
	 * @autor Carlos Parada
	 * @date 30/03/2012
	 * @return void
	 * arma la consulta sql para cargar la data del reporte
	 * */
	private void getSql(StringBuffer sql)
	{
	sql.delete(0, sql.length());
	sql.append("Insert into T_XX_ReceiptPayment"+
					   "(AD_Client_ID,"+
					   "AD_Org_ID,"+
					   "AD_PInstance_ID," +
					   "C_Cash_ID," +
					   "C_BPartner_ID)");
			sql.append("Select Distinct " +
					"CC.AD_Client_ID," +
					"CC.AD_Org_ID," +
					m_AD_PInstance_ID +","+
					"CC.C_Cash_ID," +
					"Case When CI.C_Invoice_ID Is Null Then CCL.C_BPartner_ID Else CI.C_BPartner_ID End As C_BPartner_ID " +
					"From C_Cash CC Left Join C_CashLine CCL On CC.C_Cash_ID = CCL.C_Cash_ID " +
					" Left Join C_Invoice CI On CI.C_Invoice_ID = CCL.C_Invoice_ID" +
					" Where CC.C_Cash_ID="+m_C_Cash_ID);
	}
	
	private Integer m_AD_PInstance_ID;
	private Integer m_C_Cash_ID;

}
