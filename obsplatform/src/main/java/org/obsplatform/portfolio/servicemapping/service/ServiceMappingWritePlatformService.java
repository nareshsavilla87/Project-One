package org.obsplatform.portfolio.servicemapping.service;

import org.obsplatform.infrastructure.core.api.JsonCommand;
import org.obsplatform.infrastructure.core.data.CommandProcessingResult;

public interface ServiceMappingWritePlatformService {
	
	public CommandProcessingResult createServiceMapping(JsonCommand command);

	public CommandProcessingResult updateServiceMapping(Long entityId,JsonCommand command);

}
