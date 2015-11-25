package org.obsplatform.billing.chargecode.handler;

import org.obsplatform.billing.chargecode.service.ChargeCodeWritePlatformService;
import org.obsplatform.commands.handler.NewCommandSourceHandler;
import org.obsplatform.infrastructure.core.api.JsonCommand;
import org.obsplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UpdateChargeCodeCommandHandler implements NewCommandSourceHandler {

	private final ChargeCodeWritePlatformService chargeCodeWritePlatformService;

	@Autowired
	public UpdateChargeCodeCommandHandler(
			final ChargeCodeWritePlatformService chargeCodeWritePlatformService) {
		this.chargeCodeWritePlatformService = chargeCodeWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(final JsonCommand command) {
		return chargeCodeWritePlatformService.updateChargeCode(command,
				command.entityId());
	}

}
