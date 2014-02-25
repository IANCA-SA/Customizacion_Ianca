CREATE OR REPLACE VIEW XX_RV_LineaMovimiento AS
  SELECT lm.AD_Client_ID, lm.AD_Org_ID, lm.Created, lm.CreatedBy, lm.Updated, lm.UpdatedBy,
  lm.ConfirmedQty, lm.Description, lm.M_Locator_ID, lm.M_LocatorTo_ID, 
  lm.M_Movement_ID, lm.M_MovementLine_ID, lm.MovementQty, lm.M_Product_ID,lm.Processed,
  lm.ScrappedQty, lm.TargetQty FROM M_MovementLine lm
