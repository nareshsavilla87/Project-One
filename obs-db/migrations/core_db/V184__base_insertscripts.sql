-- Charge Codes
insert ignore into `b_charge_codes`(`id`,`charge_code`,`charge_description`,`charge_type`,`charge_duration`,`duration_type`,`tax_inclusive`,`billfrequency_code`) values (null,'MSC','Monthly Subscription','RC',1,'Month(s)',0,'Monthly');
insert ignore into `b_charge_codes`(`id`,`charge_code`,`charge_description`,`charge_type`,`charge_duration`,`duration_type`,`tax_inclusive`,`billfrequency_code`) values (null,'QSC','Quaterly Subscription','RC',3,'Month(s)',0,'Quaterly');
insert ignore into `b_charge_codes`(`id`,`charge_code`,`charge_description`,`charge_type`,`charge_duration`,`duration_type`,`tax_inclusive`,`billfrequency_code`) values (null,'HSC','Half Yearly Subscription','RC',6,'Month(s)',0,'Halfyearly');
insert ignore into `b_charge_codes`(`id`,`charge_code`,`charge_description`,`charge_type`,`charge_duration`,`duration_type`,`tax_inclusive`,`billfrequency_code`) values (null,'YSC','Yearly Subscription','RC',12,'Month(s)',0,'yearly');
insert ignore into `b_charge_codes`(`id`,`charge_code`,`charge_description`,`charge_type`,`charge_duration`,`duration_type`,`tax_inclusive`,`billfrequency_code`) values (null,'OTC','One Time','NRC',1,'Month(s)',0,'Once');

-- Contract Periods
insert ignore into `b_contract_period`(`id`,`contract_period`,`contract_duration`,`contract_type`,`is_deleted`) values (null,'1 Month',1,'Month(s)','N');
insert ignore into `b_contract_period`(`id`,`contract_period`,`contract_duration`,`contract_type`,`is_deleted`) values (null,'3 Months',3,'Month(s)','N');
insert ignore into `b_contract_period`(`id`,`contract_period`,`contract_duration`,`contract_type`,`is_deleted`) values (null,'6 Months',6,'Month(s)','N');
insert ignore into `b_contract_period`(`id`,`contract_period`,`contract_duration`,`contract_type`,`is_deleted`) values (null,'1 Year',12,'Month(s)','N');

-- Billing rules 

insert ignore into `b_billing_rules`(`id`,`billing_rule`) values (null,'Prorata & DC');
insert ignore into `b_billing_rules`(`id`,`billing_rule`) values (null,'Prorata & NODC');
insert ignore into `b_billing_rules`(`id`,`billing_rule`) values (null,'Full Month & NODC');

-- Discount Codes

insert ignore into `b_discount_master`(`id`,`discount_code`,`discount_description`,`discount_type`,`discount_rate`,`start_date`,`discount_status`,`is_delete`) values (null,'None','None','Flat',0,'2015-04-16 00:00:00','ACTIVE','N');

-- Event Action Mapping

insert ignore into `b_eventaction_mapping`(`id`,`event_name`,`action_name`,`process`,`is_deleted`,`is_synchronous`) values (null,'Create Client','Send Mail','workflow_events','Y','N');
insert ignore into `b_eventaction_mapping`(`id`,`event_name`,`action_name`,`process`,`is_deleted`,`is_synchronous`) values (null,'Create Client','SEND SMS','workflow_events','Y','N');
insert ignore into `b_eventaction_mapping`(`id`,`event_name`,`action_name`,`process`,`is_deleted`,`is_synchronous`) values (null,'Order activation','Invoice','workflow_events','Y','N');
insert ignore into `b_eventaction_mapping`(`id`,`event_name`,`action_name`,`process`,`is_deleted`,`is_synchronous`) values (null,'Order booking','INVOICE','workflow_events','Y','Y');
insert ignore into `b_eventaction_mapping`(`id`,`event_name`,`action_name`,`process`,`is_deleted`,`is_synchronous`) values (null,'Close Client','SEND PROVISION','workflow_events','Y','N');
insert ignore into `b_eventaction_mapping`(`id`,`event_name`,`action_name`,`process`,`is_deleted`,`is_synchronous`) values (null,'Create Ticket','Send Email','workflow_events','Y','N');
insert ignore into `b_eventaction_mapping`(`id`,`event_name`,`action_name`,`process`,`is_deleted`,`is_synchronous`) values (null,'Create Client','Send Mail','workflow_events','Y','N');
insert ignore into `b_eventaction_mapping`(`id`,`event_name`,`action_name`,`process`,`is_deleted`,`is_synchronous`) values (null,'Create Live Event','Active Live Event','workflow_events','Y','N');
insert ignore into `b_eventaction_mapping`(`id`,`event_name`,`action_name`,`process`,`is_deleted`,`is_synchronous`) values (null,'Create Payment','RENEWAL','workflow_events','Y','N');
insert ignore into `b_eventaction_mapping`(`id`,`event_name`,`action_name`,`process`,`is_deleted`,`is_synchronous`) values (null,'Create Live Event','Active Live Event','workflow_events','Y','Y');
insert ignore into `b_eventaction_mapping`(`id`,`event_name`,`action_name`,`process`,`is_deleted`,`is_synchronous`) values (null,'Create Payment','Create Payment','workflow_events','Y','N');

