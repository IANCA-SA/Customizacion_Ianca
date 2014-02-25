SELECT * FROM C_Bank;
SELECT * FROM C_BankAccount;

UPDATE C_Bank SET AD_Org_ID = 0;
UPDATE C_BankAccount SET AD_Org_ID = 0;
UPDATE C_BankAccount_Acct SET AD_Org_ID = 0;