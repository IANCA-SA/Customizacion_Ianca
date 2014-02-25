-- DROP VIEW c_payselection_check_v;

CREATE OR REPLACE VIEW c_payselection_check_v AS 
 SELECT psc.ad_client_id, psc.ad_org_id, 'en_US'::character varying AS ad_language, 
  psc.c_payselection_id, psc.c_payselectioncheck_id, oi.c_location_id AS org_location_id, 
  oi.taxid, 0 AS c_doctype_id, bp.c_bpartner_id, bp.value AS bpvalue, bp.taxid AS bptaxid, 
  bp.naics, bp.duns, bpg.greeting AS bpgreeting, bp.name, bp.name2, 
  bpartnerremitlocation(bp.c_bpartner_id) AS c_location_id, bp.referenceno, bp.poreference, 
  ps.paydate, psc.payamt, psc.payamt AS amtinwords, psc.qty, psc.paymentrule, psc.documentno, 
  ps.description, ps.c_bankaccount_id, 
  (((('Araure, '::text || date_part('day'::text, ps.paydate)) || '  De '::text) 
  || initcap(to_char(ps.paydate, 'tmmonth'::text))) || '    '::text) || date_part('year'::text, ps.paydate) AS xx_dateinwords, 
  abs(psc.payamt) AS amtabs, pa.DocumentNo Pago, pa.CheckNo
   FROM c_payselectioncheck psc
   JOIN c_payselection ps ON psc.c_payselection_id = ps.c_payselection_id
   JOIN c_bpartner bp ON psc.c_bpartner_id = bp.c_bpartner_id
   LEFT JOIN c_greeting bpg ON bp.c_greeting_id = bpg.c_greeting_id
   JOIN ad_orginfo oi ON psc.ad_org_id = oi.ad_org_id
   LEFT JOIN C_Payment pa ON pa.C_Payment_ID = psc.C_Payment_ID