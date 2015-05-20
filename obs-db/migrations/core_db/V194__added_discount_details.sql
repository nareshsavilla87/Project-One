CREATE TABLE IF NOT EXISTS `b_discount_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `discount_id` int(20) NOT NULL,
  `category_type` varchar(30) NOT NULL,
  `discount_rate` decimal(22,6) NOT NULL,
  `is_deleted` char(1) DEFAULT 'N',
  PRIMARY KEY (`id`),
  UNIQUE KEY `discountid_with_category_uniquekey` (`discount_id`,`category_type`),
  KEY `fk_discount_id` (`discount_id`),
  CONSTRAINT `fk_discount_price_id` FOREIGN KEY (`discount_id`) REFERENCES `b_discount_master` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
