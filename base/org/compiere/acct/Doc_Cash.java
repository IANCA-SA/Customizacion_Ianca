/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.compiere.acct;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.model.MAcctSchema;
import org.compiere.model.MCash;
import org.compiere.model.MCashBook;
import org.compiere.model.MCashLine;
import org.compiere.model.MDocType;
import org.compiere.model.MSysConfig;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 * Post Invoice Documents.
 * 
 * <pre>
 *  Table:              C_Cash (407)
 *  Document Types:     CMC
 * </pre>
 * 
 * @author Jorg Janke
 * @version $Id: Doc_Cash.java,v 1.3 2006/07/30 00:53:33 jjanke Exp $
 */
public class Doc_Cash extends Doc
{
	
	/**
	 * Constructor
	 * 
	 * @param ass
	 *            accounting schemata
	 * @param rs
	 *            record
	 * @param trxName
	 *            trx
	 */
	public Doc_Cash(MAcctSchema[] ass, ResultSet rs, String trxName)
	{
		super(ass, MCash.class, rs, DOCTYPE_CashJournal, trxName);
	} // Doc_Cash

	/**
	 * Load Specific Document Details
	 * 
	 * @return error message or null
	 */
	protected String loadDocumentDetails()
	{
		MCash cash = (MCash) getPO();
		setDateDoc(cash.getStatementDate());

		// Amounts
		setAmount(Doc.AMTTYPE_Gross, cash.getStatementDifference());

		// Set CashBook Org & Currency
		MCashBook cb = MCashBook.get(getCtx(), cash.getC_CashBook_ID());
		setC_CashBook_ID(cb.getC_CashBook_ID());
		setC_Currency_ID(cb.getC_Currency_ID());

		/**
		 * @author <a href="mailto:jlct.master@gmail.com">Jorge Colmenarez</a>
		 *         Jun 21, 2013, 11:53:01 AM Acct Tax
		 */
		if (MSysConfig.getBooleanValue("TAX_ACCT_CASH", false))
			m_taxes = loadTaxes();
		/**
		 * Fin Jorge Colmenarez
		 */

		// Contained Objects
		p_lines = loadLines(cash, cb);
		log.fine("Lines=" + p_lines.length);
		return null;
	} // loadDocumentDetails

	/**
	 * Load Cash Line
	 * 
	 * @param cash
	 *            journal
	 * @param cb
	 *            cash book
	 * @return DocLine Array
	 */
	private DocLine[] loadLines(MCash cash, MCashBook cb)
	{
		ArrayList<DocLine> list = new ArrayList<DocLine>();
		MCashLine[] lines = cash.getLines(false);
		for (int i = 0; i < lines.length; i++)
		{
			MCashLine line = lines[i];
			DocLine_Cash docLine = new DocLine_Cash(line, this);
			//
			list.add(docLine);
		}

		// Return Array
		DocLine[] dls = new DocLine[list.size()];
		list.toArray(dls);
		return dls;
	} // loadLines

	/**************************************************************************
	 * Get Source Currency Balance - subtracts line amounts from total - no
	 * rounding
	 * 
	 * @return positive amount, if total invoice is bigger than lines
	 */
	public BigDecimal getBalance()
	{
		BigDecimal retValue = Env.ZERO;
		StringBuffer sb = new StringBuffer(" [");
		// Total
		retValue = retValue.add(getAmount(Doc.AMTTYPE_Gross));
		sb.append(getAmount(Doc.AMTTYPE_Gross));
		// - Lines
		for (int i = 0; i < p_lines.length; i++)
		{
			retValue = retValue.subtract(p_lines[i].getAmtSource());
			sb.append("-").append(p_lines[i].getAmtSource());
		}
		sb.append("]");
		//
		log.fine(toString() + " Balance=" + retValue + sb.toString());
		// return retValue;
		return Env.ZERO; // Lines are balanced
	} // getBalance

