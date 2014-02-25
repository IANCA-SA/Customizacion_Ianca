SELECT * FROM M_Storage WHERE M_Product_ID=1008329

SELECT * FROM C_OrderLine WHERE M_Product_ID=1008271

SELECT * FROM C_InvoiceLine WHERE M_Product_ID=1008271

SELECT * FROM M_InventoryLine WHERE M_Product_ID=1008271

SELECT * FROM C_OrderLine WHERE M_Product_ID=1008271

SELECT * FROM M_ProductPrice WHERE M_Product_ID=1008271


SELECT 'INSERT INTO M_ProductPrice VALUES(' || M_PriceList_Version_ID||',1008271,'||AD_Client_ID||','||AD_Org_ID||','||chr(39)||'Y'||chr(39)||','||chr(39)||created||chr(39)||','||createdby||','||chr(39)||updated||chr(39)||','||updatedby||','||pricelist||','||pricestd||','||pricelimit||')' FROM M_ProductPrice WHERE M_Product_ID=1008329