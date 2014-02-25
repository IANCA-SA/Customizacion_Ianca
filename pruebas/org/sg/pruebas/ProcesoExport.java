/**
 * @finalidad 
 * @author Yamel Senih
 * @date 25/03/2011
 */
package org.sg.pruebas;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.compiere.util.DB;

/**
 * @author Yamel Senih
 *
 */
public class ProcesoExport {
	public void export(){
		PreparedStatement pstmt = null;
		String sql = new String("SELECT lfa.SortNo, fa.AD_Table_ID vista_padre, tb.TableName nombre_tabla_padre, lfa.TipoCampo tipo_padre, "
				+ "lfa.prefijo prefijo_padre, lfa.AD_Column_ID campo_padre, co.ColumnName nombre_campo_padre, lfa.sufijo sufijo_padre, "
				+ "fah.AD_Table_ID vista_hija, lfah.TipoCampo tipo_hijo, lfah.prefijo prefijo_hijo, lfah.AD_Column_ID campo_hijo," 
				+ "coh.ColumnName nombre_campo_hijo, lfah.sufijo sufijo_hijo"
				+ "FROM XX_FormatoArchivo fa "
				+ "LEFT JOIN XX_LineaFormatoArchivo lfa ON(lfa.XX_FormatoArchivo_ID = fa.XX_FormatoArchivo_ID)"
				+ "LEFT JOIN XX_FormatoArchivo fah ON(fah.XX_FormatoArchivo_ID = lfa.XX_FormatoHijo_ID)"
				+ "LEFT JOIN XX_LineaFormatoArchivo lfah ON(lfah.XX_FormatoArchivo_ID = fah.XX_FormatoArchivo_ID)"
				+ "INNER JOIN C_BankAccountDoc cb ON(cb.XX_FormatoArchivo_ID=fa.XX_FormatoArchivo_ID)"
				+ "INNER JOIN AD_Table tb ON(tb.AD_Table_ID = fa.AD_Table_Id)"
				+ "LEFT JOIN AD_Table tbh ON(tbh.AD_Table_ID = fah.AD_Table_Id)"
				+ "LEFT JOIN AD_Column co ON(co.AD_Column_ID = lfa.AD_Column_ID)"
				+ "LEFT JOIN AD_Column coh ON(coh.AD_Column_ID = lfah.AD_Column_ID)"
				+ "WHERE cb.C_BankAccount_ID = ? AND cb.IsActive = 'Y' AND PaymentRule = 'T' AND cb.AD_Client_ID = ?"
				+ "ORDER BY lfa.SeqNo ASC");
		pstmt = DB.prepareStatement(sql, null);
		try {
			//pstmt.setInt(1, p_C_BankAccount_ID);
			ResultSet res = pstmt.executeQuery();
			if(res != null){
				if(res.next()){
					String nombreTabla = res.getString("nombre_tabla_padre");
					StringBuffer sqlVista = new StringBuffer("SELECT * FROM ");
					sqlVista.append(nombreTabla);
					sqlVista.append(" WHERE C_PaySelection_ID = ?");
					PreparedStatement pstmtVista = null;
					pstmtVista = DB.prepareStatement(sql, null);
					//pstmtVista.setInt(1, p_C_PaySelection_ID);
					if(pstmtVista != null){
						ResultSet resEnc = pstmtVista.executeQuery();
						while(resEnc.next()){
							if(res.getString("tipo_padre").equals("C")){
								System.out.print(resEnc.getString(res.getString("nombre_campo_padre")));
							} else if(res.getString("tipo_padre").equals("T")){
								System.out.print(resEnc.getString(res.getString("prefijo_padre")));
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
