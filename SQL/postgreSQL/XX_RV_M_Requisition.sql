--DROP VIEW XX_RV_Requisicion
CREATE OR REPLACE VIEW XX_RV_Requisicion AS
SELECT rq.M_Requisition_ID, rq.AD_Client_ID, rq.AD_Org_ID, rq.CreatedBy,
  rq.M_WareHouse_ID, rq.M_PriceList_ID, rq.DocumentNo, rq.DocStatus, rq.C_DocType_ID, rq.Description, rq.AD_User_ID,
  rq.DateDoc, rq.DateRequired, rq.TotalLines
  FROM M_Requisition rq