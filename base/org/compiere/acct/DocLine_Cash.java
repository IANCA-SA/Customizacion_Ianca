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

import org.compiere.model.MCash;
import org.compiere.model.MCashLine;
import org.compiere.model.MCharge;
import org.compiere.model.MInvoice;
import org.compiere.model.MPayment;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTax;
import org.compiere.model.X_C_Cash;
import org.compiere.util.Env;

/**
 *  Cash Journal Line
 *
 *  @author Jorg Janke
 *  @version  $Id: DocLine_Cash.java,v 1.3 2006/07/30 00:53:33 jjanke Exp $
 *  
 *  @author Michael McKey
 *  	<li>BF3441324 Cash Lines from Payments don't add BPartner dimension
 */
public class DocLine_Cash extends DocLine
{
	/**
	 *  Constructor
	 *  @param line cash line
	 *  @param doc header
	 */
	public DocLine_Cash (MCashLine line, Doc_Cash doc)
	{
		super (line, doc);
		m_CashType = line.getCashType();
		m_C_BankAccount_ID = line.getC_BankAccount_ID();
		m_C_Invoice_ID = line.getC_Invoice_ID();
		m_C_Payment_ID = line.getC_Payment_ID();
		BigDecimal baseAmt = Env.ZERO;
		//
		if (m_C_Invoice_ID != 0)
		{
			MInvoice invoice = MInvoice.get(line.getCtx(), m_C_Invoice_ID);
			setC_BPartner_ID(invoice.getC_BPartner_ID());
		}

		//
		if (m_C_Payment_ID != 0)
		{
			MPayment payment = new MPayment(line.getCtx(),m_C_Payment_ID,null);
			setC_BPartner_ID(payment.getC_BPartner_ID());
		}

		/**
		 * @author <a href="mailto:carlosaparadam@gmail.com">Carlos Parada</a> Jun 21, 2013, 11:53:01 AM
		 * Acct tax in cash  
		 */
		if (MSysConfig.getBooleanValue("TAX_ACCT_CASH", false))
		{
			baseAmt = (BigDecimal)line.get_Value("XXMontoBase");
			MCash cash = new MCash(line.getCtx(), line.getC_Cash_ID(), line.get_TrxName());
			if (m_CashType.equals(DocLine_Cash.CASHTYPE_CHARGE) && 
					baseAmt !=Env.ZERO && 
						(cash.getDocStatus().equals(X_C_Cash.DOCACTION_Complete)||cash.getDocStatus().equals(X_C_Cash.DOCACTION_Close)))
			{
				MTax tax = new MTax(line.getCtx(), line.get_ValueAsInt("C_Tax_ID"), null);
				MCharge charge = new MCharge(line.getCtx(), line.getC_Charge_ID(), null);
				BigDecimal taxAmt =tax.calculateTax(baseAmt,charge.isTaxIncluded() ,2); 
				m_BaseAmount =baseAmt.add(
						line.getAmount().subtract(baseAmt).subtract(taxAmt)
						) ;
			}
			else
				m_BaseAmount = line.getAmount();
		}
		else
			m_BaseAmount = line.getAmount();
		/**
		 * End Carlos Parada
		 */
		//
		m_Amount = line.getAmount();
		m_DiscountAmt = line.getDiscountAmt();
		m_WriteOffAmt = line.getWriteOffAmt();
		setAmount(m_Amount);


	}   //  DocLine_Cash

	/** Cash Type               */
	private String  m_CashType = "";

	//  AD_Reference_ID=217
	/** Charge - C		*/
	public static final String  CASHTYPE_CHARGE = "C";
	/** Difference - D	*/
	public static final String  CASHTYPE_DIFFERENCE = "D";
	/** Expense - E		*/
	public static final String  CASHTYPE_EXPENSE = "E";
	/** Onvoice - I 	*/
	public static final String  CASHTYPE_INVOICE = "I";
	/** Receipt - R		*/
	public static final String  CASHTYPE_RECEIPT = "R";
	/** Transfer - T	*/
	public static final String  CASHTYPE_TRANSFER = "T";

	//  References
	private int     m_C_BankAccount_ID = 0;
	private int     m_C_Invoice_ID = 0;
	private int     m_C_Payment_ID = 0;

	//  Amounts
	private BigDecimal      m_Amount = Env.ZERO;
	private BigDecimal      m_DiscountAmt = Env.ZERO;
	private BigDecimal      m_WriteOffAmt = Env.ZERO;
	
	/**
	 * @author <a href="mailto:carlosaparadam@gmail.com">Carlos Parada</a> Jun 21, 2013, 11:53:01 AM
	 * Base Amt
	 */
	private BigDecimal		m_BaseAmount = Env.ZERO;
	
	/**
	 *  Get Cash Type
	 *  @return cash type
	 */
	public String getCashType()
	{
		return m_CashType;
	}   //  getCashType

	/**
	 *  Get Bank Account
	 *  @return Bank Account
	 */
	public int getC_BankAccount_ID()
	{
		return m_C_BankAccount_ID;
	}   //  getC_BankAccount_ID

	/**
	 *  Get Invoice
	 *  @return C_Invoice_ID
	 */
	public int getC_Invoice_ID()
	{
		return m_C_Invoice_ID;
	}   //  getC_Invoice_ID

	/**
	 *  Get Amount
	 *  @return Payment Amount
	 */
	public BigDecimal getAmount()
	{
		return m_Amount;
	}
	
	/**
	 *  Get Discount
	 *  @return Discount Amount
	 */
	public BigDecimal getDiscountAmt()
	{
		return m_DiscountAmt;
	}
	/**
	 *  Get WriteOff
	 *  @return Write-Off Amount
	 */
	public BigDecimal getWriteOffAmt()
	{
		return m_WriteOffAmt;
	}

	/**
	 * 	
	 * @author <a href="mailto:carlosaparadam@gmail.com">Carlos Parada</a> Jun 21, 2013, 11:53:01 AM
	 * @return
	 * @return BigDecimal
	 * Return Base Amt
	 */
	public BigDecimal getBaseAmount()
	{
		return m_BaseAmount;
	}
}   //  DocLine_Cash
