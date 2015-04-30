CREATE TABLE `b_recurring` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `subscriber_id` varchar(200) NOT NULL,
  `client_id` bigint(25) NOT NULL,
  `order_id` bigint(20) DEFAULT NULL,
  `is_deleted` char(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`subscriber_id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='utf8_general_ci'


INSERT ignore into c_configuration values(null,'sms-configuration',0,'{"URL":"http://smscountry.com/SMSCwebservice_Bulk.aspx"}');

INSERT IGNORE INTO m_permission values(null,'billing&finance','UPDATECHANGEPAYPALSTATUS_PAYMENTGATEWAY','PAYMENTGATEWAY','UPDATECHANGEPAYPALSTATUS',0);

INSERT IGNORE INTO m_permission values(null,'billing&finance','UPDATEPAYPALPROFILESTATUS_PAYMENTGATEWAY','PAYMENTGATEWAY','UPDATEPAYPALPROFILESTATUS',0);

INSERT IGNORE INTO b_eventaction_mapping VALUES(null, 'Recurring DisConnect Order', 'RecurringDisconnect', 'workflow_events', 'N', 'N');

INSERT IGNORE INTO b_eventaction_mapping VALUES(null, 'Recurring ReConnection Order', 'RecurringReconnection', 'workflow_events', 'N', 'N');
