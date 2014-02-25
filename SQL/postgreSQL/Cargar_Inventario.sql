Select * From AD_User;
DROP SEQUENCE id;
DROP SEQUENCE linea;
CREATE SEQUENCE id START 1000022;

CREATE SEQUENCE linea START 10;

select * from M_InventoryLine where M_InventoryLine_ID = 1000008

SELECT * FROM M_Product_Category

(SELECT pro.M_Product_ID, pro.name FROM M_Product pro WHERE pro.M_Product_Category_ID = 1000074)

select max(m_inventoryline_id) from m_inventoryline

SELECT pro.M_Product_ID FROM M_Product pro WHERE pro.M_Product_Category_ID = 1000074 

/*Agroquimicos*/
INSERT INTO M_InventoryLine(M_InventoryLine_ID, M_Inventory_ID, AD_Client_ID, AD_Org_ID, M_Locator_ID, M_Product_ID, line, qtybook, qtycount, createdby, updatedby)
  SELECT nextval('id'), 1000003, 1000000, 1000002, 
  1000001, pro.M_Product_ID, nextval('linea'), 1, 1000, 100, 100 FROM M_Product pro WHERE pro.M_Product_Category_ID = 1000074;

/*Fertilizante*/
INSERT INTO M_InventoryLine(M_InventoryLine_ID, M_Inventory_ID, AD_Client_ID, AD_Org_ID, M_Locator_ID, M_Product_ID, line, qtybook, qtycount, createdby, updatedby)
  SELECT nextval('id'), 1000008, 1000000, 1000002, 
  1000013, pro.M_Product_ID, nextval('linea'), 1, 1000, 100, 100 FROM M_Product pro WHERE pro.M_Product_Category_ID = 1000069;

/*Semilla*/
INSERT INTO M_InventoryLine(M_InventoryLine_ID, M_Inventory_ID, AD_Client_ID, AD_Org_ID, M_Locator_ID, M_Product_ID, line, qtybook, qtycount, createdby, updatedby)
  SELECT nextval('id'), 1000009, 1000000, 1000002, 
  1000014, pro.M_Product_ID, nextval('linea'), 1, 1000, 100, 100 FROM M_Product pro WHERE pro.M_Product_Category_ID = 1000070;