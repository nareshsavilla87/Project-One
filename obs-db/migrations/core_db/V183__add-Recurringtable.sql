CREATE TABLE `b_recurring` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `subscriber_id` varchar(200) NOT NULL,
  `client_id` bigint(25) NOT NULL,
  `order_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`subscriber_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='utf8_general_ci'


INSERT ignore into c_configuration values(null,'sms-configuration',0,'{"URL":"http://smscountry.com/SMSCwebservice_Bulk.aspx"}');
