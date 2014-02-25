--DROP FUNCTION XX_Fn_CopiFrom_PrintFormat(Numeric)
/*******************************************************************************************
    Nombre: XX_Fn_Destructor_ADempiere.
    Descripcion: Copia los Item de un Formato de Impresin a Otro.
    Revisiones:       
	   Ver        Date                Author           Description       
	   ---------  ----------          ---------------  ------------------------------------
	   1.0        14/07/2011, 11:09   Yamel Senih      1. Creacion de la Funcion
    ********************************************************************************************/
CREATE OR REPLACE FUNCTION XX_Fn_CopiFrom_PrintFormat(PInstance_ID Numeric)
  RETURNS SETOF void AS
$BODY$
  DECLARE
    /*Parametros del Proceso*/
    p_Instancia         Numeric := $1;
    /*Variables a usar*/
    v_Form_From         Numeric;
    v_Form_To         	Numeric;
    v_Sec		Numeric;
    
  BEGIN
    /*Obteniendo Parametros del Proceso*/

    SELECT p.P_Number into v_Form_From FROM AD_PInstance i 
    LEFT JOIN AD_PInstance_Para p ON(i.AD_PInstance_ID = p.AD_PInstance_ID)       
    WHERE p.AD_PInstance_ID = p_Instancia AND P.ParameterName='AD_PrintFormat_ID'
    ORDER BY p.SeqNo;

	SELECT p.P_Number into v_Form_To FROM AD_PInstance i 
    LEFT JOIN AD_PInstance_Para p ON(i.AD_PInstance_ID = p.AD_PInstance_ID)       
    WHERE p.AD_PInstance_ID = p_Instancia AND P.ParameterName='AD_PrintFormatTo_ID'
    ORDER BY p.SeqNo;

    --BEGIN
      v_Sec := (SELECT MAX(AD_PrintFormatItem_ID) FROM AD_PrintFormatItem) + 1;
      DROP SEQUENCE AD_Pfi;
      EXECUTE('CREATE SEQUENCE AD_Pfi START ' || v_Sec);
      /*Copia las Líneas del Formato Origen */
      INSERT INTO AD_PrintFormatItem(AD_Client_ID, AD_Column_ID, AD_Org_ID, AD_PrintColor_ID, AD_PrintFont_ID, AD_PrintFormatChild_ID, 
	AD_PrintFormat_ID, AD_PrintFormatItem_ID, AD_PrintGraph_ID, ArcDiameter, BarcodeType, BelowColumn, 
	Created, CreatedBy, FieldAlignmentType, FormatPattern, ImageIsAttached, ImageURL, IsActive, IsAveraged, 
	IsCentrallyMaintained, IsCounted, IsDeviationCalc, IsFilledRectangle, IsFixedWidth, IsGroupBy, IsHeightOneLine, 
	IsImageField, IsMaxCalc, IsMinCalc, IsNextLine, IsNextPage, IsOrderBy, IsPageBreak, IsPrinted, IsRelativePosition, 
	IsRunningTotal, IsSetNLPosition, IsSummarized, IsSuppressNull, IsSuppressRepeats, IsVarianceCalc, LineAlignmentType, 
	LineWidth, MaxHeight, MaxWidth, Name, PrintAreaType, PrintFormatType, PrintName, PrintNameSuffix, RunningTotalLines, 
	SeqNo, ShapeType, SortNo, Updated, UpdatedBy, XPosition, XSpace, YPosition, YSpace)

      SELECT apfi.AD_Client_ID, apfi.AD_Column_ID, apfi.AD_Org_ID, apfi.AD_PrintColor_ID, apfi.AD_PrintFont_ID, 
	apfi.AD_PrintFormatChild_ID, v_Form_To, nextval('AD_Pfi'), apfi.AD_PrintGraph_ID, 
	apfi.ArcDiameter, apfi.BarcodeType, apfi.BelowColumn, apfi.Created, apfi.CreatedBy, apfi.FieldAlignmentType, 
	apfi.FormatPattern, apfi.ImageIsAttached, apfi.ImageURL, apfi.IsActive, apfi.IsAveraged, apfi.IsCentrallyMaintained, 
	apfi.IsCounted, apfi.IsDeviationCalc, apfi.IsFilledRectangle, apfi.IsFixedWidth, apfi.IsGroupBy, apfi.IsHeightOneLine, 
	apfi.IsImageField, apfi.IsMaxCalc, apfi.IsMinCalc, apfi.IsNextLine, apfi.IsNextPage, apfi.IsOrderBy, apfi.IsPageBreak, 
	apfi.IsPrinted, apfi.IsRelativePosition, apfi.IsRunningTotal, apfi.IsSetNLPosition, apfi.IsSummarized, apfi.IsSuppressNull, 
	apfi.IsSuppressRepeats, apfi.IsVarianceCalc, apfi.LineAlignmentType, apfi.LineWidth, apfi.MaxHeight, apfi.MaxWidth, 
	apfi.Name, apfi.PrintAreaType, apfi.PrintFormatType, apfi.PrintName, apfi.PrintNameSuffix, apfi.RunningTotalLines, 
	apfi.SeqNo, apfi.ShapeType, apfi.SortNo, apfi.Updated, apfi.UpdatedBy, apfi.XPosition, apfi.XSpace, apfi.YPosition, apfi.YSpace
	FROM AD_PrintFormatItem apfi WHERE AD_PrintFormat_ID = v_Form_From;

      /*Copia la Traducción*/
      /*INSERT INTO AD_PrintFormatItem_Trl(AD_Client_ID, AD_Language, AD_Org_ID, AD_PrintFormatItem_ID, Created, CreatedBy, 
	IsActive, IsTranslated, PrintName, PrintNameSuffix, Updated, UpdatedBy)
      SELECT trlItem.AD_Client_ID, trlItem.AD_Language, trlItem.AD_Org_ID, trlItem.AD_PrintFormatItem_ID, trlItem.Created, trlItem.CreatedBy, 
	trlItem.IsActive, trlItem.IsTranslated, trlItem.PrintName, trlItem.PrintNameSuffix, trlItem.Updated, trlItem.UpdatedBy
	FROM AD_PrintFormatItem_Trl trlItem INNER JOIN AD_PrintFormatItem item ON(item.AD_PrintFormatItem_ID = trlItem.AD_PrintFormatItem_ID)
	WHERE item.AD_PrintFormat_ID = v_Form_From;*/

      UPDATE AD_PInstance 
        SET updated = current_date ,IsProcessing = 'N', Result = 1, errormsg = 'Copiado desde : ' || v_Form_From || ' a ' || v_Form_To
          WHERE AD_PInstance_ID = p_Instancia;    
    /*EXCEPTION
      WHEN OTHERS
      THEN
        UPDATE AD_PInstance 
          SET updated = current_date ,IsProcessing = 'Y', Result = 0, errormsg = 'Error'
          WHERE AD_PInstance_ID = p_Instancia;      
    END;*/
  END;
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;