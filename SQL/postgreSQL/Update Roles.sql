UPDATE AD_Window_Access SET IsActive = 'N' WHERE AD_Role_ID = 1000019;
UPDATE AD_Process_Access SET IsActive = 'N' WHERE AD_Role_ID = 1000019;
UPDATE AD_Form_Access SET IsActive = 'N' WHERE AD_Role_ID = 1000019;


SELECT * FROM AD_Window_Access WHERE AD_Role_ID = 1000018