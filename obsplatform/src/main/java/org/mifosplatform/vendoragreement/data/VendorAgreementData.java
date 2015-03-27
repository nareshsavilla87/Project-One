/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.vendoragreement.data;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.priceregion.data.PriceRegionData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.plan.data.ServiceData;

/**
 * Immutable data object for application user data.
 */
public class VendorAgreementData {

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
    private Long vendorId;
    private List<VendorAgreementData> singleVendorData;
    private List<VendorAgreementData> vendorDetailsData;
    private Collection<MCodeData> agreementTypes;
    private String name;
    private String fileName;
    private InputStream inputStream;
    private String fileUploadLocation;
    private LocalDate localdate;
    
    
	public VendorAgreementData(List<PriceRegionData> priceRegionData,
			Collection<MCodeData> agreementTypes, List<ServiceData> servicesData,
			List<PlanCodeData> planDatas) {
		
		this.priceRegionData = priceRegionData;
		this.agreementTypes = agreementTypes;
		this.servicesData = servicesData;
		this.planDatas = planDatas;
	}
	
	public VendorAgreementData(Long id, String vendorCode, String vendorDescription,
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
	
	public VendorAgreementData(Long id, Long vendorId, String contentCode,
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

	public VendorAgreementData(String name, String fileName,
			InputStream inputStream, String fileUploadLocation, LocalDate localdate) {
		this.name = name;
		this.fileName = fileName;
		this.inputStream = inputStream;
		this.fileUploadLocation = fileUploadLocation;
		this.localdate = localdate;
	}

	public List<VendorAgreementData> getSingleVendorData() {
		return singleVendorData;
	}

	public List<VendorAgreementData> getVendorDetailsData() {
		return vendorDetailsData;
	}

	public void setSingleVendorData(List<VendorAgreementData> singleVendorData) {
		this.singleVendorData = singleVendorData;
	}

	public void setVendorDetailsData(List<VendorAgreementData> vendorDetailsData) {
		this.vendorDetailsData = vendorDetailsData;
	}

	public String getFileName() {
		return fileName;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public String getFileUploadLocation() {
		return fileUploadLocation;
	}

	public LocalDate getLocaldate() {
		return localdate;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void setFileUploadLocation(String fileUploadLocation) {
		this.fileUploadLocation = fileUploadLocation;
	}

	public void setLocaldate(LocalDate localdate) {
		this.localdate = localdate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	
}
