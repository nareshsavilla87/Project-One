package org.mifosplatform.logistics.onetimesale.data;

import org.joda.time.LocalDate;

public class AllocationDetailsData {

	private Long id;
	private String itemDescription;
	private String serialNo;
	private LocalDate allocationDate;
	private Long itemDetailId;
	private String allocationType;
	private String quality;
	private String hardwareStatus;
	private Long orderId;
	private Long serviceId;
	private Long planId;
	

	public AllocationDetailsData(final Long id, final String itemDescription,
			final String serialNo, final LocalDate allocationDate, final Long itemDetailId, String allocationType,
			String quality, String hardwareStatus) {
		this.id = id;
		this.itemDescription = itemDescription;
		this.serialNo = serialNo;
		this.allocationDate = allocationDate;
		this.itemDetailId = itemDetailId;
		this.allocationType=allocationType;
		this.quality = quality;
		this.hardwareStatus = hardwareStatus;
	}

	public AllocationDetailsData(final Long id, final Long orderId, final String serialNum,
			final Long clientId) {

		this.id = id;
		this.serialNo = serialNum;
		this.itemDescription = null;
		this.allocationDate = null;
		this.itemDetailId = null;
		this.allocationType=null;
		this.quality = null;

	}

	/**
	 * @param serviceId
	 * @param planId
	 * @param allocationType
	 * @param serialNum
	 * @param itemDescription
	 */
	public AllocationDetailsData(final Long serviceId, final Long planId,final Long orderId,
			final String allocationType, final String serialNum, final String itemDescription) {
		
		this.serviceId = serviceId;
		this.planId = planId;
		this.orderId = orderId;
		this.allocationType = allocationType;
		this.serialNo = serialNum;
		this.itemDescription = itemDescription;
	}

	public Long getId() {
		return id;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public String getSerialNo() {
		return serialNo;
	}
	
	

	public String getAllocationType() {
		return allocationType;
	}

	public LocalDate getAllocationDate() {
		return allocationDate;
	}

	public Long getItemDetailId() {
		return itemDetailId;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public String getHardwareStatus() {
		return hardwareStatus;
	}

	public void setHardwareStatus(String hardwareStatus) {
		this.hardwareStatus = hardwareStatus;
	}

	/**
	 * @return the orderId
	 */
	public Long getOrderId() {
		return orderId;
	}

	/**
	 * @return the serviceId
	 */
	public Long getServiceId() {
		return serviceId;
	}

	/**
	 * @return the planId
	 */
	public Long getPlanId() {
		return planId;
	}
	
	
	
}
