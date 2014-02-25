--DROP FUNCTION fn_get_descrip_pago(NUMERIC)
/*******************************************************************************************
      Nombre: fn_get_descrip_pago.
      Descripcion: Obtiene un String con el Numero de documento de todos los documentos relacionados
      con una seleccion de pago.
      Revisiones:       
	     Ver        Date                Author           Description       
	     ---------  ----------          ---------------  ------------------------------------
	     1.0        26/04/2011, 13:26   Yamel Senih      1. Creacion de la Funcion
********************************************************************************************/

CREATE OR REPLACE FUNCTION fn_get_descrip_pago(id_seleccion NUMERIC) 
  RETURNS SETOF VARCHAR AS
  $BODY$
    DECLARE
      descripcion VARCHAR;
      facturas CURSOR IS
        SELECT CASE WHEN lap.C_Invoice_ID IS NULL 
	  THEN 'Ord_' || trim(ord.DocumentNo) ELSE 'Fac_' || trim(fac.DocumentNo) END DocumentNo
	  FROM C_AllocationLine lap 
	  INNER JOIN C_Payment pag ON(pag.C_Payment_ID = lap.C_Payment_ID)
	  LEFT JOIN C_Invoice fac ON(fac.C_Invoice_ID = lap.C_Invoice_ID)
	  LEFT JOIN C_Order ord ON(ord.C_Order_ID = lap.C_Order_ID)
	  WHERE pag.C_Payment_ID = $1
	ORDER BY DocumentNo;
    BEGIN
      descripcion := ' ';
      FOR Docs IN facturas LOOP
        descripcion = descripcion || ' ' || Docs.DocumentNo;
      END LOOP;
      RETURN NEXT descripcion;
    END;
  $BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;