SELECT lde.DateAcct, lde.XXFechaDocumento, lde.Name, lde.Nro_Referencia, 
  lde.NroControl, lde.XXMontoBase, lde.XXMontoPagado, lde.Amount, lde.CashType,
  lde.TasaImp, (lde.XXMontoBase * lde.TasaImp) TotalImpuesto
  /*(lde.Amount - lde.XXMontoBase) MontoExento,
  (lde.Amount - lde.XXMontoBase)*/
  FROM XX_RV_Diario_Efectivo_Libro lde
  WHERE lde.XXAfectaLibro = 'Y' 
  AND lde.CashType = 'C'