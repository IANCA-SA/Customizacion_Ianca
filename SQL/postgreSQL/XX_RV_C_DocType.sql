CREATE OR REPLACE VIEW XX_RV_C_DocType AS
  SELECT AD_Client_ID, AD_Org_ID, AD_PrintFormat_ID, C_DocTypeDifference_ID, C_DocType_ID, 
	C_DocTypeInvoice_ID, C_DocTypeProforma_ID, C_DocTypeShipment_ID, Created, CreatedBy, 
	DefiniteSequence_ID, Description, DocBaseType, DocNoSequence_ID, DocSubTypeSO, DocumentCopies, 
	DocumentNote, GL_Category_ID, HasCharges, HasProforma, IsActive, IsCreateCounter, IsDefault, 
	IsDefaultCounterDoc, IsDocNoControlled, IsIndexed, IsInTransit, IsOverwriteDateOnComplete, 
	IsOverwriteSeqOnComplete, IsPickQAConfirm, IsPrepareSplitDocument, IsShipConfirm, IsSOTrx, 
	IsSplitWhenDifference, Name, PrintName, Updated, UpdatedBy, XX_Doc_Reverso_ID, XX_SerieNroControl_ID,
	CASE WHEN IsSOtrx = 'Y' THEN 1 ELSE -1 END Multiplicador
	FROM C_DocType


