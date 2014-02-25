SELECT * FROM C_Order WHERE DocumentNo = 'OCP892'

UPDATE C_Order SET DocStatus = 'CO', DocAction = 'CL', Processed = 'Y', Posted = 'N' WHERE C_Order_ID = 1001231
UPDATE C_OrderLine SET /*DocStatus = 'DR', DocAction = 'CO', */Processed = 'Y' WHERE C_Order_ID = 1001231