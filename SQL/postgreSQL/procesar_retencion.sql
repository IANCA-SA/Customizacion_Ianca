SELECT SUM(lfac.LineNetAmt) netoFactura, ret.XX_Alicuota, ret.XX_Base, ret.XX_Minimo, ret.XX_Sustraendo, ret.C_Tax_ID 
  FROM C_Invoice fac INNER JOIN C_InvoiceLine lfac ON(lfac.C_Invoice_ID = fac.C_Invoice_ID)
  INNER JOIN C_BPartner cp ON(cp.C_BPartner_ID = fac.C_BPartner_ID)
  INNER JOIN XX_Retencion ret ON(ret.XX_TipoPersona_ID = cp.XX_TipoPersona_ID)
  INNER JOIN XX_ConceptoRetencion cret ON(cret.XX_ConceptoRetencion_ID = ret.XX_ConceptoRetencion_ID)
  WHERE fac.C_Invoice_ID = 1000002
  GROUP BY fac.C_Invoice_ID, ret.XX_Alicuota, ret.XX_Base, ret.XX_Minimo, ret.XX_Sustraendo, ret.C_Tax_ID