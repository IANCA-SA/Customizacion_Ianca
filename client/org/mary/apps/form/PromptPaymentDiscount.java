package org.mary.apps.form;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.compiere.apps.ADialog;
import org.compiere.apps.form.FormFrame;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPrice;
import org.compiere.model.MSysConfig;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.compiere.util.Trx;

public class PromptPaymentDiscount {

	public PromptPaymentDiscount() {
	
		// TODO Auto-generated constructor stub
		ctx = Env.getCtx();
	}
	
	
	/**
	 * Carga Los Documentos Pedientes con el Descuento de Pronto pago en Vector para Minitable
	 * @author Carlos Parada
	 * @date 10/09/2012
	 * @param C_BPartner
	 * @param PayDate
	 * @return
	 * @throws SQLException 
	 */	
	protected void loadTable(int C_BPartner,Timestamp PayDate,DefaultTableModel model) throws SQLException,NumberFormatException,ArithmeticException
	{
		Vector<Object> line = null;
		StringBuffer sql = new StringBuffer();
		PreparedStatement ps =null;
		ResultSet rs = null;
		
		
		//Elminando Filas
		removeRows(model);
		
		sql.append("Select \n" +
					/*1*/"CI.C_Invoice_ID,\n"+
					/*2*/"CI.DocumentNo,\n"+
					/*3*/"CI.DateInvoiced,\n"+
					/*4*/"CI.C_PaymentTerm_ID,\n"+
					/*5*/"CPT.Name,\n"+
					/*6*/"CPT.NetDays,\n"+
					/*7*/"CPT.GraceDays,\n"+
					/*8*/"PL.M_PriceList_Version_ID,\n"+
					/*9*/"PL.PriceList_Version_Name,\n"+
					/*10*/"PL.ValidFrom,\n"+
					/*11*/"CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) AS DueDate, \n"+
					/*12*/"CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) - '"+PayDate.toString()+"' As PendingDays, \n" +
					/*13*/"Cast(Extract(Day From '"+PayDate.toString()+"' - CI.DateInvoiced ) as Integer) As DaysDue, \n " +
					/*14*/"Round(Sum(" +
							"/* \n " + 
							"Porcentaje de Descuento / Dias de interes mensual \n " + 
						  "*/(\n " + 
						  	  "( \n " + 
						  	   "( \n " + 
						  	   	"(PL.XX_DiscountPercentage/id.InterestDays) \n " + 
						  	   	"/*Porcentaje Diario * dias de Descuento*/ \n " + 
						  	   	" * (CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) - '"+PayDate.toString()+"') \n " + 
							   ") \n " + 
							   "/*Porcentaje de Descuento / 100*/ \n " +     
							   "/100 \n " + 
							  ") \n " + 
							  "/*Porcentaje de Descuento * precio */ \n " +     
							  " * PL.PriceList \n " + 
							 ") \n " + 
							 "* CIL.QtyInvoiced),2) as DiscountInterest \n " + 
					"From C_Invoice CI \n"+
					"Inner Join C_PaymentTerm CPT On CI.C_PaymentTerm_ID=CPT.C_PaymentTerm_ID \n"+
					"Inner Join C_InvoiceLine CIL On CI.C_Invoice_ID = CIL.C_Invoice_ID \n"+
					"Inner Join (Select PL.M_PriceList_ID,PL.Name As PriceList_Name,PLV.Name as PriceList_Version_Name,PLV.M_PriceList_Version_ID,PLV.ValidFrom,PP.M_Product_ID,PP.PriceList,PP.XX_DiscountPercentage From M_PriceList PL Inner Join M_PriceList_Version PLV On PL.M_PriceList_ID=PLV.M_PriceList_ID Inner Join M_ProductPrice PP On PLV.M_PriceList_Version_ID=PP.M_PriceList_Version_ID) \n"+  
					"PL On CI.M_PriceList_ID = PL.M_PriceList_ID And CIL.M_Product_ID=PL.M_Product_ID \n"+
					"Inner Join M_Product MP On CIL.M_Product_ID=MP.M_Product_ID, \n"+
					"(Select Cast(Value as Integer) as InterestDays From AD_SysConfig Where Name ='XXInterestMonth') id \n"+
					"Where \n"+ 
					"invoiceopen(CI.C_Invoice_ID, 0) <> 0 \n"+ 
					"And CI.IsPayScheduleValid <> 'Y' \n"+ 
					"And CI.DocStatus In ('CO', 'CL') \n"+
					"And CI.C_BPartner_ID = "+C_BPartner+  " \n"+
					"And CI.IsSoTrx='Y' \n"+
					"And PL.M_PriceList_Version_ID = XX_ValidPricelist(PL.M_PriceList_ID,CI.DateInvoiced) \n" +
					"And CI.XX_DiscountApplied='N' \n" +
					"And CI.DateInvoiced<='"+PayDate.toString()+ "' \n"+
					"Group By  \n"+
					"CI.C_Invoice_ID, \n"+
					"CI.DocumentNo, \n"+
					"CI.DateInvoiced, \n"+
					"CI.C_PaymentTerm_ID, \n"+
					"CPT.Name, \n"+
					"CPT.NetDays, \n"+
					"CPT.GraceDays, \n"+
					"PL.M_PriceList_Version_ID, \n"+
					"PL.PriceList_Version_Name, \n"+
					"PL.ValidFrom  \n"+
					"Order By CI.C_Invoice_ID");

		ps = DB.prepareStatement(sql.toString(),null);

		rs = ps.executeQuery();

		while (rs.next())
		{
				line = new Vector<Object>();

				line.add(0,new Boolean(false));
				line.add(1,new KeyNamePair(rs.getInt(1), rs.getString(2)));//C_Invoice_ID y DocumentNo
				line.add(2,rs.getTimestamp(3));//Dateinvoiced
				line.add(3,	rs.getTimestamp(11));//DueDate
				line.add(4,new KeyNamePair(rs.getInt(4), rs.getString(5)));//C_PaymentTerm y Name PaymentTerm
				line.add(5,new KeyNamePair(rs.getInt(8), rs.getString(9)));//M_PriceListVersion y Name PriceListVersion
				line.add(6,rs.getInt(12));
				line.add(7,rs.getInt(13));
				
				line.add(8,rs.getBigDecimal(14));
				model.addRow(line);
				
		}

		ps.close();
		rs.close();	
	}
	
