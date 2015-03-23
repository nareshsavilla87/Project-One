package org.mifosplatform.vendormanagementandloyalty.vendor.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_vendor_management_detail")
public class VendorDetail extends AbstractPersistable<Long> {
	
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
    @JoinColumn(name="vendor_id")
	private Vendor vendor;
 
	public VendorDetail(){
	}

	public VendorDetail(String contentCode, String loyaltyType,
			BigDecimal loyaltyShare, Long priceRegion, BigDecimal contentCost) {
		
		this.contentCode = contentCode;
		this.loyaltyType = loyaltyType;
		this.loyaltyShare = loyaltyShare;
		this.priceRegion = priceRegion;
		this.contentCost = contentCost;
		
	}

	public Vendor getMediaAsset() {
		return vendor;
	}

	public void update(Vendor vendor) {
		this.vendor=vendor;
		
	}
	
}
