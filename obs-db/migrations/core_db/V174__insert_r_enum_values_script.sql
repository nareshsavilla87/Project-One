insert ignore into r_enum_value VALUES ('radius',1,'version-1','version-1');
insert ignore into r_enum_value VALUES ('radius',2,'version-2','version-2');
SET SQL_SAFE_UPDATES=0;
update job_parameters set param_name='system' where param_name='ProvSystem';
