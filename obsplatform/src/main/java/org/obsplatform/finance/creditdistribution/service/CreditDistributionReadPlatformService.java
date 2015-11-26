package org.obsplatform.finance.creditdistribution.service;

import org.obsplatform.finance.creditdistribution.data.CreditDistributionData;
import org.obsplatform.infrastructure.core.service.Page;

public interface CreditDistributionReadPlatformService {


	Page<CreditDistributionData> getClientDistributionData(Long clientId);
}
