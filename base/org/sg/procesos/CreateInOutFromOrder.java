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
package org.sg.procesos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.*;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.*;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.*;
 
/**
 *  @author Yamel Senih 22/03/2011, 13:55:35
 *  Crea una Recepción de Material a partir de una Orden de Compra
 */
public class CreateInOutFromOrder extends SvrProcess
{
	/** Tipo de Documento de Recepción*/
	private int	p_C_DocTypeShip_ID = 0;
	/** Tipo e Documento de la Orden de Compra*/
	private int p_C_DocTypeOrder_ID = 0;
	/** Orden*/
	private int p_C_Order_ID = 0;
	/**	Created	*/
	private int m_created = 0;
	/** Received	*/
	private MInOut recepcion = null;
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	@Override
	protected void prepare()
	{
		for (ProcessInfoParameter para : getParameter()) {
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			else if (name.equals("C_DocTypeOrder_ID"))
				p_C_DocTypeOrder_ID = para.getParameterAsInt();
			else if (name.equals("C_Order_ID"))
				p_C_Order_ID = para.getParameterAsInt();
			else if (name.equals("C_DocTypeShip_ID"))
				p_C_DocTypeShip_ID = para.getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

	}

	
	/**
	 * @author Yamel Senih 23/03/2011, 8:53:35
	 * Crea una recepcion de material con los datos de la orden y 
	 * luego recorre todas las lineas de la orden para crear las lineas de 
	 * la recepcion
	 */
	@Override
	protected String doIt ()  throws Exception {			
		StringBuffer sql = new StringBuffer("SELECT * FROM C_Order o" +
			" WHERE DocStatus = 'CO'");
		if(p_C_DocTypeOrder_ID != 0)
			sql.append(" AND C_DocType_ID = ?");
		if(p_C_Order_ID != 0)
			sql.append(" AND C_Order_ID = ?");
			
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), get_TrxName());
			int index = 1;
			if (p_C_DocTypeOrder_ID != 0)
				pstmt.setInt(index++, p_C_DocTypeOrder_ID);
			if (p_C_Order_ID != 0)
				pstmt.setInt(index++, p_C_Order_ID);
		}catch (Exception e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
			
			
		return generarRecepciones(pstmt);	
			
	}
	
	/**
	 * @author Yamel Senih, 10/05/2011 11:14
	 * Genera las Recepciones de materiales
	 * @param pstmt
	 * @return
	 * @throws SQLException 
	 */
	private String generarRecepciones(PreparedStatement pstmt) throws SQLException{
		ResultSet rs;
		rs = pstmt.executeQuery ();
		while (rs.next ()){
			/*
			 * Crea la Orden a partir del ID
			 */
			MOrder orden = new MOrder (getCtx(), rs, get_TrxName());
			procOrden(orden);
		}	
		
		try{
			if (pstmt != null)
				pstmt.close ();
			pstmt = null;
		}catch (Exception e){
			pstmt = null;
		}
		return "@Created@ = " + m_created;
	}
	
	/**
	 * Este es un proceso tonto, no debo pasar de 40 min haciendolo
	 * Comienzo: 9:07
	 * Final: 9:40
	 */
	
	/**
	 * Crea las Recepciones una a una a partir de una Orden de Compra
	 * @return
	 */
	private void procOrden(MOrder orden){
		/*
		 * Se crean las lineas a partir de las lineas de la orden
		 */
		MOrderLine lineasOrden[] = orden.getLines();
		for(MOrderLine lineaOrden : lineasOrden){
			/*
			 * Crea el encabezado de la Recepcion a partir de la orden
			 */
			recepcion = new MInOut(orden, p_C_DocTypeShip_ID, orden.getDateOrdered());
			
			MBPartner m_bpartner = MBPartner.get(getCtx(), orden.getC_BPartner_ID());
			
			recepcion.setAD_Org_ID(lineaOrden.getAD_Org_ID());
			recepcion.setM_Warehouse_ID(lineaOrden.getM_Warehouse_ID());
			recepcion.setC_Activity_ID(orden.getC_Activity_ID());
			recepcion.setDateReceived(orden.getDateOrdered());
			recepcion.setBPartner(m_bpartner);
			recepcion.setSalesRep_ID(m_bpartner.getSalesRep_ID());		
			recepcion.setDateAcct(orden.getDateAcct());
			recepcion.setIsSOTrx(false);
			/*
			 * Se crea la linea de la recepción a partir de la linea de la Orden
			 */
			if(recepcion.save()){
				MInOutLine lineaRecepcion = new MInOutLine(recepcion);
				lineaRecepcion.setAD_Org_ID(orden.getAD_Org_ID());
				lineaRecepcion.setM_InOut_ID(recepcion.getM_InOut_ID());
				lineaRecepcion.setOrderLine(lineaOrden, 0, Env.ZERO);
				lineaRecepcion.setQtyEntered(lineaOrden.getQtyEntered());
				lineaRecepcion.setMovementQty(lineaOrden.getQtyOrdered());
				lineaRecepcion.setC_Activity_ID(lineaOrden.getC_Activity_ID());
				lineaRecepcion.setQty(lineaOrden.getQtyEntered());
				lineaRecepcion.setQtyEntered(lineaOrden.getQtyEntered());
				lineaRecepcion.setC_UOM_ID(lineaOrden.getC_UOM_ID());
				lineaRecepcion.setAD_Org_ID(lineaOrden.getAD_Org_ID());
				if(lineaRecepcion.save()){
					completeReceived();
				} else {
					throw new AdempiereException("@SaveError@ @M_InOutLine_ID@");
				}
			} else {
				throw new AdempiereException("@SaveError@ @M_InOut_ID@");
			}
		}
		
		orden.setDocStatus(X_C_Order.DOCACTION_Close);
		orden.setDocAction(X_C_Order.DOCACTION_None);
		if(!orden.save()){
			throw new AdempiereException("@SaveError@ @C_Order_ID@");
		}
		
	}
	
	private void completeReceived(){
		/*
		 * Completa la Recepción de Material
		 */
		recepcion.setDocAction(X_M_InOut.DOCACTION_Complete);
		if (recepcion != null){
			recepcion.completeIt();
			recepcion.setDocStatus(X_M_InOut.DOCSTATUS_Completed);
			recepcion.save();
			//
			addLog(recepcion.getM_InOut_ID(), recepcion.getDateAcct(), null, recepcion.getDocumentNo());
			m_created++;
		}
	}
	
}
