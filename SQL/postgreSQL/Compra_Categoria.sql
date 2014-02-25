--CREATE OR REPLACE VIEW XX_RV_CompraProducto AS
SELECT cpro.M_Product_Category_ID, cpro.Name Category, pro.M_Product_ID, pro.Name Product, fac.IsSOTrx,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 1 THEN lfac.LineNetAmt ELSE 0 END) January,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 2 THEN lfac.LineNetAmt ELSE 0 END) February,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 3 THEN lfac.LineNetAmt ELSE 0 END) March,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 4 THEN lfac.LineNetAmt ELSE 0 END) April,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 5 THEN lfac.LineNetAmt ELSE 0 END) May,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 6 THEN lfac.LineNetAmt ELSE 0 END) June,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 7 THEN lfac.LineNetAmt ELSE 0 END) July,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 8 THEN lfac.LineNetAmt ELSE 0 END) August,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 9 THEN lfac.LineNetAmt ELSE 0 END) September,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 10 THEN lfac.LineNetAmt ELSE 0 END) October,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 11 THEN lfac.LineNetAmt ELSE 0 END) November,
SUM(CASE WHEN extract(Month From fac.DateAcct) = 12 THEN lfac.LineNetAmt ELSE 0 END) December,
fac.AD_Client_ID, fac.AD_Org_ID
FROM C_Invoice fac
INNER JOIN C_InvoiceLine lfac ON(lfac.C_Invoice_ID = fac.C_Invoice_ID)
INNER JOIN M_Product pro ON(pro.M_Product_ID = lfac.M_Product_ID)
INNER JOIN M_Product_Category cpro ON(cpro.M_Product_Category_ID = pro.M_Product_Category_ID)
GROUP BY cpro.M_Product_Category_ID, cpro.Name, pro.M_Product_ID, pro.Name, fac.IsSOTrx, fac.AD_Client_ID, fac.AD_Org_ID