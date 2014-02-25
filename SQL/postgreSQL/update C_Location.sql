SELECT Address1, Address2, Address3, Address4 FROM C_Location;
UPDATE C_Location SET Address1 = trim(Address1) || trim(Address2) || trim(Address3) || trim(Address4) WHERE AD_Client_ID = 1000000;

SELECT Address1, Address2, Address3, Address4 FROM C_Location WHERE AD_Client_ID = 1000000 ORDER BY Address4;

SELECT CASE WHEN trim(Address1) IS NULL THEN '*****' ELSE Address1 END 
   || CASE WHEN trim(Address2) IS NULL THEN '********' ELSE trim(Address2) END 
   || CASE WHEN trim(Address3) IS NULL THEN '**********' ELSE trim(Address3) END 
   || CASE WHEN trim(Address4) IS NULL THEN '***************' ELSE trim(Address4) END 
   FROM C_Location WHERE AD_Client_ID = 1000000;

SELECT * FROM C_Location WHERE trim(Address4) = 'E' OR trim(Address4) = 'F'
UPDATE C_Location SET Address4 = null WHERE trim(Address4) = 'E' OR trim(Address4) = 'F';

SELECT CASE WHEN trim(Address1) IS NULL THEN '' ELSE Address1 END 
   || CASE WHEN Address2 IS NULL THEN '' ELSE Address2 END 
   || CASE WHEN Address3 IS NULL THEN '' ELSE Address3 END 
   || CASE WHEN Address4 IS NULL THEN '' ELSE Address4 END 
   FROM C_Location WHERE AD_Client_ID = 1000000;


SELECT CASE WHEN trim(Address1) IS NULL THEN '' ELSE Address1 || ' ' END 
   || CASE WHEN Address4 IS NULL THEN '' ELSE (CASE WHEN trim(Address1) = trim(Address4) THEN '' ELSE Address4 END)|| ' 'END 
   FROM C_Location WHERE AD_Client_ID = 1000000 AND trim(Address4) IS NOT NULL;

UPDATE C_Location SET Address1 = CASE WHEN trim(Address1) IS NULL THEN '' ELSE Address1 || ' ' END 
   || CASE WHEN Address4 IS NULL THEN '' ELSE (CASE WHEN trim(Address1) = trim(Address4) THEN '' ELSE Address4 END)|| ' 'END 
   WHERE AD_Client_ID = 1000000 AND trim(Address4) IS NOT NULL;

|| CASE WHEN Address3 IS NULL THEN '' ELSE (CASE WHEN trim(Address2) = trim(Address3) THEN '' WHEN trim(Address1) = trim(Address3) THEN '' ELSE Address2 END)|| ' ' END 


UPDATE C_Location SET Address1 = CASE WHEN trim(Address1) IS NULL THEN '' ELSE Address1 || ' ' END 
   || CASE WHEN Address2 IS NULL THEN '' ELSE (CASE WHEN trim(Address1) = trim(Address2) THEN '' ELSE Address2 END)|| ' 'END 
   WHERE AD_Client_ID = 1000000;
   

UPDATE C_Location SET Address2 = null, Address3 = null WHERE AD_Client_ID = 1000000;



SELECT * FROM AD_Process WHERE AD_Process_ID=1000003;

UPDATE AD_Process SET AD_PrintFormat_ID = 1000045 WHERE AD_Process_ID=1000003;