	/**
	 * @author Carlos Parada
	 * @param tm
	 */
	public void removeRows(DefaultTableModel tm)
	{
		int len = tm.getRowCount();
		for (int i=len-1;i>=0;i--)
			tm.removeRow(i);
	}
	
	/**
	 * @author carlos Parada
	 * @date 11/09/2012
	 */
	protected void selectAllItems(DefaultTableModel tm)
	{
		for (int i=0;i<tm.getRowCount();i++)
			tm.setValueAt(new Boolean(true), i, 0);
		
	}
	
	/**
	 * Generar Notas de Credito pr Descuento de Pronto Pago
	 * @author carlos Parada
	 * @date 11/09/2012
	 * @param tm
	 */
	@SuppressWarnings({ "static-access", "unused" })
	protected final Vector<String> process(DefaultTableModel tm,Timestamp PayDate,int C_DocType_ID,FormFrame forma,int WindowsNo)
	{
		Vector <String> result=new Vector<String>();
		int idInvoice=0;
		
		BigDecimal discount,discountline;
		MInvoice invoice,invoice_discount;
		MInvoiceLine invoiceline_discount=null;
		MProduct product=null;
		Integer C_Charge_ID=null;
		Trx trx = Trx.get("PromptPaymentDiscount", true);
		int avgDays = Integer.parseInt(MSysConfig.getValue("XXInterestMonth", "")),DiscountDays=0;
		MProductPrice price;
		int M_PriceListVersion;
		BigDecimal DailyAvg = null;
		
		try
		{
			for (int i=0;i<tm.getRowCount();i++)
			{
				//Si esta seleccionado
				if ((Boolean)tm.getValueAt(i, 0))
				{
					idInvoice = ((KeyNamePair)tm.getValueAt(i, 1)).getKey();
					DiscountDays=(Integer)tm.getValueAt(i, 6);
					discount = (BigDecimal)tm.getValueAt(i, 8);
					M_PriceListVersion =((KeyNamePair)tm.getValueAt(i,5)).getKey();
					//si La Factura existe y el Descuento es distinto de cero
					if (idInvoice!=0 && !discount.equals(BigDecimal.ZERO))
					{
						//Creando Encabezado de Documento
						invoice = new MInvoice(ctx, idInvoice, trx.getTrxName());
						invoice_discount =new MInvoice(ctx, 0, trx.getTrxName());
						PO.copyValues(invoice, invoice_discount);
						invoice_discount.setDateInvoiced(PayDate);
						invoice_discount.setDateAcct(PayDate);
						invoice_discount.setC_Order_ID(0);
						invoice_discount.setC_DocType_ID(C_DocType_ID);
						invoice_discount.setC_DocTypeTarget_ID(C_DocType_ID);
						invoice_discount.setDocumentNo(null);
						invoice_discount.setGrandTotal(discount);
						invoice_discount.setDocStatus(invoice.DOCSTATUS_Drafted);
						invoice_discount.setDocAction(invoice.DOCACTION_Complete);
						invoice_discount.setDescription(null);
						invoice_discount.set_ValueOfColumn("XX_C_Invoice_ID", invoice.getC_Invoice_ID());
						invoice_discount.set_ValueOfColumn("XX_NroControl", null);
						invoice_discount.set_ValueOfColumn("XX_Procesa_Retencion", null);
						invoice_discount.set_ValueOfColumn("XX_Conductor_ID", null);
						invoice_discount.set_ValueOfColumn("XX_Vehiculo_ID", null);
						invoice_discount.set_ValueOfColumn("XX_Fecha_Comprobante", null);
						invoice_discount.set_ValueOfColumn("XX_Nro_Comprobante", null);
						invoice_discount.set_ValueOfColumn("XX_Procesado", null);
						invoice_discount.set_ValueOfColumn("XX_Nro_ComprobanteISLR", null);
						invoice_discount.set_ValueOfColumn("XX_ProcesadoISLR", null);
						invoice_discount.set_ValueOfColumn("XX_ProcesadoISLR", null);
						invoice_discount.set_ValueOfColumn("XX_Crear_Lineas_Reverso", null);
						invoice_discount.setProcessed(false);
						invoice_discount.setIsPaid(false);
						invoice.set_ValueOfColumn("XX_DiscountApplied", "Y");
						
						//Creando Linea de Docuemnto
						invoice.saveEx(trx.getTrxName());
						invoice_discount.saveEx(trx.getTrxName());
						
						
						C_Charge_ID=0;
						invoiceline_discount =null;
						for (MInvoiceLine invoiceline:invoice.getLines())
						{
							product = invoiceline.getProduct();
							if (product !=null)
							{
								price = getProductPrice(M_PriceListVersion,product.getM_Product_ID());
								if (product.get_Value("C_Charge_ID")!=null)
								{
									if (((Integer)product.get_Value("C_Charge_ID")).intValue()!=C_Charge_ID)
									{
										if (invoiceline_discount!=null)
										{
											invoiceline_discount.setPrice(invoiceline_discount.getPriceActual().setScale(2, BigDecimal.ROUND_HALF_UP));
											invoiceline_discount.setPriceList(invoiceline_discount.getPriceList().setScale(2, BigDecimal.ROUND_HALF_UP));
											invoiceline_discount.setPriceLimit(invoiceline_discount.getPriceLimit().setScale(2, BigDecimal.ROUND_HALF_UP));
											invoiceline_discount.setLineNetAmt(invoiceline_discount.getLineNetAmt().setScale(2, BigDecimal.ROUND_HALF_UP));
											invoiceline_discount.setLineTotalAmt(invoiceline_discount.getLineTotalAmt().setScale(2, BigDecimal.ROUND_HALF_UP));
											invoiceline_discount.saveEx(trx.getTrxName());
										}
										C_Charge_ID=(Integer)product.get_Value("C_Charge_ID");
										invoiceline_discount = new MInvoiceLine(ctx, 0, trx.getTrxName());
										invoiceline_discount.set_ValueOfColumn("C_Charge_ID", C_Charge_ID);
										invoiceline_discount.setC_Invoice_ID(invoice_discount.getC_Invoice_ID());
										invoiceline_discount.setC_Activity_ID(invoiceline.getC_Activity_ID());
										invoiceline_discount.setC_Tax_ID(invoiceline.getC_Tax_ID());
										//Porcentaje de Descuento / Dias de interes mensual
										DailyAvg = ((BigDecimal)price.get_Value("XX_DiscountPercentage")).divide(new BigDecimal(avgDays),100, RoundingMode.HALF_UP);
										
										//Porcentaje Diario / dias de Descuento
										DailyAvg =	DailyAvg.multiply(new BigDecimal(DiscountDays));
										
										//Porcentaje de Descuento / 100
										DailyAvg =	DailyAvg .divide(new BigDecimal(100));
										
										//Descuento = Porcentaje de Descuento * precio * cantidad 
										discount=(DiscountDays!=0?DailyAvg.multiply(price.getPriceList()).multiply(invoiceline.getQtyInvoiced()):BigDecimal.ZERO);

										invoiceline_discount.setQty(BigDecimal.ONE);
										invoiceline_discount.setQtyInvoiced(BigDecimal.ONE);
										invoiceline_discount.setPrice(discount);
										invoiceline_discount.setPriceList(discount);
										invoiceline_discount.setPriceLimit(discount);
										invoiceline_discount.setLineNetAmt(discount);
										invoiceline_discount.setLineTotalAmt(discount);
										
										
									}
									else
									{
										//Porcentaje de Descuento / Dias de interes mensual
										DailyAvg = ((BigDecimal)price.get_Value("XX_DiscountPercentage")).divide(new BigDecimal(avgDays),100, RoundingMode.HALF_UP);
										
										//Porcentaje Diario / dias de Descuento
										DailyAvg =	DailyAvg.multiply(new BigDecimal(DiscountDays));
										
										//Porcentaje de Descuento / 100
										DailyAvg =	DailyAvg .divide(new BigDecimal(100));
										//Descuento = Porcentaje de Descuento * precio * cantidad 
										discount=invoiceline_discount.getPriceActual().add((DiscountDays!=0?DailyAvg.multiply(price.getPriceList()).multiply(invoiceline.getQtyInvoiced()):BigDecimal.ZERO));

										invoiceline_discount.setPrice(discount);
										invoiceline_discount.setPriceList(discount);
										invoiceline_discount.setPriceLimit(discount);
										invoiceline_discount.setLineNetAmt(discount);
										invoiceline_discount.setLineTotalAmt(discount);
									}
									invoiceline_discount.saveEx(trx.getTrxName());
								}
							}
						}
						
						if (invoiceline_discount==null)
						{
							trx.rollback();
							trx.close();
							ADialog.error(WindowsNo, forma, Msg.translate(ctx,"NoLines"));
							return null;
						}
						else
						{
							invoiceline_discount.setPrice(invoiceline_discount.getPriceActual().setScale(2, BigDecimal.ROUND_HALF_UP));
							invoiceline_discount.setPriceList(invoiceline_discount.getPriceList().setScale(2, BigDecimal.ROUND_HALF_UP));
							invoiceline_discount.setPriceLimit(invoiceline_discount.getPriceLimit().setScale(2, BigDecimal.ROUND_HALF_UP));
							invoiceline_discount.setLineNetAmt(invoiceline_discount.getLineNetAmt().setScale(2, BigDecimal.ROUND_HALF_UP));
							invoiceline_discount.setLineTotalAmt(invoiceline_discount.getLineTotalAmt().setScale(2, BigDecimal.ROUND_HALF_UP));
							invoiceline_discount.saveEx(trx.getTrxName());
							result.add(invoice_discount.getDocumentNo());
							
						}
						
					}
				}
			}

			trx.commit();
			trx.close();
		}catch (Exception e)
		{
			trx.rollback();
			trx.close();
			ADialog.error(WindowsNo, forma, Msg.translate(ctx,e.getMessage()));
		}
		return result;
	}
	
	
	/**
	 * Devuelve el porcentaje de descuento del producto
	 * @author carlos Parada
	 * @date 11/09/2012
	 * @param C_PriceList_Version_ID
	 * @param M_Product_ID
	 * @return
	 * @throws SQLException
	 */
	private MProductPrice getProductPrice(int M_PriceList_Version_ID,int M_Product_ID) throws SQLException
	{
		PreparedStatement ps;
		ResultSet rs;
		MProductPrice price=null;
		
		ps = DB.prepareStatement("Select * From M_ProductPrice Where M_Product_ID=? And M_PriceList_Version_ID=?",null);
		
		ps.setInt(1, M_Product_ID);
		ps.setInt(2, M_PriceList_Version_ID);
		rs = ps.executeQuery();
		
		if(rs.next())
			price=new MProductPrice(ctx, rs, null);
		rs.close();
		ps.close();
		return price;
	}
	
	
	
