﻿-- DROP VIEW XX_RV_Facturas_Vencidas_Dia
CREATE OR REPLACE VIEW XX_RV_Facturas_Vencidas_Dia As
  SELECT (CASE WHEN ABS(Current_Date - FECHA_VENCIMIENTO) < 150 THEN F_P ELSE 0 END) menor_150,
       (CASE WHEN ABS(Current_Date - FECHA_VENCIMIENTO) >= 150 THEN F_P ELSE 0 END) mayor_150,
	   NOMBRE_CONTRAPARTE, C_BPARTNER_ID,C_BP_Group_ID,
	   trim(NOMBRE_CONVENIO) NOMBRE_CONVENIO,
	   ORDEN, TO_CHAR(FECHA_ORDEN,'DD/MM/YYYY') FECHA_ORDEN, MONTO_ORDEN,
	   FACTURA, TO_CHAR(FECHA_FACTURA,'DD/MM/YYYY') FECHA_FACTURA, MONTO_FACTURA,
	   DESPACHO, TO_CHAR(FECHA_DESPACHO,'DD/MM/YYYY') FECHA_DESPACHO,
	   TO_CHAR(FECHA_VENCIMIENTO,'DD/MM/YYYY') FECHA_VENCIMIENTO, TOTAL_PAGO,
	   F_P, (C_BPARTNER_ID || FACTURA) CONCAT_CONTRAPARTE, NOMBRE_GRUPO,
	   ABS(ROUND(Current_Date - FECHA_VENCIMIENTO)) DIAS,
	   ROUND(Current_Date - FECHA_VENCIMIENTO) DIAS_MOSTRAR, XX_Convenio_Cosecha_ID,
	   IsSOtrx, AD_Client_ID, AD_Org_ID, C_Order_ID, C_Invoice_ID, M_InOut_ID
FROM XX_RV_Vencimientos_Det_O_F_D VENCIDOS
WHERE XX_C_Invoice_ID IS NULL