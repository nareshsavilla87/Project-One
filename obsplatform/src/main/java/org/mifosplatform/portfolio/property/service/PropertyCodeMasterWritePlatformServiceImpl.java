package org.mifosplatform.portfolio.property.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.property.domain.PropertyCodeMaster;
import org.mifosplatform.portfolio.property.domain.PropertyCodeMasterRepository;
import org.mifosplatform.portfolio.property.serialization.PropertyCodeMasterCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class PropertyCodeMasterWritePlatformServiceImpl implements PropertyCodeMasterWritePlatformService {
	


	private final PlatformSecurityContext context;
	 private final PropertyCodeMasterCommandFromApiJsonDeserializer propertyCodeMasterCommandFromApiJsonDeserializer;
	private final PropertyCodeMasterRepository propertyCodeMasterRepository;

	@Autowired
	 public PropertyCodeMasterWritePlatformServiceImpl(final PlatformSecurityContext context,
			 final PropertyCodeMasterRepository propertyDefinitionRepository,
			 final PropertyCodeMasterCommandFromApiJsonDeserializer propertyCodeMasterCommandFromApiJsonDeserializer)
	{
		this.context=context;
		this.propertyCodeMasterRepository=propertyDefinitionRepository;
		 this.propertyCodeMasterCommandFromApiJsonDeserializer=propertyCodeMasterCommandFromApiJsonDeserializer;
	}

	@Override
	public CommandProcessingResult createPropertyCodeMaster(JsonCommand command) {
		try
		{
			context.authenticatedUser();
			this.propertyCodeMasterCommandFromApiJsonDeserializer.validateForCreate(command.json());
			final PropertyCodeMaster propertyCodeMaster = PropertyCodeMaster.fromJson(command);
			this.propertyCodeMasterRepository.save(propertyCodeMaster);
				return new CommandProcessingResult(propertyCodeMaster.getId());

		} catch (DataIntegrityViolationException dve) {
			 handleCodeDataIntegrityIssues(command, dve);
			return  CommandProcessingResult.empty();
		}
	}
	
	private void handleCodeDataIntegrityIssues(final JsonCommand command,final DataIntegrityViolationException dve) {

	        final Throwable realCause = dve.getMostSpecificCause();
	        if (dve.getMostSpecificCause().getMessage().contains("property_code_type_with_its_code")) {
	            final String name = command.stringValueOfParameterNamed("propertyCodeType");
	            throw new PlatformDataIntegrityException("error.msg.propertycode.master.propertyCodeType.duplicate.name", "A Property Code Type with name '" + name + "' already exists",name);
	        }

	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
		
	    }

}
