--DROP TABLE XX_Registro_Pago_Comision
CREATE TABLE XX_Registro_Pago_Comision(
  C_BPartner_ID NUMERIC(10,0),
  C_Invoice_ID NUMERIC(10,0),
  C_Period_ID NUMERIC(10,0),
  C_Charge_ID NUMERIC(10,0),
  C_Payment_ID NUMERIC(10,0),
  M_Product_Category_ID NUMERIC(10,0),
  XX_Supervisores_Comision_ID NUMERIC(10,0),
  comision NUMERIC(10,2),
  monto_cobranza NUMERIC(10,2)
);


ALTER TABLE XX_Registro_Pago_Comision ADD CONSTRAINT f_Socio_Negocio FOREIGN KEY (C_BPartner_ID) REFERENCES C_BPartner(C_BPartner_ID) ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE XX_Registro_Pago_Comision ADD CONSTRAINT f_Factura FOREIGN KEY (C_Invoice_ID) REFERENCES C_Invoice(C_Invoice_ID) ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE XX_Registro_Pago_Comision ADD CONSTRAINT f_Periodo FOREIGN KEY (C_Period_ID) REFERENCES C_Period(C_Period_ID) ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE XX_Registro_Pago_Comision ADD CONSTRAINT f_Cargo FOREIGN KEY (C_Charge_ID) REFERENCES C_Charge(C_Charge_ID) ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE XX_Registro_Pago_Comision ADD CONSTRAINT f_Pago FOREIGN KEY (C_Payment_ID) REFERENCES C_Payment(C_Payment_ID) ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE XX_Registro_Pago_Comision ADD CONSTRAINT f_Categoria_Producto FOREIGN KEY (M_Product_Category_ID) REFERENCES M_Product_Category(M_Product_Category_ID) ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE XX_Registro_Pago_Comision ADD CONSTRAINT f_Comision_Supervisores FOREIGN KEY (XX_Supervisores_Comision_ID) REFERENCES XX_Supervisores_Comision(XX_Supervisores_Comision_ID) ON UPDATE CASCADE ON DELETE RESTRICT;