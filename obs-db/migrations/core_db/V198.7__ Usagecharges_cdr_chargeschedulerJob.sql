SET SQL_SAFE_UPDATES=0;

CREATE TABLE IF NOT EXISTS `b_usage_raw` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(10) NOT NULL,
  `number` varchar(30) NOT NULL,
  `time` datetime NOT NULL,
  `destination_number` varchar(40) DEFAULT NULL,
  `destination_location` varchar(50) DEFAULT NULL,
  `duration` decimal(24,4) DEFAULT NULL,
  `cost` decimal(24,4) DEFAULT NULL,
  `usage_charge_id` int(20) DEFAULT NULL,
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE IF NOT EXISTS `b_usage_charge` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(10) NOT NULL,
  `number` varchar(50) NOT NULL,
  `charge_date` datetime NOT NULL,
  `total_duration` decimal(24,4) DEFAULT NULL,
  `total_cost` decimal(24,4) NOT NULL,
  `charge_id` int(20) DEFAULT NULL,
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT IGNORE INTO m_permission VALUES(NULL,'billing', 'CREATE_CHARGES', 'CHARGES', 'CREATE', '0');

INSERT IGNORE INTO stretchy_report VALUES(NULL, 'UsageCharges', 'Table', '', 'Scheduling Job', 
'select client_id as clientId,number as number from b_usage_raw  where usage_charge_id is null  group by clientId,number', 
'Process Customer Usage Charges', '0', '0');


DROP procedure IF EXISTS jobUniqueName; 
DELIMITER //
CREATE procedure jobUniqueName() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME ='name' AND COLUMN_KEY='UNI'
     and TABLE_NAME ='job'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `job` ADD UNIQUE INDEX `name_unique` (`name` ASC);
END IF;
END //
DELIMITER ;
call jobUniqueName();
DROP procedure IF EXISTS jobUniqueName;

DROP procedure IF EXISTS jobParamUnique; 
DELIMITER //
CREATE procedure jobParamUnique() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE() and TABLE_NAME ='job_parameters'
      and COLUMN_NAME ='param_name' and INDEX_NAME = 'job_id_param')THEN
ALTER TABLE `job_parameters` ADD UNIQUE INDEX `job_id_param` (`job_id` ASC, `param_name` ASC);
END IF;
END //
DELIMITER ;
call jobParamUnique();
DROP procedure IF EXISTS jobParamUnique;

INSERT IGNORE INTO job VALUES(NULL, 'USAGECHARGES', 'Usage Charges', '0 30 23 1/1 * ? *', 'Daily once at Midnight',
 now(), '5', NULL, now(), NULL, 'USAGECHARGESJobDetaildefault _ DEFAULT', NULL, '0', '0', '1', '0', '0', '1');

SET @ID=(select id from job where name='USAGECHARGES');
INSERT IGNORE INTO job_parameters VALUES(NULL, @ID, 'reportName', 'COMBO', NULL, 'UsageCharges', 'N', NULL);

DROP procedure IF EXISTS `obsplatform-tenants`.tenantLocale; 
DELIMITER //
CREATE procedure `obsplatform-tenants`.tenantLocale() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME ='locale_name'
     and TABLE_NAME ='tenants'
     and TABLE_SCHEMA = 'obsplatform-tenants')THEN
ALTER TABLE tenants ADD COLUMN locale_name VARCHAR(20) DEFAULT NULL AFTER license_key;
UPDATE tenants SET locale_name='en_IN';
END IF;
END //
DELIMITER ;
call `obsplatform-tenants`.tenantLocale();
DROP procedure IF EXISTS `obsplatform-tenants`.tenantLocale;





