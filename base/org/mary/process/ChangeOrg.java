package org.mary.process;

import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
/**
 * 
 * @author Carlos Parada
 * Cambia la Organización de un Registro especifico de la tabla seleccionada
 *
 */
public class ChangeOrg extends SvrProcess{

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		for(ProcessInfoParameter parameter :getParameter()){
			String name = parameter.getParameterName();
			if(parameter.getParameter() == null){
				;
			}else if (name.equals("AD_Org_ID"))
				p_AD_Org_ID = parameter.getParameterAsInt();				
			else if(name.equals("AD_Table_ID"))
				p_AD_Table_ID = parameter.getParameterAsInt();
			else if(name.equals("Record_ID"))
				p_Record_ID = parameter.getParameterAsInt();			
		}
	}

	@Override
	protected String doIt() throws Exception {
		// TODO Auto-generated method stub
		PO po;
		MTable table = MTable.get (Env.getCtx(), p_AD_Table_ID);
		po=table.getPO(p_Record_ID, get_TrxName());
		
		po.setAD_Org_ID(p_AD_Org_ID);
		po.save();
		
		return "@Updated@ "+ po.get_ID();
	}

	int p_Record_ID;
	int p_AD_Org_ID;
	int p_AD_Table_ID;
}
