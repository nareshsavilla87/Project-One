CREATE TABLE IF NOT EXISTS `m_office_agreement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `office_id` bigint(20) NOT NULL,
  `agreement_status` varchar(20) NOT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `createdby_id` int(5) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` int(5) DEFAULT NULL,
  `is_deleted` char(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`),
  KEY `fk_office_agreement` (`office_id`),
  CONSTRAINT `fk_office_agreement` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
  ) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
  
  CREATE TABLE IF NOT EXISTS `m_office_agreement_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `agreement_id` bigint(20) NOT NULL,
  `source` bigint(30) NOT NULL,
  `share_amount` decimal(6,2) NOT NULL,
  `share_type` varchar(10) NOT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  `is_deleted` char(1) NOT NULL DEFAULT 'N',
    PRIMARY KEY (`id`),
  UNIQUE KEY `agreement_dtl_ai_ps_mc_uniquekey` (`agreement_id`,`source`),
  KEY `fk_agreement` (`agreement_id`),
  CONSTRAINT `fk_agreement_2` FOREIGN KEY (`agreement_id`) REFERENCES `m_office_agreement` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

Drop procedure if exists officeInfo;
DELIMITER //
create procedure officeInfo()
BEGIN
IF NOT EXISTS(SELECT * FROM INFORMATION_SCHEMA.COLUMNS
WHERE COLUMN_NAME ='is_collective' 
AND TABLE_NAME='m_office_additional_info'
AND TABLE_SCHEMA=DATABASE())THEN
Alter table m_office_additional_info add column is_collective char(2) DEFAULT 'N';
END IF;
END // 
DELIMITER ;
call officeInfo();
Drop procedure if exists officeInfo;


Drop procedure if exists officeAddress;
DELIMITER //
create procedure officeAddress()
BEGIN
IF EXISTS(SELECT * FROM INFORMATION_SCHEMA.COLUMNS
WHERE COLUMN_NAME ='company_logo' 
AND TABLE_NAME='b_office_address'
AND TABLE_SCHEMA=DATABASE())THEN
ALTER TABLE b_office_address CHANGE COLUMN  company_logo company_logo VARCHAR(500)  DEFAULT NULL;
END IF;
END // 
DELIMITER ;
call officeAddress();
Drop procedure if exists officeAddress;

insert ignore into `stretchy_report`(`id`,`report_name`,`report_type`,`report_subtype`,`report_category`,`report_sql`,`description`,`core_report`,`use_report`) 
values (null,'Agent Commission Report','Table',null,'Client' ,'select * from v_agent_commission where `invoice_date`  between ''${startDate}'' and ''${endDate}''','Agent Commission Report',1,1);

SET @id=(select id from stretchy_report where report_name='Agent Commission Report');

insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,1,'From Date');

insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,2,'To Date');

CREATE TABLE IF NOT EXISTS `b_office_commission` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `charge_id` int(20) NOT NULL DEFAULT '0',
  `partner_id` bigint(20) DEFAULT NULL,
  `office_id` bigint(20) NOT NULL,
  `invoice_date` datetime NOT NULL,
  `source` int(10),
  `share_amount` decimal(6,2),
  `share_type` varchar(10),
  `comm_source` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `amt` double(19,2) DEFAULT NULL,
  `created_dt` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

INSERT IGNORE INTO job VALUES(null, 'RESELLERCOMMISSION', 'Reseller Commission', '0 0 12 1/1 * ? *', 'Daily once at Midnight', '2015-01-26 15:59:45', '5', NULL, '2015-01-26 15:59:45', NULL, 'RESELLERCOMMISSIONJobDetaildefault _ DEFAULT', NULL, '0', '0', '1', '0', '0', '1');

SET @id=(select id from job where name='RESELLERCOMMISSION');

INSERT IGNORE INTO job_parameters VALUES(null ,@id, 'processDate', 'DATE', 'NOW()', '26 January 2015', 'Y', NULL);




