package org.obsplatform.portfolio.association.service;

import java.util.List;

import org.obsplatform.portfolio.association.data.AssociationData;
import org.obsplatform.portfolio.association.data.HardwareAssociationData;


public interface HardwareAssociationReadplatformService {

	//List<HardwareAssociationData> retrieveClientHardwareDetails(Long clientId);
	
	List<AssociationData> retrieveClientAssociationDetails(Long clientId, String serialNumber);

	List<HardwareAssociationData> retrieveClientAllocatedPlan(Long clientId, String itemCode);

	List<AssociationData> retrieveClientAssociationDetails(Long clientId);

	AssociationData retrieveSingleDetails(Long id);

	List<AssociationData> retrieveHardwareData(Long clientId);

	List<AssociationData> retrieveplanData(Long clientId);

	List<HardwareAssociationData> retrieveClientAllocatedHardwareDetails(Long clientId);

	List<AssociationData> retrieveCustomerHardwareAllocationData(Long clientId);
	
	
}
