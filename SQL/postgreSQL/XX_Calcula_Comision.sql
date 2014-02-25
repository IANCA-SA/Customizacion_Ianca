--DROP FUNCTION datos.XX_Calcula_Comision(pinstance_id NUMERIC)
CREATE OR REPLACE FUNCTION XX_Calcula_Comision(pinstance_id NUMERIC)
  RETURNS SETOF VOID AS
  $BODY$
    DECLARE
	 /******************************************************************************       
	   Nombre:       xx_calcula_comision       
	   Proposito:    Calcular Las comisiones de los tecnicos de campo       
	       
	   REVISIONES:       
	   Ver        Date        Author           Description       
	   ---------  ----------  ---------------  ------------------------------------       
	   1.0        14/06/2010  Carlos Parada y Yamel Senih        1. Creción en Oracle 10g.       
	   1.1        10/03/2011  Carlos Parada y Yamel Senih        2. Migración a PostgreSQL 
	   NOTES:       
	       
	   Automatically available Auto Replace Keywords:       
	      Object Name:     xx_calcula_comision       
	      Sysdate:         14/06/2010       
	      Date and Time:   14/06/2010, 04:38:01 , and 14/06/2010 04:38:01        
	      Username:         (set in TOAD Options, Procedure Editor)       
	      Table Name:       (set in the "New PL/SQL Object" dialog)       
	       
	******************************************************************************/       
	lnNumRows Numeric;       
	tnPeriodo Numeric; 
    BEGIN
    /*SELECT   p.p_number into tnPeriodo  FROM ad_pinstance i, ad_pinstance_para p       
  WHERE i.ad_pinstance_id = p.ad_pinstance_id(+) and p.ad_pinstance_id=pinstance_id And P.PARAMETERNAME='tnPeriodo'       
  ORDER BY p.seqno;*/
  SELECT p.p_number into tnPeriodo FROM ad_pinstance i 
    LEFT JOIN ad_pinstance_para p ON(i.ad_pinstance_id = p.ad_pinstance_id)       
  WHERE p.ad_pinstance_id=pinstance_id And P.PARAMETERNAME='tnPeriodo'       
  ORDER BY p.seqno;
   Select Count(*) into lnNumRows From XX_REGISTRO_PAGO_COMISION Where C_Period_Id=tnPeriodo;       
 	       
   If (lnNumRows>0) Then       
   	  /*Eliminando los datos del periodo*/       
   	  Delete From XX_REGISTRO_PAGO_COMISION Where C_Period_Id=tnPeriodo;       
   End If;       
       
   /*Insertando en la tabla XX_REGISTRO_PAGO_COMISION Las comisiones de los Tecnicos de campo*/       
   Insert Into XX_REGISTRO_PAGO_COMISION(C_Bpartner_ID,C_Invoice_Id,M_Product_Category_Id,Comision,Monto_Cobranza,C_Period_Id,C_Payment_Id,C_Charge_ID)       
   Select XX_CXC.C_BPARTNER_ID,       
	   XX_CXC.C_INVOICE_ID,       
	   XX_CXC.M_PRODUCT_CATEGORY_ID,       
	   XX_CXC.comision,       
	   Case When Sum(XX_CXC.Pago)>XX_CXC.GRANDTOTAL + Decode(XX_CXC.Tot_Notas,Null,0)Then XX_CXC.TotVenta Else (((Sum(XX_CXC.Pago)*100)/(XX_CXC.GRANDTOTAL+ Case When XX_CXC.Tot_Notas Is Null Then 0 Else XX_CXC.Tot_Notas End))/100) * (XX_CXC.TotVenta + Case When ((XX_CXC.Tot_Notas*100)/XX_CXC.GRANDTOTAL) Is Null Then 0 Else (XX_CXC.TotVenta*((XX_CXC.Tot_Notas*100)/XX_CXC.GRANDTOTAL))/100 End) End As Monto_Cobranza ,     
	   XX_CXC.C_PERIOD_ID,       
	   Max(XX_CXC.C_payment_ID) As C_Paiment_ID,       
	   XX_CXC.C_CHARGE_ID       
   From        
   (       
	Select Case When MPC.M_Product_Category_Id Is Null Then CCH.C_Charge_Id Else Null End As C_Charge_Id,CBP.C_BPartner_ID,DCO.C_Invoice_Id,MPC.M_Product_Category_Id,MPC.comision,Sum(DCOL.LineTotalAmt) As TotVenta,DCO.GRANDTOTAL,       
		 (Select Sum(Case When CDT.IssoTrx='Y' Then LineTotalAmt *-1 Else LineTotalAmt End)        
   	   		   From C_Invoice DCOS        
   			   Inner Join C_InvoiceLine DCOLS On DCOS.C_INVOICE_ID=DCOLS.C_INVOICE_ID        
   			   Inner Join C_DocType CDT On CDT.C_DocType_ID=DCOS.C_DocType_ID       
   			   Left Join M_Product MPS On DCOLS.M_Product_ID=MPS.M_Product_ID        
			   Left Join C_Charge CHS On CHS.C_Charge_ID =DCOLS.C_Charge_ID        
			   Where DCOS.XX_C_INVOICE_ID=DCO.C_INVOICE_ID  And ((DCOLS.M_Product_ID Is Not Null And MPS.M_Product_Category_Id=MPC.M_PRODUCT_CATEGORY_ID) Or (DCOLS.C_Charge_Id Is Not Null And CHS.Comisionable='Y' And CHS.M_Product_Category_Id=MPC.M_PRODUCT_CATEGORY_ID) Or (DCOLS.C_Charge_Id Is Not Null And CHS.Comisionable='Y' And CHS.M_Product_Category_Id Is Null))         
		  ) As Tot_Notas       
		  ,CAL.amount + CAL.DiscountAmt As Pago,CPM.C_payment_ID As C_payment_ID,CP.C_Period_ID,CPM.DATETRX,CP.StartDate,CP.EndDate       
		  From C_Invoice DCO        
		  Inner Join C_InvoiceLine DCOL On DCO.C_Invoice_Id=DCOL.C_Invoice_Id       
		  Inner Join Ad_User AU On AU.Ad_User_ID=DCO.SalesRep_Id       
		  Inner Join C_BPartner CBP On CBP.C_BPARTNER_ID=AU.C_BPARTNER_ID       
		  Left Join C_Charge CCH On DCOL.C_Charge_Id=CCH.C_Charge_ID       
		  Left Join M_Product MP On DCOL.M_Product_Id=MP.M_Product_Id       
		  Left Join M_Product_Category MPC On MPC.M_Product_Category_Id=MP.M_Product_Category_Id Or CCH.M_Product_Category_Id=MPC.M_Product_Category_Id       
		  Inner Join C_AllocationLine CAL On CAL.C_Invoice_Id=DCO.C_Invoice_Id       
		  Left Join C_Payment CPM On CAL.C_Payment_Id=CPM.C_Payment_Id,       
		  C_Period CP        
		  Where        
		  /*Mientras el documento es el original*/       
		  DCO.XX_C_INVOICE_ID Is Null        
		  /*Y El estado del documento esta contabilizado o cerrado*/       
		  And DCO.DocStatus In ('CO','CL')        
		  /*Y El estado del pago esta contabilizado o cerrado*/       
		  And (CPM.DocStatus In ('CO','CL') Or CPM.DocStatus Is Null) 
		  /*Y el Periodo sea el que pase por parametro*/       
		  And CP.C_Period_Id=tnPeriodo       
		  /*Y la lista de precios del documento sea de ventas*/       
		  And DCO.IssoTrx ='Y'       
		  /*Y el cargo sea comisionable*/       
		  And ((CCH.C_Charge_ID Is Not Null And CCH.Comisionable ='Y') Or CCH.C_Charge_ID Is Null And MPC.comision<>0)        
		  --Having Sum(CAL.amount)<>0
		  --And Sum(CAL.amount)<>0           
		  Group By CCH.C_Charge_Id,CBP.C_BPartner_ID,DCO.C_Invoice_Id,MPC.M_PRODUCT_CATEGORY_ID,MPC.comision,DCO.GRANDTOTAL,CP.C_Period_ID,CP.EndDate,CP.StartDate,CAL.amount,CAL.DiscountAmt,CAL.C_AllocationLine_Id,CPM.C_payment_ID,CPM.DATETRX      
		  Having Sum(CAL.amount)<>0) XX_CXC 
	--Having (Sum(XX_CXC.PAGO)>=XX_CXC.GRANDTOTAL + Case When XX_CXC.Tot_Notas Is Null Then 0 Else XX_CXC.Tot_Notas End) And (Max(DATETRX) Between StartDate And EndDate)  
	--Where (Sum(XX_CXC.PAGO)>=XX_CXC.GRANDTOTAL + Case When XX_CXC.Tot_Notas Is Null Then 0 Else XX_CXC.Tot_Notas End) And (Max(DATETRX) Between StartDate And EndDate)     
   Group By XX_CXC.C_BPARTNER_ID,XX_CXC.C_CHARGE_ID,XX_CXC.C_INVOICE_ID,XX_CXC.C_PERIOD_ID,XX_CXC.M_PRODUCT_CATEGORY_ID,XX_CXC.comision,XX_CXC.GRANDTOTAL,XX_CXC.TOTVENTA,XX_CXC.Tot_Notas,StartDate,EndDate       
   Having (Sum(XX_CXC.PAGO)>=XX_CXC.GRANDTOTAL + Case When XX_CXC.Tot_Notas Is Null Then 0 Else XX_CXC.Tot_Notas End) And (Max(DATETRX) Between StartDate And EndDate) 
   Order By XX_CXC.C_INVOICE_ID;       
       
   /*Insertando en la tabla XX_REGISTRO_PAGO_COMISION Las comisiones de los Supervisores*/       
   Insert InTo XX_REGISTRO_PAGO_COMISION(C_Invoice_Id,M_Product_Category_Id,Comision,Monto_Cobranza,C_Period_Id,C_Payment_Id,C_Charge_ID,XX_SUPERVISORES_COMISION_ID)       
   Select XXPC.C_Invoice_Id,XXPC.M_Product_Category_Id,XXPCS.Comision,XXPC.Monto_Cobranza,XXPC.C_Period_Id,XXPC.C_Payment_Id,XXPC.C_Charge_ID,XXS.XX_SUPERVISORES_COMISION_ID        
   		  From XX_REGISTRO_PAGO_COMISION XXPC Inner Join XX_VENDEDORES_SUP XXVS On XXPC.C_BPartner_Id=XXVS.C_BPartner_Id        
		  Inner Join XX_SUPERVISORES_COMISION XXS On XXS.XX_SUPERVISORES_COMISION_ID=XXVS.XX_SUPERVISORES_COMISION_ID       
		  Left  Join XX_PORCENTAJE_CAT_SUP XXPCS On XXPCS.XX_SUPERVISORES_COMISION_ID=XXS.XX_SUPERVISORES_COMISION_ID And XXPCS.M_Product_Category_Id=XXPC.M_Product_Category_Id        
		  Where XXPC.C_Period_Id = tnPeriodo;       
       
       
	       
	Select Count(*) into lnNumRows From XX_REGISTRO_PAGO_COMISION Where C_Period_Id=tnPeriodo;       
    if (lnNumRows>0) Then       
        UPDATE ad_pinstance       
		SET updated = SYSDATE,       
		   isprocessing = 'N',       
		   RESULT = 1,       
		   errormsg = 'Exito'       
		   WHERE ad_pinstance_id = pinstance_id;       
	Else        
	    UPDATE ad_pinstance       
		SET updated = SYSDATE,       
		   isprocessing = 'Y',       
		   RESULT = 0,       
		   errormsg = 'No hay datos para este periodo'       
		   WHERE ad_pinstance_id = pinstance_id;
	end if;
	Commit;
    END;
  $BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;

  /*--Return; 
      EXCEPTION
	  WHEN OTHERS THEN 
	  RollBack;
	  UPDATE ad_pinstance       
           SET updated = SYSDATE,       
             isprocessing = 'N',       
             RESULT = 0,       
             errormsg = 'Error';
	  RAISE NOTICE 'Error';*/
