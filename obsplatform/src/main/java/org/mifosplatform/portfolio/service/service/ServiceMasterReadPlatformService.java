package org.mifosplatform.portfolio.service.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.service.data.ServiceMasterData;
import org.mifosplatform.portfolio.service.data.ServiceMasterOptionsData;

public interface ServiceMasterReadPlatformService {
	

	List<ServiceData> retrieveAllServices();
	
	 Collection<ServiceMasterData> retrieveAllServiceMasterData() ;

	Page<ServiceMasterOptionsData> retrieveServices(SearchSqlQuery searchCodes);

	ServiceMasterOptionsData retrieveIndividualService(Long serviceId);

	List<EnumOptionData> retrieveServicesTypes();

	List<EnumOptionData> retrieveServiceUnitType();
}