	/**
	 * Carga Los Documentos Pedientes con el Descuento de Pronto pago en Vector para Minitable
	 * @author Carlos Parada
	 * @date 10/09/2012
	 * @param C_BPartner
	 * @param PayDate
	 * @return
	 * @throws SQLException 
	 */	
	protected void loadTableIn(int C_BPartner,Timestamp PayDate,DefaultTableModel model) throws SQLException,NumberFormatException,ArithmeticException
	{
		Vector<Object> line = null;
		StringBuffer sql = new StringBuffer();
		PreparedStatement ps =null;
		ResultSet rs = null;

		//Elminando Filas
		removeRows(model);
		
		sql.append("Select \n" + 
					"C_Invoice_ID, \n" +
					"DocumentNo, \n" +
					"DateInvoiced, \n" +
					"DueDate, \n" +
					"C_PaymentTerm_ID, \n" +
					"NamePaymentTerm, \n" +
					"InvoiceOpen, \n" +
					"Sum(DaysDue) As DaysDue, \n" +
					"Sum(Interest) As Interest \n" +
					"From \n" + 
					"( \n" +
					"Select \n" +
					"CI.C_Invoice_ID, \n" +
					"CI.DocumentNo, \n" +
					"CI.DateInvoiced, \n" +
					"CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) AS DueDate, \n" +
					"CI.C_PaymentTerm_ID, \n" +
					"CPT.Name As NamePaymentTerm, \n" +
					"InvoiceOpen(CI.C_Invoice_ID,0), \n" +
					"CP.CUST_Rate_ID, \n" +
					"Cast( \n" +
					"Date_Part('day', \n" +
					"/*Fecha Fin*/ \n" +
					"Case When '"+PayDate.toString()+ "' >= CP.ValidTo \n" +
					"Then CP.ValidTo \n" +
					"Else '"+PayDate.toString()+ "' \n" +
					"End \n" +
					"- \n" +
					"/*Fecha Inicio*/ \n" +
					"Case When CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) <= CP.ValidFrom \n" +
					"Then CP.ValidFrom \n" +
					"Else CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) \n" +
					"End) \n" +
					"As Integer) \n" +
					"As DaysDue \n" +
					", Round(Cast(\n" +
					"/*(--------Saldo Pendiente------- *  -Tasa Interes-) / --------------------------Días de Interes Anual---------------------------------------)*/ \n" +
					"((InvoiceOpen(CI.C_Invoice_ID,0) * CP.Rate) /(Select Cast(Value As Numeric(15,5)) From AD_SysConfig Where Name = 'XXInterestYearly')) \n" + 
					"/*----------------------------------Dias de Vencimiento en el mes----------------------------------*/ \n" +
					"* \n" +
					"Date_Part('day', \n" +
					"/*Fecha Fin*/ \n" +
					"Case When '"+PayDate.toString()+ "' >= CP.ValidTo \n" +
					"Then CP.ValidTo \n" +
					"Else '"+PayDate.toString()+ "' \n" +
					"End \n" +
					"- \n" +
					"/*Fecha Inicio*/ \n" +
					"Case When CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) <= CP.ValidFrom \n" +
					"Then CP.ValidFrom \n" +
					"Else CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) \n" +
					"End \n" +
					") \n" +
					"As Numeric),2) \n" +
					"AS Interest \n" +
					"From \n" +
					"C_Invoice CI \n" +
					"Inner Join C_PaymentTerm CPT On CI.C_PaymentTerm_ID=CPT.C_PaymentTerm_ID, \n" +
					"CUST_Rate CP \n" +
					"Where \n" +
					"InvoiceOpen(CI.C_Invoice_ID,0) <>0 And \n" +
					"( \n" +
					"( \n" +
					"CP.ValidFrom Between \n" +
					"CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) \n" +
					"And \n" +
					"'"+PayDate.toString()+ "' \n" +
					"Or \n" +
					"CP.ValidTo Between \n" +
					"CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) \n" +
					"And \n" +
					"'"+PayDate.toString()+ "' \n" +
					"Or \n" +
					"CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) \n" +
					"Between CP.ValidFrom \n" + 
					"And \n" + 
					"CP.ValidTo \n" + 
					") \n" +
					"And \n" + 
					"'"+PayDate.toString()+ "'>CI.DateInvoiced + (Case When CPT.NetDays Is Null Then 0 Else CPT.NetDays End + Case When CPT.GraceDays Is Null Then 0 Else CPT.GraceDays End) \n" +
					") \n" +
					"And C_BPartner_ID="+C_BPartner+ " \n" +
					"And CI.XX_DiscountApplied='N' \n "+
					") As INTE \n" +
					"Group By \n" + 
					"C_Invoice_ID, \n" +
					"DocumentNo, \n" +
					"DateInvoiced, \n" +
					"DueDate, \n" +
					"C_PaymentTerm_ID, \n" +
					"NamePaymentTerm, \n" +
					"InvoiceOpen ");

		//System.out.println(sql.toString());
		ps = DB.prepareStatement(sql.toString(),null);

		rs = ps.executeQuery();

		while (rs.next())
		{
				line = new Vector<Object>();

				line.add(0,new Boolean(false));
				line.add(1,new KeyNamePair(rs.getInt(1), rs.getString(2)));//C_Invoice_ID y DocumentNo
				line.add(2,rs.getTimestamp(3));//Dateinvoiced
				line.add(3,	rs.getTimestamp(4));//DueDate
				line.add(4,new KeyNamePair(rs.getInt(5), rs.getString(6)));//C_PaymentTerm y Name PaymentTerm
				//line.add(5,rs.getBigDecimal(12));
				line.add(5,rs.getInt(8));

				line.add(6,rs.getBigDecimal(9).setScale(2, BigDecimal.ROUND_HALF_UP));
				model.addRow(line);
		}

		ps.close();
		rs.close();	
	}

	
	/**
	 * Generar Notas de Debito por Intereses
	 * @author carlos Parada
	 * @date 23/01/2013
	 * @param tm
	 */
	@SuppressWarnings({ "static-access", "unused" })
	protected final Vector<String> processIn(DefaultTableModel tm,Timestamp PayDate,int C_DocType_ID,FormFrame forma,int WindowsNo,int C_Charge_ID)
	{
		Vector <String> result=new Vector<String>();
		int idInvoice=0;
		
		BigDecimal interest_Amt;
		MInvoice invoice,invoice_interest;
		MInvoiceLine invoiceline_interest=null;
		Trx trx = Trx.get("Interest", true);
		
		try
		{
			for (int i=0;i<tm.getRowCount();i++)
			{
				//Si esta seleccionado
				if ((Boolean)tm.getValueAt(i, 0))
				{
					idInvoice = ((KeyNamePair)tm.getValueAt(i, 1)).getKey();
					interest_Amt = (BigDecimal)tm.getValueAt(i,6);
					//si La Factura existe y el Descuento es distinto de cero
					if (idInvoice!=0 && !interest_Amt.equals(BigDecimal.ZERO))
					{
						//Creando Encabezado de Documento
						invoice = new MInvoice(ctx, idInvoice, trx.getTrxName());
						invoice_interest =new MInvoice(ctx, 0, trx.getTrxName());
						PO.copyValues(invoice, invoice_interest);
						invoice_interest.setDateInvoiced(PayDate);
						invoice_interest.setDateAcct(PayDate);
						invoice_interest.setC_Order_ID(0);
						invoice_interest.setC_DocType_ID(C_DocType_ID);
						invoice_interest.setC_DocTypeTarget_ID(C_DocType_ID);
						invoice_interest.setDocumentNo(null);
						invoice_interest.setGrandTotal(interest_Amt);
						invoice_interest.setDocStatus(invoice.DOCSTATUS_Drafted);
						invoice_interest.setDocAction(invoice.DOCACTION_Complete);
						invoice_interest.setDescription(null);
						invoice_interest.set_ValueOfColumn("XX_C_Invoice_ID", invoice.getC_Invoice_ID());
						invoice_interest.set_ValueOfColumn("XX_NroControl", null);
						invoice_interest.set_ValueOfColumn("XX_Procesa_Retencion", null);
						invoice_interest.set_ValueOfColumn("XX_Conductor_ID", null);
						invoice_interest.set_ValueOfColumn("XX_Vehiculo_ID", null);
						invoice_interest.set_ValueOfColumn("XX_Fecha_Comprobante", null);
						invoice_interest.set_ValueOfColumn("XX_Nro_Comprobante", null);
						invoice_interest.set_ValueOfColumn("XX_Procesado", null);
						invoice_interest.set_ValueOfColumn("XX_Nro_ComprobanteISLR", null);
						invoice_interest.set_ValueOfColumn("XX_ProcesadoISLR", null);
						invoice_interest.set_ValueOfColumn("XX_ProcesadoISLR", null);
						invoice_interest.set_ValueOfColumn("XX_Crear_Lineas_Reverso", null);
						invoice_interest.setProcessed(false);
						invoice_interest.setIsPaid(false);
						invoice.set_ValueOfColumn("XX_DiscountApplied", "Y");
						
						//Creando Linea de Docuemnto
						invoice.saveEx(trx.getTrxName());
						invoice_interest.saveEx(trx.getTrxName());
						
						
						
						invoiceline_interest = new MInvoiceLine(ctx, 0, trx.getTrxName());
						invoiceline_interest.set_ValueOfColumn("C_Charge_ID", C_Charge_ID);
						invoiceline_interest.setC_Invoice_ID(invoice_interest.getC_Invoice_ID());
						invoiceline_interest.setC_Activity_ID(invoice.getC_Activity_ID());
						invoiceline_interest.setQty(BigDecimal.ONE);
						invoiceline_interest.setQtyInvoiced(BigDecimal.ONE);
						invoiceline_interest.setPrice(interest_Amt);
						invoiceline_interest.setPriceList(interest_Amt);
						invoiceline_interest.setPriceLimit(interest_Amt);
						invoiceline_interest.setLineNetAmt(interest_Amt);
						invoiceline_interest.setLineTotalAmt(interest_Amt);
						invoiceline_interest.saveEx(trx.getTrxName());
						


						
						if (invoiceline_interest==null)
						{
							trx.rollback();
							trx.close();
							ADialog.error(WindowsNo, forma, Msg.translate(ctx,"NoLines"));
							return null;
						}
						else
						{
							result.add(invoice_interest.getDocumentNo());
							
						}
						
					}
				}
			}

			trx.commit();
			trx.close();
		}catch (Exception e)
		{
			trx.rollback();
			trx.close();
			ADialog.error(WindowsNo, forma, Msg.translate(ctx,e.getMessage()));
		}
		return result;
	}
	
	/**	Logger			*/
	public static CLogger log = CLogger.getCLogger(PromptPaymentDiscount.class);
	protected Properties ctx;
	
}
