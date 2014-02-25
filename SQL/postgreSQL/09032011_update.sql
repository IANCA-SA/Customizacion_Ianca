select * from c_region where c_country_id = 339

update c_location set c_region_id = 490 where c_region_id = 1000010;
update c_location set c_region_id = 493 where c_region_id = 1000008;
update c_location set c_region_id = 491 where c_region_id = 1000005;
update c_location set c_region_id = 471 where c_region_id = 1000004;
update c_location set c_region_id = 470 where c_region_id = 1000002;
update c_location set c_region_id = 492 where c_region_id = 1000007;
update c_location set c_region_id = 479 where c_region_id = 1000014;
update c_location set c_region_id = 489 where c_region_id = 1000003;
update c_location set c_region_id = 486 where c_region_id = 1000011;
update c_location set c_region_id = 488 where c_region_id = 1000001;
update c_location set c_region_id = 472 where c_region_id = 1000012;
select * from c_city

select * from c_country

SELECT re.name, ci.name, cp.C_BPartner_ID,
  cp.value, cp.name, clo.name, clo.phone, So_CreditLimit
  FROM C_BPartner cp 
  LEFT JOIN C_BPartner_Location clo ON(cp.C_BPartner_ID = clo.C_BPartner_ID)
  LEFT JOIN C_Location lo ON(clo.C_Location_ID = lo.C_Location_ID)
  LEFT JOIN C_City ci ON(lo.C_City_ID = ci.C_City_ID)
  LEFT JOIN C_Region re ON(lo.C_Region_ID = re.C_Region_ID)
  --WHERE cp.isCustomer = 'Y'
GROUP BY re.name, ci.name, cp.C_BPartner_ID, 
  cp.value, cp.name, clo.name, clo.phone, So_CreditLimit

select * from c_invoice



update AD_Window_Access set isactive = 'Y' where ad_role_id = 1000002;
update AD_Process_Access set isactive = 'Y' where ad_role_id = 1000006;
update AD_Form_Access set isactive = 'Y' where ad_role_id = 1000002;
update AD_Document_Action_Access set isactive = 'Y' where ad_role_id = 1000002;
update AD_Window_Access set isactive = 'Y' where ad_role_id = 1000002
