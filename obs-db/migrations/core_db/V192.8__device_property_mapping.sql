CREATE TABLE IF NOT EXISTS `b_propertydevice_mapping` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) NOT NULL,
  `allocate_date` datetime NOT NULL,
  `serial_number` varchar(50) NOT NULL,
  `property_code` varchar(50) NOT NULL,
  `is_deleted` char(1) default 'N',
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
   PRIMARY KEY (`id`)
  );
  
insert ignore into m_permission VALUES (null,'client&orders','ALLOCATEDEVICE_PROPERTY','PROPERTY','ALLOCATEDEVICE',0);
