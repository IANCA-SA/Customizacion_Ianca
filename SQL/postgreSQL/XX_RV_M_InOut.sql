--DROP VIEW XX_RV_M_InOut

CREATE OR REPLACE VIEW XX_RV_M_InOut AS 
SELECT AD_Client_ID, AD_Org_ID, IsActive, Created, CreatedBy, Updated, UpdatedBy, DatePrinted 
  M_InOut_ID, IsSotrx, DocumentNo, DocStatus, C_DocType_ID, Description, C_Order_ID,
  DateOrdered, MovementDate, DateAcct, C_BPartner_ID, C_BPartner_Location_ID, M_Warehouse_ID,
  DeliveryRule, DeliveryViaRule, M_Shipper_ID, C_Charge_ID, ChargeAmt, PriorityRule, C_Invoice_ID,
  AD_User_ID, C_Activity_ID
 FROM M_InOut