--DROP FUNCTION adempiere.establece_nro_control()
CREATE OR REPLACE FUNCTION adempiere.establece_nro_control()
RETURNS trigger AS
  $BODY$
    DECLARE
      dos_digitos NUMERIC(2, 0);
      consecutivo NUMERIC(25, 0);
      ID_Nro_Control NUMERIC(10,0);
    BEGIN
      IF NEW.IsSOTrx='Y' AND NEW.DocStatus= 'CO' AND NEW.XX_NroControl IS NULL THEN
        SELECT nc.XX_Dos_Digitos, nc.XX_Consecutivo, nc.XX_SerieNroControl_ID
          INTO dos_digitos, consecutivo, ID_Nro_Control 
          FROM XX_SerieNroControl nc INNER JOIN C_DocType td ON(nc.XX_SerieNroControl_ID = td.XX_SerieNroControl_ID)
          WHERE td.C_DocType_ID = NEW.C_DocTypeTarget_ID;        
        BEGIN
          dos_digitos = COALESCE(dos_digitos, 0);
          consecutivo = COALESCE(consecutivo, 0);
          NEW.XX_NroControl := lpad(cast(dos_digitos AS varchar), 2, '0') || '-' || lpad(cast(consecutivo AS varchar), 7, '0');
          UPDATE XX_SerieNroControl SET XX_Consecutivo = consecutivo + 1
          WHERE XX_SerieNroControl_ID = ID_Nro_Control;
          RETURN NEW;
        EXCEPTION WHEN invalid_transaction_termination THEN
          RAISE NOTICE 'Error al Intentar Cargar el Número de Control';
          RETURN NULL;
        END;
      END IF;
      RETURN NEW;
    END;
  $BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;

--DROP TRIGGER tg_est_nro_control ON adempiere.C_Invoice
CREATE TRIGGER tg_est_nro_control
  BEFORE UPDATE
  ON adempiere.C_Invoice
  FOR EACH ROW
  EXECUTE PROCEDURE adempiere.establece_nro_control();