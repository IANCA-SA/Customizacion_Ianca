SELECT TRUNC(EndDate) FROM C_Period 
  WHERE C_Year_ID IN (SELECT C_Year_ID FROM C_Year WHERE C_Calendar_ID= 1000002) 
  AND '2011-03-31 00:00:00.0' BETWEEN TRUNC(StartDate) AND TRUNC(EndDate) AND IsActive='Y' AND PeriodType='S'


SELECT * FROM C_Period WHERE C_Year_ID IN (SELECT C_Year_ID FROM C_Year 
  WHERE C_Calendar_ID= 1000002) AND cast('2011-03-31 00:00:00.0' as date) BETWEEN cast(StartDate as date)
AND cast(EndDate as date) AND IsActive='Y' AND PeriodType='S'