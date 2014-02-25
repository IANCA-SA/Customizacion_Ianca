package org.mary.process;

import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

/**
 *  @author Dixon Martinez 
 *  Cambia el Estado del Documento y la Accion del Documento
 *  indicado.
 *
 */
public class ChangeDocStatus extends SvrProcess {

	@Override
	protected void prepare() {
		for(ProcessInfoParameter parameter :getParameter()){
			String name = parameter.getParameterName();
			if(parameter.getParameter() == null){
				;
			}else if(name.equals("AD_Table_ID"))
				p_AD_Table_ID = parameter.getParameterAsInt();
			else if(name.equals("Record_ID"))
				p_Record_ID = parameter.getParameterAsInt();			
		}
	}

	@Override
	protected String doIt() throws Exception {
		PO po;
		MTable table = MTable.get (Env.getCtx(), p_AD_Table_ID);
		po=table.getPO(p_Record_ID, get_TrxName());
		po.set_CustomColumn("DocStatus", "CO");
		po.set_CustomColumn("DocAction", "CL");
		po.save();		
		return "@Updated@ "+ po.get_ID();
	}
	
	int p_AD_Table_ID;
	int p_Record_ID;
	

}
