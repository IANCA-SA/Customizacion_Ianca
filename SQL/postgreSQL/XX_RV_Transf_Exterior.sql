-- DROP VIEW XX_RV_Transf_Exterior
CREATE OR REPLACE VIEW XX_RV_Transf_Exterior AS
SELECT 'J300620808' || rpad(rtrim(substring(cb.AccountNo, 1, 20)), 20) ||
  ltrim(to_char((SELECT COUNT (*) FROM C_PaySelectionCheck scp 
  WHERE scp.C_PaySelection_ID = sp.C_PaySelection_ID),'0000')) ||
  lpad(trim(to_char(sum(lsp.PayAmt)*100, '0000000000000')), 13) ||
  to_char(sp.PayDate, 'ddmmyyyy') || '01' encabezado, sp.AD_Client_ID, sp.AD_Org_ID, 
  sp.C_PaySelection_ID, lsp.C_PaySelection_ID orden, 0 orden1
FROM C_PaySelection sp INNER JOIN C_BankAccount cb ON(cb.C_BankAccount_ID = sp.C_BankAccount_ID)
  INNER JOIN C_PaySelectionCheck lsp ON(sp.C_PaySelection_ID = lsp.C_PaySelection_ID)
  INNER JOIN C_BPartner cp ON(cp.C_BPartner_ID = lsp.C_BPartner_ID)
  INNER JOIN C_BP_BankAccount ccp ON(cp.C_BPartner_ID = ccp.C_BPartner_ID)
  WHERE ccp.C_BP_BankAccount_ID = BPFirstAccountID(cp.C_BPartner_ID)
  GROUP BY sp.C_PaySelection_ID, cb.AccountNo, 
  sp.PayDate, sp.AD_Client_ID, sp.AD_Org_ID, 
  lsp.C_PaySelection_ID
UNION ALL
SELECT rpad(substring(cp.Name, 1, 60), 60) ||
  lpad(trim(to_char(lsp.PayAmt*100, '000000000000')), 12) ||
  rpad(substring(fn_get_descrip_pago(p.C_Payment_ID), 1, 120), 120) ||
  rpad(substring(ccp.AccountNo, 2, 3), 3) ||
  rpad(coalesce(substring(ccp.AccountNo, 1, 20),' '), 20) ||
  rpad(coalesce(substring(ccp.A_Email,1,60),'finanzas@mary-iancarina.com'),60) ||
  lpad(substring(trim(p.DocumentNo), 1, 8), 8, '0') || 
  substring(ccp.A_Ident_DL, 1, 1) || 
  lpad(substring(ccp.A_Ident_DL, 2, length(ccp.A_Ident_DL)-1),9, '0') encabezado, sp.AD_Client_ID, sp.AD_Org_ID, 
  sp.C_PaySelection_ID, lsp.C_PaySelectionCheck_ID orden, 1 orden1
FROM C_PaySelection sp INNER JOIN C_BankAccount cb ON(cb.C_BankAccount_ID = sp.C_BankAccount_ID)
  INNER JOIN C_PaySelectionCheck lsp ON(sp.C_PaySelection_ID = lsp.C_PaySelection_ID)
  INNER JOIN C_BPartner cp ON(cp.C_BPartner_ID = lsp.C_BPartner_ID)
  INNER JOIN C_BP_BankAccount ccp ON(cp.C_BPartner_ID = ccp.C_BPartner_ID)
  INNER JOIN C_Payment p ON(p.C_Payment_ID = lsp.C_Payment_ID)
  WHERE ccp.C_BP_BankAccount_ID = BPFirstAccountID(cp.C_BPartner_ID)
ORDER BY orden1