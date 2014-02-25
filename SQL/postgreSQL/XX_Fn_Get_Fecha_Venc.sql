--DROP FUNCTION XX_Fn_Get_Fecha_Venc(Date, Integer)
/*******************************************************************************************
    Nombre: XX_Fn_Get_Fecha_Venc.
    Descripcion: Obtiene la fecha de Vencimiento de una Orden.
    Revisiones:       
	   Ver        Date                Author           Description       
	   ---------  ----------          ---------------  ------------------------------------
	   1.0        30/04/2011, 16:26   Yamel Senih      1. Creacion de la Funcion
    ********************************************************************************************/
CREATE OR REPLACE FUNCTION XX_Fn_Get_Fecha_Venc(P_Fecha Date, P_AD_Client_ID Integer)
  RETURNS SETOF Date AS
  $BODY$
    DECLARE
      V_Fecha_Inicio	Date := $1;
      V_AD_CLient_ID	Integer := $2;
      V_Dias		Integer := 1;
      V_Incremento	Integer;

    BEGIN
      WHILE V_Dias <= 3 LOOP
          SELECT 
            CASE 
              WHEN EXTRACT(DOW FROM V_Fecha_Inicio) = 6
                   OR EXTRACT(DOW FROM V_Fecha_Inicio) = 0
                   OR cast(V_Fecha_Inicio As Date) IN(SELECT Date1 FROM C_NonBusinessDay WHERE AD_Client_ID = V_AD_Client_ID 
                   AND cast(Date1 As Date) BETWEEN cast(V_Fecha_Inicio As Date) AND cast(V_Fecha_Inicio As Date) + 40)
              THEN 0 
              ELSE 1
            END Into V_Incremento;
        V_Dias := V_Dias + V_Incremento;
        V_Fecha_Inicio := V_Fecha_Inicio + 1;
      END LOOP;
      Return Next V_Fecha_Inicio;
    END
    
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;