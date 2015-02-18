package org.mifosplatform.integrationtests.common.discount;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class DiscountDomain  {

	private Long id;
	private String discountCode;
	private String discountDescription;
	private String discountType;
	private BigDecimal discountRate;
	private LocalDate discountStartDate;
	private LocalDate discountEndDate;
	private String isDeleted;
	private String discountStatus;
	
	public DiscountDomain(){
		//Jackson uses default constructor to create the instances of java class using reflection. 
		// If default constructor is not provided, then you will get JsonMappingException in runtime.
	}
	
	public Long getId() {
		return id;
	}

	public String getDiscountCode() {
		return discountCode;
	}

	public String getDiscountDescription() {
		return discountDescription;
	}

	public String getDiscountType() {
		return discountType;
	}

	public BigDecimal getDiscountRate() {
		return discountRate;
	}

	public LocalDate getDiscountStartDate() {
		return discountStartDate;
	}

	public LocalDate getDiscountEndDate() {
		return discountEndDate;
	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public String getDiscountStatus() {
		return discountStatus;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDiscountCode(String discountCode) {
		this.discountCode = discountCode;
	}

	public void setDiscountDescription(String discountDescription) {
		this.discountDescription = discountDescription;
	}

	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	public void setDiscountRate(BigDecimal discountRate) {
		this.discountRate = discountRate;
	}

	public void setDiscountStartDate(LocalDate discountStartDate) {
		this.discountStartDate = discountStartDate;
	}

	public void setDiscountEndDate(LocalDate discountEndDate) {
		this.discountEndDate = discountEndDate;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void setDiscountStatus(String discountStatus) {
		this.discountStatus = discountStatus;
	}

}
