CREATE OR REPLACE VIEW XX_RV_Movimiento_Inv AS 
  SELECT mi.AD_Client_ID, mi.AD_Org_ID, mi.Created, mi.CreatedBy, mi.Updated, mi.UpdatedBy,
  mi.C_Activity_ID, mi.ApprovalAmt, mi.C_BPartner_ID, mi.C_BPartner_Location_ID, mi.C_Charge_ID,
  mi.C_DocType_ID, mi.ChargeAmt, mi.DateReceived, mi.DeliveryRule, mi.DeliveryViaRule, mi.Description,
  mi.DocAction, mi.DocStatus, mi.DocumentNo, mi.FreightAmt, mi.FreightCostRule, mi.M_Movement_ID,
  mi.MovementDate, mi.M_Shipper_ID, mi.PriorityRule, mi.SalesRep_ID
  FROM M_Movement mi