	/**
	 * Create Facts (the accounting logic) for CMC.
	 * 
	 * <pre>
	 *  Expense
	 *          CashExpense     DR
	 *          CashAsset               CR
	 *  Receipt
	 *          CashAsset       DR
	 *          CashReceipt             CR
	 *  Charge
	 *          Charge          DR
	 *          CashAsset               CR
	 *  Difference
	 *          CashDifference  DR
	 *          CashAsset               CR
	 *  Invoice
	 *          CashAsset       DR
	 *          CashTransfer            CR
	 *  Transfer
	 *          BankInTransit   DR
	 *          CashAsset               CR
	 * </pre>
	 * 
	 * @param as
	 *            account schema
	 * @return Fact
	 */
	public ArrayList<Fact> createFacts(MAcctSchema as)
	{
		// Need to have CashBook
		if (getC_CashBook_ID() == 0)
		{
			p_Error = "C_CashBook_ID not set";
			log.log(Level.SEVERE, p_Error);
			return null;
		}

		// create Fact Header
		Fact fact = new Fact(this, as, Fact.POST_Actual);

		// Header posting amt as Invoices and Transfer could be differenet
		// currency
		// CashAsset Total
		BigDecimal assetAmt = Env.ZERO;

		// Lines
		for (int i = 0; i < p_lines.length; i++)
		{
			DocLine_Cash line = (DocLine_Cash) p_lines[i];
			String CashType = line.getCashType();

			if (CashType.equals(DocLine_Cash.CASHTYPE_EXPENSE))
			{ // amount is negative
				// CashExpense DR
				// CashAsset CR
				fact.createLine(line, getAccount(Doc.ACCTTYPE_CashExpense, as),
						getC_Currency_ID(), line.getAmount().negate(), null);
				// fact.createLine(line, getAccount(Doc.ACCTTYPE_CashAsset, as),
				// p_vo.C_Currency_ID, null, line.getAmount().negate());
				assetAmt = assetAmt.subtract(line.getAmount().negate());
			} else if (CashType.equals(DocLine_Cash.CASHTYPE_RECEIPT))
			{ // amount is positive
				// CashAsset DR
				// CashReceipt CR
				// fact.createLine(line, getAccount(Doc.ACCTTYPE_CashAsset, as),
				// p_vo.C_Currency_ID, line.getAmount(), null);
				assetAmt = assetAmt.add(line.getAmount());
				fact.createLine(line, getAccount(Doc.ACCTTYPE_CashReceipt, as),
						getC_Currency_ID(), null, line.getAmount());
			} else if (CashType.equals(DocLine_Cash.CASHTYPE_CHARGE))
			{ // amount is negative
				// Charge DR
				// CashAsset CR

				/**
				 * @author <a href="mailto:jlct.master@gmail.com">Carlos
				 *         Parada</a> Jun 21, 2013, 11:53:01 AM Set Base Amt in
				 *         Acct
				 */
				// fact.createLine(line, line.getChargeAccount(as,
				// line.getAmount().negate()),
				// getC_Currency_ID(), line.getAmount().negate());
				fact.createLine(line, line.getChargeAccount(as, getAmount()),
						getC_Currency_ID(), line.getBaseAmount().negate(), null);
				/**
				 * End Jorge Colmenarez
				 */
				// fact.createLine(line, getAccount(Doc.ACCTTYPE_CashAsset, as),
				// p_vo.C_Currency_ID, null, line.getAmount().negate());
				assetAmt = assetAmt.subtract(line.getBaseAmount().negate());
			} else if (CashType.equals(DocLine_Cash.CASHTYPE_DIFFERENCE))
			{ // amount is pos/neg
				// CashDifference DR
				// CashAsset CR
				fact.createLine(line,
						getAccount(Doc.ACCTTYPE_CashDifference, as),
						getC_Currency_ID(), line.getAmount().negate());
				// fact.createLine(line, getAccount(Doc.ACCTTYPE_CashAsset, as),
				// p_vo.C_Currency_ID, line.getAmount());
				assetAmt = assetAmt.add(line.getAmount());
			} else if (CashType.equals(DocLine_Cash.CASHTYPE_INVOICE))
			{ // amount is pos/neg
				// CashAsset DR dr -- Invoice is in Invoice Currency !
				// CashTransfer cr CR
				if (line.getC_Currency_ID() == getC_Currency_ID())
					assetAmt = assetAmt.add(line.getAmount());
				else
					fact.createLine(line,
							getAccount(Doc.ACCTTYPE_CashAsset, as),
							line.getC_Currency_ID(), line.getAmount());
				fact.createLine(line,
						getAccount(Doc.ACCTTYPE_CashTransfer, as),
						line.getC_Currency_ID(), line.getAmount().negate());
			} else if (CashType.equals(DocLine_Cash.CASHTYPE_TRANSFER))
			{ // amount is pos/neg
				// BankInTransit DR dr -- Transfer is in Bank Account Currency
				// CashAsset dr CR
				int temp = getC_BankAccount_ID();
				setC_BankAccount_ID(line.getC_BankAccount_ID());
				fact.createLine(line,
						getAccount(Doc.ACCTTYPE_BankInTransit, as),
						line.getC_Currency_ID(), line.getAmount().negate());
				setC_BankAccount_ID(temp);
				if (line.getC_Currency_ID() == getC_Currency_ID())
					assetAmt = assetAmt.add(line.getAmount());
				else
					fact.createLine(line,
							getAccount(Doc.ACCTTYPE_CashAsset, as),
							line.getC_Currency_ID(), line.getAmount());
			}
		} // lines

		/**
		 * 	@author <a href="mailto:jlct.master@gmail.com">Jorge Colmenarez</a>
		 *         Jun 21, 2013, 11:53:01 AM Add Tax To acct
		 * 	@collaborator <a href="mailto:jlct.master@gmail.com">Jorge Colmenarez</a>
		 * 	Add Support for set Account Type from Document Type of the Cash
		 */
		if (MSysConfig.getBooleanValue("TAX_ACCT_CASH", false))
		{
			BigDecimal amt = Env.ZERO;
			/**	Added By Jorge Colmenarez 2014-12-18 */
			MCash mCash = new MCash(getCtx(), get_ID(), getTrxName());
			MDocType mDocType = new MDocType(getCtx(), mCash.get_ValueAsInt("C_DocTypeTarget_ID"), getTrxName());
			/** End Jorge Colmenarez */
			// TaxDue CR
			for (int i = 0; i < m_taxes.length; i++)
			{
				amt = m_taxes[i].getAmount();
				if (amt != null && amt.signum() != 0)
				{
					/**
					 * 	Commented By Jorge Colmenarez 2014-12-18 
					 *  FactLine tl = fact.createLine(null,m_taxes[i].getAccount(DocTax.ACCTTYPE_TaxDue, as),getC_Currency_ID(), amt.negate(), null);
					 *  
					 *  Added By Jorge Colmenarez 
					 *  Set Account Type from Document Type 
					 *   */
					FactLine tl;
					if(mDocType.isSOTrx()==true){
						tl = fact.createLine(null, m_taxes[i].getAccount(DocTax.ACCTTYPE_TaxDue, as),
								getC_Currency_ID(), amt.negate(), null);
					}else{
						tl = fact.createLine(null, m_taxes[i].getAccount(DocTax.ACCTTYPE_TaxCredit, as),
							getC_Currency_ID(), amt.negate(), null);
					}
					
					assetAmt=assetAmt.add(amt);
					
					/** End Jorge Colmenarez */
					if (tl != null)
						tl.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
					
				}
			}
		}
		/**
		 * End Jorge Colmenarez
		 */

		if (assetAmt.compareTo(Env.ZERO) != 0)
		{ // Carlos Ruiz - globalqss [BF 2904269]
			// Cash Asset
			fact.createLine(null, getAccount(Doc.ACCTTYPE_CashAsset, as),
					getC_Currency_ID(), assetAmt);
		}

		//
		ArrayList<Fact> facts = new ArrayList<Fact>();
		facts.add(fact);
		return facts;
	} // createFact

