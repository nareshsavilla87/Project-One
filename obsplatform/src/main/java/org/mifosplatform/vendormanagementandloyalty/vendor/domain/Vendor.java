package org.mifosplatform.vendormanagementandloyalty.vendor.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_vendor_management")
public class Vendor extends  AbstractPersistable<Long> {

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
	private List<VendorDetail> vendorDetails = new ArrayList<VendorDetail>();

	public  Vendor() {
		
	}
	
	public Vendor(String vendorCode, String vendorDescription,
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

	public List<VendorDetail> getMediaassetLocations() {
		return vendorDetails;
	}

	
	public static Vendor fromJson(final JsonCommand command) {
		
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
		
		 return new Vendor(vendorCode, vendorDescription, vendorEmailId, contactName,
				 vendormobileNo, vendorTelephoneNo, vendorAddress, agreementStatus, vendorCountry, vendorCurrency, 
				 agreementStartDate.toDate(), agreementEndDate.toDate(), contentType);
	}


	public void addMediaLocations(final VendorDetail vendorDetails) {
		vendorDetails.update(this);
        this.vendorDetails.add(vendorDetails);
	}
	
}