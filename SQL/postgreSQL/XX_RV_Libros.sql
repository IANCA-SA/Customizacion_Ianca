--DROP VIEW XX_RV_Libros

CREATE OR REPLACE VIEW XX_RV_Libros AS
  SELECT fac.AD_Client_ID, fac.AD_Org_ID, fac.C_Invoice_ID, fac.C_BPartner_ID,
    fac.DateAcct, fac.DateInvoiced,
    fac.IsSOTrx, fac.TotalLines, 
    fac.XX_NroControl, trim(cp.Value) Rif,
    CASE WHEN fac.XX_C_Invoice_ID IS NULL THEN fac.DocumentNo END DocumentNo,
    tdoc.DocBaseType, ifac.TasaImpuesto, 
    (CASE WHEN fac.DocStatus = 'RE' THEN 'Anulado' ELSE '' END) MuestAnulado,
    facaf.DocumentNo DocRelacionado, CASE WHEN (fac.XX_C_Invoice_ID IS NOT NULL 
				AND tdoc.DocBaseType NOT IN('ARC', 'APC'))
				THEN fac.DocumentNo END Nota_Debito,
    CASE WHEN (fac.XX_C_Invoice_ID IS NOT NULL 
				AND tdoc.DocBaseType IN('ARC', 'APC'))
				THEN fac.DocumentNo END Nota_Credito,
    cp.Name Nomb_Socio, 
    (((ifac.TotalEx + TotalBi + TotalImpuesto) - (sum(lfac.LineNetAmt)) * fac.Multiplicador)) TotalPagado,
    (CASE WHEN fac.DocStatus = 'RE' THEN 'Anulado' ELSE cp.Name END) SocioNegocio,
    (CASE WHEN fac.DocStatus = 'RE' THEN '' ELSE trim(cp.Value) END) Rif_Libro,
    to_char(ifac.TasaImpuesto, '99') || ' %' Alicuota_Libro,

    /*Montos del Libro*/
    ((ifac.TotalEx + TotalBi + TotalImpuesto) * fac.Multiplicador * fac.M_Total) GrandTotal,
    (ifac.TotalEx * fac.Multiplicador * fac.M_Total) TotalEx, 
    (ifac.TotalBi * fac.Multiplicador * fac.M_Total) TotalBi, 
    (ifac.TotalImpuesto * fac.Multiplicador * fac.M_Total) TotalImpuesto, fac.C_DocType_ID,
    fac.DocStatus
    
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
    GROUP BY fac.C_Invoice_ID, fac.AD_Client_ID, fac.AD_Org_ID, fac.C_BPartner_ID,
    fac.DateAcct, fac.DateInvoiced, fac.GrandTotal, fac.IsSOTrx, fac.TotalLines,
    fac.XX_NroControl, cp.value, fac.DocumentNo, 
    fac.DocStatus, ifac.TotalEx, ifac.TotalBi, ifac.TotalImpuesto, cp.Name, facaf.DocumentNo,
    tdoc.IsSoTrx, fac.XX_C_Invoice_ID, tdoc.DocBaseType, 
    ifac.TasaImpuesto, 
    fac.Multiplicador, fac.M_Total, fac.C_DocType_ID

  UNION ALL 

  /*Documentos de Caja*/
    SELECT fac.AD_Client_ID, fac.AD_Org_ID, fac.C_Invoice_ID, fac.C_BPartner_ID,
    fac.DateAcct, fac.XXFechaDocumento DateInvoiced,
    'N' IsSOTrx, 0 TotalLines,
    fac.NroControl, trim(cp.Value) Rif,
    fac.Nro_Referencia DocumentNo, 
    tdoc.DocBaseType, fac.Rate TasaImpuesto,
    (CASE WHEN fac.DocStatus = 'RE' THEN 'Anulado' ELSE '' END) MuestAnulado,
    '' DocRelacionado, '' Nota_Debito,
    '' Nota_Credito,
    cp.Name Nomb_Socio,
    (fac.Amount * -1) TotalPagado,
    (CASE WHEN fac.DocStatus = 'RE' THEN 'Anulado' ELSE cp.Name END) SocioNegocio,
    (CASE WHEN fac.DocStatus = 'RE' THEN '' ELSE trim(cp.Value) END) Rif_Libro,
    to_char(fac.Rate, '99') || ' %' Alicuota_Libro,
    CASE WHEN fac.DocStatus = 'RE' THEN 0 ELSE 1 END * (fac.Amount * -1) GrandTotal, 
    CASE WHEN fac.DocStatus = 'RE' THEN 0 ELSE 1 END * abs(fac.TotalEx) TotalEx,
    CASE WHEN fac.DocStatus = 'RE' THEN 0 ELSE 1 END * (fac.XXMontoBase * -1) TotalBi, 
    CASE WHEN fac.DocStatus = 'RE' THEN 0 ELSE 1 END * (fac.TotalImpuesto * -1) TotalImpuesto,
    fac.C_DocTypeTarget_ID C_DocType_ID, fac.DocStatus
    FROM XX_RV_Diario_Efectivo_Libro fac 
    INNER JOIN C_BPartner cp ON(cp.C_BPartner_ID = fac.C_BPartner_ID)
    INNER JOIN C_DocType tdoc ON(tdoc.C_DocType_ID = fac.C_DocTypeTarget_ID)
    WHERE fac.XXAfectaLibro = 'Y' AND fac.CashType = 'C'