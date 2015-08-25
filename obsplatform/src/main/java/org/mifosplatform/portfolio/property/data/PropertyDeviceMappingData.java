package org.mifosplatform.portfolio.property.data;

public class PropertyDeviceMappingData {

	Long id;
	String serialNumber;
	String propertycode;
	
	public PropertyDeviceMappingData(Long id, String serialNumber,String propertyCode) {
		
		this.id = id;
		this.serialNumber = serialNumber;
		this.propertycode = propertyCode;
				
	}

	public Long getId() {
		return id;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getPropertycode() {
		return propertycode;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setPropertycode(String propertycode) {
		this.propertycode = propertycode;
	}
	
	
	

}
