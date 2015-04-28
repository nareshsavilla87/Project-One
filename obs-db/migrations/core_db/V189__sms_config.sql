DELIMITER $$
CREATE  FUNCTION sms_conf(message_id int)
  RETURNS TEXT
  LANGUAGE SQL
BEGIN
  DECLARE smsto VARCHAR(10);
  DECLARE smstext VARCHAR(255);
  DECLARE postdata VARCHAR(255);
  SET @smsto = '';
  SET @smstext = '';
  SET @postdata = '';
select message_to,body into @smsto,@smstext from b_message_data where id=message_id;
  SET @postdata = CONCAT('User=venkat&passwd=venkat@123&mobilenumber=',@smsto,'&message=',@smstext,'&sid=xxxxxxxx&mtype=N&DR=Y');
  RETURN @postdata;
END
$$

insert into c_configuration (id,name,enabled,value) VALUES (null,'align-biiling-cycle',0,0);