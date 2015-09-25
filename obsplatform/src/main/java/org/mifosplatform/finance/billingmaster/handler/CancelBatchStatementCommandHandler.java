package org.mifosplatform.finance.billingmaster.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.finance.billingmaster.service.BillMasterWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelBatchStatementCommandHandler implements NewCommandSourceHandler {
	
	 private final BillMasterWritePlatformService writePlatformService;
	 
	@Autowired
    public CancelBatchStatementCommandHandler(final BillMasterWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		// TODO Auto-generated method stub
		return this.writePlatformService.cancelBatchStatement(command.getSupportedEntityType());
	}

}
