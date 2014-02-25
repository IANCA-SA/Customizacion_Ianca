/**
 * @author Carlos Parada
 * @Date 25/01/2012
 */
package org.sg.report;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.compiere.model.MBankStatement;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;

/**
 * @author Carlos Parada
 *
 */
public class XX_Conciliacion extends SvrProcess{

	/* (non-Javadoc)
	 * @see org.compiere.process.SvrProcess#prepare()
	 */
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub		
		MBankStatement bs = new MBankStatement(getCtx(), getRecord_ID(), null);
		m_C_BankStatement_ID = bs.getC_BankStatement_ID();
		m_C_BankAccount_ID = bs.getC_BankAccount_ID();
	}

	/* (non-Javadoc)
	 * @see org.compiere.process.SvrProcess#doIt()
	 */
	@Override
	protected String doIt() throws Exception {
		// TODO Auto-generated method stub
		prepare();
		loadConciliacion();
		return null;
	}

	
	private ResultSet GetBankStatementFinal()
	{

		StringBuffer sql = new StringBuffer();
		ResultSet rs=null;
		
		sql.append("select c_bankstatement_id,statementdate,beginningbalance,c_bankaccount_id,ad_org_id " +
				"	from c_bankstatement " +
				"	where c_bankstatement_id =? " );
		
		try {
			PreparedStatement ps = DB.prepareStatement(sql.toString(),null);
			ps.setInt(1, m_C_BankStatement_ID);
			rs = ps.executeQuery();
			if (rs.next())
				;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.log(Level.SEVERE,e.getMessage().toString());
		}
		
		return rs;
	}
	
	private void loadConciliacion()
	{
		
		String trxName = Trx.createTrxName("XXCO");	
		Trx trx = Trx.get(trxName, true);
		ResultSet rs_finalStatement=GetBankStatementFinal();
		PreparedStatement psInsert = null;
		StringBuffer sql = new StringBuffer();
		if (rs_finalStatement!=null)
		{
			try {
				///Insertando el Encabezado de la Conciliacion
				sql.append("insert into T_XX_Conciliacion(C_Bankstatement_ID,C_BankAccount_ID,AD_Client_ID,AD_Org_ID,AD_PInstance_ID,AmountNotReconciled,T_XX_Conciliacion_ID)" +
				" Values(?,?,?,?,?,(Select " +  
						"sum(Case When CP.IsReceipt='Y' then CP.payamt else CP.payamt * -1 end) " + 
						"From "  +
						"c_payment CP "+ 
						"Where " +
						"CP.isreconciled='N' And CP.processed='Y' And CP.docstatus NOT IN ('DR') And CP.datetrx<=? " +
						"And CP.c_bankaccount_id=?),"+m_C_BankStatement_ID+")");
				psInsert = DB.prepareStatement(sql.toString(),trxName);
				psInsert.setInt(1,m_C_BankStatement_ID);
				psInsert.setInt(2, rs_finalStatement.getInt("c_bankaccount_id"));
				psInsert.setInt(3,getAD_Client_ID());
				psInsert.setInt(4,rs_finalStatement.getInt("ad_org_id"));
				psInsert.setInt(5,getAD_PInstance_ID());
				psInsert.setDate(6,rs_finalStatement.getDate("statementdate"));
				psInsert.setInt(7, rs_finalStatement.getInt("c_bankaccount_id"));
				
				
				psInsert.executeUpdate();
				
				//Insertando el Detalle de la Conciliacion
				//Movimientos Conciliados
				
				sql.delete(0, sql.length());
				
				sql.append("insert into T_XX_ConciliacionLine(AD_Client_ID,AD_Org_ID,AD_PInstance_ID,isreconciled,StatementDate,trxamt,stmtamt,C_Payment_ID,chargeamt,C_Charge_ID,ismanual,tendertype,c_bankstatementline_id)" +
							"Select "+
							"CBSL.ad_client_id, "+
							"CBSL.ad_org_id, "+
							"? as ad_pinstance_id, "+
							"'Y' isreconciled, "+
							"CBS.statementdate, "+
							"CBSL.trxamt, "+
							"CBSL.stmtamt, "+
							"CP.C_Payment_ID, "+
							"CBSL.chargeamt, "+
							"CBSL.c_charge_id, "+
							"CBSL.ismanual, "+
							"CP.tendertype, "+
							"CBSL.c_bankstatementline_id "+
							"From "+
							"c_bankstatement CBS "+ 
							"Inner Join c_bankstatementline CBSL On CBS.c_bankstatement_id=CBSL.c_bankstatement_id "+
							"Left Join C_Payment CP On CBSL.C_Payment_ID=CP.C_Payment_ID "+
							"Left Join C_Charge CCH On CBSL.c_charge_id=CCH.c_charge_id "+
							"Where "+ 
							"((/*CP.isreconciled='Y' And*/ CP.docstatus NOT IN ('DR')) Or CP.c_payment_id is null) "+
							" And CBS.c_bankstatement_id=? "+
							" /*And CBSL.processed='Y'*/");
				
				//Union
				sql.append(" union ");
				
				//Movimientos No Conciliados
				sql.append("Select "+ 
							"CP.ad_client_id,"+
							"CP.ad_org_id,"+
							"? as ad_pinstance_id,"+
							"'N' isreconciled,"+
							"CP.datetrx, "+
							"Case When CP.IsReceipt='Y' then CP.payamt else CP.payamt * -1 end as trxamt, "+
							"Case When CP.IsReceipt='Y' then CP.payamt else CP.payamt * -1 end as stmamt, "+
							"CP.c_payment_id, "+
							"0 as chargeamt, "+
							"null as c_charge_id, "+
							"'N' as ismanual, "+
							"CP.tendertype, "+
							"null  as c_bankstatementline "+
							"From "+
							"c_payment CP "+
							"Where "+
							"CP.isreconciled='N'/*not exists(Select 1 from c_bankstatement cbs inner join c_bankstatementline cbsl on cbs.c_bankstatement_id = cbsl.c_bankstatement_id Where cbsl.c_payment_id=CP.c_payment_id and cbs.statementdate<=? and cbs.c_bankaccount_id=CP.c_bankaccount_id)*/ And CP.processed='Y' And CP.docstatus NOT IN ('DR') And CP.datetrx<=?"+
							" And CP.c_bankaccount_id=? "+
							"Order By 4 desc");
				
				psInsert = DB.prepareStatement(sql.toString(),trxName);
				psInsert.setInt(1, getAD_PInstance_ID());
				psInsert.setInt(2,m_C_BankStatement_ID);
				psInsert.setInt(3,getAD_PInstance_ID());
				psInsert.setDate(4,rs_finalStatement.getDate("statementdate"));
				//psInsert.setDate(5,rs_finalStatement.getDate("statementdate"));
				psInsert.setInt(5,m_C_BankAccount_ID);
				psInsert.executeUpdate();
				
				//Guardando
				trx.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				trx.rollback();
				log.log(Level.SEVERE,e.getMessage().toString());
			}
		}
		else
			log.info(Msg.translate(Env.getCtx(), "NotConciliation"));
		
	}
	
	
	
	
	private int m_C_BankStatement_ID;
	private int m_C_BankAccount_ID;
	
}
