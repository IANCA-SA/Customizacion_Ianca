SELECT (SUM(TaxBaseAmt) ) Total_Impuesto, ret.Monto_Retener Porc_Retener, ret.C_Tax_ID Impuesto, ret.C_Charge_ID Cargo FROM XX_RV_ImpuestoFactura iFac INNER JOIN C_Invoice fac ON(fac.C_Invoice_ID = iFac.C_Invoice_ID) INNER JOIN C_BPartner cp ON(cp.C_BPartner_ID = fac.C_BPartner_ID)INNER JOIN XX_Retencion_Iva ret ON(ret.XX_Retencion_Iva_ID = cp.XX_Retencion_Iva_ID)GROUP BY fac.C_Invoice_ID, ret.Monto_Retener, ret.C_Tax_ID, ret.C_Charge_ID



SELECT 75/100

SELECT ret.Monto_Retener Porc_Retencion, ret.C_Tax_ID Impuesto, ret.C_Charge_ID Cargo FROM XX_Retencion_Iva ret INNER JOIN C_BPartner cp ON(cp.XX_Retencion_Iva_ID = ret.XX_Retencion_Iva_ID) 