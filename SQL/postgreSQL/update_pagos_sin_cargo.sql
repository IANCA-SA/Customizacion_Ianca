SELECT * FROM UPDATE C_Payment SET C_Charge_ID = 1000125 WHERE C_DocType_ID IN(1000069, 1000070) AND C_Charge_ID IS NULL AND IsAllocated = 'N' AND DocStatus = 'CO'


--Efectivo por Depositar
SELECT * FROM C_Charge order by name

SELECT * FROM C_DocType order by name