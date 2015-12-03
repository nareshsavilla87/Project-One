SET SQL_SAFE_UPDATES=0;
UPDATE stretchy_report SET report_sql = 'SELECT DISTINCT id AS billId FROM b_bill_master WHERE filename=''invoice''
AND is_deleted=''N'' AND Due_date=DATE_FORMAT(NOW() + INTERVAL 7 DAY ,''%Y-%m-%d'')' WHERE report_name='PDF Statement';
UPDATE job SET cron_expression='0 10 0 1/1 * ? *' WHERE name='INVOICING';
SET @ID=(SELECT ID FROM job WHERE name='PDF');
UPDATE job_parameters SET  is_dynamic = 'Y' WHERE job_id=@ID AND param_name='reportName'
