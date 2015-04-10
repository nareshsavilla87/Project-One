CREATE TABLE IF NOT EXISTS `b_property_defination` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `property_type_id` int(20) NOT NULL,
  `property_code` varchar(50) NOT NULL,
  `unit_code` varchar(20) NOT NULL,
  `floor` int(20) NOT NULL,
  `building_code` varchar(20) NOT NULL,
  `parcel` varchar(50) NOT NULL,
  `street` varchar(50) DEFAULT NULL,
  `precinct` varchar(50) NOT NULL,
  `po_box` varchar(20) NOT NULL,
  `state` varchar(20) NOT NULL,
  `country` varchar(20) NOT NULL,
  `status` varchar(10) DEFAULT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `property_code_constraint` (`property_code`),
  KEY `fk_prop_cid` (`client_id`),
  CONSTRAINT `fk_prop_cid` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

INSERT IGNORE INTO m_permission VALUES(null, 'organisation', 'CREATE_PROPERTY', 'PROPERTY', 'CREATE', '0');
INSERT IGNORE INTO m_permission VALUES(null, 'organisation', 'READ_PROPERTY', 'PROPERTY', 'READ', '0');
INSERT IGNORE INTO m_permission VALUES(null, 'organisation', 'UPDATE_PROPERTY', 'PROPERTY', 'UPDATE', '0');
INSERT IGNORE INTO m_permission VALUES(null, 'organisation', 'DELETE_PROPERTY', 'PROPERTY', 'DELETE', '0');

INSERT IGNORE INTO `m_code`(id,code_name,is_system_defined,code_description) VALUES(null,'Property Type',0,'Define Customer Property Type');
SET @a_lid:=(select id from m_code where code_name='Property Type');
INSERT IGNORE INTO `m_code_value`(id,code_id,code_value,order_position) VALUES(null,@a_lid,'Tower',0);
INSERT IGNORE INTO `m_code_value`(id,code_id,code_value,order_position) VALUES(null,@a_lid,'Plot Villa',0);
INSERT IGNORE INTO `m_code_value`(id,code_id,code_value,order_position) VALUES(null,@a_lid,'Hotel',0);

INSERT IGNORE INTO c_configuration VALUES(null, 'property-master', '1', '');