CREATE OR REPLACE VIEW  `v_office_commission`
AS
   SELECT `bc`.`id` AS `charge_id`,
          `mc`.`office_id` AS `office_id`,
          `bi`.`invoice_date` AS `invoice_date`,
          `oad`.`source` AS `source`,
          `oad`.`share_amount` AS `share_amount`,
          `oad`.`share_type` AS `share_type`,
          `mcv`.`code_value` AS `comm_source`,
          (CASE
              WHEN (    (`oad`.`share_type` = 'Flat')
                    AND (`bc`.`charge_type` = 'NRC'))
              THEN
                 round(`oad`.`share_amount`, 2)
              WHEN (    (`oad`.`share_type` = 'Percentage')
                    AND (`bc`.`charge_type` <> 'NRC'))
              THEN
                 round(
                    ((`bc`.`netcharge_amount` * `oad`.`share_amount`) / 100),
                    2)
              ELSE
                 0
           END)
             AS `amt`,
          now() AS `created_dt`
     FROM (   (   (   (   (    `b_charge` `bc`
                           JOIN
                               `b_invoice` `bi`
                           ON ((`bc`.`invoice_id` = `bi`.`id`)))
                       JOIN
                           `m_client` `mc`
                       ON ((`bc`.`client_id` = `mc`.`id`)))
                   JOIN
                       `m_office_agreement` `oa`
                   ON ((`oa`.`office_id` = `mc`.`office_id`)))
               LEFT JOIN
                   `m_office_agreement_detail` `oad`
               ON ((    (`oad`.`agreement_id` = `oa`.`id`)
                    AND (`oad`.`is_deleted` = 'N'))))
           LEFT JOIN
               `m_code_value` `mcv`
           ON (((`mcv`.`id` = `oad`.`source`) AND (`mcv`.`code_id` = 75))));
		   

 CREATE OR REPLACE VIEW  `v_agent_commission` 
  AS 
  SELECT `boc`.`office_id` AS `reseller_id`,`r`.`name` AS `reseller`,`bc`.`client_id` AS `client_id`,
  `mc`.`display_name` AS `client_name`,cast(`boc`.`invoice_date` AS date) AS `invoice_date`,
  date_format(`boc`.`invoice_date`,'%b-%Y') AS `month`,`bca`.`country` AS `country`,
  coalesce(`bpm`.`plan_description`,`im`.`item_description`) AS `plan_item`,`mcv`.`code_value` AS `source`,
  cast(round(`bc`.`netcharge_amount`,2) AS char CHARSET utf8) AS `netcharge_amount`,
  `boc`.`share_amount` AS `share_amount`,`boc`.`share_type` AS `share_type`,
  cast(round(`boc`.`amt`,2) AS char CHARSET utf8) AS `comm_amount` FROM (((((((((( `b_office_commission` `boc` 
  LEFT JOIN  `m_code_value` `mcv` ON((`mcv`.`id` = `boc`.`source`))) JOIN  `m_office` `r` ON((`boc`.`office_id` = `r`.`id`))) 
  JOIN  `m_office` `s` ON((`boc`.`office_id` = `s`.`id`))) JOIN  `b_charge` `bc` ON((`boc`.`charge_id` = `bc`.`id`))) 
  JOIN  `m_client` `mc` ON((`bc`.`client_id` = `mc`.`id`))) JOIN  `b_client_address` `bca` ON(((`bc`.`client_id` = `bca`.`client_id`) 
  AND (`bca`.`address_key` = 'PRIMARY')))) LEFT JOIN  `b_onetime_sale` `bos` ON(((`bc`.`order_id` = `bos`.`id`) AND (`bc`.`priceline_id` = 0)))) LEFT JOIN  `b_item_master` `im` ON((`bos`.`item_id` = `im`.`id`))) LEFT JOIN  `b_orders` `bo` ON(((`bc`.`order_id` = `bo`.`id`) AND (`bc`.`priceline_id` <> 0)))) LEFT JOIN  `b_plan_master` `bpm` ON((`bo`.`plan_id` = `bpm`.`id`)));
		   
		   


