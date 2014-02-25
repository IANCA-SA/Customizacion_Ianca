/**
 * 
 */
package org.sg.procesos;

import java.math.BigDecimal;
import java.util.logging.Level;

import org.compiere.apps.ADialog;
import org.compiere.model.MBPartner;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

/**
 * @author info-analista2
 *
 */
public class LoadReverseFromReceived extends SvrProcess {


	/** ID Factura*/
	private int p_C_Invoice_ID = 0;
	
	/** ID Orden*/
	private int p_M_InOut_ID = 0;
	
	/** ID Socio de Negocios*/
	private int p_C_BPartner_ID = 0;
	
	/**	Cargo de Provisión	*/
	private int p_C_Charge_ID_Prov = 0;
	
	/**	Cargo de Recepción no Facturada	*/
	private int p_C_Charge_ID_NoFac = 0;
	
	@Override
	protected void prepare() {
		for (ProcessInfoParameter para : getParameter()) {
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			else if (name.equals("C_BPartner_ID"))
				p_C_BPartner_ID = para.getParameterAsInt();
			else if (name.equals("M_InOut_ID"))
				p_M_InOut_ID = para.getParameterAsInt();
			else if (name.equals("C_Charge_ID_Prov"))
				p_C_Charge_ID_Prov = para.getParameterAsInt();
			else if (name.equals("C_Charge_ID_NoFac"))
				p_C_Charge_ID_NoFac = para.getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}		
		p_C_Invoice_ID = getRecord_ID();
	}

	/* 
	 * doIt()
	 */
	@Override
	protected String doIt() throws Exception {
		
		MInvoice factura = new MInvoice(getCtx(), p_C_Invoice_ID, get_TrxName());
		if(factura.getC_BPartner_ID() == p_C_BPartner_ID){
			MInOut recepcion = new MInOut(getCtx(), p_M_InOut_ID, get_TrxName());
			MInOutLine lineasRecepcion[] = recepcion.getLines();
			
			for(MInOutLine lineaRecepcion : lineasRecepcion){
				
				BigDecimal priceActualProd = lineaRecepcion.getC_OrderLine().getPriceActual();
				
				MInvoiceLine lineaFactura = new MInvoiceLine(factura);
				
				lineaFactura.setM_Product_ID(lineaRecepcion.getM_Product_ID());
				lineaFactura.setC_UOM_ID(lineaRecepcion.getC_UOM_ID());
				lineaFactura.setPrice(priceActualProd);
				lineaFactura.setM_InOutLine_ID(lineaRecepcion.getM_InOutLine_ID());
				lineaFactura.setQtyEntered(lineaRecepcion.getQtyEntered());
				lineaFactura.setQtyInvoiced(lineaRecepcion.getMovementQty());
				lineaFactura.setC_Activity_ID(lineaRecepcion.getC_Activity_ID());
				lineaFactura.save();
				
				//Linea de Reverso con Cargo de Provisión
				MInvoiceLine lineaRevProv = new MInvoiceLine(factura);			
				lineaRevProv = new MInvoiceLine(factura);
				lineaRevProv.setC_UOM_ID(100);
				lineaRevProv.setQtyEntered(Env.ONE);
				lineaRevProv.setQtyInvoiced(Env.ONE);
				lineaRevProv.setPriceEntered(lineaFactura.getLineTotalAmt().negate());
				lineaRevProv.setPriceActual(lineaFactura.getLineTotalAmt().negate());
				lineaRevProv.setPriceLimit(lineaFactura.getPriceLimit());
				lineaRevProv.setPriceList(lineaFactura.getPriceList());
				lineaRevProv.setC_Charge_ID(p_C_Charge_ID_Prov);
				lineaRevProv.setC_Activity_ID(lineaRecepcion.getC_Activity_ID());
				lineaRevProv.save();
				//Linea de Reverso con Cargo de Provisión
				
				
				//Linea de Reverso con Cargo de Compra
				MInvoiceLine lineaReverso = new MInvoiceLine(factura);			
				lineaReverso = new MInvoiceLine(factura);
				lineaReverso.setC_UOM_ID(100);
				lineaReverso.setQtyEntered(Env.ONE);
				lineaReverso.setQtyInvoiced(Env.ONE);
				lineaReverso.setPriceEntered(lineaFactura.getLineTotalAmt());
				lineaReverso.setPriceActual(lineaFactura.getLineTotalAmt());
				lineaReverso.setPriceLimit(lineaFactura.getPriceLimit());
				lineaReverso.setPriceList(lineaFactura.getPriceList());
				lineaReverso.setC_Charge_ID(p_C_Charge_ID_NoFac);
				lineaReverso.setC_Activity_ID(lineaRecepcion.getC_Activity_ID());
				lineaReverso.save();
				//Linea de Reverso con Cargo de Compra	
				
			}
			factura.save();
			return "";
		} else {
			MBPartner socioParametro = new MBPartner(getCtx(), p_C_BPartner_ID, get_TrxName());
			ADialog.error(0, null,"Error de Parámetros", 
					"El Socio no coincide con el Socio de Negocios de la Factura" +
					"\nSocio Parámetro = " + socioParametro.getName() +
					"\nSocio de Factura = " + factura.getC_BPartner().getName());
			return "El Socio no coincide con el Socio de Negocios de la Factura";
		}
	}

}
