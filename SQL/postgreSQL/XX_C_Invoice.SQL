--DROP VIEW XX_C_Invoice

CREATE OR REPLACE VIEW XX_C_Invoice AS
SELECT fac.AD_Client_ID,fac.AD_Org_ID, fac.AD_OrgTrx_ID, fac.AD_User_ID, fac.Bill_Location_ID, 
	fac.C_Activity_ID, fac.C_BPartner_ID, fac.C_BPartner_Location_ID, fac.C_Campaign_ID, 
	fac.C_CashLine_ID, fac.C_Charge_ID, fac.C_ConversionType_ID, fac.C_Currency_ID, 
	fac.C_DocType_ID, fac.C_DocTypeTarget_ID, fac.C_DunningLevel_ID, fac.ChargeAmt, 
	fac.C_Invoice_ID, fac.CopyFrom, fac.C_Order_ID, fac.C_Payment_ID, fac.C_PaymentTerm_ID, 
	fac.C_Project_ID, fac.Created, fac.CreatedBy, fac.CreateFrom, fac.DateAcct, fac.DateInvoiced, 
	fac.DateOrdered, fac.DatePrinted, fac.DeliveryViaRule, fac.Description, fac.DocAction, 
	fac.DocStatus, fac.DocumentNo, fac.DunningGrace, fac.GenerateTo, 
	fac.GrandTotal, fac.InvoiceCollectionType, fac.IsActive, fac.IsApproved, fac.IsDiscountPrinted, 
	fac.IsInDispute, fac.IsPaid, fac.IsPayScheduleValid, fac.IsPrinted, fac.IsSelfService, 
	fac.IsSOTrx, fac.IsTaxIncluded, fac.IsTransferred, fac.M_PriceList_ID, fac.M_RMA_ID, 
	fac.M_Shipper_ID, fac.PaymentRule, fac.POReference, fac.Posted, fac.Processed, 
	fac.ProcessedOn, fac.Processing, fac.Ref_Invoice_ID, fac.Reversal_ID, fac.SalesRep_ID, 
	fac.SendEMail, fac.TotalLines, fac.Updated, fac.UpdatedBy, fac.User1_ID, fac.User2_ID, 
	fac.XX_C_Invoice_ID, fac.XX_ConceptoRetencion_ID, fac.XX_Conductor_ID, fac.XX_Convenio_Cosecha_ID, 
	fac.XX_Crear_Lineas_Reverso, fac.XX_Fecha_Comprobante, fac.XX_Fecha_ComprobanteISLR, fac.XX_Nro_Comprobante, 
	fac.XX_Nro_ComprobanteISLR, fac.XX_NroControl, fac.XX_Procesado, fac.XX_ProcesadoISLR, fac.XX_Procesa_Retencion, 
	fac.XX_Vehiculo_ID, 
	(CASE 
		WHEN (fac.XX_C_Invoice_ID IS NOT NULL 
		AND tdoc.DocBaseType IN('ARC', 'APC'))
	THEN -1 
	ELSE 1	
	END) Multiplicador,
	(CASE WHEN fac.DocStatus = 'RE' THEN 0 ELSE 1 END) M_Total
FROM C_Invoice fac
INNER JOIN C_DocType tdoc ON(fac.C_DocType_ID = tdoc.C_DocType_Id)
