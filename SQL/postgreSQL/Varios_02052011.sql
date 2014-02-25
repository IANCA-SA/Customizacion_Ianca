SELECT fac.C_Invoice_ID FROM C_Invoice fac
                        INNER JOIN C_InvoiceLine lfac ON(lfac.C_Invoice_ID = fac.C_Invoice_ID)
                        WHERE fac.XX_Nro_Comprobante IS NULL 
                        AND fac.XX_Procesado = 'N'
                        AND Date(fac.DateAcct) BETWEEN Date('10/01/2011') And Date('10/05/2011')
                        AND fac.DocStatus IN ('CO', 'CL')
                        AND (1000021 = lfac.C_Charge_ID OR 1000119 = lfac.C_Charge_ID)
                        GROUP BY fac.C_Invoice_ID

SELECT * FROM C_Invoice WHERE XX_Nro_Comprobante IS NOT NULL

UPDATE C_INVOICE SET XX_Nro_Comprobante = NULL

UPDATE C_Invoice
SET XX_Nro_Comprobante = to_char(current_date, 'YYYYMM') || XX_Fn_Actualiza_Sec(1000242),
        XX_Fecha_Comprobante = current_timestamp
        --WHERE C_Invoice_ID = 1000242;

SELECT p.P_Number into p_Secuencia FROM AD_PInstance i 
    LEFT JOIN AD_PInstance_Para p ON(i.AD_PInstance_ID = p.AD_PInstance_ID)
    LEFT JOIN AD_Sequence s ON(p_Secuencia = s.AD_Sequence_ID)       
    WHERE p.AD_PInstance_ID = p_Instancia AND P.ParameterName='p_Secuencia';

SELECT * FROM AD_PInstance_Para



SELECT to_char(current_date, 'YYYYMM') || XX_Fn_Actualiza_Sec(1000242)



