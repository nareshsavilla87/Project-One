package org.mifosplatform.freeradius.radius.data;


public class RadiusServiceData  {
	
	private final Long id;
	private final String serviceName;
	private final String upRate;
	private final String downRate;
	private final Long nextServicId;
	private final Long trafficUnitdl;
	private String nextService;

	public RadiusServiceData(Long id, String serviceName, String downRate, String upRate, Long nextServicId, Long trafficUnitdl, String nextService) {
    
		this.id= id;
		this.serviceName = serviceName;
		this.downRate = downRate;
		this.upRate = upRate;
		this.nextServicId = nextServicId;
		this.trafficUnitdl = trafficUnitdl;
		this.nextService = nextService;
	}

	public Long getId() {
		return id;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getUpRate() {
		return upRate;
	}

	public String getDownRate() {
		return downRate;
	}

	public Long getNextServicId() {
		return nextServicId;
	}

	public Long getTrafficUnitdl() {
		return trafficUnitdl;
	}

	public String getNextService() {
		return nextService;
	}
	
	
	

}
