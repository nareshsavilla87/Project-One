﻿truncate table stretchy_report;
truncate table stretchy_parameter;
truncate table stretchy_report_parameter;

INSERT INTO `stretchy_report`
   VALUES (1,
           'Client Listing',
           'Table',
           NULL,
           'Client',
           'select \r\nconcat(repeat(\"..\",   \r\n   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as \"Office/Branch\",\r\n c.account_no as \"Client Account No.\",  \r\nc.display_name as \"Name\",  \n\nc.joined_date as \"Joined\", c.external_id as \"External Id\"\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand \n\nounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\nwhere o.id = ${officeId}\r\nand c.is_deleted=0\r\n\n\norder by ounder.hierarchy, c.account_no',
           'Individual Client Report\r\n\r\nLists the small number of defined fields on the client table.  Would expect to copy this \n\nreport and add any \'one to one\' additional data for specific tenant needs.\r\n\r\nCan be run for any size MFI but you\'d expect it only to be run within a branch for \n\nlarger ones.  Depending on how many columns are displayed, there is probably is a limit of about 20/50k clients returned for html display (export to excel doesn\'t \n\nhave that client browser/memory impact).',
           1,
           1),
          (15,
           'Portfolio at Risk',
           'Table',
           NULL,
           'Loan',
           'select x.Currency, x.`Principal Outstanding`, x.`Principal Overdue`, x.`Interest Outstanding`, x.`Interest Overdue`, \r\nx.`Fees Outstanding`, x.`Fees Overdue`, x.`Penalties Outstanding`, x.`Penalties Overdue`,\r\n\r\n	(case\r\n	when ${parType} = 1 then\r\n    cast(round((x.`Principal Overdue` * 100) / x.`Principal Outstanding`, 2) as char)\r\n	when ${parType} = 2 then\r\n    cast(round(((x.`Principal Overdue` + x.`Interest Overdue`) * 100) / (x.`Principal Outstanding` + x.`Interest Outstanding`), 2) as char)\r\n	when ${parType} = 3 then\r\n    cast(round(((x.`Principal Overdue` + x.`Interest Overdue` + x.`Fees Overdue`) * 100) / (x.`Principal Outstanding` + x.`Interest Outstanding` + x.`Fees Outstanding`), 2) as char)\r\n	when ${parType} = 4 then\r\n    cast(round(((x.`Principal Overdue` + x.`Interest Overdue` + x.`Fees Overdue` + x.`Penalties Overdue`) * 100) / (x.`Principal Outstanding` + x.`Interest Outstanding` + x.`Fees Outstanding` + x.`Penalties Overdue`), 2) as char)\r\n	else \"invalid PAR Type\"\r\n	end) as \"Portfolio at Risk %\"\r\n from \r\n(select  ifnull(cur.display_symbol, l.currency_code) as Currency,  \r\nsum(l.principal_outstanding_derived) as \"Principal Outstanding\",\r\nsum(laa.principal_overdue_derived) as \"Principal Overdue\",\r\n\r\nsum(l.interest_outstanding_derived) as \"Interest Outstanding\",\r\nsum(laa.interest_overdue_derived) as \"Interest Overdue\",\r\n\r\nsum(l.fee_charges_outstanding_derived)  as \"Fees Outstanding\",\r\nsum(laa.fee_charges_overdue_derived) as \"Fees Overdue\",\r\n\r\nsum(penalty_charges_outstanding_derived) as \"Penalties Outstanding\",\r\nsum(laa.penalty_charges_overdue_derived) as \"Penalties Overdue\"\r\n\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\njoin  m_loan l on l.client_id = c.id\r\nleft join m_staff lo on lo.id = l.loan_officer_id\r\nleft join m_currency cur on cur.code = l.currency_code\r\nleft join m_fund f on f.id = l.fund_id\r\nleft join m_code_value purp on purp.id = l.loanpurpose_cv_id\r\nleft join m_product_loan p on p.id = l.product_id\r\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\r\nwhere o.id = ${officeId}\r\nand (l.currency_code = \"${currencyId}\" or \"-1\" = \"${currencyId}\")\r\nand (l.product_id = \"${loanProductId}\" or \"-1\" = \"${loanProductId}\")\r\nand (ifnull(l.loan_officer_id, -10) = \"${loanOfficerId}\" or \"-1\" = \"${loanOfficerId}\")\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\r\nand l.loan_status_id = 300\r\ngroup by l.currency_code\r\norder by l.currency_code) x',
           'Covers all loans.\r\n\r\nFor larger MFIs … we should add some derived fields on loan (or a 1:1 loan related table like mifos 2.x does)\r\nPrinciple, Interest, Fees, Penalties Outstanding and Overdue (possibly waived and written off too)',
           1,
           1); */

          
