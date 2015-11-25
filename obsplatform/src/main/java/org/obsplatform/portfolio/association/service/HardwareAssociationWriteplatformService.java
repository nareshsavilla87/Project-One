package org.obsplatform.portfolio.association.service;

import org.obsplatform.infrastructure.core.api.JsonCommand;
import org.obsplatform.infrastructure.core.data.CommandProcessingResult;

public interface HardwareAssociationWriteplatformService {

	void createNewHardwareAssociation(Long clientId, Long id, String serialNo, Long orderId, String allocationType);

	CommandProcessingResult createAssociation(JsonCommand command);

	CommandProcessingResult updateAssociation(JsonCommand command);

	CommandProcessingResult deAssociationHardware(Long orderId);

}
