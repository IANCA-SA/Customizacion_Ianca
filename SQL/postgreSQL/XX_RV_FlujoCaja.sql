
SELECT SUM(CASE WHEN extract(Month From cb.StatementDate) = 1 THEN cb.StatementDifference ELSE 0 END) January,
SUM(CASE WHEN extract(Month From cb.StatementDate) = 2 THEN cb.StatementDifference ELSE 0 END) February,
SUM(CASE WHEN extract(Month From cb.StatementDate) = 3 THEN cb.StatementDifference ELSE 0 END) March,
SUM(CASE WHEN extract(Month From cb.StatementDate) = 4 THEN cb.StatementDifference ELSE 0 END) April,
SUM(CASE WHEN extract(Month From cb.StatementDate) = 5 THEN cb.StatementDifference ELSE 0 END) May,
SUM(CASE WHEN extract(Month From cb.StatementDate) = 6 THEN cb.StatementDifference ELSE 0 END) June,
SUM(CASE WHEN extract(Month From cb.StatementDate) = 7 THEN cb.StatementDifference ELSE 0 END) July,
SUM(CASE WHEN extract(Month From cb.StatementDate) = 8 THEN cb.StatementDifference ELSE 0 END) August,
SUM(CASE WHEN extract(Month From cb.StatementDate) = 9 THEN cb.StatementDifference ELSE 0 END) September,
SUM(CASE WHEN extract(Month From cb.StatementDate) = 10 THEN cb.StatementDifference ELSE 0 END) October,
SUM(CASE WHEN extract(Month From cb.StatementDate) = 11 THEN cb.StatementDifference ELSE 0 END) November,
SUM(CASE WHEN extract(Month From cb.StatementDate) = 12 THEN cb.StatementDifference ELSE 0 END) December,
cb.AD_Client_ID, 0 As AD_Org_ID, 'Saldo Conciliado' AS Name, 0 As SeqNo
FROM C_BankStatement cb
WHERE cb.DocStatus IN('CO', 'CO') 
AND cb.AD_Client_ID = 1000000
GROUP BY cb.AD_Client_ID
UNION ALL
SELECT SUM(CASE WHEN extract(Month From fac.DateAcct) = 1 THEN fac.GrandTotal ELSE 0 END) January,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 2 THEN fac.GrandTotal ELSE 0 END) February,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 3 THEN fac.GrandTotal ELSE 0 END) March,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 4 THEN fac.GrandTotal ELSE 0 END) April,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 5 THEN fac.GrandTotal ELSE 0 END) May,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 6 THEN fac.GrandTotal ELSE 0 END) June,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 7 THEN fac.GrandTotal ELSE 0 END) July,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 8 THEN fac.GrandTotal ELSE 0 END) August,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 9 THEN fac.GrandTotal ELSE 0 END) September,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 10 THEN fac.GrandTotal ELSE 0 END) October,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 11 THEN fac.GrandTotal ELSE 0 END) November,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 12 THEN fac.GrandTotal ELSE 0 END) December,
fac.AD_Client_ID,  0 As AD_Org_ID, 'CxC Clientes' AS Name, 1 As SeqNo
FROM C_Invoice fac
WHERE fac.IsSOTrx = 'Y' AND fac.DocStatus IN('CO', 'CO') 
AND fac.AD_Client_ID = 1000000
GROUP BY fac.AD_Client_ID
UNION ALL
SELECT SUM(CASE WHEN extract(Month From fac.DateAcct) = 1 THEN fac.GrandTotal *-1 ELSE 0 END) January,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 2 THEN fac.GrandTotal *-1 ELSE 0 END) February,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 3 THEN fac.GrandTotal *-1 ELSE 0 END) March,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 4 THEN fac.GrandTotal *-1 ELSE 0 END) April,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 5 THEN fac.GrandTotal *-1 ELSE 0 END) May,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 6 THEN fac.GrandTotal *-1 ELSE 0 END) June,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 7 THEN fac.GrandTotal *-1 ELSE 0 END) July,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 8 THEN fac.GrandTotal *-1 ELSE 0 END) August,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 9 THEN fac.GrandTotal *-1 ELSE 0 END) September,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 10 THEN fac.GrandTotal *-1 ELSE 0 END) October,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 11 THEN fac.GrandTotal *-1 ELSE 0 END) November,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 12 THEN fac.GrandTotal *-1 ELSE 0 END) December,
fac.AD_Client_ID, 0 As AD_Org_ID, 'CxP Proveedores' AS Name, 2 As SeqNo
FROM C_Invoice fac
WHERE fac.IsSOTrx = 'N' AND fac.DocStatus IN('CO', 'CO') 
AND fac.AD_Client_ID = 1000000
GROUP BY fac.AD_Client_ID