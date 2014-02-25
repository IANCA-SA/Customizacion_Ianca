CREATE OR REPLACE VIEW XX_V_Reporte_Comision AS
  Select CI.Ad_Client_ID,
    CI.Ad_Org_Id,
    Case When XXRPC.C_BPARTNER_ID = Null Then XXRPC.XX_SUPERVISORES_COMISION_Id Else XXRPC.C_BPARTNER_ID End As IDComisionista,
    Case When XXRPC.C_BPARTNER_ID = Null Then XXSC.Nombre Else CBP.Name End As Comisionista,
    Case When XXRPC.M_Product_Category_ID = Null Then CCH.Name Else MPC.NAME End As Concepto,
    XXRPC.C_INVOICE_ID,
    XXRPC.COMISION,
    Round(XXRPC.MONTO_COBRANZA,2) MONTO_COBRANZA,
    XXRPC.C_PERIOD_ID,
    XXRPC.C_Payment_Id,
    cast(XXRPC.C_Payment_Id As Character) As XX_C_Payment,
    Case When XXRPC.C_BPARTNER_ID is Null Then 1 Else 0 End As Supervisor,
    Round(XXRPC.MONTO_COBRANZA,2)*(XXRPC.COMISION/100) As Monto_Comision,
    CI.C_BPartner_ID 
  from XX_Registro_Pago_Comision XXRPC
  Left Join M_Product_Category MPC On MPC.M_Product_Category_ID=XXRPC.M_Product_Category_ID
  Left Join C_Charge CCH On XXRPC.C_Charge_Id=CCH.C_Charge_Id
  Left Join C_BPartner CBP On CBP.C_BPARTNER_ID=XXRPC.C_BPARTNER_ID
  Left Join XX_Supervisores_Comision XXSC On XXSC.XX_SUPERVISORES_COMISION_Id=XXRPC.XX_SUPERVISORES_COMISION_Id
  Inner Join C_Invoice CI On CI.C_Invoice_ID=XXRPC.C_Invoice_ID
  Order By Supervisor,Comisionista,Concepto,MPC.NAME,XXRPC.C_INVOICE_ID