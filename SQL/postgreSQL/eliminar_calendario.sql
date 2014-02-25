UPDATE C_OrderLine SET C_Tax_ID = 1000450 WHERE C_Tax_ID = 1000035
UPDATE C_InvoiceLine SET C_Tax_ID = 1000450 WHERE C_Tax_ID = 1000035

UPDATE C_InvoiceLine SET C_Tax_ID = 1000450 WHERE C_InvoiceLine_ID = 1000007

SELECT * FROM M_Product WHERE C_TaxCategory_ID = 1000000

UPDATE M_Product SET C_TaxCategory_ID = 1000025 WHERE C_TaxCategory_ID = 1000000


UPDATE C_InvoiceLine SET C_Tax_ID = 1000450 WHERE C_InvoiceLine_ID = 1000017

SELECT * FROM C_Invoice WHERE CreatedBy = 100 AND AD_Client_ID = 1000000 AND IssoTrx = 'Y'

DELETE FROM C_Invoice WHERE CreatedBy = 100 AND AD_Client_ID = 1000000 AND IssoTrx = 'Y'

UPDATE C_Invoice SET C_Activity_ID = null WHERE CreatedBy = 100 AND AD_Client_ID = 1000000 AND IssoTrx = 'Y'

SELECT * FROM AD_User

SELECT C_Activity_ID FROM C_Invoice WHERE C_Invoice_ID = 1000023

SELECT * FROM C_Invoice

DELETE FROM C_Calendar WHERE C_Calendar_ID = 1000000;

DELETE FROM C_Year WHERE C_Calendar_ID = 1000002;

SELECT * FROM C_Year WHERE C_Calendar_ID = 1000000;

SELECT C_Period_ID FROM C_Period WHERE C_Year_ID = 1000000;

DELETE FROM C_Period WHERE C_Year_ID IN(1000004, 1000005, 1000006, 1000007, 1000008);

DELETE FROM C_PeriodControl WHERE C_Period_ID IN(SELECT C_Period_ID FROM C_Period WHERE C_Year_ID IN(1000004, 1000005, 1000006, 1000007, 1000008));

SELECT * FROM C_Order

DELETE FROM fact_acct;

SELECT * FROM M_DemandLine;


SELECT * FROM C_AcctSchema WHERE C_AcctSchema_ID = 1000000;

UPDATE C_AcctSchema SET C_Period_ID = null WHERE C_AcctSchema_ID = 1000000;

SELECT * FROM AD_ClientInfo;

SELECT * FROM C_PeriodControl WHERE C_Period_ID = 1000048;

UPDATE C_PeriodControl SET periodaction = 'O' WHERE C_Period_ID IN(SELECT C_Period_ID FROM C_Period WHERE C_Year_ID IN(1000009, 1000010));


UPDATE C_Calendar SET AD_Org_ID = 0 
select * from C_Calendar


SELECT * FROM C_acctschema_element

SELECT pro.M_Product_ID FROM M_Product pro WHERE pro.M_Product_ID NOT IN (SELECT cos.M_Product_ID FROM M_Cost cos)




UPDATE 

