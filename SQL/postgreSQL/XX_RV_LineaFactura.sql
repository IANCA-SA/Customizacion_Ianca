--DROP VIEW XX_RV_LineaFactura
CREATE OR REPLACE VIEW XX_RV_LineaFactura AS 
 SELECT il.ad_client_id, il.ad_org_id, il.c_invoiceline_id, i.c_invoice_id, i.salesrep_id, i.c_bpartner_id, 
        CASE
            WHEN il.m_product_id IS NULL THEN car.Name
            ELSE pr.Name
        END AS NombreProducto, 
        i.documentno, i.dateinvoiced, i.dateacct, i.issotrx, i.docstatus, round(i.multiplier * il.linenetamt, 2) AS linenetamt, 
        round(i.multiplier * il.pricelist * il.qtyinvoiced, 2) AS linelistamt, 
        CASE
            WHEN COALESCE(il.pricelimit, 0::numeric) = 0::numeric THEN round(i.multiplier * il.linenetamt, 2)
            ELSE round(i.multiplier * il.pricelimit * il.qtyinvoiced, 2)
        END AS linelimitamt, round(i.multiplier * il.pricelist * il.qtyinvoiced - il.linenetamt, 2) AS linediscountamt, 
        CASE
            WHEN COALESCE(il.pricelimit, 0::numeric) = 0::numeric THEN 0::numeric
            ELSE round(i.multiplier * il.linenetamt - il.pricelimit * il.qtyinvoiced, 2)
        END AS lineoverlimitamt, il.qtyinvoiced, il.qtyentered, il.line, il.c_orderline_id, 
        il.c_uom_id, il.c_campaign_id, il.c_project_id, il.c_activity_id, il.c_projectphase_id, 
        il.c_projecttask_id, il.priceEntered, 
        CASE WHEN iI.IsTaxExempt = 'Y' THEN '"E"' ELSE '' END LetraImp
   FROM c_invoice_v i 
   INNER JOIN c_invoiceline il ON(i.C_Invoice_ID = il.C_Invoice_ID)
   INNER JOIN C_Tax ii ON(ii.C_Tax_ID = il.C_Tax_ID)
   LEFT JOIN M_Product pr ON(pr.M_Product_ID = il.M_Product_ID)
   LEFT JOIN C_Charge car ON(car.C_Charge_ID = il.C_Charge_ID)
