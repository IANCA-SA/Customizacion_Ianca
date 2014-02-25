--DROP TABLE XX_Vencimiento_Invoice

CREATE TABLE XX_Vencimiento_Invoice(
  Vencimiento_Invoice_ID Numeric Not Null,
  Dias_Desde Numeric Not Null,
  Dias_Hasta Numeric Not Null,
  "Name" Varchar(30)
);

INSERT INTO XX_Vencimiento_Invoice VALUES(1000000, 0, 30, '1. DE 0 A 30 DIAS'),
  (1000001, 31, 60, '2. De 31 A 60 Días'),
  (1000002, 61, 90, '3. De 61 A 90 Días'),
  (1000003, 91, 120, '4. De 91 A 120 Días'),
  (1000004, 121, 150, '5. De 121 A 150 Días'),
  (1000005, 150, 1000000, '6. Más DE 150 Días');