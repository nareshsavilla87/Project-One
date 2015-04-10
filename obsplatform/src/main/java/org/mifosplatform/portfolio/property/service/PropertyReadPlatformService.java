package org.mifosplatform.portfolio.property.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.portfolio.property.data.PropertyDefinationData;

public interface PropertyReadPlatformService {

	Page<PropertyDefinationData> retrieveAllProperties(SearchSqlQuery searchPropertyDetails);

	List<PropertyDefinationData> retrieveAllPropertiesForSearch(String propertyCode);

	PropertyDefinationData retrievePropertyDetails(Long propertyId);

}
