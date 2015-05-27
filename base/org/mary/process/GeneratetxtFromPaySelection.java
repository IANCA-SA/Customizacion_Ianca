package org.mary.process;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import javax.swing.JFileChooser;

import org.compiere.apps.ADialog;
import org.compiere.model.MPaySelection;
import org.compiere.model.MUser;
import org.compiere.process.SvrProcess;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

public class GeneratetxtFromPaySelection  extends SvrProcess{

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		M_Payselection_ID= getRecord_ID();
		paysel = new MPaySelection(getCtx(), M_Payselection_ID, null);
		C_BankAccount_ID=paysel.getC_BankAccount_ID();
			
	}

	@Override
	protected String doIt() throws Exception {
		// TODO Auto-generated method stub
		
		if (ADialog.ask(0, null, Msg.translate(getCtx(), "GeneratePaymentTXT?")))
		{
			//	Yamel Senih 27/04/2011, 16:31
			//	Consulta de la ruta destino del Archivo de Transferencia
			//	Usuario
			int m_AD_User_ID = Env.getAD_User_ID(getCtx());
			
			String ruta = null;
			String ext = null;
			
			MUser usuario = new MUser(Env.getCtx(), m_AD_User_ID, null);
			/*	
			 * Se obtiene la ruta y la extensión del archivo ACH
			 * */
			
			ruta = usuario.get_ValueAsString("XXRutaArcTransf");
			ext = "txt"; //usuario.get_ValueAsString("TXT");
			
			ruta = (ruta == null? "test": ruta + File.separator);
			ext = (ext == null? "txt": ext + File.separator);
			
			//  Get File Info
			String fecha = new SimpleDateFormat("ddMMyyyy").format(new Date());
			String nombArchivo = fecha + "_" + M_Payselection_ID;
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle(Msg.getMsg(Env.getCtx(), "Export"));
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setMultiSelectionEnabled(false);
			
			fc.setSelectedFile(new java.io.File(ruta + nombArchivo + "." + ext));
			if (fc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
				return "";
		    
			exportToFile (C_BankAccount_ID,M_Payselection_ID , fc.getSelectedFile());
		}
		return "";
	}
	
	/**
	 * Este Método fue Sobrecargado con la finalidad de generar las trasferencias bancarias a partir de una vista y 
	 * así poder darle funcionalidad en Venezuela, por razones de desconocimiento, se le agregaron dos parametros mas
	 * que son: La cuenta Bancaria y el ID de Selección de pago
	 * @author Yamel Senih 28/03/2011, 17:39:02
	 * @author Yamel Senih 12/10/2011, 17:39
	 * @param checks
	 * @param p_C_BankAccount_ID
	 * @param p_C_PaySelection_ID
	 * @param file
	 * @return
	 * @return int
	 */
	public static int exportToFile (int p_C_BankAccount_ID, int p_C_PaySelection_ID, File file)
	{	
		int noLines = 0;
		//  Must be a file
		if (file.isDirectory())
		{
			log.log(Level.WARNING, "File is directory - " + file.getAbsolutePath());
			return 0;
		}
		//  delete if exists
		try
		{
			if (file.exists())
				file.delete();
		}
		catch (Exception e)
		{
			log.log(Level.WARNING, "Could not delete - " + file.getAbsolutePath(), e);
		}
		
		StringBuffer flujoEscritor = new StringBuffer();
		/*
		 * Modificado por Yamel Senih 12/11/2011, 15:27 
		 * Cambios en la Consulta
		 * */
		
		PreparedStatement pstmt = null;
		String sql = new String("SELECT cb.AD_Table_ID, tb.TableName tabla FROM C_BankAccountDoc cb " +
				"INNER JOIN AD_Table tb ON(tb.AD_Table_ID = cb.AD_Table_Id) " +
				"WHERE cb.C_BankAccount_ID = ? " +
				"AND cb.IsActive = 'Y' " +
				"AND PaymentRule = 'T'");
		pstmt = DB.prepareStatement(sql, null);
		try {
			pstmt.setInt(1, p_C_BankAccount_ID);
			ResultSet res = pstmt.executeQuery();
			if(res != null){
				if(res.next()){
					String nombreTabla = res.getString("tabla");
					pstmt.close();
					res.close();
					StringBuffer sqlVista = new StringBuffer("SELECT * FROM ");
					sqlVista.append(nombreTabla);
					sqlVista.append(" WHERE C_PaySelection_ID = ?");
					PreparedStatement pstmtVista = null;
					pstmtVista = DB.prepareStatement(sqlVista.toString(), null);
					pstmtVista.setInt(1, p_C_PaySelection_ID);
					ResultSet resEnc = pstmtVista.executeQuery();
					while(resEnc.next()){
						flujoEscritor.append(resEnc.getString(1));
						flujoEscritor.append(Env.NL);
						noLines++;
					}
					pstmtVista.close();
					resEnc.close();
				}
			} else{
				return 0;
			}
			if(noLines > 0){
				FileWriter escritorArchivo = new FileWriter(file);
				escritorArchivo.write(flujoEscritor.toString());
				escritorArchivo.flush();
				escritorArchivo.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return noLines;
	}   //  exportToFile

	private static CLogger log = CLogger.getCLogger(GeneratetxtFromPaySelection.class);
	private int M_Payselection_ID=0;
	private int C_BankAccount_ID=0;
	private MPaySelection paysel ; 
}
