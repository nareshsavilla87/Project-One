package org.mifosplatform.portfolio.propertycode.master.service;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.portfolio.propertycode.master.data.PropertyCodeMasterData;

public interface PropertyCodeMasterReadPlatformService {
	
	Page<PropertyCodeMasterData> retrieveAllPropertyCodeMasterData(SearchSqlQuery searchPropertyDetails);

}
