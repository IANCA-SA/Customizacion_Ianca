package org.mary.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

public class XXCopyDiscountValues extends SvrProcess{

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub

		ProcessInfoParameter[] para = getParameter();

		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("M_PriceList_Version_ID"))
				m_PriceList_Version_ID = para[i].getParameterAsInt();
			else if (name.equals("XX_DiscountPercentage"))
				m_XX_DiscountPercentage = (BigDecimal)para[i].getParameter();
			else
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
			
			m_Product_ID = getRecord_ID();
		}
	}

	@Override
	protected String doIt() throws Exception {
		// TODO Auto-generated method stub
		
		String resp = updateDiscount();

		this.addLog("@Updated@=" + resp);
		return resp;
	}
	
	private String updateDiscount() 
	{
		String result = new String();
		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		
		try {
			if (m_PriceList_Version_ID!=-1)
				sql.append("Update M_ProductPrice Set XX_DiscountPercentage=? where M_PriceList_Version_ID = ? And M_Product_ID = ?");
			else
				sql.append("Update M_ProductPrice Set XX_DiscountPercentage=? where M_Product_ID = ? And M_PriceList_Version_ID In ( Select M_PriceList_Version_ID From M_PriceList_Version Where " +  
							"Exists( Select 1 From (Select Max(PV.ValidFrom) as ValidFrom,PV.M_PriceList_ID From M_PriceList_Version PV Inner Join M_PriceList PL On PV.M_PriceList_ID=PL.M_PriceList_ID where PV.ValidFrom<=now() And PV.IsActive='Y' And PL.IsSoPricelist='Y' Group By PV.M_PriceList_ID) as Current " + 
							"Where M_PriceList_Version.M_PriceList_ID=Current.M_PriceList_ID And M_PriceList_Version.ValidFrom=Current.ValidFrom) " + 
							") ");
			
				/*sql.append("Update M_ProductPrice Set XX_DiscountPercentage=? where M_Product_ID = ? And Exists( "+ 
					"Select 1 From " +
					"(Select Max(PV.ValidFrom) as ValidFrom,PV.M_PriceList_ID From M_PriceList_Version PV Inner Join M_PriceList PL On PV.M_PriceList_ID=PL.M_PriceList_ID where PV.ValidFrom<=now() And PV.IsActive='Y' And PL.IsSoPricelist='Y' Group By PV.M_PriceList_ID) as Current " +
					"Where M_PriceList_Version.M_PriceList_ID=Current.M_PriceList_ID And M_PriceList_Version.ValidFrom=Current.ValidFrom) ");
			*/
			
					
			ps = DB.prepareStatement(sql.toString(),null);
			ps.setBigDecimal(1, m_XX_DiscountPercentage);			
			ps.setInt(2, m_Product_ID);
			if (m_PriceList_Version_ID!=-1)
				ps.setInt(3, m_PriceList_Version_ID);
			
			
			ps.executeUpdate();
			result = ((Integer)ps.getUpdateCount()).toString();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			result= e.getMessage();
		}
		return result;
		
	}
	int m_Product_ID;
	int m_PriceList_Version_ID=-1;
	BigDecimal m_XX_DiscountPercentage;
	

}
