--DROP VIEW XX_RV_LineaOrden
CREATE OR REPLACE VIEW XX_RV_LineaOrden AS 
 SELECT lord.ad_client_id, lord.ad_org_id, lord.C_OrderLine_ID, ord.C_Order_ID, ord.salesrep_id, ord.c_bpartner_id, 
        CASE
            WHEN lord.m_product_id IS NULL THEN car.Name
            ELSE pr.Name
        END AS NombreProducto, 
        lord.linenetamt, 
        lord.pricelist * lord.qtyinvoiced AS linelistamt, 
        lord.qtyentered, lord.line, lord.c_uom_id, lord.c_campaign_id, lord.c_project_id, 
        lord.c_activity_id, lord.c_projectphase_id, lord.c_projecttask_id,
        lord.datepromised, lord.PriceEntered, 
        CASE WHEN io.IsTaxExempt = 'Y' THEN '"E"' ELSE '' END LetraImp
   FROM C_Order ord 
   INNER JOIN C_OrderLine lord ON(ord.C_Order_ID = lord.C_Order_ID)
   INNER JOIN C_Tax io ON(io.C_Tax_ID = lord.C_Tax_ID)
   LEFT JOIN M_Product pr ON(pr.M_Product_ID = lord.M_Product_ID)
   LEFT JOIN C_Charge car ON(car.C_Charge_ID = lord.C_Charge_ID)