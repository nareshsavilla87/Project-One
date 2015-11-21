package org.mifosplatform.portfolio.order.data;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.portfolio.addons.data.AddonsPriceData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;

public class OrderAddonsData {
	
	private Long id;
	private Long serviceId;
	private String serviceCode;
	private String status;
	private LocalDate startDate;
	private LocalDate endDate;
	private BigDecimal price;
	private List<AddonsPriceData> addonsPriceDatas;
	private List<SubscriptionData> contractPeriods;
	private LocalDate date;
	private String addOnSerialNo;

	public OrderAddonsData(List<AddonsPriceData> addonsPriceDatas,List<SubscriptionData> contractPeriods) {

		this.addonsPriceDatas = addonsPriceDatas;
		this.contractPeriods = contractPeriods;

	}

	public OrderAddonsData(Long id, Long serviceId, String serviceCode,LocalDate startDate,
                   LocalDate endDate, String status,BigDecimal price,String addOnSerialNo) {

		this.id = id;
		this.serviceId = serviceId;
		this.serviceCode = serviceCode;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = status;
		this.price = price;
		this.addOnSerialNo = addOnSerialNo;

	}

	public Long getId() {
		return id;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public String getStatus() {
		return status;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public List<AddonsPriceData> getAddonsPriceDatas() {
		return addonsPriceDatas;
	}

	public List<SubscriptionData> getContractPeriods() {
		return contractPeriods;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getAddOnSerialNo() {
		return addOnSerialNo;
	}
	

}
