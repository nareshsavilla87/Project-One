drop procedure if exists p_int_fa0 ;
DELIMITER //

create procedure p_int_fa0( p_todt date)

begin

create table IF NOT EXISTS INT_FA 
(id int(10) not null AUTO_INCREMENT,
int_date datetime,
obsTable varchar(50),
From_id  bigint(20),
to_id    bigint(20),
from_dt  dateTIME,
to_dt	 dateTIME,
records	 int(10),
filename	varchar(50),
createdby_id int(10),
created_dt datetime,
PRIMARY KEY (`id`)
);

insert into INT_FA
select null id,current_date() int_date, 'm_client' obsTable,
min(id) fromId,max(id) toId, 
min(activation_date) fromdt, max(activation_date) todt,count(id) records, 
'clients.csv',1 createdby_id,current_date() created_dt
from m_client 
where activation_date <= p_todt  
union all
select null id, current_date() int_date,'b_invoice' obsTable,
min(id),max(id), min(invoice_date) , max(invoice_date) , count(id), 'invoices.csv',
1 createdby_id,now() created_dt
from b_invoice 
where invoice_date <= p_todt  
union all 
select null id, current_date() int_date,'b_payments' obsTable,
min(id),max(id), min(payment_date) , max(payment_date) , count(id), 'payments.csv',
1 createdby_id,now() created_dt
from b_payments 
where payment_date <= p_todt 
union all 
select null id, current_date() int_date,'b_adjustments' obsTable,
min(id),max(id), min(adjustment_date) , max(adjustment_date) , count(id), 'adjustments.csv',
1 createdby_id,now() created_dt
from b_adjustments 
where adjustment_date <= p_todt
;
end //

DELIMITER ;


Drop procedure if exists loginHistory;
DELIMITER //

create procedure loginHistory()
BEGIN
IF EXISTS(SELECT * FROM INFORMATION_SCHEMA.COLUMNS
WHERE COLUMN_NAME ='username' 
AND TABLE_NAME='b_login_history'
AND TABLE_SCHEMA=DATABASE())THEN
ALTER TABLE b_login_history MODIFY username varchar(100) NOT NULL;
END IF;
END // 
DELIMITER ;
call loginHistory();
Drop procedure if exists loginHistory;


drop procedure if exists p_int_fa ;
DELIMITER //

create  procedure p_int_fa(p_todt date) 
begin

DECLARE _exists  TINYINT(1) DEFAULT 0;

    SELECT COUNT(*) INTO _exists
    FROM information_schema.tables 
    WHERE table_schema =  DATABASE()
    AND table_name =  'int_fa';


if _exists =0 then call p_int_fa0(p_todt) ;
else 
insert into INT_FA 
select * from 
(select null id,current_date() int_date, 'm_client' obsTable,
min(id) fromId,max(id) toId, 
min(activation_date) fromdt, max(activation_date) todt,count(id) records, 
'clients.csv',1 createdby_id,current_date() created_dt
from m_client 
where activation_date between 
(select max(to_dt) from int_fa where obsTable='m_client')  and p_todt 
and id > (select max(to_id) from int_fa where obsTable='m_client')
union all
select null id, current_date() int_date,'b_invoice' obsTable,
min(id),max(id), min(invoice_date) , max(invoice_date) , count(id), 'invoices.csv',
1 createdby_id,now() created_dt
from b_invoice 
where invoice_date between 
(select max(to_dt) from int_fa where obsTable='b_invoice')  and p_todt 
and id > (select max(to_id) from int_fa where obsTable='b_invoice')
union all 
select null id, current_date() int_date,'b_payments' obsTable,
min(id),max(id), min(payment_date) , max(payment_date) , count(id), 'payments.csv',
1 createdby_id,now() created_dt
from b_payments 
where payment_date between 
(select max(to_dt) from int_fa where obsTable='b_payments')  and p_todt
and id > (select max(to_id) from int_fa where obsTable='b_payments')
union all 
select null id, current_date() int_date,'b_adjustments' obsTable,
min(id),max(id), min(adjustment_date) , max(adjustment_date) , count(id), 'adjustments.csv',
1 createdby_id,now() created_dt
from b_adjustments 
where adjustment_date between 
(select max(to_dt) from int_fa where obsTable='b_adjustments')  and p_todt
 and id > (select max(to_id) from int_fa where obsTable='b_adjustments')
)a
where records >0;

end if;
end  // 
DELIMITER ;






