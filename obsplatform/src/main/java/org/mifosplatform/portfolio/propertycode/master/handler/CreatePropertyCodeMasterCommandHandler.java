package org.mifosplatform.portfolio.propertycode.master.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.propertycode.master.service.PropertyCodeMasterWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePropertyCodeMasterCommandHandler implements NewCommandSourceHandler {

	 private final PropertyCodeMasterWritePlatformService propertyCodeMasterWritePlatformService;

	    @Autowired
	    public CreatePropertyCodeMasterCommandHandler(final PropertyCodeMasterWritePlatformService propertyCodeMasterWritePlatformService) {
	        this.propertyCodeMasterWritePlatformService = propertyCodeMasterWritePlatformService;
	    }

	    @Transactional
	    @Override
	    public CommandProcessingResult processCommand(final JsonCommand command) {

	        return this.propertyCodeMasterWritePlatformService.createPropertyCodeMaster(command);
	    }

}
