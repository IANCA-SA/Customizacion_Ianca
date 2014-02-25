SELECT 'dact.' || co.ColumnName || ', ' FROM AD_Table ta 
  INNER JOIN AD_Column co ON(co.AD_Table_ID = ta.AD_Table_ID)
WHERE TableName = 'AD_Document_Action_Access'