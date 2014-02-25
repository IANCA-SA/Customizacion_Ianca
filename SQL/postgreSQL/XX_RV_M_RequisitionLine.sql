--DROP VIEW XX_RV_M_RequisitionLine
CREATE OR REPLACE VIEW XX_RV_M_RequisitionLine AS
SELECT M_RequisitionLine_ID, M_Requisition_ID, AD_Client_ID, AD_Org_ID, IsActive, Created, CreatedBy, Updated, UpdatedBy, 
  qty, M_Product_ID, Description, PriceActual, LineNetAmt, C_Charge_ID, C_BPartner_ID, C_Uom_ID
  FROM M_RequisitionLine