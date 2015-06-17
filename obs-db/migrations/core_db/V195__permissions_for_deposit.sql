insert ignore  into `m_permission`(`id`,`grouping`,`code`,`entity_name`,`action_name`,`can_maker_checker`) values (null,'client&orders','CREATE_DEPOSIT','DEPOSIT','CREATE',0);

insert ignore into c_paymentgateway_conf (id,name,enabled,value,description) VALUES (null,'evo',0,'{\"url\":\"https://spg.evopayments.eu/pay/payssl.aspx\",\"merchantId\":\"pg_57966\"}',"Evo"); 