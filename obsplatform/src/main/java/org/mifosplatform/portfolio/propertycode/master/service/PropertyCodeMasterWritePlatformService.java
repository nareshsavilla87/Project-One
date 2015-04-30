package org.mifosplatform.portfolio.propertycode.master.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface PropertyCodeMasterWritePlatformService {

	CommandProcessingResult createPropertyCodeMaster(JsonCommand command);
}
