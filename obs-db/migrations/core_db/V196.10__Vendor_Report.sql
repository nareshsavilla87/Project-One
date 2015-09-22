SET SQL_SAFE_UPDATES=0;
CREATE or REPLACE VIEW `netplnactiv_content_vw` AS 
select `nav`.`year_mon` AS `Month`,
`plan_id` AS planId,plan, `bad`.`vendor_agmt_id` AS agmtId,
sum(`nav`.`Op_Active`) AS `Op_Bal`,
sum(`nav`.`new`) AS `New`,
sum(`nav`.`rec`) AS `Reconn`,
sum(`nav`.`ren`) AS `Renewal`,
(sum(`nav`.`Op_Pending_add`) + sum(`nav`.`Pending_add`)) AS `Pending_add`,
sum(((((`nav`.`new` + `nav`.`rec`) + `nav`.`ren`) + `nav`.`Op_Pending_add`) + `nav`.`Pending_add`)) AS `NetAdditions`,
sum(((((`nav`.`Op_Active` + `nav`.`new`) + `nav`.`rec`) + `nav`.`Op_Pending_add`) + `nav`.`Pending_add`)) AS `Total`,
sum(`nav`.`Del`) AS `Deletions`,
sum(`nav`.`Pending_del`) AS `Pending_del`,
sum(`nav`.`Cum_Pending`) AS `Cum_Pending`,
(sum(`nav`.`Del`) + sum(`nav`.`Pending_del`)) AS `NetSub`,
sum(((((((`nav`.`Op_Active` + `nav`.`new`) + `nav`.`rec`) + `nav`.`Op_Pending_add`) + `nav`.`Pending_add`) - `nav`.`Del`) - `nav`.`Pending_del`)) AS `NetBal`,
sum(`nav`.`Op_Active` + `nav`.`new` + `nav`.`rec`   - `nav`.`Del`)  AS `NetBalP`,
sum(`nav`.`Cl_Active`) AS `ClosingBal` ,
sum(`content_cost`) AS `Purchase_Cost` ,
sum(`nav`.`Cl_Active` * bad.content_cost) AS `Net_Purchase_Cost`,
sum(`nav`.`Cl_Active` * bad.content_sellprice ) AS `Content_Sell`,
sum(`nav`.`Cl_Active` * bad.content_sellprice )  - sum(`nav`.`Cl_Active` * `content_cost`) AS `NetMarkup`,
(sum(`nav`.`Cl_Active` * bad.content_cost) * loyalty_share /100 ) AS `Vendor_Commission`,
sum(`nav`.`Cl_Active` * bad.content_sellprice )  - sum(`nav`.`Cl_Active` * `content_cost`) +
(sum(`nav`.`Cl_Active` * bad.content_cost) * loyalty_share /100 ) AS `Vendor_Profit`
from `net_activedtls_vw` nav 
left join  `b_vendor_agmt_detail` bad on ( nav.plan_id = bad.content_code and content_cost is not null)
left join `b_order_price` bop on (nav.order_id = bop.order_id )
group by `nav`.`year_mon` ,plan_id,plan, bad.vendor_agmt_id 
order by `nav`.`month_number`;

UPDATE stretchy_report SET report_name='Vendor Commission Report' WHERE report_name='Plan wise Revenue Report for Vendor';
