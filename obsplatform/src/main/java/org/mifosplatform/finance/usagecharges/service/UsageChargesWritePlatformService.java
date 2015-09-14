
package org.mifosplatform.finance.usagecharges.service;

import java.util.List;

import org.mifosplatform.finance.billingorder.commands.BillingOrderCommand;
import org.mifosplatform.finance.billingorder.data.BillingOrderData;
import org.mifosplatform.finance.usagecharges.data.UsageChargesData;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

/**
 * @author Ranjith
 *
 */
public interface UsageChargesWritePlatformService {

	CommandProcessingResult createUsageChargesRawData(JsonCommand command);

	void processCustomerUsageRawData(UsageChargesData customerData);

	BillingOrderCommand checkOrderUsageCharges(Long clientId,Long orderId, List<BillingOrderData> products);
	
	void UpdateChargeId(Long chargeId);

}
