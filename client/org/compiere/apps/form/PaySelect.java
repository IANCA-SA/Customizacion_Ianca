/******************************************************************************
 * Copyright (C) 2009 Low Heng Sin                                            *
 * Copyright (C) 2009 Idalica Corporation                                     *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package org.compiere.apps.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.minigrid.ColumnInfo;
import org.compiere.minigrid.IDColumn;
import org.compiere.minigrid.IMiniTable;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MLookupInfo;
import org.compiere.model.MPaySelection;
import org.compiere.model.MPaySelectionLine;
import org.compiere.model.MRole;
import org.compiere.model.X_C_Order;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Language;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.ValueNamePair;


public class PaySelect
{
	/** @todo withholding */

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 2872767371244295934L;

	/**	Window No			*/
	public int         	m_WindowNo = 0;

	/** Format                  */
	public DecimalFormat   m_format = DisplayType.getNumberFormat(DisplayType.Amount);
	/** Bank Balance            */
	private BigDecimal      m_bankBalance = new BigDecimal(0.0);
	/** SQL for Query           */
	private String          m_sql;
	/** Number of selected rows */
	public int             m_noSelected = 0;
	/** Client ID               */
	private int             m_AD_Client_ID = 0;
	/**/
	public boolean         m_isLocked = false;
	/** Payment Selection		*/
	public MPaySelection	m_ps = null;
	/**	Logger			*/
	public static CLogger log = CLogger.getCLogger(PaySelect.class);
	
	/**	Columnas de la Tabla	*/
	public static final int OPEN_AMT = 4;
	public static final int PAY_AMT = 8;

	public ArrayList<BankInfo> getBankAccountData()
	{
		ArrayList<BankInfo> data = new ArrayList<BankInfo>();
		//
		m_AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
		//  Bank Account Info
		String sql = MRole.getDefault().addAccessSQL(
			"SELECT ba.C_BankAccount_ID,"                       //  1
			+ "b.Name || ' ' || ba.AccountNo AS Name,"          //  2
			+ "ba.C_Currency_ID, c.ISO_Code,"                   //  3..4
			+ "ba.CurrentBalance, "                              //  5
			//Carlos Parada 11/05/2012 Saldo Disponible del Banco
			+ "ba.CurrentBalance  + " +
					" Case When Exists(Select 1 From C_Payment CP Where CP.isreconciled='N'And CP.processed='Y' And CP.docstatus NOT IN ('DR') And CP.datetrx<=current_date And CP.c_bankaccount_id=ba.C_BankAccount_ID) Then " +
					"	(Select Sum(Case When CP.IsReceipt='Y' then CP.payamt else CP.payamt * -1 end) From C_Payment CP Where CP.isreconciled='N'And CP.processed='Y' And CP.docstatus NOT IN ('DR') And CP.datetrx<=current_date And CP.c_bankaccount_id=ba.C_BankAccount_ID)" +
					"	   Else 0 " +
					" End As Available "
			//Fin Carlos Parada
			+ "FROM C_Bank b, C_BankAccount ba, C_Currency c "
			+ "WHERE b.C_Bank_ID=ba.C_Bank_ID"
			+ " AND ba.C_Currency_ID=c.C_Currency_ID AND ba.IsActive='Y' "
			+ " AND EXISTS (SELECT * FROM C_BankAccountDoc d WHERE d.C_BankAccount_ID=ba.C_BankAccount_ID AND d.IsActive='Y' ) "
			+ "ORDER BY 2",
			"b", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RW);
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql, null);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				boolean transfers = false;
				BankInfo bi = new BankInfo (rs.getInt(1), rs.getInt(3),
					rs.getString(2), rs.getString(4),
					rs.getBigDecimal(5), transfers,rs.getBigDecimal(6));
				data.add(bi);
			}
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		
		return data;
	}
	
	public ArrayList<KeyNamePair> getBPartnerData()
	{
		ArrayList<KeyNamePair> data = new ArrayList<KeyNamePair>();
		
		//  Optional BusinessPartner with unpaid AP Invoices
		KeyNamePair pp = new KeyNamePair(0, "");
		data.add(pp);
		
		String sql = MRole.getDefault().addAccessSQL(
			"SELECT bp.C_BPartner_ID, bp.Name FROM C_BPartner bp", "bp",
			MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO)
			+ " AND EXISTS (SELECT * FROM C_Invoice i WHERE bp.C_BPartner_ID=i.C_BPartner_ID"
			//	X_C_Order.PAYMENTRULE_DirectDebit
			  + " AND (i.IsSOTrx='N' OR (i.IsSOTrx='Y' AND i.PaymentRule='D'))"
			  + " AND i.IsPaid<>'Y') "
			+ "ORDER BY 2";

		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql, null);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				pp = new KeyNamePair(rs.getInt(1), rs.getString(2));
				data.add(pp);
			}
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		
		return data;
	}
	
	public ArrayList<KeyNamePair> getDocTypeData()
	{
		ArrayList<KeyNamePair> data = new ArrayList<KeyNamePair>();
		String sql = null;
		/**Document type**/
		try
		{
			/*
			 * Modificado por Yamel Senih 13/04/2011, 17:44:35
			 * Para Agregar los Documentos Base del tipo Gasstos Generales "DGS"
			 */
			sql = MRole.getDefault().addAccessSQL(
				"SELECT doc.c_doctype_id,doc.name FROM c_doctype doc WHERE doc.ad_client_id = ? AND doc.docbasetype in ('API','APC', 'DGS') ORDER BY 2", "doc",
				MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);

			KeyNamePair dt = new KeyNamePair(0, "");
			data.add(dt);
			PreparedStatement pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, m_AD_Client_ID);		//	Client
			ResultSet rs = pstmt.executeQuery();

			while (rs.next())
			{
				dt = new KeyNamePair(rs.getInt(1), rs.getString(2));
				data.add(dt);
			}
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		
		return data;
	}
	
	public void prepareTable(IMiniTable miniTable)
	{
		Properties ctx = Env.getCtx();
		/**  prepare MiniTable
		 *
		SELECT i.C_Invoice_ID, i.DateInvoiced+p.NetDays AS DateDue,
		bp.Name, i.DocumentNo, c.ISO_Code, i.GrandTotal,
		paymentTermDiscount(i.GrandTotal, i.C_PaymentTerm_ID, i.DateInvoiced, SysDate) AS Discount,
		SysDate-paymentTermDueDays(i.C_PaymentTerm_ID,i.DateInvoiced) AS DiscountDate,
		i.GrandTotal-paymentTermDiscount(i.GrandTotal,i.C_PaymentTerm_ID,i.DateInvoiced,SysDate) AS DueAmount,
		currencyConvert(i.GrandTotal-paymentTermDiscount(i.GrandTotal,i.C_PaymentTerm_ID,i.DateInvoiced,SysDate,null),
			i.C_Currency_ID,xx100,SysDate) AS PayAmt, bpba.AccountNo
		FROM C_Invoice i, C_BPartner bp, C_Currency c, C_PaymentTerm p, C_BP_BankAccount bpba
		WHERE i.IsSOTrx='N'
		AND i.C_BPartner_ID=bp.C_BPartner_ID
		AND i.C_Currency_ID=c.C_Currency_ID
		AND i.C_PaymentTerm_ID=p.C_PaymentTerm_ID
		AND i.DocStatus IN ('CO','CL')
		AND i.C_BPartner_ID=bpba.C_BPartner_ID
		ORDER BY 2,3
		 */

		m_sql = miniTable.prepareTable(new ColumnInfo[] {
			//	Yamel Senih 17/05/2011, 10:16 Cambio de Orden de columnas por Defecto
			//  0..4
			new ColumnInfo(" ", "i.C_Invoice_ID", IDColumn.class, false, false, null),
			new ColumnInfo(Msg.translate(ctx, "C_BPartner_ID"), "bp.Name", KeyNamePair.class, true, false, "i.C_BPartner_ID"),
			new ColumnInfo(Msg.translate(ctx, "DocumentNo"), "i.DocumentNo", String.class),
			new ColumnInfo(Msg.translate(ctx, "AccountNo"), "bpfirstaccountno(i.C_BPartner_ID)", String.class), //AQuiros e-evolution de Vzla
			new ColumnInfo(Msg.getMsg(ctx, "AmountDue"), "currencyConvert(invoiceOpen(i.C_Invoice_ID,i.C_InvoicePaySchedule_ID),i.C_Currency_ID, ?,?,i.C_ConversionType_ID, i.AD_Client_ID,i.AD_Org_ID)", BigDecimal.class),
			
			/**/
			//new ColumnInfo(Msg.translate(ctx, "DueDate"), "paymentTermDueDate(i.C_PaymentTerm_ID, i.DateInvoiced) AS DateDue", Timestamp.class, true, true, null),
			new ColumnInfo(Msg.translate(ctx, "DueDate"), "i.DueDate", Timestamp.class, true, true, null),
			
			
			new ColumnInfo(Msg.translate(ctx, "DiscountAmt"), "paymentTermDiscount(i.GrandTotal,i.C_Currency_ID,i.C_PaymentTerm_ID,i.DateInvoiced, ?)", BigDecimal.class),
			new ColumnInfo(Msg.getMsg(ctx, "DiscountDate"), "SysDate-paymentTermDueDays(i.C_PaymentTerm_ID,i.DateInvoiced,SysDate)", Timestamp.class),
			
			
			
			new ColumnInfo(Msg.getMsg(ctx, "AmountPay"), "currencyConvert(invoiceOpen(i.C_Invoice_ID,i.C_InvoicePaySchedule_ID)-paymentTermDiscount(i.GrandTotal,i.C_Currency_ID,i.C_PaymentTerm_ID,i.DateInvoiced, ?),i.C_Currency_ID, ?,?,i.C_ConversionType_ID, i.AD_Client_ID,i.AD_Org_ID)", BigDecimal.class),
			
			
			
			
			new ColumnInfo(Msg.translate(ctx, "C_Currency_ID"), "c.ISO_Code", KeyNamePair.class, true, false, "i.C_Currency_ID"),
			// 5..9
			new ColumnInfo(Msg.translate(ctx, "GrandTotal"), "i.GrandTotal", BigDecimal.class),
			
			
			},
			//	FROM
			"C_Invoice_v i"
			+ " INNER JOIN C_BPartner bp ON (i.C_BPartner_ID=bp.C_BPartner_ID)"
			+ " INNER JOIN C_Currency c ON (i.C_Currency_ID=c.C_Currency_ID)"
			+ " INNER JOIN C_PaymentTerm p ON (i.C_PaymentTerm_ID=p.C_PaymentTerm_ID)",
			//	WHERE
			"i.IsSOTrx=? AND IsPaid='N'"
			//	Different Payment Selection
			+ " AND NOT EXISTS (SELECT * FROM C_PaySelectionLine psl"
			+                 " INNER JOIN C_PaySelectionCheck psc ON (psl.C_PaySelectionCheck_ID=psc.C_PaySelectionCheck_ID)"
			+                 " LEFT OUTER JOIN C_Payment pmt ON (pmt.C_Payment_ID=psc.C_Payment_ID)"
			+                 " WHERE i.C_Invoice_ID=psl.C_Invoice_ID AND psl.IsActive='Y'"
			+				  " AND (pmt.DocStatus IS NULL OR pmt.DocStatus NOT IN ('VO','RE')) )"
			+ " AND i.DocStatus IN ('CO','CL')"
			//	Yamel Senih 17/05/2011, 10:15 No mostrar los Documentos en Negociación
			+ " AND i.IsInDispute = 'N'"
			//	Fin Yamel Senih
			+ " AND i.AD_Client_ID=?",	//	additional where & order in loadTableInfo()
			true, "i");

	}   //  dynInit

	/**
	 *  Load Bank Info - Load Info from Bank Account and valid Documents (PaymentRule)
	 */
	public ArrayList<ValueNamePair> getPaymentRuleData(BankInfo bi)
	{
		if (bi == null)
			return null;
		m_bankBalance = bi.Balance;
		
		ArrayList<ValueNamePair> data = new ArrayList<ValueNamePair>();
		
		int AD_Reference_ID = 195;  //  MLookupInfo.getAD_Reference_ID("All_Payment Rule");
		Language language = Env.getLanguage(Env.getCtx());
		MLookupInfo info = MLookupFactory.getLookup_List(language, AD_Reference_ID);
		String sql = info.Query.substring(0, info.Query.indexOf(" ORDER BY"))
			+ " AND " + info.KeyColumn
			+ " IN (SELECT PaymentRule FROM C_BankAccountDoc WHERE C_BankAccount_ID=?) "
			+ info.Query.substring(info.Query.indexOf(" ORDER BY"));
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, bi.C_BankAccount_ID);
			ResultSet rs = pstmt.executeQuery();
			ValueNamePair vp = null;
			while (rs.next())
			{
				vp = new ValueNamePair(rs.getString(2), rs.getString(3));   //  returns also not active
				data.add(vp);
			}
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		return data;
	}

	/**
	 *  Query and create TableInfo
	 */
	public void loadTableInfo(BankInfo bi, Timestamp payDate, ValueNamePair paymentRule, boolean onlyDue, 
			KeyNamePair bpartner, KeyNamePair docType, IMiniTable miniTable)
	{
		log.config("");
		//  not yet initialized
		if (m_sql == null)
			return;

		String sql = m_sql;
		//  Parameters
		String isSOTrx = "N";
		if (paymentRule != null && X_C_Order.PAYMENTRULE_DirectDebit.equals(paymentRule.getValue()))
		{
			isSOTrx = "Y";
			sql += " AND i.PaymentRule='" + X_C_Order.PAYMENTRULE_DirectDebit + "'";
		}
		//
		if (onlyDue)
			sql += " AND paymentTermDueDate(i.C_PaymentTerm_ID, i.DateInvoiced) <= ?";
		//
		KeyNamePair pp = bpartner;
		int C_BPartner_ID = pp.getKey();
		if (C_BPartner_ID != 0)
			sql += " AND i.C_BPartner_ID=?";
		//Document Type
		KeyNamePair dt = docType;
		int c_doctype_id  = dt.getKey();
		if (c_doctype_id   != 0)
			sql += " AND i.c_doctype_id =?";
		sql += " ORDER BY 2,3";

		log.finest(sql + " - C_Currency_ID=" + bi.C_Currency_ID + ", C_BPartner_ID=" + C_BPartner_ID + ", C_doctype_id=" + c_doctype_id  );
		//  Get Open Invoices
		try
		{
			int index = 1;
			PreparedStatement pstmt = DB.prepareStatement(sql, null);
			
			pstmt.setInt(index++, bi.C_Currency_ID);	//	DueAmt
			pstmt.setTimestamp(index++, payDate);
			
			pstmt.setTimestamp(index++, payDate);		//	DiscountAmt
			
			pstmt.setTimestamp(index++, payDate);		//	PayAmt
			pstmt.setInt(index++, bi.C_Currency_ID);
			pstmt.setTimestamp(index++, payDate);
			pstmt.setString(index++, isSOTrx);			//	IsSOTrx
			pstmt.setInt(index++, m_AD_Client_ID);		//	Client
			if (onlyDue)
				pstmt.setTimestamp(index++, payDate);
			if (C_BPartner_ID != 0)
				pstmt.setInt(index++, C_BPartner_ID);
			if (c_doctype_id  != 0)                    //Document type
				pstmt.setInt(index++, c_doctype_id );
			//
			ResultSet rs = pstmt.executeQuery();
			miniTable.loadTable(rs);
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
		}
	}   //  loadTableInfo

	/**
	 *  Calculate selected rows.
	 *  - add up selected rows
	 */
	public String calculateSelection(IMiniTable miniTable)
	{
		m_noSelected = 0;
		BigDecimal invoiceAmt = new BigDecimal(0.0);

		int rows = miniTable.getRowCount();
		for (int i = 0; i < rows; i++)
		{
			IDColumn id = (IDColumn)miniTable.getValueAt(i, 0);
			if (id.isSelected())
			{
				// Yamel Senih Cambio (9) por PAY_AMT 17/05/2011, 10:49
				BigDecimal amt = (BigDecimal)miniTable.getValueAt(i, PAY_AMT);
				if (amt != null)
					invoiceAmt = invoiceAmt.add(amt);
				m_noSelected++;
			}
		}

		//  Information
		BigDecimal remaining = m_bankBalance.subtract(invoiceAmt);
		StringBuffer info = new StringBuffer();
		info.append(m_noSelected).append(" ").append(Msg.getMsg(Env.getCtx(), "Selected")).append(" - ");
		info.append(m_format.format(invoiceAmt)).append(", ");
		info.append(Msg.getMsg(Env.getCtx(), "Remaining")).append(" ").append(m_format.format(remaining));
		return info.toString();
	}   //  calculateSelection

	public Trx trx = null;
	
	/**
	 *  Generate PaySelection
	 */
	public String generatePaySelect(IMiniTable miniTable, ValueNamePair paymentRule, Timestamp payDate, BankInfo bi)
	{
		log.info("");
	//	String trxName Trx.createTrxName("PaySelect");
	//	Trx trx = Trx.get(trxName, true);	trx needs to be committed too
		String trxName = null;
		trx = null;

		String PaymentRule = paymentRule.getValue();

		//	Yamel Senih 27/04/2011, 18:07
		//	Se quita el valor de la fecha en el nombre de la selección de pago
		
		//  Create Header
		m_ps = new MPaySelection(Env.getCtx(), 0, trxName);
		m_ps.setName (Msg.getMsg(Env.getCtx(), "VPaySelect")
				+ " - " + paymentRule.getName()
				/*+ " - " + payDate*/);
		
		//	Fin Yamel Senih
		
		m_ps.setPayDate (payDate);
		m_ps.setC_BankAccount_ID(bi.C_BankAccount_ID);
		m_ps.setIsApproved(true);
		if (!m_ps.save())
		{
			m_ps = null;
			return Msg.translate(Env.getCtx(), "C_PaySelection_ID");
		}
		log.config(m_ps.toString());

		//  Create Lines
		int rows = miniTable.getRowCount();
		int line = 0;
		for (int i = 0; i < rows; i++)
		{
			IDColumn id = (IDColumn)miniTable.getValueAt(i, 0);
			if (id.isSelected())
			{
				line += 10;
				MPaySelectionLine psl = new MPaySelectionLine (m_ps, line, PaymentRule);
				int C_Invoice_ID = id.getRecord_ID().intValue();
				
				//	Yamel Senih 17/05/2011 Se crean Constantes para las posiciones de la tabla
				
				BigDecimal OpenAmt = (BigDecimal)miniTable.getValueAt(i, OPEN_AMT);
				BigDecimal PayAmt = (BigDecimal)miniTable.getValueAt(i, PAY_AMT);
				boolean isSOTrx = false;
				//
				psl.setInvoice(C_Invoice_ID, isSOTrx,
					OpenAmt, PayAmt, OpenAmt.subtract(PayAmt));
				if (!psl.save(trxName))
				{
					return Msg.translate(Env.getCtx(), "C_PaySelectionLine_ID");
				}
				log.fine("C_Invoice_ID=" + C_Invoice_ID + ", PayAmt=" + PayAmt);
			}
		}   //  for all rows in table
		
		return null;
	}   //  generatePaySelect

	/**************************************************************************
	 *  Bank Account Info
	 */
	public class BankInfo
	{
		/**
		 * 	BankInfo
		 *	@param newC_BankAccount_ID
		 *	@param newC_Currency_ID
		 *	@param newName
		 *	@param newCurrency
		 *	@param newBalance
		 *	@param newTransfers
		 */
		public BankInfo (int newC_BankAccount_ID, int newC_Currency_ID,
			String newName, String newCurrency, BigDecimal newBalance, boolean newTransfers, BigDecimal newAvailable)
		{
			C_BankAccount_ID = newC_BankAccount_ID;
			C_Currency_ID = newC_Currency_ID;
			Name = newName;
			Currency = newCurrency;
			Balance = newBalance;
			Available = newAvailable;
		}
		int C_BankAccount_ID;
		int C_Currency_ID;
		String Name;
		public String Currency;
		public BigDecimal Balance;
		public BigDecimal Available;
		boolean Transfers;

		/**
		 * 	to String
		 *	@return info
		 */
		public String toString()
		{
			return Name;
		}
	}   //  BankInfo

}   //  VPaySelect
