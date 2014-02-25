-- DROP VIEW XX_RV_Seg_Mercantil
--CREATE OR REPLACE VIEW XX_RV_Seg_Mercantil AS
SELECT 1 /*falta el Nº de lote*/|| to_char(current_timestamp, 'DDMMYYYYHHMMSS') ||
  'J300620808'


  (SELECT count(*) FROM C_Payment pa 
    INNER JOIN C_DocType tdoc ON(tdoc.C_DocType_ID = pa.C_DocType_ID)
    WHERE pa.DateAcct = current_date AND pa.TenderType = 'K' AND pa.DocStatus = 'CO' AND tdoc.IsSOTrx = 'N'
    AND pa.AD_Client_ID = 1000000)


/*UNION ALL
SELECT rpad(substring(cp.Name, 1, 60), 60) ||
  lpad(trim(to_char(lsp.PayAmt*100, '000000000000')), 12) ||
  rpad(substring(fn_get_descrip_pago(p.C_Payment_ID), 1, 120), 120) ||
  rpad(substring(ccp.AccountNo, 2, 3), 3) ||
  rpad(coalesce(substring(ccp.AccountNo, 1, 20),' '), 20) ||
  rpad(coalesce(substring(ccp.A_Email,1,60),'finanzas@mary-iancarina.com'),60) ||
  lpad(substring(trim(p.DocumentNo), 1, 8), 8, '0') || ccp.A_Ident_DL encabezado, sp.AD_Client_ID, sp.AD_Org_ID, 
  sp.C_PaySelection_ID, lsp.C_PaySelectionCheck_ID orden, 1 orden1
FROM C_PaySelection sp INNER JOIN C_BankAccount cb ON(cb.C_BankAccount_ID = sp.C_BankAccount_ID)
  INNER JOIN C_PaySelectionCheck lsp ON(sp.C_PaySelection_ID = lsp.C_PaySelection_ID)
  INNER JOIN C_BPartner cp ON(cp.C_BPartner_ID = lsp.C_BPartner_ID)
  INNER JOIN C_BP_BankAccount ccp ON(cp.C_BPartner_ID = ccp.C_BPartner_ID)
  INNER JOIN C_Payment p ON(p.C_Payment_ID = lsp.C_Payment_ID)
  WHERE ccp.C_BP_BankAccount_ID = BPFirstAccountID(cp.C_BPartner_ID)
ORDER BY orden1*/