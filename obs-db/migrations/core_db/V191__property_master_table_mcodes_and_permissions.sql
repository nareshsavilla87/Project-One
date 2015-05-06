SET SQL_SAFE_UPDATES=0;
CREATE  TABLE IF NOT EXISTS `b_property_master` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `property_code_type` VARCHAR(40) NOT NULL ,
  `code` VARCHAR(20) NOT NULL ,
  `description` VARCHAR(200)  DEFAULT NULL ,
  `reference_value` VARCHAR(100)  DEFAULT NULL ,
  `is_deleted` char(2) Default 'N' ,
   PRIMARY KEY (`id`) ,
   UNIQUE KEY `property_code_type_with_its_code` (`property_code_type`, `code`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT IGNORE INTO m_permission VALUES(null,'organisation','READ_PROPERTYMASTER','PROPERTYMASTER','READ',0);
INSERT IGNORE INTO m_permission VALUES(null,'organisation','CREATE_PROPERTYMASTER','PROPERTYMASTER','CREATE',0);
INSERT IGNORE INTO m_permission VALUES(null,'organisation','UPDATE_PROPERTYMASTER','PROPERTYMASTER','UPDATE',0);
INSERT IGNORE INTO m_permission VALUES(null,'organisation','DELETE_PROPERTYMASTER','PROPERTYMASTER','DELETE',0);

INSERT IGNORE INTO m_code VALUES (null,'Property Code Type',0,'Define Customer Property Code Type');
SET @id = (select id from m_code where code_name='Property Code Type');

INSERT IGNORE INTO m_code_value VALUES (null,@id,'Parcel',0);
INSERT IGNORE INTO m_code_value VALUES (null,@id,'Level/Floor',1);
INSERT IGNORE INTO m_code_value VALUES (null,@id,'Building Codes',2);
INSERT IGNORE INTO m_code_value values (null, @id,'Unit Codes', '3');



 
