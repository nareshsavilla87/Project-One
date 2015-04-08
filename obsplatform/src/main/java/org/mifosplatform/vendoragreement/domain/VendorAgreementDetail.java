package org.mifosplatform.vendoragreement.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_vendor_agmt_detail")
public class VendorAgreementDetail extends AbstractPersistable<Long> {
	
	@Column(name = "content_code")
	private String contentCode;

	@Column(name = "price_region")
	private Long priceRegion;

	@Column(name = "loyalty_type")
	private String loyaltyType;
	
	@Column(name = "loyalty_share")
	private BigDecimal loyaltyShare;

	@Column(name = "content_cost")
	private BigDecimal contentCost;
	
	@ManyToOne
    @JoinColumn(name="vendor_agmt_id")
	private VendorAgreement vendor;
	
	@Column(name = "is_deleted")
	private String isDeleted="N";
	
	public VendorAgreementDetail(){
	}

	public VendorAgreementDetail(String contentCode, String loyaltyType,
			BigDecimal loyaltyShare, Long priceRegion, BigDecimal contentCost) {
		
		this.contentCode = contentCode;
		this.loyaltyType = loyaltyType;
		this.loyaltyShare = loyaltyShare;
		this.priceRegion = priceRegion;
		this.contentCost = contentCost;
		
	}

	public VendorAgreement getMediaAsset() {
		return vendor;
	}

	public void update(VendorAgreement vendor) {
		this.vendor=vendor;
		
	}

	public String getContentCode() {
		return contentCode;
	}

	public Long getPriceRegion() {
		return priceRegion;
	}

	public String getLoyaltyType() {
		return loyaltyType;
	}

	public BigDecimal getLoyaltyShare() {
		return loyaltyShare;
	}

	public BigDecimal getContentCost() {
		return contentCost;
	}

	public VendorAgreement getVendor() {
		return vendor;
	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public void setContentCode(String contentCode) {
		this.contentCode = contentCode;
	}

	public void setPriceRegion(Long priceRegion) {
		this.priceRegion = priceRegion;
	}

	public void setLoyaltyType(String loyaltyType) {
		this.loyaltyType = loyaltyType;
	}

	public void setLoyaltyShare(BigDecimal loyaltyShare) {
		this.loyaltyShare = loyaltyShare;
	}

	public void setContentCost(BigDecimal contentCost) {
		this.contentCost = contentCost;
	}

	public void setVendor(VendorAgreement vendor) {
		this.vendor = vendor;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	
	
}
