package org.mifosplatform.scheduledjobs.scheduledjobs.data;

import java.util.Date;

public class BatchHistoryData {
	private Long id;
	private Date transactionDate;
	private String transactionType;
	private String countValue;
	private String batchid;
	
	
	
	public BatchHistoryData(Long id, Date transactionDate,
			String transactionType, String countValue, String batchId) {
		this.id = id;
		this.transactionDate = transactionDate;
		this.transactionType = transactionType;
		this.countValue = countValue;
		this.batchid = batchId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getCountValue() {
		return countValue;
	}
	public void setCountValue(String countValue) {
		this.countValue = countValue;
	}
	public String getBatchid() {
		return batchid;
	}
	public void setBatchid(String batchid) {
		this.batchid = batchid;
	}
	
}
