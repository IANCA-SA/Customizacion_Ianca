--DROP FUNCTION XX_Fn_Genera_Comprobante_Ret(Numeric)

CREATE OR REPLACE FUNCTION XX_Fn_Genera_Comprobante_Ret(PInstance_ID Numeric)
  RETURNS SETOF void AS
$BODY$
  DECLARE
    p_Instancia Numeric;
    p_Cargo Numeric;
  BEGIN
    p_Instancia := $1;
    SELECT p.P_Number into p_Cargo FROM AD_PInstance i 
    LEFT JOIN AD_PInstance_Para p ON(i.AD_PInstance_ID = p.AD_PInstance_ID)       
    WHERE p.AD_PInstance_ID = p_Instancia AND P.ParameterName='C_Charge_ID'
    ORDER BY p.SeqNo;

    




    UPDATE AD_PInstance 
      SET updated = current_date ,IsProcessing = 'N', Result = 1, errormsg = 'Exito' 
      WHERE AD_PInstance_ID = p_Instancia;


  END;
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;