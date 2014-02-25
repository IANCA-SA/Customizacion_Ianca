--DROP FUNCTION XX_Fn_Genera_Comprobante_Ret_ISLR(Numeric)
/*******************************************************************************************
    Nombre: XX_Fn_Genera_Comprobante_Ret_ISLR.
    Descripcion: Genera Comprobantes de Retenciones en los documentos con retenciones aplicadas de 
    Impuesto Sobre La Renta.
    Revisiones:       
	   Ver        Date                Author           Description       
	   ---------  ----------          ---------------  ------------------------------------
	   1.0        04/05/2011, 13:08   Yamel Senih      1. Creacion de la Funcion
	   
    *******************************************************	*************************************/
CREATE OR REPLACE FUNCTION XX_Fn_Genera_Comprobante_Ret_ISLR(PInstance_ID Numeric)
  RETURNS SETOF void AS
$BODY$
  DECLARE
    /*Parametros del Proceso*/
    p_Instancia         Numeric := $1;
    p_TipoRetencion     Varchar(2);
    p_Inicio            Date;
    p_Fin               Date;
    p_FComprobante      Date;
    /*Variables a usar*/
    v_NRetencion        Varchar(20);
    Factura          	Record;
    v_Secuencia         Numeric := 0;
    v_Cant_Comp         Numeric := 0;
    
  BEGIN
    /*Obteniendo Parametros del Proceso*/

    SELECT CASE WHEN max(substring(fac.XX_Nro_ComprobanteISLR, 7, 8)) IS NULL
		THEN '0' ELSE max(substring(fac.XX_Nro_ComprobanteISLR, 7, 8)) END
    into v_Secuencia
    FROM C_Invoice fac 
    INNER JOIN C_InvoiceLine lfac ON(lfac.C_Invoice_ID = fac.C_Invoice_ID)
    WHERE lfac.XX_TipoRetencion = 'IS';

    SELECT p.P_Date, P_Date_To into p_Inicio, p_Fin FROM AD_PInstance i 
    LEFT JOIN AD_PInstance_Para p ON(i.AD_PInstance_ID = p.AD_PInstance_ID)       
    WHERE p.AD_PInstance_ID = p_Instancia AND P.ParameterName='Intervalo'
    ORDER BY p.SeqNo;

    SELECT coalesce(p.P_Date, current_date) into p_FComprobante FROM AD_PInstance i 
    LEFT JOIN AD_PInstance_Para p ON(i.AD_PInstance_ID = p.AD_PInstance_ID)       
    WHERE p.AD_PInstance_ID = p_Instancia AND P.ParameterName='FComprobante'
    ORDER BY p.SeqNo; 


    --BEGIN
      FOR Factura IN SELECT fac.C_Invoice_ID FROM C_Invoice fac
                        INNER JOIN C_InvoiceLine lfac ON(lfac.C_Invoice_ID = fac.C_Invoice_ID)
                        WHERE fac.XX_Nro_ComprobanteISLR IS NULL 
                        AND fac.XX_ProcesadoISLR = 'N'
                        AND Date(fac.DateAcct) BETWEEN Date(p_Inicio) And Date(p_Fin)
                        AND fac.DocStatus IN ('CO', 'CL')
                        AND lfac.XX_TipoRetencion = 'IS'
                        GROUP BY fac.C_Invoice_ID LOOP

        v_Secuencia := v_Secuencia + 1;
        
        UPDATE C_Invoice
        SET XX_Nro_ComprobanteISLR = to_char(p_FComprobante, 'YYYYMM') || lpad(cast(v_Secuencia AS Varchar), 8, '0'),
        XX_Fecha_ComprobanteISLR = p_FComprobante
        WHERE C_Invoice_ID = Factura.C_Invoice_ID;
        
        v_Cant_Comp := v_Cant_Comp + 1;
        
      END LOOP;
      UPDATE AD_PInstance 
        SET updated = current_date ,IsProcessing = 'N', Result = 1, errormsg = 'Comprobantes I.S.L.R. Generados: ' || v_Cant_Comp	
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