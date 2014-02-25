-- Function: bpfirstaccountno(numeric)

-- DROP FUNCTION bpfirstaccountno(numeric);

CREATE OR REPLACE FUNCTION bpfirstaccountno(c_bpartner_id numeric)
  RETURNS character varying AS
$BODY$
DECLARE
	c_c_bpartner_id numeric;
	v_accountNo varchar;
BEGIN
	c_c_bpartner_id = $1;
	IF c_c_bpartner_id <= 0 THEN
		RETURN '';
	END IF;
	SELECT 
		bpba.accountno 
	INTO v_accountNo
	FROM C_BP_BankAccount bpba WHERE bpba.c_bpartner_id = c_c_bpartner_id AND isActive = 'Y' AND isAch = 'Y' ORDER BY bpba.c_bp_bankaccount_id LIMIT 1;
	RETURN v_accountNo;
END;	
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION bpfirstaccountno(numeric) OWNER TO adempiere;
