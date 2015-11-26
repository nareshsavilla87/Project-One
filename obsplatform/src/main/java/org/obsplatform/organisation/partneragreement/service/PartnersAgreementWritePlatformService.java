package org.obsplatform.organisation.partneragreement.service;

import org.obsplatform.infrastructure.core.api.JsonCommand;
import org.obsplatform.infrastructure.core.data.CommandProcessingResult;

public interface PartnersAgreementWritePlatformService {

	CommandProcessingResult createNewPartnerAgreement(JsonCommand command);

	CommandProcessingResult UpdatePartnerAgreement(JsonCommand command);

	CommandProcessingResult deletePartnerAgreement(Long entityId);

	
}
