-- DROP VIEW XX_RV_Transf_Venezuela
CREATE OR REPLACE VIEW XX_RV_Transf_Venezuela AS
SELECT 'HEADER  '|| lpad(ltrim(to_char(sp.C_PaySelection_ID, '9999999999')),'8','0') || lpad(ltrim(to_char(sp.C_PaySelection_ID, '9999999999')),'8','0') || 
  'J300620808' || to_char(current_date, 'DD/MM/YYYY') || to_char(sp.PayDate, 'DD/MM/YYYY') encabezado,
  sp.AD_Client_ID, sp.AD_Org_ID, sp.C_PaySelection_ID, 0 orden, 0 orden1
  FROM C_PaySelection sp INNER JOIN AD_Org org ON(org.AD_Org_ID = sp.AD_Org_ID)
UNION ALL
SELECT 'DEBITO  ' || lpad(substring(trim(pa.DocumentNo), 1, 8),'8','0') || 'J300620808INSUMOS AGRICOLA CULTIVADORES ASOC '||
  to_char(sp.PayDate, 'DD/MM/YYYY') || 
  CASE WHEN cb.BankAccountType = 'C' THEN '00' ELSE '00' END ||
  rpad(rtrim(cb.AccountNo), 20, '0') || 
  lpad(ltrim(to_char(lsp.PayAmt, '9999999999999999999D99')),18,'0') ||
  'VEB40' encabezado, sp.AD_Client_ID, sp.AD_Org_ID, sp.C_PaySelection_ID, lsp.C_PaySelectionCheck_ID orden, 1 orden1
  FROM C_PaySelection sp 
  INNER JOIN C_BankAccount cb ON(cb.C_BankAccount_ID = sp.C_BankAccount_ID)
  INNER JOIN C_PaySelectionCheck lsp ON(sp.C_PaySelection_ID = lsp.C_PaySelection_ID)
  INNER JOIN C_Payment pa ON(pa.C_Payment_ID = lsp.C_Payment_ID)
  INNER JOIN AD_Org org ON(org.AD_Org_ID = sp.AD_Org_ID)
UNION ALL
SELECT 'CREDITO ' || lpad(substring(trim(pa.DocumentNo), 1, 8),'8','0') || 
  rpad(substring(coalesce(ccp.A_Ident_Dl, 'NO CI/RIF'), 1, 10), 10, ' ') ||
  rpad(substring(trim(cp.Name), 1, 30), 30, ' ') || '00' || 
  rpad(substring(trim(ccp.AccountNo), 1, 20), 20) || ltrim(to_char(abs(lsp.PayAmt), '000000000000000D99')) ||
  CASE WHEN substring(ban.SwiftCode, 1, 12) = 'BSCHVECA' THEN '10' ELSE '00' END || substring(ban.SwiftCode, 1, 12) encabezado, 
  sp.AD_Client_ID, sp.AD_Org_ID, sp.C_PaySelection_ID, lsp.C_PaySelectionCheck_ID orden, 2 orden1
  FROM C_PaySelection sp INNER JOIN C_BankAccount cb ON(cb.C_BankAccount_ID = sp.C_BankAccount_ID)
  INNER JOIN C_PaySelectionCheck lsp ON(sp.C_PaySelection_ID = lsp.C_PaySelection_ID)
  INNER JOIN C_Payment pa ON(pa.C_Payment_ID = lsp.C_Payment_ID)
  INNER JOIN C_BPartner cp ON(cp.C_BPartner_ID = lsp.C_BPartner_ID)
  INNER JOIN C_BP_BankAccount ccp ON(cp.C_BPartner_ID = ccp.C_BPartner_ID)
  INNER JOIN C_Bank ban ON(ban.C_Bank_ID = ccp.C_Bank_ID)
  INNER JOIN AD_Org org ON(org.AD_Org_ID = sp.AD_Org_ID)
  WHERE ccp.C_BP_BankAccount_ID = BPFirstAccountID(cp.C_BPartner_ID)
UNION ALL
SELECT 'TOTAL   ' || lpad(ltrim(to_char((SELECT count(*) FROM C_PaySelection clsp WHERE clsp.C_PaySelection_ID = sp.C_PaySelection_ID), '9999999999')),5,'0') ||
  lpad(ltrim(to_char((SELECT count(*) FROM C_PaySelection clsp WHERE clsp.C_PaySelection_ID = sp.C_PaySelection_ID), '9999999999')),5,'0') ||
  lpad(ltrim(to_char(sp.totalamt, '9999999999999999999D99')),18,'0') encabezado,
  sp.AD_Client_ID, sp.AD_Org_ID, sp.C_PaySelection_ID, 9999999999999 orden, 3 orden1
  FROM C_PaySelection sp INNER JOIN AD_Org org ON(org.AD_Org_ID = sp.AD_Org_ID)
ORDER BY orden, orden1