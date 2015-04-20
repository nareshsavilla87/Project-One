package org.mifosplatform.billing.servicetransfer.service;

import org.mifosplatform.organisation.feemaster.data.FeeMasterData;

public interface ServiceTransferReadPlatformService {
	
	FeeMasterData retrieveSingleFeeDetails(Long clientId,Long feeId,boolean isWithClientId);
}
