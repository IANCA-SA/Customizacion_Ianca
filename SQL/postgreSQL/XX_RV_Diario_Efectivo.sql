--DROP VIEW XX_RV_Diario_Efectivo

CREATE OR REPLACE VIEW XX_RV_Diario_Efectivo AS 
 SELECT de.AD_Client_ID, de.AD_Org_ID, de.Created, de.CreatedBy, de.Updated, de.UpdatedBy,
 de.C_Activity_ID, de.BeginningBalance, de.C_CashBook_ID, de.C_Cash_ID, de.C_DocTypeTarget_ID,
 de.DateAcct, de.Description, de.DocAction, de.DocStatus, de.DocumentNo, de.EndingBalance,
 de.Name, de.StatementDate, de.StatementDifference, de.XXMontoPagado
 FROM C_Cash de