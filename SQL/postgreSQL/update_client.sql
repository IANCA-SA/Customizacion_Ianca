UPDATE AD_PrintFormatItem_Trl SET AD_Client_ID = 0, AD_Org_ID = 0;
UPDATE AD_PrintForm SET AD_Client_ID = 0, AD_Org_ID = 0;
UPDATE AD_PrintPaper SET AD_Client_ID = 0, AD_Org_ID = 0;
UPDATE AD_PrintFont SET AD_Client_ID = 0, AD_Org_ID = 0;
UPDATE AD_Table SET AD_Client_ID = 0, AD_Org_ID = 0;
UPDATE AD_Column SET AD_Client_ID = 0, AD_Org_ID = 0;
UPDATE AD_PrintTableFormat SET AD_Client_ID = 0, AD_Org_ID = 0;
SELECT Drop_Client('adempiere', 1000000);