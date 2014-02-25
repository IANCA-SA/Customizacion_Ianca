1000045, 1000070
INSERT INTO AD_PrintFormatItem  WHERE AD_PrintFormat_ID = 1000045;
UPDATE AD_PrintFormatItem_Trl SET AD_Client_ID = 1000000 WHERE AD_PrintFormatItem_ID IN (SELECT AD_PrintFormatItem_ID FROM AD_PrintFormat WHERE AD_PrintFormat_ID = 1000045);


SELECT * FROM AD_PrintFormatItem WHERE AD_PrintFormat_ID = 1000045;

INSERT INTO M_InventoryLine(M_InventoryLine_ID, M_Inventory_ID, AD_Client_ID, AD_Org_ID, M_Locator_ID, M_Product_ID, line, qtybook, qtycount, createdby, updatedby)
  SELECT nextval('id'), 1000003, 1000000, 1000002, 
  1000001, pro.M_Product_ID, nextval('linea'), 1, 1000, 100, 100 FROM M_Product pro WHERE pro.M_Product_Category_ID = 1000074;


INSERT INTO AD_PrintFormatItem(AD_PrintFormat_ID, AD_PrintFormatItem_ID, AD_Client_ID, AD_Org_ID, CreatedBy, UpdatedBy, Name, PrintName, IsPrinted, PrintAreaType, SeqNo, 
  PrintFormatType, AD_Column_ID, AD_PrintFormatChild_ID, IsRelativePosition, IsNextLine, Xspace, Yspace, Xposition, Yposition, maxWidth, IsHeightOneLine, MaxHeight,
  FieldAlignMentType, LineAlignMentType, AD_PrintFont_ID, ImageUrl, IsSetnlPosition, IsSupPressNull, RunningTotalLines, LineWidth, ShapeType, SortNo)

SELECT 1000196, nextval('AD_Pfi'), AD_Client_ID, AD_Org_ID, CreatedBy, UpdatedBy, Name, PrintName, IsPrinted, PrintAreaType, SeqNo, 
  PrintFormatType, AD_Column_ID, AD_PrintFormatChild_ID, IsRelativePosition, IsNextLine, Xspace, Yspace, Xposition, Yposition, maxWidth, IsHeightOneLine, MaxHeight,
  FieldAlignMentType, LineAlignMentType, AD_PrintFont_ID, ImageUrl, IsSetnlPosition, IsSupPressNull, RunningTotalLines, LineWidth, ShapeType, SortNo
  FROM AD_PrintFormatItem WHERE AD_PrintFormat_ID = 1000194;

SELECT MAX(AD_PrintFormatItem_ID) FROM AD_PrintFormatItem

DROP SEQUENCE AD_Pfi
CREATE SEQUENCE AD_Pfi START 1007907;
