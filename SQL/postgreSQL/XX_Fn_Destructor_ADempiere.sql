--DROP FUNCTION XX_Fn_Destructor_ADempiere(Numeric)
/*******************************************************************************************
    Nombre: XX_Fn_Destructor_ADempiere.
    Descripcion: Hace creer al usuario que esta dañando el sistema.
    Revisiones:       
	   Ver        Date                Author           Description       
	   ---------  ----------          ---------------  ------------------------------------
	   1.0        10/05/2011, 18:26   Yamel Senih      1. Creacion de la Funcion
    ********************************************************************************************/
CREATE OR REPLACE FUNCTION XX_Fn_Destructor_ADempiere(PInstance_ID Numeric)
  RETURNS SETOF void AS
$BODY$
  DECLARE
    /*Parametros del Proceso*/
    p_Instancia         Numeric := $1;
    /*Variables a usar*/
    v_Usuario         	Numeric;
    
  BEGIN
    /*Obteniendo Parametros del Proceso*/

    SELECT p.P_Number into v_Usuario FROM AD_PInstance i 
    LEFT JOIN AD_PInstance_Para p ON(i.AD_PInstance_ID = p.AD_PInstance_ID)       
    WHERE p.AD_PInstance_ID = p_Instancia AND P.ParameterName='AD_User_ID'
    ORDER BY p.SeqNo;

    --BEGIN

      INSERT INTO XX_Destructores
      
      UPDATE AD_PInstance 
        SET updated = current_date ,IsProcessing = 'N', Result = 1, errormsg = 'Comprobantes I.V.A. Generados: ' || v_Cant_Comp
          WHERE AD_PInstance_ID = p_Instancia;    
    /*EXCEPTION
      WHEN OTHERS
      THEN
        UPDATE AD_PInstance 
          SET updated = current_date ,IsProcessing = 'Y', Result = 0, errormsg = 'Error'
          WHERE AD_PInstance_ID = p_Instancia;      
    END;*/
  END;
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;