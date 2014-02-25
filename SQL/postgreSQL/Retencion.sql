SELECT fac.DocumentNo, cp.Name, fac.GrandTotal, sum(tx.TaxAmt) impuesto, sum(tx.TaxAmt)*0.75 retencion  FROM C_Invoice fac
  INNER JOIN C_InvoiceLine lfac ON(lfac.C_Invoice_ID = fac.C_Invoice_ID)
  INNER JOIN C_BPartner cp ON(cp.C_BPartner_ID = fac.C_BPartner_ID)
  LEFT JOIN XX_RV_ImpuestoFactura tx ON(tx.C_Invoice_ID = fac.C_Invoice_ID)
  WHERE cp.XX_Retencion_Iva_ID = 1000000
  AND lfac.XX_TipoRetencion = 'IV'
  GROUP BY fac.DocumentNo, cp.Name, fac.GrandTotal



