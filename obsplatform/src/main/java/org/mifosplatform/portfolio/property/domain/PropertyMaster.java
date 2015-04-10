package org.mifosplatform.portfolio.property.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_property_defination", uniqueConstraints = @UniqueConstraint(name = "property_code_constraint", columnNames = { "property_code" }))
public class PropertyMaster extends AbstractPersistable<Long> {

	private static final long serialVersionUID = 1L;

	@Column(name = "property_type_id")
	private Long propertyTypeId;

	@Column(name = "property_code")
	private String propertyCode;

	@Column(name = "unit_code")
	private String unitCode;

	@Column(name = "floor")
	private Long floor;

	@Column(name = "building_code")
	private String buildingCode;

	@Column(name = "parcel")
	private String parcel;

	@Column(name = "precinct")
	private String precinct;

	@Column(name = "street")
	private String street;

	@Column(name = "po_box")
	private String poBox;

	@Column(name = "state")
	private String state;

	@Column(name = "country")
	private String country;

	@Column(name = "status")
	private String status;

	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "is_deleted")
	private char isDeleted = 'N';

	public PropertyMaster(){
		
	}
	
	
	public PropertyMaster(final Long propertyTypeId, final String propertyCode,final String unitCode, final Long floor, final String buildingCode,
			final String parcel, final String precinct, final String poBox, final String street,final String state, final String country, String status) {
        
		this.propertyTypeId = propertyTypeId;
		this.propertyCode = propertyCode;
		this.unitCode = unitCode;
	    this.floor = floor;
	    this.buildingCode = buildingCode;
	    this.parcel = parcel;
	    this.precinct = precinct;
	    this.poBox = poBox;
	    this.street = street;
	    this.state = state;
	    this.status = status;
	    this.country = country;
	    this.status = CodeNameConstants.CODE_PROPERTY_VACANT;
	}


	public static PropertyMaster fromJson(final JsonCommand command) {

		final String propertyCode = command.stringValueOfParameterNamed("propertyCode");
		final Long propertyTypeId = command.longValueOfParameterNamed("propertyType");
		final String unitCode = command.stringValueOfParameterNamed("unitCode");
		final Long floor = command.longValueOfParameterNamed("floor");
		final String buildingCode = command.stringValueOfParameterNamed("buildingCode");
		final String parcel = command.stringValueOfParameterNamed("parcel");
		final String precinct = command.stringValueOfParameterNamed("precinct");
		final String poBox = command.stringValueOfParameterNamed("poBox");
		final String street = command.stringValueOfParameterNamed("street");
		final String state = command.stringValueOfParameterNamed("state");
		final String country = command.stringValueOfParameterNamed("country");
		
		return new PropertyMaster(propertyTypeId,propertyCode,unitCode,floor,buildingCode,parcel,precinct,poBox,street,state,country,CodeNameConstants.CODE_PROPERTY_VACANT);

	}


	public Long getPropertyTypeId() {
		return propertyTypeId;
	}


	public void setPropertyTypeId(Long propertyTypeId) {
		this.propertyTypeId = propertyTypeId;
	}


	public String getPropertyCode() {
		return propertyCode;
	}


	public void setPropertyCode(String propertyCode) {
		this.propertyCode = propertyCode;
	}


	public String getUnitCode() {
		return unitCode;
	}


	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}


	public Long getFloor() {
		return floor;
	}


	public void setFloor(Long floor) {
		this.floor = floor;
	}


	public String getBuildingCode() {
		return buildingCode;
	}


	public void setBuildingCode(String buildingCode) {
		this.buildingCode = buildingCode;
	}


	public String getParcel() {
		return parcel;
	}


	public void setParcel(String parcel) {
		this.parcel = parcel;
	}


	public String getPrecinct() {
		return precinct;
	}


	public void setPrecinct(String precinct) {
		this.precinct = precinct;
	}


	public String getStreet() {
		return street;
	}


	public void setStreet(String street) {
		this.street = street;
	}


	public String getPoBox() {
		return poBox;
	}


	public void setPoBox(String poBox) {
		this.poBox = poBox;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Long getClientId() {
		return clientId;
	}


	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}


	public void delete() {
		
		if (this.isDeleted == 'N') {
			this.isDeleted = 'Y';
			this.propertyCode = this.propertyCode+"_"+this.getId();
		}
	}

	
	
}
