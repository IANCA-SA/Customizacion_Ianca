CREATE OR REPLACE VIEW XX_RV_SaldoProducto AS 
 SELECT s.ad_client_id, s.ad_org_id, s.m_product_id, p.value, p.name, p.description, p.upc, p.sku, p.c_uom_id, p.m_product_category_id, 
 p.classification, p.weight, p.volume, p.versionno, p.guaranteedays, p.guaranteedaysmin, s.m_locator_id, l.m_warehouse_id, l.x, l.y, l.z, 
 SUM(s.qtyonhand) qtyonhand, SUM(s.qtyreserved) qtyreserved, SUM(s.qtyonhand - s.qtyreserved) AS qtyavailable, SUM(s.qtyordered) qtyordered,
 asi.serno, asi.lot, asi.m_lot_id, asi.guaranteedate, daysbetween(asi.guaranteedate::timestamp with time zone, getdate()) AS shelflifedays, 
 daysbetween(asi.guaranteedate::timestamp with time zone, getdate())::numeric - p.guaranteedaysmin AS goodfordays, 
        CASE
            WHEN COALESCE(p.guaranteedays, 0::numeric) > 0::numeric THEN round(daysbetween(asi.guaranteedate::timestamp with time zone, getdate())::numeric / p.guaranteedays * 100::numeric, 0)
            ELSE NULL::numeric
        END AS shelfliferemainingpct, p.yy_sub_categoria_id
   FROM m_storage s
   JOIN m_locator l ON s.m_locator_id = l.m_locator_id
   JOIN m_product p ON s.m_product_id = p.m_product_id
   LEFT JOIN m_attributesetinstance asi ON s.m_attributesetinstance_id = asi.m_attributesetinstance_id
   GROUP BY s.ad_client_id, s.ad_org_id, s.m_product_id, p.value, p.name, p.description, p.upc, p.sku, p.c_uom_id, p.m_product_category_id, 
 p.classification, p.weight, p.volume, p.versionno, p.guaranteedays, p.guaranteedaysmin, s.m_locator_id, l.m_warehouse_id, l.x, l.y, l.z, 
 asi.serno, asi.lot, asi.m_lot_id, asi.guaranteedate,
 p.yy_sub_categoria_id