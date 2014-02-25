-- View: xx_recibolinea

-- DROP VIEW xx_recibolinea;

CREATE OR REPLACE VIEW xx_recibolinea AS 
 SELECT ccl.ad_client_id, ccl.ad_org_id, ccl.isactive, ccl.c_cashline_id, ccl.c_cash_id, ccl.line, 
  ccl.description, ccl.cashtype, ccl.c_bankaccount_id, ccl.c_charge_id, ccl.c_currency_id, 
  ccl.amount, ccl.discountamt, ccl.writeoffamt, ccl.isgenerated, ccl.processed, ccl.c_payment_id, 
  ccl.c_bank_id, ccl.cuentacheque, ccl.nrocheque, ccl.c_cash_id AS xx_recibo_id, ccl.c_invoice_id, 
  ccl.c_bpartner_id, ccl.nro_referencia, cc.documentno, cc.c_doctypetarget_id, ccl.XXAfectaLibro,
  ccl.XXFechaDocumento, ccl.XXMontoBase
   FROM c_cashline ccl
   JOIN c_cash cc ON ccl.c_cash_id = cc.c_cash_id;

ALTER TABLE xx_recibolinea OWNER TO adempiere;

