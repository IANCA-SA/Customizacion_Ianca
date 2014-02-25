INSERT INTO C_InvoiceTax (C_Tax_ID, C_Invoice_ID, AD_Client_ID, AD_Org_ID,
  IsActive, Created, CreatedBy, Updated, UpdatedBy, TaxBaseAmt,
  TaxAmt, Processed, IsTaxIncluded)
  (SELECT lf.C_Tax_ID, lf.C_Invoice_ID, lf.AD_Client_ID, lf.AD_Org_ID, lf.IsActive, 
  '14/06/2011', lf.CreatedBy, '14/06/2011', lf.UpdatedBy, sum(lf.LineNetAmt), 
  round(sum(lf.LineNetAmt) * ilf.Rate/100, 2), lf.IsActive, 'N'
  FROM C_InvoiceLine lf INNER JOIN C_Tax ilf ON(ilf.C_Tax_ID = lf.C_tax_ID)
  WHERE lf.C_Invoice_ID IN(1005561) AND lf.C_Tax_ID NOT IN (1000452)
GROUP BY lf.C_Tax_ID, lf.C_Invoice_ID, lf.AD_Client_ID, lf.AD_Org_ID, 
lf.IsActive, lf.CreatedBy, lf.UpdatedBy, ilf.Rate)

DELETE FROM C_InvoiceTax WHERE C_Invoice_ID IN(1005561)





SELECT * FROM UPDATE C_InvoiceLine SET C_Tax_ID = 1000450 WHERE C_Invoice_ID IN(1002774, 1003585, 1003249, 1004807, 1004025) AND M_Product_ID <> 1008056


SELECT li.C_Tax_ID, li.C_Charge_ID FROM C_InvoiceLine li
WHERE li.C_Invoice_ID IN(1004021, 1003834, 1000456, 1003832, 1004023, 1003835)

UPDATE C_InvoiceLine SET C_Tax_ID = 1000466 WHERE C_Invoice_ID IN(1004021, 1003834, 1000456, 1003832, 1004023, 1003835)


SELECT * FROM C_Invoice WHERE C_Invoice_ID = 1005941  DocumentNo = '000461'