package org.mifosplatform.logistics.onetimesale.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "b_deposit_refund")
public class DepositAndRefund extends AbstractAuditableCustom<AppUser, Long>{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2876090423570296480L;

	@Column(name="client_id", nullable=false, length=20)
	private Long clientId;
	
	@Column(name="transaction_date",nullable=false,length=100)
	private Date transactionDate;
	
	@Column(name="transaction_type", nullable=false, length=50)
	private String transactionType;
	
	@Column(name="item_id", nullable=true, length=20)
	private Long itemId;
	
	@Column(name="credit_amount", nullable=true, length=20)
	private BigDecimal creditAmount;
	
	@Column(name="debit_amount", nullable=true, length=20)
	private BigDecimal debitAmount;
	

	public DepositAndRefund(){}


	public DepositAndRefund(Long clientId, Long itemId, BigDecimal amount,
			Date transactionDate, String transactionType) {
		
		this.clientId = clientId;
		this.transactionDate = transactionDate;
		this.transactionType = transactionType;
		this.itemId = itemId;
		this.debitAmount = amount;
	}


	public static DepositAndRefund fromJson(Long clientId,
			JsonCommand command) {
		
		final Long itemId = command.longValueOfParameterNamed("itemId");
		final BigDecimal amount = command.bigDecimalValueOfParameterNamed("amount");
		final Date transactionDate = DateUtils.getLocalDateOfTenant().toDate();
		final String transactionType = "Deposit";
		
		return new DepositAndRefund(clientId, itemId, amount, transactionDate, transactionType);
	}
	
}
