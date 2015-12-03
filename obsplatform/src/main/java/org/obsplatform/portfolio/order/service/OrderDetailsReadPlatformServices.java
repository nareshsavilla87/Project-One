package org.obsplatform.portfolio.order.service;

import java.util.List;

import org.obsplatform.billing.planprice.data.PriceData;
import org.obsplatform.portfolio.plan.data.ServiceData;

public interface OrderDetailsReadPlatformServices {
	
	List<ServiceData> retrieveAllServices(Long plan_code);

	List<PriceData> retrieveAllPrices(Long plan_code, String billingFreq,Long clientId);

	List<PriceData> retrieveDefaultPrices(Long planId, String billingFrequency,Long clientId);

	Long retrieveClientActivePlanOrderDetails(Long clientId, Long planId);
}
