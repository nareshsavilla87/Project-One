package org.mifosplatform.finance.usagecharges.data;

public class UsageChargesData {

	private Long clientId;
	private String number;

	public UsageChargesData(Long clientId, String number) {

		this.clientId = clientId;
		this.number = number;

	}

	public Long getClientId() {
		return clientId;
	}

	public String getNumber() {
		return number;
	}

}
