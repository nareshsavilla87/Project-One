CREATE TABLE IF NOT EXISTS `b_deposit_refund` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(10) NOT NULL,
  `transaction_date` datetime NOT NULL,
  `transaction_type` varchar(50) NOT NULL,
  `item_id` int(10) DEFAULT NULL,
  `credit_amount` decimal(24,4) DEFAULT NULL,
  `debit_amount` decimal(24,4) DEFAULT NULL,
  `ref_id` bigint(20) DEFAULT NULL,
  `payment_id` int(10) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `is_refund` char(1) DEFAULT 'N',
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DELIMITER $$
Drop procedure IF EXISTS itemid $$
create procedure itemid() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'item_id'
     and TABLE_NAME = 'b_fee_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER table b_fee_master add `item_id` int(10) DEFAULT NULL;
end if;

IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_refundable'
     and TABLE_NAME = 'b_fee_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER table b_fee_master add `is_refundable` varchar(1) DEFAULT NULL;
end if;

END $$
delimiter ;
call itemid();
Drop procedure IF EXISTS itemid ;


INSERT IGNORE INTO m_code VALUES (null,'Transaction Type',0,'Define Customer Transaction Type');
SET @id = (select id from m_code where code_name='Transaction Type');
INSERT IGNORE INTO m_code_value VALUES (null,@id,'Deposit',3);

INSERT IGNORE INTO m_permission values(null,'billing','CREATE_REFUND','REFUND','CREATE',0);


