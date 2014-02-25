--DROP FUNCTION actualiza_correlativo_cheque()
CREATE OR REPLACE FUNCTION actualiza_correlativo_cheque()
  RETURNS trigger AS
  $BODY$
    BEGIN
      IF NEW.actual = 'Y' THEN
        UPDATE datos.m_lista_precio SET actual = 'N';	
        IF found THEN
          RETURN NEW;
        END IF;
      END IF;
      RETURN NEW;
    END;
  $BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;

--DROP TRIGGER tg_lista_precio ON datos.m_lista_precio
CREATE TRIGGER tg_lista_precio
  BEFORE INSERT OR UPDATE
  ON datos.m_lista_precio
  FOR EACH ROW
  EXECUTE PROCEDURE actualiza_correlativo_cheque();