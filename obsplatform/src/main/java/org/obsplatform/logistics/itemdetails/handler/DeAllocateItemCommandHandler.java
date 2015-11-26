package org.obsplatform.logistics.itemdetails.handler;

import org.obsplatform.commands.handler.NewCommandSourceHandler;
import org.obsplatform.infrastructure.core.api.JsonCommand;
import org.obsplatform.infrastructure.core.data.CommandProcessingResult;
import org.obsplatform.logistics.itemdetails.service.ItemDetailsWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeAllocateItemCommandHandler implements NewCommandSourceHandler{

	private ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService;
	
	
	@Autowired
	public DeAllocateItemCommandHandler(final ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService){
		this.inventoryItemDetailsWritePlatformService = inventoryItemDetailsWritePlatformService;
	}
	
	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return inventoryItemDetailsWritePlatformService.deAllocateHardware(command);
	}

}