INSERT INTO `stretchy_parameter` VALUES (1,'startDateSelect','startDate','startDate','date','date','today',NULL,NULL,NULL,NULL,NULL),(2,'endDateSelect','endDate','endDate','date','date','today',NULL,NULL,NULL,NULL,NULL),(3,'obligDateTypeSelect','obligDateType','obligDateType','select','number','0',NULL,NULL,NULL,'select * from\r\n(select 1 as id, \"Closed\" as `name` union all\r\nselect 2, \"Disbursal\" ) x\r\norder by x.`id`',NULL),(5,'OfficeIdSelectOne','officeId','Office','select','number','0',NULL,'Y',NULL,'select id, \r\nconcat(substring(\"........................................\", 1, \r\n   \n\n((LENGTH(`hierarchy`) - LENGTH(REPLACE(`hierarchy`, \'.\', \'\')) - 1) * 4)), \r\n   `name`) as tc\r\nfrom m_office\r\nwhere hierarchy like concat\n\n(\'${currentUserHierarchy}\', \'%\')\r\norder by hierarchy',NULL),(6,'loanOfficerIdSelectAll','loanOfficerId','Loan Officer','select','number','0',NULL,NULL,'Y','(select lo.id, lo.display_name as `Name` \r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\njoin m_staff lo on lo.office_id = ounder.id\r\nwhere lo.is_loan_officer = true\r\nand o.id = ${officeId})\r\nunion all\r\n(select -10, \'-\')\r\norder by 2',5),(10,'currencyIdSelectAll','currencyId','Currency','select','number','0',NULL,NULL,'Y','select `code`, `name`\r\nfrom m_organisation_currency\r\norder by `code`',NULL),(20,'fundIdSelectAll','fundId','Fund','select','number','0',NULL,NULL,'Y','(select id, `name`\r\nfrom m_fund)\r\nunion all\r\n(select -10, \'-\')\r\norder by 2',NULL),(25,'loanProductIdSelectAll','loanProductId','Product','select','number','0',NULL,NULL,'Y','select p.id, p.`name`\r\nfrom m_product_loan p\r\nwhere p.currency_code = \'${currencyId}\'\r\norder by 2',10),(26,'loanPurposeIdSelectAll','loanPurposeId','Loan Purpose','select','number','0',NULL,NULL,'Y','select -10 as id, \'-\' as code_value\r\nunion all\r\nselect * from (select v.id, v.code_value\r\nfrom m_code c\r\njoin m_code_value v on v.code_id = c.id\r\nwhere c.code_name = \"loanPurpose\"\r\norder by v.order_position)  x',NULL),(100,'parTypeSelect','parType','parType','select','number','0',NULL,NULL,NULL,'select * from\r\n(select 1 as id, \"Principal Only\" as `name` union all\r\nselect 2, \"Principal + Interest\" union all\r\nselect 3, \"Principal + Interest + Fees\" union all\r\nselect 4, \"Principal + Interest + Fees + Penalties\") x\r\norder by x.`id`',NULL),(1001,'FullReportList',NULL,'n/a','n/a','n/a','n/a','Y',NULL,NULL,'select  r.report_id, r.report_name, r.report_type, r.report_subtype, r.report_category,\r\n  \n\nrp.parameter_id, rp.report_parameter_name, p.parameter_name\r\n  from stretchy_report r\r\n  left join stretchy_report_parameter rp on rp.report_id = r.report_id\r\n  \n\nleft join stretchy_parameter p on p.parameter_id = rp.parameter_id\r\n  where r.use_report is true\r\n  and exists\r\n  (\r\n select \'f\'\r\n  from m_appuser_role ur \n\n\r\n  join m_role r on r.id = ur.role_id\r\n  join m_role_permission rp on rp.role_id = r.id\r\n  join m_permission p on p.id = rp.permission_id\r\n  where \n\nur.appuser_id = ${currentUserId}\r\n  and (p.code in (\'ALL_FUNCTIONS_READ\', \'ALL_FUNCTIONS\') or p.code = concat(\"READ_\", r.report_name))\r\n )\r\n  order by \n\nr.report_category, r.report_name, rp.parameter_id',NULL),(1002,'FullParameterList',NULL,'n/a','n/a','n/a','n/a','Y',NULL,NULL,'select sp.parameter_name, sp.parameter_variable, sp.parameter_label, sp.parameter_displayType, \r\nsp.parameter_FormatType, sp.parameter_default, sp.selectOne,  sp.selectAll, spp.parameter_name as parentParameterName\r\nfrom stretchy_parameter sp\r\nleft join stretchy_parameter spp on spp.parameter_id = sp.parent_parameter_id\r\nwhere sp.special is null\r\nand exists \r\n	(select \'f\' \r\n	from stretchy_report sr\r\n	join stretchy_report_parameter srp on srp.report_id = sr.report_id\r\n	where sr.report_name in(${reportListing})\r\n	and srp.parameter_id = sp.parameter_id\r\n	)\r\norder by sp.parameter_id',NULL),(1003,'reportCategoryList',NULL,'n/a','n/a','n/a','n/a','Y',NULL,NULL,'select  r.report_id, r.report_name, r.report_type, r.report_subtype, \n\nr.report_category,\r\n  rp.parameter_id, rp.report_parameter_name, p.parameter_name\r\n  from stretchy_report r\r\n  left join stretchy_report_parameter rp on \n\nrp.report_id = r.report_id\r\n  left join stretchy_parameter p on p.parameter_id = rp.parameter_id\r\n  where r.report_category = \'${reportCategory}\'\r\n  and \n\nr.use_report is true\r\n  and exists\r\n  (\r\n select \'f\'\r\n  from m_appuser_role ur \r\n  join m_role r on r.id = ur.role_id\r\n  join m_role_permission rp on \n\nrp.role_id = r.id\r\n  join m_permission p on p.id = rp.permission_id\r\n  where ur.appuser_id = ${currentUserId}\r\n  and (p.code in (\'ALL_FUNCTIONS_READ\', \n\n\'ALL_FUNCTIONS\') or p.code = concat(\"READ_\", r.report_name))\r\n )\r\n  order by r.report_category, r.report_name, rp.parameter_id',NULL);
INSERT INTO `stretchy_report_parameter` VALUES (1,5,NULL),(2,5,NULL),(2,6,NULL),(2,10,NULL),(2,20,NULL),(2,25,NULL),(2,26,NULL),(5,5,NULL),(5,6,NULL),(5,10,NULL),(5,20,NULL),(5,25,NULL),(5,26,NULL),(6,5,NULL),(6,6,NULL),(6,10,NULL),(6,20,NULL),(6,25,NULL),(6,26,NULL),(7,5,NULL),(7,6,NULL),(7,10,NULL),(7,20,NULL),(7,25,NULL),(7,26,NULL),(8,5,NULL),(8,6,NULL),(8,10,NULL),(8,25,NULL),(8,26,NULL),(11,5,NULL),(11,6,NULL),(11,10,NULL),(11,20,NULL),(11,25,NULL),(11,26,NULL),(11,100,NULL),(12,5,NULL),(12,6,NULL),(12,10,NULL),(12,20,NULL),(12,25,NULL),(12,26,NULL),(13,1,NULL),(13,2,NULL),(13,3,NULL),(13,5,NULL),(13,6,NULL),(13,10,NULL),(13,20,NULL),(13,25,NULL),(13,26,NULL),(14,1,NULL),(14,2,NULL),(14,3,NULL),(14,5,NULL),(14,6,NULL),(14,10,NULL),(14,20,NULL),(14,25,NULL),(14,26,NULL),(15,5,NULL),(15,6,NULL),(15,10,NULL),(15,20,NULL),(15,25,NULL),(15,26,NULL),(15,100,NULL),(16,5,NULL),(16,6,NULL),(16,10,NULL),(16,20,NULL),(16,25,NULL),(16,26,NULL),(16,100,NULL),(20,1,NULL),(20,2,NULL),(20,10,NULL),(20,20,NULL),(21,1,NULL),(21,2,NULL),(21,5,NULL),(21,10,NULL),(21,20,NULL),(48,5,'branch'),(48,2,'date'),(49,5,'branch'),(49,1,'fromDate'),(49,2,'toDate'),(50,5,'branch'),(50,1,'fromDate'),(50,2,'toDate'),(51,1,NULL),(51,2,NULL),(51,5,NULL),(51,10,NULL),(51,25,NULL),(52,5,NULL),(53,5,NULL),(53,10,NULL),(54,1,NULL),(54,2,NULL),(54,5,NULL),(54,10,NULL),(54,25,NULL),(55,5,NULL),(55,6,NULL),(55,10,NULL),(55,20,NULL),(55,25,NULL),(55,26,NULL),(56,5,NULL),(56,6,NULL),(56,10,NULL),(56,20,NULL),(56,25,NULL),(56,26,NULL),(56,100,NULL),(57,5,NULL),(57,6,NULL),(57,10,NULL),(57,20,NULL),(57,25,NULL),(57,26,NULL),(58,5,NULL),(58,6,NULL),(58,10,NULL),(58,20,NULL),(58,25,NULL),(58,26,NULL),(58,100,NULL),(59,1,NULL),(59,2,NULL),(59,5,NULL),(59,6,NULL),(59,10,NULL),(59,20,NULL),(59,25,NULL),(59,26,NULL),(61,5,NULL),(61,10,NULL),(92,1,'fromDate'),(92,5,'selectOffice'),(92,2,'toDate'),(93,1,NULL),(93,2,NULL),(93,5,NULL),(93,6,NULL),(94,2,'endDate'),(94,6,'loanOfficerId'),(94,5,'officeId'),(94,1,'startDate');

insert into m_permission(grouping, `code`, entity_name, action_name, can_maker_checker)
select 'report', concat('READ_', r.report_name), r.report_name, 'READ', false
from stretchy_report r;

