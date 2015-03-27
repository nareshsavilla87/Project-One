package org.mifosplatform.vendoragreement.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_vendor_management")
public class VendorAgreement extends  AbstractPersistable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "vendor_code")
	private String vendorCode;
	
	@Column(name = "vendor_description")
	private String vendorDescription;
	
	@Column(name = "vendor_emailid")
	private String vendorEmailid;
	
	@Column(name = "vendor_contact_name")
	private String vendorContactName;
	
	@Column(name = "vendor_mobileno")
	private String vendorMobileno;
	
	@Column(name = "vendor_telephoneno")
	private String vendorTelephoneno;
	
	@Column(name = "vendor_address")
	private String vendorAddress;
	
	@Column(name = "agreement_status")
	private String agreementStatus;

	@Column(name = "vendor_country")
	private String vendorCountry;

	@Column(name = "vendor_currency")
	private String vendorCurrency;

	@Column(name = "agreement_startdate")
	private Date agreementStartdate;
	
	@Column(name = "agreement_enddate")
	private Date agreementEnddate;

	@Column(name = "content_type")
	private String contentType;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "vendor", orphanRemoval = true)
	private List<VendorAgreementDetail> vendorDetails = new ArrayList<VendorAgreementDetail>();

	public  VendorAgreement() {
		
	}
	
	public VendorAgreement(String vendorCode, String vendorDescription,
			String vendorEmailId, String contactName, String vendormobileNo,
			String vendorTelephoneNo, String vendorAddress,
			String agreementStatus, String vendorCountry,
			String vendorCurrency, Date agreementStartDate,
			Date agreementEndDate, String contentType) {
		
		this.vendorCode = vendorCode;
		this.vendorDescription = vendorDescription;
		this.vendorEmailid = vendorEmailId;
		this.vendorContactName = contactName;
		this.vendorMobileno = vendormobileNo;
		this.vendorTelephoneno = vendorTelephoneNo;
		this.vendorAddress = vendorAddress;
		this.agreementStatus = agreementStatus;
		this.vendorCountry = vendorCountry;
		this.vendorCurrency = vendorCurrency;
		this.agreementStartdate = agreementStartDate;
		this.agreementEnddate = agreementEndDate;
		this.contentType = contentType;
		
	}

	public List<VendorAgreementDetail> getMediaassetLocations() {
		return vendorDetails;
	}

	
	public static VendorAgreement fromJson(final JsonCommand command) {
		
		 final String vendorCode = command.stringValueOfParameterNamed("vendorCode");
		 final String vendorDescription = command.stringValueOfParameterNamed("vendorDescription");
		 final String vendorEmailId = command.stringValueOfParameterNamed("vendorEmailId");
		 final String contactName = command.stringValueOfParameterNamed("contactName");
		 final String vendormobileNo = command.stringValueOfParameterNamed("vendormobileNo");
		 final String vendorTelephoneNo = command.stringValueOfParameterNamed("vendorTelephoneNo");
		 final String vendorAddress = command.stringValueOfParameterNamed("vendorAddress");
		 final String agreementStatus = command.stringValueOfParameterNamed("agreementStatus");
		 final String vendorCountry = command.stringValueOfParameterNamed("vendorCountry");
		 final String vendorCurrency = command.stringValueOfParameterNamed("vendorCurrency");
		 final LocalDate agreementStartDate = command.localDateValueOfParameterNamed("agreementStartDate");
		 final LocalDate agreementEndDate = command.localDateValueOfParameterNamed("agreementEndDate");
		 final String contentType = command.stringValueOfParameterNamed("contentType");
	
		 /*final BigDecimal cpShareValue=command.bigDecimalValueOfParameterNamed("cpShareValue");*/
		
		 return new VendorAgreement(vendorCode, vendorDescription, vendorEmailId, contactName,
				 vendormobileNo, vendorTelephoneNo, vendorAddress, agreementStatus, vendorCountry, vendorCurrency, 
				 agreementStartDate.toDate(), agreementEndDate.toDate(), contentType);
	}


	public void addVendorDetails(final VendorAgreementDetail vendorDetails) {
		vendorDetails.update(this);
        this.vendorDetails.add(vendorDetails);
	}
	
	public Map<String, Object> update(JsonCommand command){
	
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String vendorCodeParamName = "vendorCode";
		if(command.isChangeInStringParameterNamed(vendorCodeParamName, this.vendorCode)){
			final String newValue = command.stringValueOfParameterNamed(vendorCodeParamName);
			actualChanges.put(vendorCodeParamName, newValue);
			this.vendorCode = StringUtils.defaultIfEmpty(newValue,null);
		}
		final String vendorDescriptionParamName = "vendorDescription";
		if(command.isChangeInStringParameterNamed(vendorDescriptionParamName, this.vendorDescription)){
			final String newValue = command.stringValueOfParameterNamed(vendorDescriptionParamName);
			actualChanges.put(vendorDescriptionParamName, newValue);
			this.vendorDescription = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String vendorEmailIdParamName = "vendorEmailId";
		if(command.isChangeInStringParameterNamed(vendorEmailIdParamName,this.vendorEmailid)){
			final String newValue = command.stringValueOfParameterNamed(vendorEmailIdParamName);
			actualChanges.put(vendorEmailIdParamName, newValue);
			this.vendorEmailid = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		final String contactNameParamName = "contactName";
		if(command.isChangeInStringParameterNamed(contactNameParamName,this.vendorContactName)){
			final String newValue = command.stringValueOfParameterNamed(contactNameParamName);
			actualChanges.put(contactNameParamName, newValue);
			this.vendorContactName = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		final String vendormobileNoParamName = "vendormobileNo";
		if(command.isChangeInStringParameterNamed(vendormobileNoParamName, this.vendorMobileno)){
			final String newValue = command.stringValueOfParameterNamed(vendormobileNoParamName);
			actualChanges.put(vendormobileNoParamName, newValue);
			this.vendorMobileno = StringUtils.defaultIfEmpty(newValue,null); 
		}
		
		final String vendorTelephoneNoParamName = "vendorTelephoneNo";
		if(command.isChangeInStringParameterNamed(vendorTelephoneNoParamName, this.vendorTelephoneno)){
			final String newValue = command.stringValueOfParameterNamed(vendorTelephoneNoParamName);
			actualChanges.put(vendorTelephoneNoParamName, newValue);
			this.vendorTelephoneno = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		final String vendorAddressParamName = "vendorAddress";
		if(command.isChangeInStringParameterNamed(vendorAddressParamName, this.vendorAddress)){
			final String newValue = command.stringValueOfParameterNamed(vendorAddressParamName);
			actualChanges.put(vendorAddressParamName, newValue);
			this.vendorAddress = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		final String agreementStatusParamName = "agreementStatus";
		if(command.isChangeInStringParameterNamed(agreementStatusParamName, this.agreementStatus)){
			final String newValue = command.stringValueOfParameterNamed(agreementStatusParamName);
			actualChanges.put(agreementStatusParamName, newValue);
			this.agreementStatus = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		final String vendorCountryParamName = "vendorCountry";
		if(command.isChangeInStringParameterNamed(vendorCountryParamName, this.vendorCountry)){
			final String newValue = command.stringValueOfParameterNamed(vendorCountryParamName);
			actualChanges.put(vendorCountryParamName, newValue);
			this.vendorCountry = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		final String vendorCurrencyParamName = "vendorCurrency";
		if(command.isChangeInStringParameterNamed(vendorCurrencyParamName, this.vendorCurrency)){
			final String newValue = command.stringValueOfParameterNamed(vendorCurrencyParamName);
			actualChanges.put(vendorCurrencyParamName, newValue);
			this.vendorCurrency = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		final String agreementStartDateParamName = "agreementStartDate";
		if(command.isChangeInDateParameterNamed(agreementStartDateParamName, this.agreementStartdate)){
			final LocalDate newValue = command.localDateValueOfParameterNamed(agreementStartDateParamName);
			actualChanges.put(agreementStartDateParamName,newValue.toDate());
			this.agreementStartdate = newValue.toDate();
		}
		
		final String agreementEndDateParamName = "agreementEndDate";
		if(command.isChangeInDateParameterNamed(agreementEndDateParamName, this.agreementEnddate)){
			final LocalDate newValue = command.localDateValueOfParameterNamed(agreementEndDateParamName);
			actualChanges.put(agreementEndDateParamName,newValue.toDate());
			this.agreementEnddate = newValue.toDate();
		}
		
		final String contentTypeParamName = "contentType";
		if(command.isChangeInStringParameterNamed(contentTypeParamName, this.contentType)){
			final String newValue = command.stringValueOfParameterNamed(contentTypeParamName);
			actualChanges.put(contentTypeParamName, newValue);
			this.contentType = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		return actualChanges;
	
	}
	
}