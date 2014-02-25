--DROP FUNCTION XX_Fn_Actualiza_Sec(Numeric)
/*******************************************************************************************
    Nombre: XX_Fn_Actualiza_Sec.
    Descripcion: Actualiza una determinada secuencia indicada por el ID.
    Revisiones:       
	   Ver        Date                Author           Description       
	   ---------  ----------          ---------------  ------------------------------------
	   1.0        30/04/2011, 16:26   Yamel Senih      1. Creacion de la Funcion
    ********************************************************************************************/
CREATE OR REPLACE FUNCTION XX_Fn_Actualiza_Sec(ID_Sec Numeric)
  RETURNS SETOF VARCHAR AS
$BODY$
  DECLARE
    ID_Secuencia      Numeric;
    Secuencia         Numeric;
    Prefijo           Varchar;
  BEGIN
    ID_Secuencia := $1;
    secuencia:=0;
    SELECT sq.Prefix ,sq.CurrentNext Into Prefijo ,Secuencia
      FROM AD_Sequence sq
      WHERE sq.AD_Sequence_ID = ID_Secuencia;

   UPDATE AD_Sequence SET CurrentNext = Secuencia + 1
      WHERE AD_Sequence_ID = ID_Secuencia;
   
    RETURN NEXT COALESCE(Prefijo, '') || lpad(cast(Secuencia AS Varchar), 8, '0');
  END;
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;