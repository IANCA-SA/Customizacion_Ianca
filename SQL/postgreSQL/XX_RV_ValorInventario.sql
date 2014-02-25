--DROP VIEW XX_RV_ValorInventario

CREATE OR REPLACE VIEW XX_RV_ValorInventario AS
SELECT iv.AD_Client_ID, iv.AD_Org_ID, iv.AD_PInstance_ID, iv.C_Currency_ID, 
  iv.Cost, iv.CostAmt, iv.CostStandard, iv.CostStandardAmt, iv.DateValue, 
  iv.M_AttributeSetInstance_ID, iv.M_CostElement_ID, iv.M_PriceList_Version_ID, 
  iv.M_Product_ID, iv.M_Warehouse_ID, iv.PriceLimit, iv.PriceLimitAmt, iv.PriceList, 
  iv.PriceListAmt, iv.PricePO, iv.PricePOAmt, iv.PriceStd, iv.PriceStdAmt, iv.QtyOnHand,
  pr.M_Product_Category_ID, sp.YY_Sub_Categoria_ID
  FROM T_InventoryValue iv
  INNER JOIN M_Product pr ON(iv.M_Product_ID = pr.M_Product_ID)
  INNER JOIN M_Product_Category cp ON(cp.M_Product_Category_ID = pr.M_Product_Category_ID)
  INNER JOIN YY_Sub_Categoria sp ON(sp.YY_Sub_Categoria_ID = pr.YY_Sub_Categoria_ID)