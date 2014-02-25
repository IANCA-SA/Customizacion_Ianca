/**
 * @author Carlos Parada
 * Clase Para generar reporte de Recibo de cobro por socio de negocio
 * @Date 05/05/2012
 */
package org.sg.report;

import java.util.logging.Level;
import org.compiere.process.SvrProcess;

/**
 * @author Carlos Parada
 *
 */
public class XX_ReceiptPayment extends SvrProcess{
	/* 
	 * @author Carlos Parada
	 * @Date 23/03/2012 
	 * @Return void 
	 * Carga las variables del reporte dependiendo de lo que viene por parametro
	 */
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub	
		m_receiptpayment = new MXX_ReceiptPayment(getAD_PInstance_ID(),getRecord_ID());
	}

	/* 
	 * @author Carlos Parada
	 * @Date 23/03/2012 
	 * @Return void 
	 * Busca los Datos del Recibo de Cobro con los parametros enviados, si no devuelve ningun dato envia un msg al log
	 */
	@Override
	protected String doIt() throws Exception {
		// TODO Auto-generated method stub
		if (!m_receiptpayment.LoadDataReport())
			log.log(Level.SEVERE, "No hay datos con los parametros indicados");
		return null;
	}
	
	
	private MXX_ReceiptPayment m_receiptpayment; 
	

}
