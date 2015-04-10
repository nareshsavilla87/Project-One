CREATE TABLE `b_recurring` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `subscriber_id` varchar(200) NOT NULL,
  `client_id` bigint(25) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`subscriber_id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='utf8_general_ci'
