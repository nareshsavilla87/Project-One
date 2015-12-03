DROP TABLE IF EXISTS `b_chargevariant`;
CREATE TABLE `b_chargevariant` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `chargevariant_code` varchar(20) NOT NULL,
  `status` varchar(15) NOT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `is_delete` char(1) DEFAULT 'N',
  PRIMARY KEY (`id`),
  UNIQUE KEY `chargevariantcode` (`chargevariant_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `b_chargevariant_detail`;
CREATE TABLE `b_chargevariant_detail` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `chargevariant_id` int(10) NOT NULL,
  `variant_type` varchar(10) NOT NULL,
  `from_range` int(10) DEFAULT NULL,
  `to_range` int(10) DEFAULT NULL,
  `amount_type` varchar(10) NOT NULL,
  `amount` decimal(19,6) NOT NULL,
  `is_deleted` char(1) NOT NULL DEFAULT 'n',
  PRIMARY KEY (`id`),
  KEY `CHD_FK1` (`chargevariant_id`),
  CONSTRAINT `CHD_FK1` FOREIGN KEY (`chargevariant_id`) REFERENCES `b_chargevariant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


insert ignore into m_permission values(null,'billing','CREATE_CHARGEVARIANT','CHARGEVARIANT','CREATE',0);
insert ignore into m_permission values(null,'billing','UPDATE_CHARGEVARIANT','CHARGEVARIANT','UPDATE',0);
insert ignore into m_permission values(null,'billing','DELETE_CHARGEVARIANT','CHARGEVARIANT','DELETE',0);

INSERT IGNORE INTO m_code VALUES (null,'Variant Type',0,'Define Variant Type');
SET @id = (select id from m_code where code_name='Variant Type');

INSERT IGNORE INTO m_code_value VALUES (null,@id,'ANY',0);
INSERT IGNORE INTO m_code_value VALUES (null,@id,'Range',1);
