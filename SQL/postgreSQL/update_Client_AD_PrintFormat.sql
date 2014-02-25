SELECT * FROM AD_PrintFormat WHERE AD_PrintFormat_ID = 1000062;
UPDATE AD_PrintFormat SET AD_Client_ID = 1000000 WHERE AD_PrintFormat_ID = 1000045;
UPDATE AD_PrintFormatItem SET AD_Client_ID = 1000000 WHERE AD_PrintFormat_ID = 1000045;
UPDATE AD_PrintFormatItem_Trl SET AD_Client_ID = 1000000 WHERE AD_PrintFormatItem_ID IN (SELECT AD_PrintFormatItem_ID FROM AD_PrintFormat WHERE AD_PrintFormat_ID = 1000045);

UPDATE AD_PrintFormat SET AD_Client_ID = 1000000 WHERE AD_PrintFormat_ID = 104;
UPDATE AD_PrintFormatItem SET AD_Client_ID = 1000000 WHERE AD_PrintFormat_ID = 104;
UPDATE AD_PrintFormatItem_Trl SET AD_Client_ID = 1000000 WHERE AD_PrintFormatItem_ID IN (SELECT AD_PrintFormatItem_ID FROM AD_PrintFormat WHERE AD_PrintFormat_ID = 104);


SELECT AD_PrintFont_ID FROM AD_PrintFormatItem WHERE AD_PrintFormatItem_ID = 1002140;
UPDATE AD_PrintFormatItem SET AD_PrintFont_ID = 151 WHERE AD_PrintFormat_ID = 10;
UPDATE AD_PrintFormatItem SET AD_PrintFont_ID = 151 WHERE AD_PrintFormat_ID = 1000044;

UPDATE AD_PrintTableFormat SET AD_Client_ID = 1000000 WHERE AD_PrintTableFormat_ID = 101;


SELECT * FROM C_DocType WHERE C_DocType_ID = 0;
UPDATE C_DocType SET DocBaseType = '' WHERE C_DocType_ID = 0;

UPDATE AD_PrintFormat SET AD_Client_ID = 0 WHERE AD_PrintFormat_ID = 156;
UPDATE AD_PrintFormatItem SET AD_Client_ID = 0 WHERE AD_PrintFormat_ID = 156;
UPDATE AD_PrintFormatItem_Trl SET AD_Client_ID = 0 WHERE AD_PrintFormatItem_ID IN (SELECT AD_PrintFormatItem_ID FROM AD_PrintFormat WHERE AD_PrintFormat_ID = 156);

UPDATE AD_PrintFormat SET AD_Client_ID = 1000000 WHERE AD_PrintFormat_ID = 156;
UPDATE AD_PrintFormatItem SET AD_Client_ID = 1000000 WHERE AD_PrintFormat_ID = 156;
UPDATE AD_PrintFormatItem_Trl SET AD_Client_ID = 1000000 WHERE AD_PrintFormatItem_ID IN (SELECT AD_PrintFormatItem_ID FROM AD_PrintFormat WHERE AD_PrintFormat_ID = 156);



UPDATE AD_PrintFormat SET AD_Org_ID = 0
UPDATE AD_PrintFormatItem SET AD_Org_ID = 0


SELECT * FROM AD_PrintFormat

SELECT * FROM XX_Retencion

UPDATE XX_Retencion SET AD_Org_ID = 0
UPDATE XX_Retencion_IVA SET AD_Org_ID = 0

UPDATE AD_Process SET AD_PrintFormat_ID = 1000080 WHERE AD_Process_ID = 1000005

SELECT * FROM UPDATE AD_ImpFormat SET AD_Client_ID = 1000000 WHERE AD_ImpFormat_ID = 1000003

SELECT * FROM UPDATE AD_ImpFormat_Row SET AD_Client_ID = 1000000 WHERE AD_ImpFormat_ID = 1000003

DELETE FROM AD_PrintFormat WHERE AD_PrintFormat_ID = 156

SELECT * FROM AD_Process WHERE AD_PrintFormat_ID = 153

   