--DROP FUNCTION XX_Fn_GetDocumentNo(Numeric, Numeric)
/*******************************************************************************************
    Nombre: XX_Fn_GetDocumentNo.
    Descripcion: Obtiene el Numero de Documento de un registro a partir del 
    ID de la tabla y el ID del registro.
    Revisiones:       
	   Ver        Date                Author           Description       
	   ---------  ----------          ---------------  ------------------------------------
	   1.0        02/08/2011, 13:50   Yamel Senih      1. Creacion de la Funcion
    ********************************************************************************************/
CREATE OR REPLACE FUNCTION XX_Fn_GetDocumentNo(AD_Table_ID Numeric, Record_ID Numeric)
  RETURNS SETOF Varchar AS
$BODY$
  DECLARE
    /*Parametros del Proceso*/
    p_AD_Table_ID       Numeric := $1;
    p_Record_ID		Numeric := $2;
    /*Variables a usar*/
    v_DocumentNo	Varchar(60);
    v_Tabla		Varchar(60);
    
  BEGIN
    SELECT t.TableName Into v_Tabla FROM AD_Table t WHERE t.AD_Table_ID = p_AD_Table_ID;
    EXECUTE 'SELECT DocumentNo FROM ' 
      || v_Tabla || ' WHERE ' || v_Tabla 
      || '_ID = ' || p_Record_ID INTO v_DocumentNo;
    RETURN NEXT v_DocumentNo;
  END;
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;