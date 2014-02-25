--DROP VIEW XX_RV_Comprobante_RetencionISLR

--CREATE OR REPLACE VIEW XX_RV_Comprobante_RetencionISLR AS
  SELECT fac.AD_Client_ID, fac.AD_Org_ID, fac.C_Invoice_ID, fac.C_BPartner_ID,
    fac.DateAcct, fac.DateInvoiced,  
    fac.IsSOTrx, fac.TotalLines,
    fac.XX_ConceptoRetencion_ID, fac.XX_Fecha_ComprobanteISLR, fac.XX_Nro_ComprobanteISLR, 
    fac.XX_NroControl, fac.XX_ProcesadoISLR, trim(cp.Value) Rif,
    CASE WHEN fac.XX_C_Invoice_ID IS NULL THEN fac.DocumentNo END DocumentNo,
    fac.DocStatus, ifac.TotalEx, ifac.TotalBi, ifac.TotalImpuesto, 
    tdoc.DocBaseType,lfac.XX_TipoRetencion, ifac.TasaImpuesto,
    facaf.DocumentNo DocRelacionado, CASE WHEN (fac.XX_C_Invoice_ID IS NOT NULL 
				AND tdoc.IsSoTrx = 'Y')
				THEN fac.DocumentNo END Nota_Debito,
    CASE WHEN (fac.XX_C_Invoice_ID IS NOT NULL 
				AND tdoc.IsSoTrx = 'N')
				THEN fac.DocumentNo END Nota_Credito,
    to_char(fac.XX_Fecha_ComprobanteISLR, 'YYYY MM') Period, cp.Name Nomb_Socio,
    (fac.GrandTotal - sum(CASE 
				WHEN lfac.XX_TipoRetencion IS NOT NULL 
				THEN lfac.LineNetAmt 
				ELSE 0 
				END)) GrandTotal,
    (sum(CASE WHEN lfac.XX_TipoRetencion = 'IS' THEN lfac.LineNetAmt ELSE 0 END)*tdoc.Multiplicador) MontoRetenido
    FROM C_Invoice fac
    INNER JOIN XX_RV_C_DocType tdoc ON(fac.C_DocType_ID = tdoc.C_DocType_ID)
    INNER JOIN C_InvoiceLine lfac ON(fac.C_Invoice_ID = lfac.C_Invoice_ID)
    INNER JOIN C_BPartner cp ON(cp.C_BPartner_ID = fac.C_BPartner_ID)
    INNER JOIN (SELECT li.C_Invoice_ID,sum(CASE WHEN li.TaxAmt = 0 THEN li.TaxBaseAmt ELSE 0 END) AS TotalEx, 
    sum(CASE WHEN li.TaxAmt <> 0 THEN li.TaxBaseAmt ELSE 0 END) AS TotalBi,
    sum(CASE WHEN li.TaxAmt <> 0 THEN li.TaxAmt ELSE 0 END) AS TotalImpuesto,
    max(im.Rate) TasaImpuesto 
    FROM C_InvoiceTax li INNER JOIN C_Tax im ON(im.C_Tax_ID = li.C_Tax_ID)
    GROUP BY C_Invoice_ID) ifac ON(ifac.C_Invoice_ID = fac.C_Invoice_ID)
    LEFT JOIN C_Invoice facaf ON(facaf.C_Invoice_ID = fac.XX_C_Invoice_ID)
    GROUP BY fac.C_Invoice_ID, fac.AD_Client_ID, fac.AD_Org_ID, fac.C_BPartner_ID,
    fac.DateAcct, fac.DateInvoiced, fac.GrandTotal, fac.IsSOTrx, fac.TotalLines,
    fac.XX_ConceptoRetencion_ID, fac.XX_Fecha_ComprobanteISLR,
    fac.XX_Nro_ComprobanteISLR, fac.XX_NroControl, fac.XX_ProcesadoISLR, cp.value, fac.DocumentNo, 
    fac.DocStatus, ifac.TotalEx, ifac.TotalBi, ifac.TotalImpuesto, cp.Name, facaf.DocumentNo,
    tdoc.Multiplicador, tdoc.IsSoTrx, fac.XX_C_Invoice_ID, tdoc.DocBaseType, lfac.XX_TipoRetencion, ifac.TasaImpuesto