	/**
	 * @author <a href="mailto:jlct.master@gmail.com">Jorge Colmenarez</a> Jun
	 *         08, 2015, 15:30:01 AM Load Cash Taxes
	 * @return DocTax Array
	 */
	private DocTax[] loadTaxes()
	{
		ArrayList<DocTax> list = new ArrayList<DocTax>();
		String sql = 
				"   SELECT ct.C_Tax_ID, t.Name, t.Rate, ct.TaxBaseAmt, ct.TaxAmt, t.IsSalesTax"
				+ " FROM C_Tax t "
				+ " INNER JOIN LVE_CashTax ct ON t.C_Tax_ID = ct.C_Tax_ID"
				+ " INNER JOIN C_Cash c ON ct.C_Cash_ID = c.C_Cash_ID"
				+ " WHERE "
				+ "		ct.C_Cash_ID = ?"
				+ "		AND c.DocStatus IN ('DR','CO','CL')";
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql, getTrxName());
			pstmt.setInt(1, get_ID());
			ResultSet rs = pstmt.executeQuery();
			//
			while (rs.next())
			{
				int C_Tax_ID = rs.getInt(1);
				String name = rs.getString(2);
				BigDecimal rate = rs.getBigDecimal(3);
				BigDecimal taxBaseAmt = rs.getBigDecimal(4);
				BigDecimal amount = rs.getBigDecimal(5);
				boolean salesTax = "Y".equals(rs.getString(6));
				//
				DocTax taxLine = new DocTax(C_Tax_ID, name, rate, taxBaseAmt,
						amount, salesTax);
				log.fine(taxLine.toString());
				list.add(taxLine);
			}
			//
			rs.close();
			pstmt.close();
		} catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
			return null;
		}

		// Return Array
		DocTax[] tl = new DocTax[list.size()];
		list.toArray(tl);
		return tl;
	} // loadTaxes

	/** Contained Optional Tax Lines */
	private DocTax[]	m_taxes	= null;

	/**
	 * End Jorge Colmenarez
	 */

} // Doc_Cash
