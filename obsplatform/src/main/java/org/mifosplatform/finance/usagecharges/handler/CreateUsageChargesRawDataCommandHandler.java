package org.mifosplatform.finance.usagecharges.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.finance.usagecharges.service.UsageChargesWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateUsageChargesRawDataCommandHandler implements NewCommandSourceHandler {

	private final UsageChargesWritePlatformService writePlatformService;

	@Autowired
	public CreateUsageChargesRawDataCommandHandler(final UsageChargesWritePlatformService writePlatformService) {
		this.writePlatformService = writePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(final JsonCommand command) {

		return this.writePlatformService.createUsageChargesRawData(command);
	}
	
	
}
