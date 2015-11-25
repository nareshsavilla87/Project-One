package org.obsplatform.billing.servicetransfer.service;

import java.util.List;

import org.obsplatform.organisation.feemaster.data.FeeMasterData;

public interface ServiceTransferReadPlatformService {
	
	List<FeeMasterData> retrieveSingleFeeDetails(Long clientId, String transationType);
}
