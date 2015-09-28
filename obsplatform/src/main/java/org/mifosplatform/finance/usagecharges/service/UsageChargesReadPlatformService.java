
package org.mifosplatform.finance.usagecharges.service;

import java.util.List;

import org.mifosplatform.finance.usagecharges.data.UsageChargesData;

/**
 * @author Ranjith
 * 
 */
public interface UsageChargesReadPlatformService {

	List<UsageChargesData> retrieveOrderCdrData(Long clientId, Long orderId);

}
