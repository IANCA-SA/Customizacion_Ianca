SELECT ot.ad_client_id, ot.ad_org_id, ot.c_order_id, ot.c_tax_id, t.NAME, SUM (ot.taxbaseamt) taxbaseamt, SUM (ot.taxamt) taxamt
       FROM c_ordertax ot, c_tax t
      WHERE ot.c_tax_id = t.c_tax_id AND ot.taxamt <> 0
   GROUP BY ot.ad_client_id, ot.ad_org_id, ot.c_order_id, ot.c_tax_id, t.NAME


   SELECT * FROM AD_PrintFormat WHERE AD_PrintFormat_ID = 1000004;
   DELETE FROM AD_PrintFormat WHERE AD_PrintFormat_ID = 1000004;
   DELETE FROM AD_PrintFormatItem WHERE AD_PrintFormat_ID = 1000004;

   SELECT * FROM AD_PrintForm WHERE Shipment_PrintFormat_ID = 104;

   INSERT INTO AD_PrintForm VALUES(102, 1000000, 0, 'Y', current_timestamp, 0, current_timestamp, 0, 'Prueba', '',null,null,null, 104, null,null,null,null,null,null,null,null,null,null);

   UPDATE AD_PrintFormat SET AD_Org_ID = 0 WHERE AD_PrintFormat_ID = 1000058;


   SELECT * FROM AD_PrintForm WHERE Order_PrintFormat_ID = 100;