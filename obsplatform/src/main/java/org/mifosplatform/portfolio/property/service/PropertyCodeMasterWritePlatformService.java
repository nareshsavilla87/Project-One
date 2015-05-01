package org.mifosplatform.portfolio.property.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface PropertyCodeMasterWritePlatformService {

	CommandProcessingResult createPropertyCodeMaster(JsonCommand command);
}
