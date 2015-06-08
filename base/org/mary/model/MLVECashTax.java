/******************************************************************************
 * Product: Compiere ERP & CRM Smart Business Solution                        *
 * Copyright (C) 1999-2007 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 3600 Bridge Parkway #102, Redwood City, CA 94065, USA      *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.mary.model;

import java.math.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.*;

import org.compiere.model.MCash;
import org.compiere.model.MCashLine;
import org.compiere.model.MCharge;
import org.compiere.model.MTax;
import org.compiere.util.*;

/**
 *	Invoice Tax Model
 *	
 *  @author Jorg Janke
 *  @version $Id: MInvoiceTax.java,v 1.5 2006/10/06 00:42:24 jjanke Exp $
 */
public class MLVECashTax extends X_LVE_CashTax
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** Cash Taxes */
	public static MLVECashTax[] m_taxes;

	/**
	 * 	Get Tax Line for Invoice Line
	 *	@param line invoice line
	 *	@param precision currency precision
	 *	@param oldTax if true old tax is returned
	 *	@param trx transaction name
	 *	@return existing or new tax
	 */
	public static MLVECashTax get (MCashLine line, int precision, 
		boolean oldTax, String trx)
	{
		MLVECashTax retValue = null;
		if (line == null || line.getC_Cash_ID() == 0 )
			return null;
		int C_Tax_ID = line.get_ValueAsInt("C_Tax_ID");
		if (oldTax && line.is_ValueChanged("C_Tax_ID"))
		{
			Object old = line.get_ValueOld("C_Tax_ID");
			if (old == null)
				return null;
			C_Tax_ID = ((Integer)old).intValue();
		}
		if (C_Tax_ID == 0)
		{
			s_log.warning("C_Tax_ID=0");
			return null;
		}
		
		String sql = "SELECT * FROM LVE_CashTax WHERE C_Cash_ID=? AND C_Tax_ID=?";
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement (sql, trx);
			pstmt.setInt (1, line.getC_Cash_ID());
			pstmt.setInt (2, C_Tax_ID);
			ResultSet rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				retValue = new MLVECashTax (line.getCtx(), rs, trx);
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			s_log.log(Level.SEVERE, sql, e); 
		}
		try
		{
			if (pstmt != null)
				pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			pstmt = null;
		}
		if (retValue != null)
		{
			retValue.set_TrxName(trx);
			retValue.setPrecision(precision);
			s_log.fine("(old=" + oldTax + ") " + retValue);
			return retValue;
		}
		
		//	Create New
		retValue = new MLVECashTax(line.getCtx(), 0, trx);
		retValue.set_TrxName(trx);
		retValue.setClientOrg(line);
		retValue.setC_Cash_ID(line.getC_Cash_ID());
		retValue.setC_Tax_ID(line.get_ValueAsInt("C_Tax_ID"));
		retValue.setPrecision(precision);
		retValue.setIsTaxIncluded(isTaxIncluded(line));
		s_log.fine("(new) " + retValue);
		return retValue;
	}	//	get
	
	/**	Static Logger	*/
	private static CLogger	s_log	= CLogger.getCLogger (MLVECashTax.class);
	
	
	/**************************************************************************
	 * 	Persistency Constructor
	 *	@param ctx context
	 *	@param ignored ignored
	 *	@param trx transaction
	 */
	public MLVECashTax (Properties ctx, int ignored, String trx)
	{
		super(ctx, 0, trx);
		if (ignored != 0)
			throw new IllegalArgumentException("Multi-Key");
		setTaxAmt (Env.ZERO);
		setTaxBaseAmt (Env.ZERO);
		setIsTaxIncluded(false);
	}	//	MInvoiceTax

	/**
	 * 	Load Constructor.
	 * 	Set Precision and TaxIncluded for tax calculations!
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trx transaction
	 */
	public MLVECashTax (Properties ctx, ResultSet rs, String trx)
	{
		super(ctx, rs, trx);
	}	//	MInvoiceTax
	
	/** Tax							*/
	private MTax 		m_tax = null;
	/** Cached Precision			*/
	public static Integer		m_precision = null;
	
	
	/**
	 * 	Get Precision
	 * 	@return Returns the precision or 2
	 */
	public static int getPrecision ()
	{
		if (m_precision == null)
			return 2;
		return m_precision.intValue();
	}	//	getPrecision

	/**
	 * 	Set Precision
	 *	@param precision The precision to set.
	 */
	public void setPrecision (int precision)
	{
		m_precision = Integer.valueOf(precision);
	}	//	setPrecision

	/**
	 * 	Get Tax
	 *	@return tax
	 */
	public MTax getTax()
	{
		if (m_tax == null)
			m_tax = MTax.get(getCtx(), getC_Tax_ID());
		return m_tax;
	}	//	getTax
	
	
	/**************************************************************************
	 * 	Calculate/Set Tax Base Amt from Invoice Lines
	 * 	@return true if tax calculated
	 */
	public boolean calculateTaxFromLines ()
	{
		BigDecimal taxBaseAmt = Env.ZERO;
		BigDecimal taxAmt = Env.ZERO;
		//
		boolean documentLevel = getTax().isDocumentLevel();
		MTax tax = getTax();
		//
		String sql = "SELECT COALESCE(cl.XXMontoBase,cl.Amount), cl.Amount-COALESCE(cl.XXMontoBase,0),cl.XXAfectaLibro "
			+ " FROM C_CashLine cl "
			+ " INNER JOIN C_Cash c ON (cl.C_Cash_ID=c.C_Cash_ID) "
			+ " WHERE cl.C_Cash_ID=? AND cl.C_Tax_ID=?";
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement (sql, get_TrxName());
			pstmt.setInt (1, getC_Cash_ID());
			pstmt.setInt (2, getC_Tax_ID());
			ResultSet rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				//	BaseAmt
				BigDecimal baseAmt = rs.getBigDecimal(1);
				taxBaseAmt = taxBaseAmt.add(baseAmt);
				//	TaxAmt
				BigDecimal amt = rs.getBigDecimal(2);
				if (amt == null)
					amt = Env.ZERO;
				//boolean isSOTrx = "Y".equals(rs.getString(3));
				//
				if (documentLevel || baseAmt.signum() == 0)
					amt = Env.ZERO;
				/*else if (amt.signum() != 0 )	//	manually entered
					;*/
				else	// calculate line tax
					amt = tax.calculateTax(baseAmt, isTaxIncluded(), getPrecision());
				//
				taxAmt = taxAmt.add(amt);
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "setTaxBaseAmt", e);
			taxBaseAmt = null;
		}
		try
		{
			if (pstmt != null)
				pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			pstmt = null;
		}
		if (taxBaseAmt == null)
			return false;
		
		//	Calculate Tax
		if (documentLevel || taxAmt.signum() == 0)
			taxAmt = tax.calculateTax(taxBaseAmt, isTaxIncluded(), getPrecision());
		setTaxAmt(taxAmt);

		//	Set Base
		if (isTaxIncluded())
			setTaxBaseAmt (taxBaseAmt.subtract(taxAmt));
		else
			setTaxBaseAmt (taxBaseAmt);
		return true;
	}	//	calculateTaxFromLines

	/**
	 * 	String Representation
	 *	@return info
	 */
	@Override
	public String toString ()
	{
		StringBuffer sb = new StringBuffer ("MLVECashTax[");
		sb.append("C_Cash=").append(getC_Cash_ID())
			.append(",C_Tax_ID=").append(getC_Tax_ID())
			.append(", Base=").append(getTaxBaseAmt()).append(",Tax=").append(getTaxAmt())
			.append ("]");
		return sb.toString ();
	}	//	toString
	
	/**
	 * @author <a href="mailto:carlosaparadam@gmail.com">Carlos Parada</a> Jun 17, 2013, 10:45:20 AM
	 * @param line
	 * @return
	 * @return boolean
	 * Is Tax Included in Amount
	 */
	public static boolean isTaxIncluded(MCashLine line)
	{
		MCharge charge = new MCharge(line.getCtx(), line.getC_Charge_ID(), null);
		return charge.isTaxIncluded();
	}
	
	
	/**
	 * Get Taxes
	 * 
	 * @param requery
	 *            requery
	 * @return array of taxes
	 */
	public static MLVECashTax[] getTaxes(boolean requery,MCash cash) {
		if (m_taxes != null && !requery)
			return m_taxes;
		String sql = "SELECT * FROM LVE_CashTax WHERE C_Cash_ID=?";
		ArrayList<MLVECashTax> list = new ArrayList<MLVECashTax>();
		PreparedStatement pstmt = null;
		try {
			pstmt = DB.prepareStatement(sql, cash.get_TrxName());
			pstmt.setInt(1, cash.getC_Cash_ID());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MLVECashTax(cash.getCtx(), rs, cash.get_TrxName()));
			rs.close();
			pstmt.close();
			pstmt = null;
		} catch (Exception e) {
			s_log.log(Level.SEVERE, "getTaxes", e);
		}
		try {
			if (pstmt != null)
				pstmt.close();
			pstmt = null;
		} catch (Exception e) {
			pstmt = null;
		}

		m_taxes = new MLVECashTax[list.size()];
		list.toArray(m_taxes);
		return m_taxes;
	} // getTaxes

	/**
	 * Calculate Tax and Total
	 * 
	 * @return true if calculated
	 */
	public static boolean calculateTaxTotal(MCash cash) {
		s_log.fine("");
		// Delete Taxes
		DB.executeUpdate("DELETE FROM LVE_CashTax WHERE C_Cash_ID="
				+ cash.getC_Cash_ID(), cash.get_TrxName());
		m_taxes = null;

		// Lines
		//BigDecimal totalLines = Env.ZERO;
		ArrayList<Integer> taxList = new ArrayList<Integer>();
		MCashLine[] lines = cash.getLines(false);
		for (MCashLine line : lines) {

			Integer taxID = Integer.valueOf(line.get_ValueAsInt("C_Tax_ID"));
			if (!taxList.contains(taxID)) {
				MLVECashTax iTax = MLVECashTax.get(line, getPrecision(), false,
						cash.get_TrxName()); // current Tax
				if (iTax != null) {
					
					iTax.setIsTaxIncluded(isTaxIncluded(line));
					if (!iTax.calculateTaxFromLines())
						return false;
					if (!iTax.save())
						return false;
					taxList.add(taxID);
				}
			}
			//totalLines = totalLines.add(line.getLineNetAmt());
		}

		// Taxes
		//BigDecimal grandTotal = totalLines;
		MLVECashTax[] taxes = getTaxes(true,cash);
		for (MLVECashTax iTax : taxes) {
			MTax tax = iTax.getTax();
			if (tax.isSummary()) {
				MTax[] cTaxes = tax.getChildTaxes(false); // Multiple taxes
				for (MTax cTax : cTaxes) {
					BigDecimal taxAmt = cTax.calculateTax(iTax.getTaxBaseAmt(),
							iTax.isTaxIncluded(), getPrecision());
					//
					MLVECashTax newITax = new MLVECashTax(cash.getCtx(), 0,
							cash.get_TrxName());
					newITax.setClientOrg(cash);
					newITax.setC_Cash_ID(cash.getC_Cash_ID());
					newITax.setC_Tax_ID(cTax.getC_Tax_ID());
					newITax.setPrecision(getPrecision());
					newITax.setIsTaxIncluded(iTax.isTaxIncluded());
					newITax.setTaxBaseAmt(iTax.getTaxBaseAmt());
					newITax.setTaxAmt(taxAmt);
					if (!newITax.save(cash.get_TrxName()))
						return false;
					//
					/*if (!isTaxIncluded())
						grandTotal = grandTotal.add(taxAmt);
					*/
				}
				if (!iTax.delete(true, cash.get_TrxName()))
					return false;
			} /*else {
				if (!isTaxIncluded())
					grandTotal = grandTotal.add(iTax.getTaxAmt());
			}*/
		}
		//
		/*
		setTotalLines(totalLines);
		setGrandTotal(grandTotal);
		*/
		return true;
	} // calculateTaxTotal

}	//	MLVECashTax