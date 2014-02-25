-- DROP VIEW XX_RV_OrdenCalidad
CREATE OR REPLACE VIEW XX_RV_OrdenCalidad AS
SELECT oc.AD_Client_ID, oc.AD_Org_ID, oc.AD_Workflow_ID, oc.Assay, oc.C_Activity_ID, oc.C_BPartner_ID, oc.C_BPartnerRelation_ID, 
oc.C_Campaign_ID, oc.C_DocType_ID, oc.C_DocTypeTarget_ID, oc.C_OrderLine_ID, oc.C_Project_ID, oc.C_UOM_ID, oc.CopyFrom, 
oc.Created, oc.CreatedBy, oc.DateConfirm, oc.DateDelivered, oc.DateFinish, oc.DateFinishSchedule, oc.DateFrom, 
oc.DateOrdered, oc.DatePromised, oc.DateStart, oc.DateStartSchedule, oc.DateTo, oc.Description, oc.DocAction, 
oc.DocStatus, oc.DocumentNo, oc.DocumentQuality_ID, oc.FloatAfter, oc.FloatBefored, oc.GenerateTo, oc.getWeight, 
oc.Humedad, oc.Impurezas, oc.IsActive, oc.IsApproved, oc.IsInclude, oc.IsPrinted, oc.IsQtyPercentage, oc.IsSeco, 
oc.IsSelected, oc.IsSOTrx, oc.Line, oc.Lot, oc.M_AttributeSetInstance_ID, oc.M_InOutLine_ID, oc.M_Product_ID, oc.M_Shipper_ID, 
oc.M_ShippingGuide_ID, oc.M_Warehouse_ID, oc.OrderType, oc.Planner_ID, oc.Posted, oc.PP_Order_ID, oc.PP_Product_BOM_ID, 
oc.PriorityRule, oc.Processed, oc.ProcessedOn, oc.Processing, oc.QtyBatchs, oc.QtyBatchSize, oc.QtyDelivered, oc.QtyEntered, 
oc.QtyOrdered, oc.QtyReject, oc.QtyReserved, oc.QtyScrap, oc.ReferenceNo, oc.S_Resource_ID, oc.ScheduleType, oc.SerNo, 
oc.TotalWeight, oc.TotalWeight2, oc.Updated, oc.UpdatedBy, oc.User1_ID, oc.User2_ID, oc.Weight, oc.Weight2, oc.Weight3, 
oc.XX_Conductor_ID, oc.XX_DirerenciaPeso, oc.XX_Ticket_Vigilancia_ID, oc.XX_TipoArroz, oc.XX_TipoPesada, 
oc.XX_Vehiculo_ID, (cd.cedula || ' - ') || cd.nombre AS conductor, (ve.placa || ' - ') || ve.nombre AS vehiculo, 
oc.PP_Order_ID XX_RV_OrdenCalidad_ID, CASE WHEN oc.XX_Estatus_Calidad = 'A' THEN 'Aprobado' WHEN 'R' THEN 'Rechazado' ELSE 'Inválido' END XX_Estatus_Calidad
FROM PP_Order oc
INNER JOIN XX_Conductor cd ON(cd.XX_Conductor_ID = oc.XX_Conductor_ID)
INNER JOIN XX_Vehiculo ve ON(ve.XX_Vehiculo_ID = oc.XX_Vehiculo_ID)