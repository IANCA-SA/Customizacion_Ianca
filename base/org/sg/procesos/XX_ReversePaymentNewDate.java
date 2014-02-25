/**
 * @author info-analista1
 * @Date 26/03/2012
 */
package org.sg.procesos;

import java.sql.Timestamp;

import org.compiere.apps.ADialog;
import org.compiere.model.MPayment;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Trx;

/**
 * @author info-analista1
 *
 */
public class XX_ReversePaymentNewDate extends SvrProcess{

	/* (non-Javadoc)
	 * @see org.compiere.process.SvrProcess#prepare()
	 */
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		for(ProcessInfoParameter par:getParameter())
			if (par.getParameterName().toLowerCase().equals("newdate"))
				m_NewDate=(Timestamp)par.getParameter();
	}

	/**@Autor Carlos Parada
	 * @date 26/03/2012
	 * Reversa Documento de Pago y crea nuevo documento con fecha seleccionada
	 * @see org.compiere.process.SvrProcess#doIt()
	 */
	@Override
	protected String doIt() throws Exception {
		// TODO Auto-generated method stub
		String sucess ="OK";
		String trxName = Trx.createTrxName("XX_ReversePaymentNewDate");	
		Trx trx = Trx.get(trxName, true);
		MPayment payment = new MPayment(Env.getCtx(), getRecord_ID(), trxName);
		if (payment.getDocStatus().equals("CO"))
		{
			if (ADialog.ask(0, null, "Transacción Inversa (corrección) invirtiendo el signo con la fecha "+m_NewDate+"¿Desea Continuar?"))
			{
				if(!payment.reverseItNewDate(m_NewDate))
				{
					sucess="Error reversando";
					trx.rollback();
					ADialog.error(0, null,sucess);
				}
				else
				{
					payment.save();
					trx.commit();
				}
			}
		}
		else 
			sucess = "El Documento Debe estar Completado";
		return sucess;
	}
	
	Timestamp m_NewDate;
}
