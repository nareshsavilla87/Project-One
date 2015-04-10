package org.mifosplatform.portfolio.property.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.organisation.address.data.CityDetailsData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

public class PropertyDefinationData {
	
	private Long id;
	private Long propertyTypeId;
	private String propertyType;
	private String propertyCode;
	private String unitCode;
	private Long floor;
	private String buildingCode;
	private String parcel;
	private String precinct;
	private String street;
	private String poBox;
	private String state;
	private String country;
	private String status;
	private String clientId;
	private Collection<MCodeData> propertyTypes;
	private List<String> countryData;
	private List<String> statesData;
	private List<CityDetailsData> citiesData;
	

	public PropertyDefinationData(final Long id, final Long propertyTypeId,final String propertyType, final String propertyCode, final String unitCode,
			final Long floor, final String buildingCode, final String parcel, final String precinct,final String street, final String poBox, final String state, 
			final String country,final String status, final String clientId) {
		
		this.id = id;
		this.propertyTypeId = propertyTypeId;
		this.propertyType = propertyType;
		this.propertyCode = propertyCode;
		this.unitCode = unitCode;
		this.floor = floor;
		this.buildingCode = buildingCode;
		this.parcel = parcel;
		this.precinct = precinct;
		this.street = street;
		this.poBox = poBox;
		this.state = state;
		this.country = country;
		this.status = status;
		this.clientId = clientId;
		
	
	}


	public PropertyDefinationData(Collection<MCodeData> propertyTypes, List<CityDetailsData> citiesData) {
	
		this.propertyTypes = propertyTypes;
		this.citiesData = citiesData;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getPropertyTypeId() {
		return propertyTypeId;
	}


	public void setPropertyTypeId(Long propertyTypeId) {
		this.propertyTypeId = propertyTypeId;
	}


	public String getPropertyType() {
		return propertyType;
	}


	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
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


	public String getClientId() {
		return clientId;
	}


	

	public Collection<MCodeData> getPropertyTypes() {
		return propertyTypes;
	}


	public void setPropertyTypes(Collection<MCodeData> propertyTypes) {
		this.propertyTypes = propertyTypes;
	}


	public List<String> getCountryData() {
		return countryData;
	}


	public void setCountryData(List<String> countryData) {
		this.countryData = countryData;
	}


	public List<String> getStatesData() {
		return statesData;
	}


	public void setStatesData(List<String> statesData) {
		this.statesData = statesData;
	}


	public List<CityDetailsData> getCitiesData() {
		return citiesData;
	}


	public void setCitiesData(List<CityDetailsData> citiesData) {
		this.citiesData = citiesData;
	}
	
	
}
