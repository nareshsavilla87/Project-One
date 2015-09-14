package org.mifosplatform.finance.billingorder.commands;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.mifosplatform.billing.discountmaster.data.DiscountMasterData;
import org.mifosplatform.finance.usagecharges.data.UsageChargesData;



public class BillingOrderCommand {

	private final Long clientOrderId;
	private final Long orderPriceId;
	private final Long clientId;
	private final Date startDate;
	private final Date nextBillableDate;
	private final Date endDate;
	private final String billingFrequency;
	private final String chargeCode;
	private final String chargeType;
	private final Integer chargeDuration;
	private final String durationType;
	private final Date invoiceTillDate;
	private final BigDecimal price;
	private final String billingAlign;
	private final List<InvoiceTaxCommand> listOfTax;
	private final Date billStartDate;
	private final Date billEndDate;
	private final DiscountMasterData discountMasterData;
	private final Integer taxInclusive;
	private final List<UsageChargesData> cdrData;

	public BillingOrderCommand(Long clientOrderId, Long oderPriceId,Long clientId, Date startDate, Date nextBillableDate, Date endDate,
			String billingFrequency, String chargeCode, String chargeType,Integer chargeDuration, String durationType, Date invoiceTillDate,
			BigDecimal price, String billingAlign,final List<InvoiceTaxCommand> listOfTax, final Date billStartDate,final Date billEndDate,
			final DiscountMasterData discountMasterData, final Integer taxInclusive, final List<UsageChargesData> cdrData) {
		
		this.clientOrderId = clientOrderId;
		this.orderPriceId = (oderPriceId != null) ? oderPriceId : new Long(0);
		this.clientId = clientId;
		this.startDate = startDate;
		this.nextBillableDate = nextBillableDate;
		this.endDate = endDate;
		this.billingFrequency = billingFrequency;
		this.chargeCode = chargeCode;
		this.chargeType = chargeType;
		this.chargeDuration = chargeDuration;
		this.durationType = durationType;
		this.invoiceTillDate = invoiceTillDate;
		this.price = price;
		this.billingAlign = billingAlign;
		this.billStartDate = billStartDate;
		this.billEndDate = billEndDate;
		this.listOfTax = listOfTax;
		this.discountMasterData = discountMasterData;
		this.taxInclusive = taxInclusive;
		this.cdrData = cdrData;
	}

	public Long getClientId() {
		return clientId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getNextBillableDate() {
		return nextBillableDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public String getChargeType() {
		return chargeType;
	}

	public Integer getChargeDuration() {
		return chargeDuration;
	}

	public String getDurationType() {
		return durationType;
	}

	public Date getInvoiceTillDate() {
		return invoiceTillDate;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public String getBillingAlign() {
		return billingAlign;
	}

	public Long getClientOrderId() {
		return clientOrderId;
	}

	public Long getOrderPriceId() {
		return orderPriceId;
	}

	public List<InvoiceTaxCommand> getListOfTax() {
		return listOfTax;
	}

	public Date getBillStartDate() {
		return billStartDate;
	}

	public Date getBillEndDate() {
		return billEndDate;
	}

	public DiscountMasterData getDiscountMasterData() {
		return discountMasterData;
	}

	public Integer getTaxInclusive() {
		return taxInclusive;
	}

	public List<UsageChargesData> getCdrData() {
		return cdrData;
	}

	
}
