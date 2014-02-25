--DROP VIEW XX_RV_Diario_Cobro
CREATE OR REPLACE VIEW XX_RV_Diario_Cobro AS
SELECT ldef.AD_Client_ID, ldef.AD_Org_ID, ldef.Amount, ldef.C_Activity_ID, ldef.CashType, ldef.C_BankAccount_ID, 
  ldef.C_Bank_ID, ldef.C_BPartner_ID, ldef.C_Cash_ID, ldef.C_Charge_ID, ldef.C_Currency_ID, 
  ldef.C_Invoice_ID, ldef.C_Tax_ID, ldef.CuentaCheque, ldef.Description, ldef.DiscountAmt, 
  ldef.IsActive, to_char(ldef.Updated, 'DD/MM/YYYY') Updated, ldef.UpdatedBy, to_char(ldef.Created, 'DD/MM/YYYY') Created, 
  ldef.CreatedBy, ldef.XXFechaDocumento, fac.IsSOTrx, fac.DocumentNo Factura, 
  fac.SalesRep_ID, def.DateAcct,  to_char(def.DateAcct, 'DD/MM/YYYY') Fecha, def.DocStatus, fac.GrandTotal
FROM C_Cash def
  INNER JOIN C_DocType tdoc ON(tdoc.C_DocType_ID = def.C_DocTypeTarget_ID)
  INNER JOIN C_CashLine ldef ON(ldef.C_Cash_ID = def.C_Cash_ID)
  INNER JOIN C_Invoice fac ON(fac.C_Invoice_ID = ldef.C_Invoice_ID)