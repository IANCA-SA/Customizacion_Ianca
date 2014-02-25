--DROP VIEW XX_RV_Diario_Efectivo_Libro

CREATE OR REPLACE VIEW XX_RV_Diario_Efectivo_Libro AS
SELECT de.AD_Client_ID, de.AD_Org_ID, de.Created, de.CreatedBy, de.Updated, de.UpdatedBy,
 de.C_Activity_ID, de.BeginningBalance, de.C_CashBook_ID, de.C_Cash_ID, de.C_DocTypeTarget_ID,
 de.DateAcct, de.Description, de.DocAction, de.DocStatus, de.DocumentNo, de.EndingBalance,
 de.Name, de.StatementDate, de.StatementDifference, de.XXMontoPagado,  
 lde.CashType, lde.C_BankAccount_ID,  lde.C_Bank_ID, lde.C_BPartner_ID, 
 lde.C_CashLine_ID, lde.C_Charge_ID, lde.C_Currency_ID, lde.C_Invoice_ID, lde.C_Payment_ID, 
 lde.CuentaCheque, lde.Description Descripcion_Linea, lde.DiscountAmt, lde.IsActive, lde.IsGenerated, 
 lde.IsOverUnderPayment, lde.Line, lde.NroCheque, lde.NroControl, lde.Nro_Referencia, 
 lde.Processed, lde.WriteOffAmt, lde.XXAfectaLibro, lde.XXFechaDocumento, lde.XXMontoBase, lde.C_Tax_ID, 
 im.Rate, ((Rate/100)*XXMontoBase) TotalImpuesto, (Amount - ((Rate*XXMontoBase/100) + XXMontoBase)) TotalEx, lde.Amount
 FROM C_Cash de 
 INNER JOIN C_CashLine lde ON(lde.C_Cash_ID = de.C_Cash_ID)
 LEFT JOIN C_Charge car ON(car.C_Charge_ID = lde.C_Charge_ID)
 LEFT JOIN C_Tax im ON(im.C_Tax_ID = lde.C_Tax_ID) 
