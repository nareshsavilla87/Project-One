package org.mifosplatform.organisation.address.data;

public class CityDetailsData {

	private String cityName;

	private String cityCode;

	public CityDetailsData(String cityName, String cityCode) {

		this.cityCode = cityCode;
		this.cityName = cityName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	
	
}
