-- DROP VIEW c_invoice_header_v;

CREATE OR REPLACE VIEW c_invoice_header_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, 
 i.updatedby, 'en_US'::character varying AS ad_language, i.c_invoice_id, i.issotrx, 
 i.documentno, i.docstatus, i.c_doctype_id, i.c_bpartner_id, bp.value AS bpvalue, 
 bp.taxid AS bptaxid, bp.naics, bp.duns, oi.c_location_id AS org_location_id, oi.taxid, 
 dt.printname AS documenttype, dt.documentnote AS documenttypenote, i.c_order_id, 
 i.salesrep_id, COALESCE(ubp.name, u.name) AS salesrep_name, i.dateinvoiced, 
 bpg.greeting AS bpgreeting, bp.name, bp.name2, bpcg.greeting AS bpcontactgreeting, 
 bpc.title, bpl.phone, NULLIF(bpc.name::text, bp.name::text) AS contactname, bpl.c_location_id, 
 bp.referenceno, l.postal::text || l.postal_add::text AS postal, i.description, i.poreference, 
 i.dateordered, i.c_currency_id, pt.name AS paymentterm, pt.documentnote AS paymenttermnote, i.c_charge_id, 
 i.chargeamt, i.totallines, i.grandtotal, i.grandtotal AS amtinwords, i.m_pricelist_id, i.istaxincluded, 
 i.c_campaign_id, i.c_project_id, i.c_activity_id, i.ispaid, COALESCE(oi.logo_id, ci.logo_id) AS logo_id, 
 i.bill_location_id, cc.xx_convenio_cosecha_id, tr.m_shipper_id, con.xx_conductor_id, veh.xx_vehiculo_id, 
 i.xx_nrocontrol, sum(
        CASE
            WHEN if.taxamt = 0::numeric THEN if.taxbaseamt
            ELSE 0::numeric
        END) AS totalex, sum(
        CASE
            WHEN if.taxamt <> 0::numeric THEN if.taxbaseamt
            ELSE 0::numeric
        END) AS totalbi, o.DocumentNo Nro_Doc_Orden,
   dt.Name Nomb_Doc, faf.DocumentNo Documento_Afectado
   FROM c_invoice i
   JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
   JOIN c_paymentterm pt ON i.c_paymentterm_id = pt.c_paymentterm_id
   JOIN c_bpartner bp ON i.c_bpartner_id = bp.c_bpartner_id
   LEFT JOIN c_greeting bpg ON bp.c_greeting_id = bpg.c_greeting_id
   JOIN c_bpartner_location bpl ON i.c_bpartner_location_id = bpl.c_bpartner_location_id
   JOIN c_location l ON bpl.c_location_id = l.c_location_id
   LEFT JOIN ad_user bpc ON i.ad_user_id = bpc.ad_user_id
   LEFT JOIN c_greeting bpcg ON bpc.c_greeting_id = bpcg.c_greeting_id
   JOIN ad_orginfo oi ON i.ad_org_id = oi.ad_org_id
   JOIN ad_clientinfo ci ON i.ad_client_id = ci.ad_client_id
   LEFT JOIN ad_user u ON i.salesrep_id = u.ad_user_id
   LEFT JOIN c_bpartner ubp ON u.c_bpartner_id = ubp.c_bpartner_id
   LEFT JOIN xx_convenio_cosecha cc ON cc.xx_convenio_cosecha_id = i.xx_convenio_cosecha_id
   LEFT JOIN m_shipper tr ON tr.m_shipper_id = i.m_shipper_id
   LEFT JOIN xx_conductor con ON con.xx_conductor_id = i.xx_conductor_id
   LEFT JOIN xx_vehiculo veh ON veh.xx_vehiculo_id = i.xx_vehiculo_id
   LEFT JOIN c_invoicetax if ON if.c_invoice_id = i.c_invoice_id
   LEFT JOIN C_Order o ON o.C_Order_ID = i.C_Order_ID
   LEFT JOIN C_Invoice faf ON(faf.C_Invoice_ID = i.XX_C_Invoice_ID)
  GROUP BY i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, 
  i.updatedby, i.c_invoice_id, i.issotrx, i.documentno, i.docstatus, i.c_doctype_id, 
  i.c_bpartner_id, bp.value, bp.taxid, bp.naics, bp.duns, oi.c_location_id, oi.taxid, 
  dt.printname, dt.documentnote, i.c_order_id, i.salesrep_id, i.dateinvoiced, bpg.greeting, 
  bp.name, bp.name2, bpcg.greeting, bpc.title, bpl.phone, bpl.c_location_id, bp.referenceno, 
  i.description, i.poreference, i.dateordered, i.c_currency_id, pt.name, pt.documentnote, 
  i.c_charge_id, i.chargeamt, i.totallines, i.grandtotal, i.m_pricelist_id, i.istaxincluded, 
  i.c_campaign_id, i.c_project_id, i.c_activity_id, i.ispaid, i.bill_location_id, 
  cc.xx_convenio_cosecha_id, tr.m_shipper_id, con.xx_conductor_id, veh.xx_vehiculo_id, 
  i.xx_nrocontrol, ubp.name, u.name, bpc.name, l.postal, l.postal_add, oi.logo_id, ci.logo_id, 
  o.DocumentNo, dt.Name, faf.DocumentNo;
