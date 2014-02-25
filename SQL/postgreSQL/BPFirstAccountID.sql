-- DROP FUNCTION BPFirstAccountID(numeric);

CREATE OR REPLACE FUNCTION BPFirstAccountID(c_bpartner_id numeric)
  RETURNS NUMERIC AS
$BODY$
DECLARE
	c_c_bpartner_id numeric;
	m_C_BP_BankAccount_ID numeric;
	v_accountNo varchar;
BEGIN
	c_c_bpartner_id = $1;
	IF c_c_bpartner_id <= 0 THEN
		RETURN '';
	END IF;
	SELECT 
		bpba.C_BP_BankAccount_ID
	INTO m_C_BP_BankAccount_ID
	FROM C_BP_BankAccount bpba WHERE bpba.c_bpartner_id = c_c_bpartner_id AND isActive = 'Y' AND isAch = 'Y' ORDER BY bpba.c_bp_bankaccount_id LIMIT 1;
	RETURN m_C_BP_BankAccount_ID;
END;	
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;