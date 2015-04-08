/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.vendormanagementandloyalty.vendor.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.address.data.CountryDetails;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.organisation.priceregion.data.PriceRegionData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.plan.data.ServiceData;

/**
 * Immutable data object for application user data.
 */
public class VendorData {

    private Long id;
    private String vendorCode;
    private String vendorDescription;
    private String vendorEmailId;
    private String contactName;
    private String vendormobileNo;
    
    private String vendorTelephoneNo;
    private String vendorAddress;
    private String agreementStatus;
    
    private String vendorCountry;
    private String vendorCurrency;
    private Date agreementStartDate;
    private Date agreementEndDate;
    private String contentType;
    
    private String contentCode;
    private String loyaltyType;
    private BigDecimal loyaltyShare;
    private Long priceRegion;
    private BigDecimal contentCost;
    private List<PriceRegionData> priceRegionData;
    private List<EnumOptionData> statusData;
    private List<ServiceData> servicesData;
    private List<PlanCodeData> planDatas;
    private List<CountryDetails> countryData;
    private Collection<CurrencyData> currencyOptions;
    private Long vendorId;
    private List<VendorData> singleVendorData;
    private List<VendorData> vendorDetailsData;
    
    
	public VendorData(List<PriceRegionData> priceRegionData,
			List<EnumOptionData> statusData, List<ServiceData> servicesData,
			List<PlanCodeData> planDatas, List<CountryDetails> countryData,
			Collection<CurrencyData> currencyOptions) {
		
		this.priceRegionData = priceRegionData;
		this.statusData = statusData;
		this.servicesData = servicesData;
		this.planDatas = planDatas;
		this.countryData = countryData;
		this.currencyOptions = currencyOptions;
	}
	
	public VendorData(Long id, String vendorCode, String vendorDescription,
			String vendorEmailId, String contactName, String vendormobileNo,
			String vendorTelephoneNo, String vendorAddress,
			String agreementStatus, String vendorCountry,
			String vendorCurrency, Date agreementStartDate,
			Date agreementEndDate, String contentType) {
		
		this.id = id;
		this.vendorCode = vendorCode;
		this.vendorDescription = vendorDescription;
		this.vendorEmailId = vendorEmailId;
		this.contactName = contactName;
		this.vendormobileNo = vendormobileNo;
		this.vendorTelephoneNo = vendorTelephoneNo;
		this.vendorAddress = vendorAddress;
		this.agreementStatus = agreementStatus;
		this.vendorCountry = vendorCountry;
		this.vendorCurrency = vendorCurrency;
		this.agreementStartDate = agreementStartDate;
		this.agreementEndDate = agreementEndDate;
		this.contentType = contentType;
	}
	
	public VendorData(Long id, Long vendorId, String contentCode,
			String loyaltyType, BigDecimal loyaltyShare, Long priceRegion,
			BigDecimal contentCost) {
		
		this.id = id;
		this.vendorId = vendorId;
		this.loyaltyType = loyaltyType;
		this.loyaltyShare = loyaltyShare;
		this.contentCode = contentCode;
		this.priceRegion = priceRegion;
		this.contentCost = contentCost;
	}

	public List<VendorData> getSingleVendorData() {
		return singleVendorData;
	}

	public List<VendorData> getVendorDetailsData() {
		return vendorDetailsData;
	}

	public void setSingleVendorData(List<VendorData> singleVendorData) {
		this.singleVendorData = singleVendorData;
	}

	public void setVendorDetailsData(List<VendorData> vendorDetailsData) {
		this.vendorDetailsData = vendorDetailsData;
	}

	
}
