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
package org.sg.modelos;

import java.util.Properties;

import org.compiere.apps.ADialog;
import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.util.Msg;

/**
 *	CalloutBPartner 
 *
 *  @author carlos Parada
 *  @version  1.0
 */
public class CalloutBPartner extends CalloutEngine
{

	public String validateSeniat(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		String XX_ValidateSeniat = (String)value;
		String m_Value =mTab.get_ValueAsString("Value");
			//Env.getContext(ctx, WindowNo, Env.TAB_INFO, "Value"); 
		if (XX_ValidateSeniat.equals("") || XX_ValidateSeniat == null)
			return "";
		if (m_Value.equals("") || m_Value==null)
		{
			return "";
		}	
			
		XXValidateRifSeniat l_ValidateSeniat = new XXValidateRifSeniat(m_Value);
		if(l_ValidateSeniat.isM_exists())
		{
			ADialog.info(WindowNo, null, Msg.translate(ctx, "Value")+": "+l_ValidateSeniat.getM_Rif()+"\n"+
										 Msg.translate(ctx, "Name")+": "+l_ValidateSeniat.getM_Name()+"\n"+
										 "Agente Retencion de IVA: "+l_ValidateSeniat.getM_Agent()+"\n"+
										 "Contribuyente IVA: "+l_ValidateSeniat.getM_Contributor()+"\n"
			);
			
			mTab.setValue("Name", l_ValidateSeniat.getM_Name());
		}
		else
			ADialog.error(WindowNo, null, l_ValidateSeniat.getM_error());
			
		return "";
	}
}	//	CalloutBPartner
