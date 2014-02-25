-- DROP VIEW xx_recibo;

CREATE OR REPLACE VIEW xx_recibo AS 
 SELECT cc.ad_client_id, cc.ad_org_id, cc.isactive, cc.c_cash_id, cc.dateacct, cc.name, cc.c_cashbook_id, cc.description, cc.beginningbalance, cc.endingbalance, cc.statementdifference, cc.processed, cc.ad_orgtrx_id, cc.c_project_id, cc.c_campaign_id, cc.c_activity_id, cc.isapproved, cc.docstatus, cc.c_cash_id AS xx_recibo_id, cc.documentno, cc.c_doctypetarget_id
   FROM c_cash cc;