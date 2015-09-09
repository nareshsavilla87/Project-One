package org.mifosplatform.finance.billingmaster.service;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.finance.billingorder.data.BillDetailsData;
import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.BatchHistoryData;

public interface BillMasterReadPlatformService {

	List<FinancialTransactionsData> retrieveFinancialData(Long clientId);
	Page<FinancialTransactionsData> retrieveInvoiceFinancialData(SearchSqlQuery searchFinancialTransaction, Long clientId);
	BillDetailsData retrievebillDetails(Long clientId);
	List<FinancialTransactionsData> getFinancialTransactionData(Long id);
	Page<FinancialTransactionsData> retrieveStatments(SearchSqlQuery searchCodes,Long clientId);
	BigDecimal retrieveClientBalance(Long clientId);
	List<FinancialTransactionsData> retrieveSingleInvoiceData(Long invoiceId);
	List<BillDetailsData> retrievegetStatementDetails(Long billId);
	Page<FinancialTransactionsData> retrieveSampleData(
			SearchSqlQuery searchFinancialTransaction, Long clientId, String type);
	List<FinancialTransactionsData> retriveDataForDownload(Long clientId, String fromDate, String toDate);
	List<BatchHistoryData> retriveStatementDetailsForBill();
	List<Long> retriveStatementsIdsByBatchId(String batchId);  
	
}
