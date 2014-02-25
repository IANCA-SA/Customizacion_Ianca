DROP VIEW XX_RV_Pesada

CREATE OR REPLACE VIEW XX_RV_Pesada AS 
  SELECT AD_Client_ID, AD_Org_ID, DocumentNo, QtyEntered,
  DocumentQuality_ID, XX_Vehiculo_ID, XX_Conductor_ID,Weight, Weight2, 
  Weight3, C_BPartner_ID, C_DocTypeTarget_ID, getWeight, C_BPartnerRelation_ID, 
  IsSOTrx, IsSeco, Humedad, M_Warehouse_ID, QtyDelivered, QtyOrdered,
  QtyReserved, DocStatus, SerNo, DateStartSchedule, DateStart, M_Product_ID, 
  M_AttributeSetInstance_ID, Description, PP_Order_ID, DateOrdered, Impurezas, 
  DatePromised, TotalWeight2, ReferenceNo, TotalWeight, FloatAfter, FloatBefored
  FROM PP_Order pp

SELECT * FROM AD_Table WHERE TableName = 'PP_Order'
SELECT * FROM AD_Column WHERE AD_Table_ID = 53027 

Planner_ID


CopyFrom

DateDelivered
CreatedBy
IsSelected
DocumentNo

C_Activity_ID
C_Campaign_ID
C_DocType_ID
C_OrderLine_ID
C_Project_ID
C_UOM_ID
Created
DateFinish
DocAction

IsActive
IsApproved
IsPrinted
Line
Lot
PriorityRule
Processed
Processing


ScheduleType
Updated
UpdatedBy



User1_ID
User2_ID
AD_OrgTrx_ID
AD_Org_ID
QtyBatchSize
QtyBatchs
QtyReject
AD_Workflow_ID
IsQtyPercentage
Assay
PP_Product_BOM_ID

Yield

OrderType
DateFinishSchedule
QtyScrap
ProcessedOn
DateConfirm
S_Resource_ID


DateTo
DateFrom
GenerateTo
IsInclude



Posted