CREATE OR REPLACE VIEW  `fin_trans_vw`
AS
   SELECT DISTINCT
           `b_invoice`.`id` AS `transId`,
           `m_appuser`.`username` AS `username`,
           `b_invoice`.`client_id` AS `client_id`,
          if(( `b_charge`.`charge_type` = 'NRC'),
             'Once',
             'Periodic')
             AS `tran_type`,
          cast( `b_invoice`.`invoice_date` AS date)
             AS `transDate`,
          'INVOICE' AS `transType`,
          if(( `b_invoice`.`invoice_amount` > 0),
              `b_invoice`.`invoice_amount`,
             0)
             AS `dr_amt`,
          if(( `b_invoice`.`invoice_amount` < 0),
             abs( `b_invoice`.`invoice_amount`),
             0)
             AS `cr_amt`,
          1 AS `flag`
     FROM (   (    `b_invoice`
               JOIN
                   `m_appuser`)
           JOIN
               `b_charge`)
    WHERE (    ( `b_invoice`.`createdby_id` =
                    `m_appuser`.`id`)
           AND ( `b_invoice`.`id` =
                    `b_charge`.`invoice_id`)
           AND ( `b_invoice`.`invoice_date` <= now()))
   UNION ALL
   SELECT DISTINCT
           `b_adjustments`.`id` AS `transId`,
           `m_appuser`.`username` AS `username`,
           `b_adjustments`.`client_id` AS `client_id`,
          (SELECT  `m_code_value`.`code_value`
             FROM  `m_code_value`
            WHERE (    ( `m_code_value`.`code_id` = 12)
                   AND ( `b_adjustments`.`adjustment_code` =
                            `m_code_value`.`id`)))
             AS `tran_type`,
          cast( `b_adjustments`.`adjustment_date` AS date)
             AS `transdate`,
          'ADJUSTMENT' AS `transType`,
          0 AS `dr_amt`,
          (CASE  `b_adjustments`.`adjustment_type`
              WHEN 'CREDIT'
              THEN
                  `b_adjustments`.`adjustment_amount`
           END)
             AS `cr_amount`,
          1 AS `flag`
     FROM (    `b_adjustments`
           JOIN
               `m_appuser`)
    WHERE (    ( `b_adjustments`.`adjustment_date` <= now())
           AND ( `b_adjustments`.`adjustment_type` = 'CREDIT')
           AND ( `b_adjustments`.`createdby_id` =
                    `m_appuser`.`id`))
   UNION ALL
   SELECT DISTINCT
           `b_adjustments`.`id` AS `transId`,
           `m_appuser`.`username` AS `username`,
           `b_adjustments`.`client_id` AS `client_id`,
          (SELECT  `m_code_value`.`code_value`
             FROM  `m_code_value`
            WHERE (    ( `m_code_value`.`code_id` = 12)
                   AND ( `b_adjustments`.`adjustment_code` =
                            `m_code_value`.`id`)))
             AS `tran_type`,
          cast( `b_adjustments`.`adjustment_date` AS date)
             AS `transdate`,
          'ADJUSTMENT' AS `transType`,
          (CASE  `b_adjustments`.`adjustment_type`
              WHEN 'DEBIT'
              THEN
                  `b_adjustments`.`adjustment_amount`
           END)
             AS `dr_amount`,
          0 AS `cr_amt`,
          1 AS `flag`
     FROM (    `b_adjustments`
           JOIN
               `m_appuser`)
    WHERE (    ( `b_adjustments`.`adjustment_date` <= now())
           AND ( `b_adjustments`.`adjustment_type` = 'DEBIT')
           AND ( `b_adjustments`.`createdby_id` =
                    `m_appuser`.`id`))
   UNION ALL
   SELECT DISTINCT
           `b_payments`.`id` AS `transId`,
           `m_appuser`.`username` AS `username`,
           `b_payments`.`client_id` AS `client_id`,
          (SELECT  `m_code_value`.`code_value`
             FROM  `m_code_value`
            WHERE (    ( `m_code_value`.`code_id` = 11)
                   AND ( `b_payments`.`paymode_id` =
                            `m_code_value`.`id`)))
             AS `tran_type`,
          cast( `b_payments`.`payment_date` AS date)
             AS `transDate`,
          'PAYMENT' AS `transType`,
          0 AS `dr_amt`,
           `b_payments`.`amount_paid` AS `cr_amount`,
           `b_payments`.`is_deleted` AS `flag`
     FROM (    `b_payments`
           JOIN
               `m_appuser`)
    WHERE (    ( `b_payments`.`createdby_id` =
                    `m_appuser`.`id`)
           AND isnull( `b_payments`.`ref_id`)
           AND ( `b_payments`.`payment_date` <= now()))
   UNION ALL
   SELECT DISTINCT
           `b_payments`.`id` AS `transId`,
           `m_appuser`.`username` AS `username`,
           `b_payments`.`client_id` AS `client_id`,
          (SELECT  `m_code_value`.`code_value`
             FROM  `m_code_value`
            WHERE (    ( `m_code_value`.`code_id` = 11)
                   AND ( `b_payments`.`paymode_id` =
                            `m_code_value`.`id`)))
             AS `tran_type`,
          cast( `b_payments`.`payment_date` AS date)
             AS `transDate`,
          'PAYMENT CANCELED' AS `transType`,
          abs( `b_payments`.`amount_paid`) AS `dr_amt`,
          0 AS `cr_amount`,
           `b_payments`.`is_deleted` AS `flag`
     FROM (    `b_payments`
           JOIN
               `m_appuser`)
    WHERE (    ( `b_payments`.`is_deleted` = 1)
           AND ( `b_payments`.`ref_id` IS NOT NULL)
           AND ( `b_payments`.`createdby_id` =
                    `m_appuser`.`id`)
           AND ( `b_payments`.`payment_date` <= now()))
   UNION ALL
   SELECT DISTINCT `bjt`.`id` AS `transId`,
                   `ma`.`username` AS `username`,
                   `bjt`.`client_id` AS `client_id`,
                   'Event Journal' AS `tran_type`,
                   cast(`bjt`.`jv_date` AS date) AS `transDate`,
                   'JOURNAL VOUCHER' AS `transType`,
                   ifnull(`bjt`.`debit_amount`, 0) AS `dr_amt`,
                   ifnull(`bjt`.`credit_amount`, 0) AS `cr_amount`,
                   1 AS `flag`
     FROM (    `b_jv_transactions` `bjt`
           JOIN
               `m_appuser` `ma`
           ON ((    (`bjt`.`createdby_id` = `ma`.`id`)
                AND (`bjt`.`jv_date` <= now()))))
   UNION ALL
   SELECT DISTINCT `bdr`.`id` AS `transId`,
                    `m_appuser`.`username` AS `username`,
                   `bdr`.`client_id` AS `client_id`,
                   `bdr`.`transaction_type` AS `tran_type`,
                   cast(`bdr`.`transaction_date` AS date) AS `transDate`,
                   'DEPOSIT&REFUND' AS `transType`,
                   0 AS `dr_amt`,
                   `bdr`.`credit_amount` AS `cr_amount`,
                   `bdr`.`is_refund` AS `flag`
     FROM (    `b_deposit_refund` `bdr`
           JOIN
               `m_appuser`)
    WHERE (    (`bdr`.`createdby_id` =  `m_appuser`.`id`)
           AND isnull(`bdr`.`ref_id`)
           AND (`bdr`.`transaction_date` <= now()))
   UNION ALL
   SELECT DISTINCT `bdr`.`id` AS `transId`,
                    `m_appuser`.`username` AS `username`,
                   `bdr`.`client_id` AS `client_id`,
                   `bdr`.`transaction_type` AS `tran_type`,
                   cast(`bdr`.`transaction_date` AS date) AS `transDate`,
                   'DEPOSIT&REFUND' AS `transType`,
                   `bdr`.`debit_amount` AS `dr_amt`,
                   0 AS `cr_amount`,
                   `bdr`.`is_refund` AS `flag`
     FROM (    `b_deposit_refund` `bdr`
           JOIN
               `m_appuser`)
    WHERE (    (`bdr`.`createdby_id` =  `m_appuser`.`id`)
           AND (`bdr`.`transaction_date` <= now())
           AND (`bdr`.`ref_id` IS NOT NULL))
   ORDER BY 1, 2;


