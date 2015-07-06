SET SQL_SAFE_UPDATES=0;

UPDATE stretchy_report SET report_sql='select 
    custinv.client_id as customerId,
    custinv.Name,
   -- custinv.office_id,
    custinv.office,
   -- custinv.invoice_date,
    custinv.invoiceAmount,
    bdr.depositAmount,
    adjust.debitAdjustment,
    adjust.creditAdjustment,
    bdr.refundAmount,
    ifnull(cast(round(sum(pay.amount_paid), 2) as char),0) as collectionAmount,
    cast(round(custinv.invoiceAmount + ifnull(bdr.depositAmount,0) + ifnull(adjust.debitAdjustment,0)
    - ifnull(sum(pay.amount_paid), 0) - ifnull(adjust.creditAdjustment,0) - ifnull(bdr.refundAmount,0)) as char) as Balance
from
   ( (select 
        cust.account_no as Customer_Id,
            cust.display_name as Name,
            cust.id as client_id,
            off.id as office_id,
            off.name as Office,
           -- inv.invoice_date,
            cast(round(sum(inv.invoice_amount), 2) as char) as invoiceAmount
    from
        m_client cust, m_office off, b_invoice inv
    where
        cust.office_id = off.id
            and cust.id = inv.client_id
    group by cust.id
    Order by cust.id) custinv
        left outer join
    b_payments pay ON custinv.client_id = pay.client_id
        left outer join
    (SELECT 
        ADJ.CLIENT_ID,
            sum(ADJ.DBADJ) as debitAdjustment,
            sum(ADJ.CRADJ) as creditAdjustment
    FROM
        (SELECT 
        client_id,
            adjustment_type,
            SUM((CASE adjustment_type
                WHEN ''DEBIT'' THEN adjustment_amount
                ELSE 0
            END)) AS DBADJ,
            SUM((CASE adjustment_type
                WHEN ''CREDIT'' THEN adjustment_amount
                ELSE 0
            END)) AS CRADJ
    FROM
        b_adjustments
    GROUP BY client_id , adjustment_type) AS ADJ
    GROUP BY ADJ.CLIENT_ID) adjust ON custinv.client_id = adjust.client_id
left outer join 
(SELECT DBR.clientId AS clientId,
TRUNCATE(sum(DBR.depositAmount),2) AS depositAmount,
TRUNCATE(sum(DBR.creditAmount),2) AS refundAmount
FROM
(SELECT client_id AS clientId,
transaction_type AS transactionType,
SUM((CASE transaction_type
                WHEN ''Deposit'' THEN debit_amount
                ELSE 0
            END)) AS depositAmount,
            SUM((CASE transaction_type
                WHEN ''Refund'' AND description=''Refund'' THEN credit_amount
                ELSE 0
            END)) AS creditAmount
FROM b_deposit_refund
GROUP BY client_id,transaction_type) AS DBR
GROUP BY clientId) bdr ON bdr.clientId = custinv.client_id) where custinv.office_id=''${officeId}'' or -1 = ''${officeId}''
GROUP BY custinv.client_id
ORDER BY custinv.client_id' where report_name='Customer Outstanding Report';
