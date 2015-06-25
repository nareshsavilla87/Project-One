package org.mifosplatform.portfolio.property.data;

public class PropertyDeviceMappingData {

	final Long id;
	final String serialNumber;
	final String propertycode;
	
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
	
	

}
