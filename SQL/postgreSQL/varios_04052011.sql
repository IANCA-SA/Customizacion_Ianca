SELECT * FROM C_Invoice WHERE C_Invoice_ID in(1003624, 1003618)


SELECT * FROM XX_ConceptoRetencion



SELECT * FROM XX_RV_Comprobante_Retencion WHERE C_Invoice_ID=1003601 AND 
DocStatus IN ('CO','CL','RE') AND IsSotrx = 'N' AND DocBaseType IN ('API', 'APC') AND XX_TipoRetencion = 'IV'


SELECT * FROM AD_Table WHERE TableName = 'XX_Retencion'

SELECT ColumnName || ', ' FROM AD_Column WHERE AD_Table_ID = 1000012

UPDATE AD_PrintFormatItem SET AD_Org_ID = 0






AD_Client_ID, 
AD_Org_ID, 
C_Charge_ID, 
Created, 
CreatedBy, 
C_Tax_ID, 
Description, 
IsActive, 
Name, 
Updated, 
UpdatedBy, 
XX_ALICUOTA, 
XX_Codigo_Retencion, 
XX_COLUMNA, 
XX_ConceptoRetencion_ID, 
XX_MINIMO, 
XX_NOTA1, 
XX_Retencion_ID, 
XX_SUSTRAENDO, 
XX_TipoPersona_ID, 