-- Message Templates

insert ignore into `b_message_template`(`id`,`template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) values (1,'TICKET_TEMPLATE','TICKET','TICKET','create ticket','Thank You','E',1,null,1,'2015-04-16 12:10:37','N');
insert ignore into `b_message_template`(`id`,`template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) values (2,'Bill_Email','TAX INVOICE from Obs','Bill','Please find the attached TAX INVOCIE from Spicenet','Thanks','E',1,'2014-07-30 12:33:42',1,'2014-07-30 12:33:42','N');
insert ignore into `b_message_template`(`id`,`template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) values (5,'CREATE SELFCARE','OBS Selfcare','Dear <PARAM1>','Your Selfcare User Account has been successfully created,Following are the User login Details. <br/> userName : <PARAM2> , <br/> password : <PARAM3> .','Thankyou','E',null,null,null,null,'N');
insert ignore into `b_message_template`(`id`,`template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) values (6,'SELFCARE REGISTRATION','Register Confirmation','Hai','Your Registration has been successfully completed.To approve this Registration please click on this link: <br/> URL : <PARAM1>.','Thankyou','E',null,null,null,null,'N');
insert ignore into `b_message_template`(`id`,`template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) values (7,'NEW SELFCARE PASSWORD','Reset Password','Dear <PARAM1>','The password for your SelfCare User Portal Account- <PARAM2>  was reset. . <br/> Password : <PARAM3>.','Thankyou','E',null,null,null,null,'N');
insert ignore into `b_message_template`(`id`,`template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) values (8,'PROVISION CREDENTIALS','OBS Provision Credentials','Dear <PARAM1>','Your OBS Subscriber Account has been successfully created And Following are the Account Details.  <br/> subscriberUid : <PARAM2> , <br/>  Authpin : <PARAM3> .','Thankyou','E',null,null,null,null,'N');
insert ignore into `b_message_template`(`id`,`template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) values (9,'CREATE USER','OBS User Creation','Dear <PARAM1>','OBS User Account has been successfully created .You can login using the following credentials. 
 userName : <PARAM2> , 
 password : <PARAM3> .','Thankyou','E',null,null,null,null,'N');
insert ignore into `b_message_template`(`id`,`template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) values (10,'PAYMENT_RECEIPT','Payment Confirmation','Dear <PARAM1><br/><br/>','Thank you for making your purchase for OBS.<br/><br/>
 This is a confirmation of your payment.<br/><br/> Result : <PARAM2>,<br/> Description : <PARAM3>,<br/>Amount : <PARAM4>,<br/>
 ReceiptNo : <PARAM5>.<br/>','Thankyou','E',null,null,null,null,'N');
insert ignore into `b_message_template`(`id`,`template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) values (11,'Reminder For Expiry','service expiry','Dear <Param1>','Your service with <Param2> is going to expired on <Param3>.
Please renew or top-up to avoid service disconnection. Please call us or do the renew through your selfcare portal <Param4>','Thanks <br/> <Param5> <br/> <Param6>','E',1,'2015-04-14 18:23:13',1,'2015-04-14 18:25:24','N');
insert ignore into `b_message_template`(`id`,`template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) values (12,'Newly_Activated_Customers','New_customers','Dear <Param1>','Thanks for subscribing to our services. 
Your service with <Param2> is activated on <Param3>.','Thanks <br/> <Param4> <br/> <Param5>','E',1,'2015-04-14 18:54:51',1,'2015-04-14 18:54:51','N');
insert ignore into `b_message_template`(`id`,`template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) values (13,'Disconnected_Customers','Disconnected Customers','Dear <Param1>','Your service with <Param2> is disconnected on <Param3>.
Please call us or do the renew or Top-up through your selfcare portal <Param4>','Thanks <br/> <Param5> <br/> <Param6>','E',1,'2015-04-14 18:56:51',1,'2015-04-14 18:56:51','N');
insert ignore into `b_message_template`(`id`,`template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) values (14,'Renew_or_Top-up_Customer','Renew or Top-up Customer','Dear <Param1>','Thanks for subscribing to our services. 
Your service with <Param2> is activated on <Param3>.','Thanks <br/> <Param4> <br/> <Param5>','E',1,'2015-04-14 18:59:11',1,'2015-04-14 18:59:11','N');

-- Provisioning Actions

insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (null,'Create Client','CLIENT ACTIVATION','CMS','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (null,'Close Client','TERMINATE','CMS','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (null,'Event Order','PROVISION IT','Radius','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (null,'Release Device','RELEASE DEVICE','Radius','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (null,'Create Agent','CREATE AGENT','CMS','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (null,'Create Nas','CREATE NAS','Radius','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (null,'Create RadSevice','CREATE RADSERVICE','Radius','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (null,'Change Credentials','CHANGE CREDENTIALS','Radius','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (null,'Update RadService','UPDATE RADSERVICE','Radius','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (null,'Remove RadService','REMOVE RADSERVICE','Radius','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (null,'Update RadSevice','UPDATE RADSERVICE','Radius','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (null,'Remove RadSevice','REMOVE RADSERVICE','Radius','N','N');

-- Price Region 
insert ignore into b_priceregion_master (id,priceregion_code,priceregion_name,createdby_id,created_date,is_deleted) 
 VALUES (null,'Default','Default Region',null,null,'N');

 
 insert ignore into b_priceregion_detail (priceregion_id,country_id,state_id,is_deleted)
select prm.id,0,0,'N' from b_priceregion_master prm where prm.priceregion_code ='Default';

-- Views
-----------
  
-- Financial Transaction view 
CREATE OR REPLACE VIEW  `fin_trans_vw` AS SELECT  `m_appuser`.`username` AS `username`, `b_invoice`.`client_id` AS `client_id`, `b_invoice`.`id` AS `transId`,if(( `b_charge`.`charge_type` = 'NRC'),'Once','Periodic') AS `tran_type`,cast( `b_invoice`.`invoice_date` AS date) AS `transDate`,'INVOICE' AS `transType`,if(( `b_invoice`.`invoice_amount` > 0), `b_invoice`.`invoice_amount`,0) AS `dr_amt`,if(( `b_invoice`.`invoice_amount` < 0),abs( `b_invoice`.`invoice_amount`),0) AS `cr_amt`,1 AS `flag` FROM (( `b_invoice` JOIN  `m_appuser`) JOIN  `b_charge`) WHERE (( `b_invoice`.`createdby_id` =  `m_appuser`.`id`) AND ( `b_invoice`.`id` =  `b_charge`.`invoice_id`) AND ( `b_invoice`.`invoice_date` <= now())) UNION ALL SELECT  `m_appuser`.`username` AS `username`, `b_adjustments`.`client_id` AS `client_id`, `b_adjustments`.`id` AS `transId`,(SELECT  `m_code_value`.`code_value` FROM  `m_code_value` WHERE (( `m_code_value`.`code_id` = 12) AND ( `b_adjustments`.`adjustment_code` =  `m_code_value`.`id`))) AS `tran_type`,cast( `b_adjustments`.`adjustment_date` AS date) AS `transdate`,'ADJUSTMENT' AS `transType`,0 AS `dr_amt`,(CASE  `b_adjustments`.`adjustment_type` WHEN 'CREDIT' THEN  `b_adjustments`.`adjustment_amount` END) AS `cr_amount`,1 AS `flag` FROM ( `b_adjustments` JOIN  `m_appuser`) WHERE (( `b_adjustments`.`adjustment_date` <= now()) AND ( `b_adjustments`.`adjustment_type` = 'CREDIT') AND ( `b_adjustments`.`createdby_id` =  `m_appuser`.`id`)) UNION ALL SELECT  `m_appuser`.`username` AS `username`, `b_adjustments`.`client_id` AS `client_id`, `b_adjustments`.`id` AS `transId`,(SELECT  `m_code_value`.`code_value` FROM  `m_code_value` WHERE (( `m_code_value`.`code_id` = 12) AND ( `b_adjustments`.`adjustment_code` =  `m_code_value`.`id`))) AS `tran_type`,cast( `b_adjustments`.`adjustment_date` AS date) AS `transdate`,'ADJUSTMENT' AS `transType`,(CASE  `b_adjustments`.`adjustment_type` WHEN 'DEBIT' THEN  `b_adjustments`.`adjustment_amount` END) AS `dr_amount`,0 AS `cr_amt`,1 AS `flag` FROM ( `b_adjustments` JOIN  `m_appuser`) WHERE (( `b_adjustments`.`adjustment_date` <= now()) AND ( `b_adjustments`.`adjustment_type` = 'DEBIT') AND ( `b_adjustments`.`createdby_id` =  `m_appuser`.`id`)) UNION ALL SELECT  `m_appuser`.`username` AS `username`, `b_payments`.`client_id` AS `client_id`, `b_payments`.`id` AS `transId`,(SELECT  `m_code_value`.`code_value` FROM  `m_code_value` WHERE (( `m_code_value`.`code_id` = 11) AND ( `b_payments`.`paymode_id` =  `m_code_value`.`id`))) AS `tran_type`,cast( `b_payments`.`payment_date` AS date) AS `transDate`,'PAYMENT' AS `transType`,0 AS `dr_amt`, `b_payments`.`amount_paid` AS `cr_amount`, `b_payments`.`is_deleted` AS `flag` FROM ( `b_payments` JOIN  `m_appuser`) WHERE (( `b_payments`.`createdby_id` =  `m_appuser`.`id`) AND ( `b_payments`.`payment_date` <= now())) UNION ALL SELECT  `m_appuser`.`username` AS `username`, `b_payments`.`client_id` AS `client_id`, `b_payments`.`id` AS `transId`,(SELECT  `m_code_value`.`code_value` FROM  `m_code_value` WHERE (( `m_code_value`.`code_id` = 11) AND ( `b_payments`.`paymode_id` =  `m_code_value`.`id`))) AS `tran_type`,cast( `b_payments`.`payment_date` AS date) AS `transDate`,'PAYMENT CANCELED' AS `transType`, `b_payments`.`amount_paid` AS `dr_amt`,0 AS `cr_amount`, `b_payments`.`is_deleted` AS `flag` FROM ( `b_payments` JOIN  `m_appuser`) WHERE (( `b_payments`.`is_deleted` = 1) AND ( `b_payments`.`createdby_id` =  `m_appuser`.`id`) AND ( `b_payments`.`payment_date` <= now())) UNION ALL SELECT `ma`.`username` AS `username`,`bjt`.`client_id` AS `client_id`,`bjt`.`id` AS `transId`,'Event Journal' AS `tran_type`,cast(`bjt`.`jv_date` AS date) AS `transDate`,'JOURNAL VOUCHER' AS `transType`,ifnull(`bjt`.`debit_amount`,0) AS `dr_amt`,ifnull(`bjt`.`credit_amount`,0) AS `cr_amount`,1 AS `flag` FROM ( `b_jv_transactions` `bjt` JOIN  `m_appuser` `ma` ON(((`bjt`.`createdby_id` = `ma`.`id`) AND (`bjt`.`jv_date` <= now())))) ORDER BY 1,2;

-- Movie all
CREATE OR REPLACE VIEW  `mvAll_vw` AS select  `b_media_asset`.`id` AS `mediaId`, `b_media_asset`.`title` AS `title`, `b_media_asset`.`category_id` AS `category`, `b_media_asset`.`image` AS `image`, `b_media_asset`.`rating` AS `rating`, `b_mod_master`.`event_description` AS `eventDescription`, `b_mod_pricing`.`Opt_type` AS `optType`, `b_mod_pricing`.`event_id` AS `eventId`, `b_mod_pricing`.`format_type` AS `quality`,`c`.`code_value` AS `clientType`, `b_mod_pricing`.`price` AS `price` from (((( `b_mod_master` left join  `b_mod_detail` on(( `b_mod_master`.`id` =  `b_mod_detail`.`event_id`))) left join  `b_mod_pricing` on(( `b_mod_master`.`id` =  `b_mod_pricing`.`event_id`))) left join  `b_media_asset` on(( `b_media_asset`.`id` =  `b_mod_detail`.`media_id`))) left join  `m_code_value` `c` on(( `b_mod_pricing`.`client_typeid` = `c`.`id`))) where (date_format(now(),'%Y-%m-%d') between date_format( `b_mod_master`.`event_start_date`,'%Y-%m-%d') and ifnull(date_format( `b_mod_master`.`event_end_date`,'%Y-%m-%d'),'2090-12-31'));

-- mvComing_view
CREATE OR REPLACE  VIEW  `mvComing_vw` AS select `m`.`id` AS `mediaId`,`m`.`title` AS `title`,`m`.`image` AS `image`,`m`.`rating` AS `rating`,0 AS `eventId`,'C' AS `assetTag` from  `b_media_asset` `m` where (`m`.`category_id` = 19);

-- Discountmovies
CREATE OR REPLACE VIEW  `mvDiscount_vw` AS select `m`.`id` AS `mediaId`,`m`.`title` AS `title`,`m`.`image` AS `image`,`m`.`rating` AS `rating`,`ed`.`event_id` AS `eventId`,count(`ed`.`media_id`) AS `media_count` from ((( `b_media_asset` `m` join  `b_mod_detail` `ed` on((`ed`.`media_id` = `m`.`id`))) join  `b_mod_master` `em` on((`em`.`id` = `ed`.`event_id`))) join  `b_mod_pricing` `ep` on((`em`.`id` = `ep`.`event_id`))) where (`ep`.`discount_id` >= 1) group by `m`.`id` having (count(distinct `ed`.`event_id`) >= 1);

-- Highrated movies
CREATE OR REPLACE VIEW    `mvHighRate_vw` AS select `m`.`id` AS `mediaId`,`m`.`title` AS `title`,`m`.`image` AS `image`,`m`.`rating` AS `rating`,`ed`.`event_id` AS `eventId`,count(`ed`.`media_id`) AS `media_count` from (( `b_media_asset` `m` left join  `b_mod_detail` `ed` on((`ed`.`media_id` = `m`.`id`))) left join  `b_mod_master` `em` on((`em`.`id` = `ed`.`event_id`))) where (`m`.`rating` >= 4.5) group by `m`.`id`,`m`.`title`;
-- Release movies
CREATE OR REPLACE VIEW  `mvNewRelease_vw` AS select `m`.`id` AS `mediaId`,`m`.`title` AS `title`,`m`.`image` AS `image`,`m`.`rating` AS `rating`,`ed`.`event_id` AS `eventId`,count(`ed`.`media_id`) AS `media_count` from (( `b_media_asset` `m` left join  `b_mod_detail` `ed` on((`ed`.`media_id` = `m`.`id`))) left join  `b_mod_master` `em` on((`em`.`id` = `ed`.`event_id`))) where (`m`.`release_date` <= (now() + interval -(3) month)) group by `m`.`id`;

-- Promotion movies
CREATE OR REPLACE VIEW  `mvPromotion_vw` AS select `ed`.`event_id` AS `event_id`,`ma`.`id` AS `mediaId`,`ma`.`title` AS `title`,`ma`.`image` AS `image`,`ed`.`event_id` AS `eventId`,`ma`.`rating` AS `rating` from ( `b_media_asset` `ma` join  `b_mod_detail` `ed` on((`ed`.`media_id` = `ma`.`id`))) where `ed`.`event_id` in (select `ed`.`event_id` from ( `b_mod_master` `em` join  `b_mod_detail` `ed` on((`em`.`id` = `ed`.`event_id`))) group by `ed`.`event_id` having (count(`ed`.`event_id`) > 1));

-- Watched movies
CREATE OR REPLACE VIEW  `mvWatched_vw` AS select `m`.`id` AS `mediaId`,`m`.`title` AS `title`,`m`.`image` AS `image`,`m`.`rating` AS `rating`,'W' AS `assetTag`,`m`.`release_date` AS `release_date`,`ed`.`event_id` AS `eventId`,count(`eo`.`id`) AS `COUNT(eo.id)` from (( `b_media_asset` `m` join  `b_mod_detail` `ed` on((`m`.`id` = `ed`.`media_id`))) join  `b_modorder` `eo` on((`eo`.`event_id` = `ed`.`event_id`))) order by 6 desc;


-- Statement generation --

CREATE OR REPLACE VIEW `billdetails_v` AS 
select 
    b.client_id AS client_id,
    a.id AS transId,
    b.invoice_date AS transDate,
    'SERVICE_CHARGES' AS transType,
   -- if((a.charge_type = 'DC'),
     --   (0 - a.netcharge_amount),
        a.netcharge_amount AS amount,
    concat(date_format(a.charge_start_date, '%Y-%m-%d'),
            ' to ',
            date_format(a.charge_end_date, '%Y-%m-%d')) AS description,
    c.plan_id AS plan_code,
    a.charge_type AS service_code
from
    ((b_charge a
    join b_invoice b)
    join b_orders c)
where
    ((a.invoice_id = b.id) and (a.order_id = c.id) and isnull(a.bill_id) and (b.invoice_date <= now()) and (a.priceline_id <> 0)) 
union all select 
    b.client_id AS client_id,
    a.id AS transId,
    date_format(b.invoice_date, '%Y-%m-%d') AS transDate,
    'TAXES' AS transType,
   -- if(((c.charge_type = 'DC') or (a.tax_value = 1)),
    --    (0 - a.tax_amount),
        a.tax_amount AS amount,
    a.tax_code AS description,
    NULL AS plan_code,
    c.charge_type AS service_code
from
    ((b_charge_tax a
    join b_invoice b)
    join b_charge c)
where
    ((a.invoice_id = b.id) and (a.charge_id = c.id) and isnull(a.bill_id) and (b.invoice_date <= now())) 
union all select 
    b_adjustments.client_id AS client_id,
    b_adjustments.id AS transId,
    date_format(b_adjustments.adjustment_date,
            '%Y-%m-%d') AS transDate,
    'ADJUSTMENT' AS transType,
    (case b_adjustments.adjustment_type
        when 'DEBIT' then b_adjustments.adjustment_amount
        when 'CREDIT' then -(b_adjustments.adjustment_amount)
    end) AS amount,
    b_adjustments.remarks AS remarks,
    b_adjustments.adjustment_type AS adjustment_type,
    NULL AS service_code
from
    b_adjustments
where
    (isnull(b_adjustments.bill_id) and (b_adjustments.adjustment_date <= now())) 
union all select 
    pa.client_id AS client_id,
    pa.id AS transId,
    date_format(pa.payment_date, '%Y-%m-%d') AS transDate,
    concat('PAYMENT', ' - ', p.code_value) AS transType,
    pa.amount_paid AS invoiceAmount,
    pa.Remarks AS remarks,
    p.code_value AS code_value,
    NULL AS service_code
from
    (b_payments pa
    join m_code_value p)
where
    (isnull(pa.bill_id) and (pa.payment_date <= now()) and (pa.paymode_id = p.id)) 
union all select 
    b.client_id AS client_id,
    a.id AS transId,
    date_format(c.sale_date, '%Y-%m-%d') AS transDate,
    'ONETIME_CHARGES' AS transType,
    a.netcharge_amount AS amount,
    c.charge_code AS charge_code,
    c.item_id AS item_id,
    a.charge_type AS service_code
from
    ((b_charge a
    join b_invoice b)
    join b_onetime_sale c)
where
    ((a.invoice_id = b.id) and (a.order_id = c.id) and isnull(a.bill_id) and (c.sale_date <= now()) and (c.invoice_id = b.id) and (a.priceline_id = 0))
union all select 
    b.client_id AS client_id,
    a.id AS transId,
    b.invoice_date AS transDate,
    'SERVICE_TRANSFER' AS transType,
     a.netcharge_amount AS amount,
    concat(date_format(a.charge_start_date, '%Y-%m-%d'),
            ' to ',
            date_format(a.charge_end_date, '%Y-%m-%d')) AS description,
    ph.property_code AS plan_code,
    a.charge_type AS service_code
from
    ((b_charge a
    join b_invoice b)
    join b_property_history ph)
where
    ((a.invoice_id = b.id) and (a.order_id = ph.id) 
and isnull(a.bill_id) and (b.invoice_date <= now()) and (a.priceline_id = -1)) ;











