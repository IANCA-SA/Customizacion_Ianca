CREATE OR REPLACE VIEW XX_RV_ImpuestoFactura AS
SELECT it.AD_Client_ID, it.AD_Org_ID, it.C_Invoice_ID , ip.C_TaxCategory_ID, ip.Name,
  SUM(it.TaxBaseAmt) TaxBaseAmt, SUM(it.TaxAmt) TaxAmt 
  FROM C_InvoiceTax it INNER JOIN C_Tax t ON(it.C_Tax_ID = t.C_Tax_ID)
  INNER JOIN C_TaxCategory ip ON(ip.C_TaxCategory_ID = t.C_TaxCategory_ID)
  WHERE it.TaxAmt <> 0
  GROUP BY ip.C_TaxCategory_ID, ip.Name, it.AD_Client_ID, it.AD_Org_ID, it.C_Invoice_ID