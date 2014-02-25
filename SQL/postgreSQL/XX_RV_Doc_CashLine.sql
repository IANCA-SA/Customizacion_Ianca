CREATE OR REPLACE VIEW XX_RV_Doc_CashLine AS 
 SELECT fp.AD_Client_ID, fp.AD_Org_ID, fp.Created, fp.CreatedBy, fp.Updated, fp.UpdatedBy,
 fp.C_Bank_ID, fp.C_Cash_ID, fp.Monto, fp.Nro_Referencia, fp.TipoPago, fp.XX_Doc_CashLine_ID
 FROM XX_Doc_CashLine fp