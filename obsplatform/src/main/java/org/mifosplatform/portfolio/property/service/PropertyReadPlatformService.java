package org.mifosplatform.portfolio.property.service;

import java.util.List;

import org.mifosplatform.portfolio.property.data.PropertyDefinationData;

public interface PropertyReadPlatformService {

	List<PropertyDefinationData> retrieveAllProperties();

}
