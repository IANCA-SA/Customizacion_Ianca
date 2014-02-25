CREATE OR REPLACE VIEW XX_RV_OrderTax AS
SELECT ot.ad_client_id, ot.ad_org_id, ot.c_order_id, ot.c_tax_id, t.NAME,
  SUM (ot.taxbaseamt) taxbaseamt, SUM (ot.taxamt) taxamt 
  FROM c_ordertax ot INNER JOIN c_tax t ON(ot.c_tax_id = t.c_tax_id)
  WHERE ot.taxamt <> 0
  GROUP BY ot.ad_client_id, ot.ad_org_id, ot.c_order_id, ot.c_tax_id, t.NAME