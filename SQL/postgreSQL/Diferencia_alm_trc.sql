(SELECT 1000000, diff.AD_Org_ID, NULL, current_timestamp, 0, 'Y',
0, NULL, NULL, diff.M_Locator_ID, NULL, current_timestamp, Diferencia, 
diff.MvType, diff.M_Product_ID, NULL, nextval('AD_Pfi'), NULL, current_timestamp, 0
FROM (SELECT pro.Value, pro.M_Product_ID, pro.Name,sum(alm.QtyOnHand/* - alm.QtyReserved*/) Almacenado, 
trc.SumMovementQty Transaccion, alm.M_Locator_ID, 'I-' MvType, alm.AD_Org_ID,
sum(alm.QtyOnHand/* - alm.QtyReserved*/) - trc.SumMovementQty Diferencia
FROM M_Storage alm 
INNER JOIN M_Product pro ON(pro.M_Product_ID = alm.M_Product_ID)
INNER JOIN (SELECT sum(trc.MovementQty) SumMovementQty, trc.M_Product_ID,
trc.M_Locator_ID
FROM M_Transaction trc
GROUP BY trc.M_Locator_ID, trc.M_Product_ID
HAVING count(trc.MovementQty) > 1) trc ON(trc.M_Product_ID = pro.M_Product_ID)
WHERE pro.Value = '0302100012' 
AND trc.M_Locator_ID = alm.M_Locator_ID
GROUP BY alm.M_Locator_ID, pro.Value, Pro.Name, trc.SumMovementQty, 
trc.M_Locator_ID, alm.AD_Org_ID, MvType, pro.M_Product_ID
HAVING trc.SumMovementQty <> sum(alm.QtyOnHand/* - alm.QtyReserved*/)) diff)




INSERT INTO M_Transaction(AD_Client_ID, AD_Org_ID, C_ProjectIssue_ID, Created, CreatedBy, IsActive, M_AttributeSetInstance_ID, 
M_InOutLine_ID, M_InventoryLine_ID, M_Locator_ID, M_MovementLine_ID, MovementDate, MovementQty, 
MovementType, M_Product_ID, M_ProductionLine_ID, M_Transaction_ID, PP_Cost_Collector_ID, 
Updated, UpdatedBy) (SELECT 1000000, diff.AD_Org_ID, NULL, current_timestamp, 0, 'Y',
0, NULL, NULL, diff.M_Locator_ID, NULL, current_timestamp, Diferencia, 
diff.MvType, diff.M_Product_ID, NULL, nextval('AD_Pfi'), NULL, current_timestamp, 0
FROM (SELECT pro.Value, pro.M_Product_ID, pro.Name,sum(alm.QtyOnHand/* - alm.QtyReserved*/) Almacenado, 
trc.SumMovementQty Transaccion, alm.M_Locator_ID, 'I-' MvType, alm.AD_Org_ID,
sum(alm.QtyOnHand/* - alm.QtyReserved*/) - trc.SumMovementQty Diferencia
FROM M_Storage alm 
INNER JOIN M_Product pro ON(pro.M_Product_ID = alm.M_Product_ID)
INNER JOIN (SELECT sum(trc.MovementQty) SumMovementQty, trc.M_Product_ID,
trc.M_Locator_ID
FROM M_Transaction trc
GROUP BY trc.M_Locator_ID, trc.M_Product_ID
HAVING count(trc.MovementQty) > 1) trc ON(trc.M_Product_ID = pro.M_Product_ID)
WHERE /*pro.Value = '0302100012' 
AND */trc.M_Locator_ID = alm.M_Locator_ID
GROUP BY alm.M_Locator_ID, pro.Value, Pro.Name, trc.SumMovementQty, 
trc.M_Locator_ID, alm.AD_Org_ID, MvType, pro.M_Product_ID
HAVING trc.SumMovementQty <> sum(alm.QtyOnHand/* - alm.QtyReserved*/)) diff)


SELECT nextval('AD_Pfi')
SELECT * FROM M_Transaction
SELECT nextval('AD_Pfi')
SELECT * FROM M_Storage





SELECT pro.Value, pro.M_Product_ID, pro.Name,sum(alm.QtyOnHand/* - alm.QtyReserved*/) Almacenado, 
trc.SumMovementQty Transaccion, alm.M_Locator_ID, 'I-' MvType, trc.org,
sum(alm.QtyOnHand/* - alm.QtyReserved*/) - trc.SumMovementQty Diferencia
FROM M_Storage alm 
INNER JOIN M_Product pro ON(pro.M_Product_ID = alm.M_Product_ID)
INNER JOIN (SELECT sum(trc.MovementQty) SumMovementQty, trc.M_Product_ID,
trc.M_Locator_ID, trc.AD_Org_ID, trc.AD_Org_ID org
FROM M_Transaction trc
GROUP BY trc.M_Locator_ID, trc.M_Product_ID, trc.AD_Org_ID
HAVING count(trc.MovementQty) > 1) trc ON(trc.M_Product_ID = pro.M_Product_ID)
WHERE pro.Value = '0302100012' 
AND trc.M_Locator_ID = alm.M_Locator_ID
GROUP BY alm.M_Locator_ID, pro.Value, Pro.Name, trc.SumMovementQty, 
trc.M_Locator_ID, trc.org, MvType, pro.M_Product_ID
--HAVING trc.SumMovementQty <> sum(alm.QtyOnHand/* - alm.QtyReserved*/)



SELECT * /*sum(MovementQty)*/ FROM M_Transaction WHERE M_Product_ID = 1008173


UPDATE M_Transaction set MovementDate = '10/03/2011' 
WHERE M_Transaction_ID IN(SELECT M_Transaction_ID FROM M_Transaction WHERE UpdatedBy = 0 AND to_char(Updated, 'DD/MM/YYYY') = '31/05/2011')
