--DROP VIEW XX_RV_Comprobante_Retencion

CREATE OR REPLACE VIEW XX_RV_Comprobante_Retencion AS
  SELECT fac.AD_Client_ID, fac.AD_Org_ID, fac.C_Invoice_ID, fac.C_BPartner_ID,
    fac.DateAcct, fac.DateInvoiced,
    fac.IsSOTrx, fac.TotalLines,
    fac.XX_ConceptoRetencion_ID, fac.XX_Fecha_Comprobante, fac.XX_Nro_Comprobante, 
    fac.XX_Fecha_ComprobanteISLR, fac.XX_Nro_ComprobanteISLR, fac.XX_ProcesadoISLR,
    fac.XX_NroControl, fac.XX_Procesado, trim(cp.Value) Rif,
    CASE WHEN fac.XX_C_Invoice_ID IS NULL THEN fac.DocumentNo END DocumentNo,
    fac.DocStatus, (ifac.TotalEx * fac.Multiplicador) TotalEx, 
    (ifac.TotalBi * fac.Multiplicador) TotalBi, 
    (ifac.TotalImpuesto * fac.Multiplicador) TotalImpuesto, 
    tdoc.DocBaseType,lfac.XX_TipoRetencion, ifac.TasaImpuesto,
    cret.Name ConceptoRetencion,ret.XX_ALICUOTA, ret.XX_Codigo_Retencion, ret.XX_MINIMO, 
    ret.XX_NOTA1, ret.XX_Retencion_ID, ret.XX_SUSTRAENDO,
    (CASE WHEN fac.DocStatus = 'RE' THEN 'Anulado' ELSE '' END) MuestAnulado,
    facaf.DocumentNo DocRelacionado, CASE WHEN (fac.XX_C_Invoice_ID IS NOT NULL 
				AND tdoc.DocBaseType NOT IN('ARC', 'APC'))
				THEN fac.DocumentNo END Nota_Debito,
    CASE WHEN (fac.XX_C_Invoice_ID IS NOT NULL 
				AND tdoc.DocBaseType IN('ARC', 'APC'))
				THEN fac.DocumentNo END Nota_Credito,
    to_char(fac.XX_Fecha_Comprobante, 'YYYY MM') Period, cp.Name Nomb_Socio, 
    ((ifac.TotalEx + TotalBi + TotalImpuesto) * fac.Multiplicador) GrandTotal,
    (((ifac.TotalEx + TotalBi + TotalImpuesto) + (sum(lfac.LineNetAmt)) * fac.Multiplicador)) TotalPagado,
    ((sum(lfac.LineNetAmt) * fac.Multiplicador)*-1) MontoRetenido,
    (CASE WHEN fac.DocStatus = 'RE' THEN 'Anulado' ELSE cp.Name END) SocioNegocio,
    (CASE WHEN fac.DocStatus = 'RE' THEN '' ELSE trim(cp.Value) END) Rif_Libro,
    to_char(ifac.TasaImpuesto, '99') || ' %' Alicuota_Libro, fac.C_DocType_ID
    
    FROM XX_C_Invoice fac
    INNER JOIN C_DocType tdoc ON(fac.C_DocType_ID = tdoc.C_DocType_ID)
    INNER JOIN C_InvoiceLine lfac ON(fac.C_Invoice_ID = lfac.C_Invoice_ID)
    INNER JOIN C_BPartner cp ON(cp.C_BPartner_ID = fac.C_BPartner_ID)
    /*Relacion con las lineas de Impuesto de la Factura*/
    INNER JOIN (SELECT li.C_Invoice_ID,
    sum(CASE WHEN li.TaxAmt = 0 THEN li.TaxBaseAmt ELSE 0 END) AS TotalEx, 
    sum(CASE WHEN li.TaxAmt <> 0 THEN li.TaxBaseAmt ELSE 0 END) AS TotalBi,
    sum(CASE WHEN li.TaxAmt <> 0 THEN li.TaxAmt ELSE 0 END) AS TotalImpuesto,
    max(im.Rate) TasaImpuesto 
    FROM C_InvoiceTax li INNER JOIN C_Tax im ON(im.C_Tax_ID = li.C_Tax_ID)
    GROUP BY C_Invoice_ID) ifac ON(ifac.C_Invoice_ID = fac.C_Invoice_ID)
    
    LEFT JOIN C_Invoice facaf ON(facaf.C_Invoice_ID = fac.XX_C_Invoice_ID)
    LEFT JOIN XX_ConceptoRetencion cret ON(cret.XX_ConceptoRetencion_ID = fac.XX_ConceptoRetencion_ID)
    LEFT JOIN XX_Retencion ret ON(ret.XX_TipoPersona_ID = cp.XX_TipoPersona_ID 
      AND ret.XX_ConceptoRetencion_ID = fac.XX_ConceptoRetencion_ID)
    GROUP BY fac.C_Invoice_ID, fac.AD_Client_ID, fac.AD_Org_ID, fac.C_BPartner_ID,
    fac.DateAcct, fac.DateInvoiced, fac.GrandTotal, fac.IsSOTrx, fac.TotalLines,
    fac.XX_ConceptoRetencion_ID, fac.XX_Fecha_Comprobante,
    fac.XX_Nro_Comprobante, fac.XX_NroControl, fac.XX_Procesado, cp.value, fac.DocumentNo, 
    fac.DocStatus, ifac.TotalEx, ifac.TotalBi, ifac.TotalImpuesto, cp.Name, facaf.DocumentNo,
    tdoc.IsSoTrx, fac.XX_C_Invoice_ID, tdoc.DocBaseType, 
    lfac.XX_TipoRetencion, ifac.TasaImpuesto, fac.XX_Fecha_ComprobanteISLR, 
    fac.XX_Nro_ComprobanteISLR, fac.XX_ProcesadoISLR, cret.Name, 
    ret.XX_ConceptoRetencion_ID, ret.XX_Retencion_ID, ret.XX_SUSTRAENDO, 
    fac.Multiplicador, fac.M_Total, fac.C_DocType_ID, 
    ret.XX_MINIMO, ret.XX_NOTA1, ret.XX_ALICUOTA, 
    ret.XX_Codigo_Retencion