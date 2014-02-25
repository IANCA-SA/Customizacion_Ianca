SELECT pr.M_Product_ID, pr.Name, /*COUNT(al.M_Product_ID),*/ lo.M_Locator_ID, lo.Name, al.QtyOnHand, al.QtyReserved, al.QtyOrdered 
  FROM M_Product pr 
  LEFT JOIN M_Locator lo ON(pr.M_Locator_ID = lo.M_Locator_ID)
  LEFT JOIN M_Storage al ON(pr.M_Product_ID = al.M_Product_ID)
  GROUP BY pr.M_Product_ID, pr.Name, lo.M_Locator_ID, lo.Name, al.QtyOnHand, al.QtyReserved, al.QtyOrdered 
  HAVING COUNT(al.M_Product_ID) > 1


DELETE FROM TRUNCATE TABLE M_Storage

DELETE FROM M_Storage WHERE 

  SELECT * DELETE FROM I_Inventory WHERE LocatorValue = 'Asoportuguesa'

SELECT * FROM I_Inventory

UPDATE I_Inventory SET MovementDate = '01/04/2011'

  DELETE FROM M_Storage WHERE QtyOnHand = 0

  SELECT * FROM RV_Storage WHERE m_product_id = 1008152 and m_locator_id = 1000004




	SELECT * FROM UPDATE AD_Process SET AD_ReportView_ID=1000012 WHERE AD_Process_ID=236

SELECT * FROM AD_Process  WHERE AD_Process_ID=236
	


  SELECT * FROM M_Storage WHERE M_Product_ID = 1008189 AND M_AttributeSetInstance_ID = 1001278
  SELECT * FROM M_Locator

  select * from m_attributesetinstance

  select * from M_InventoryLine where M_InventoryLine_ID=1002050






SELECT * FROM M_Product WHERE M_Product_ID = 1008287

1008190  1008367  1008287  


 SELECT * FROM M_Storage WHERE M_Product_ID = 1008287 AND M_Locator_ID = 1000001 AND M_AttributeSetInstance_ID = 1000963

  SELECT * FROM UPDATE M_Storage SET QtyReserved = 20 WHERE M_Product_ID = 1008287 AND M_Locator_ID = 1000001 AND M_AttributeSetInstance_ID = 1000963