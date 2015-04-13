package org.mifosplatform.portfolio.property.service;

import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.portfolio.property.domain.PropertyMaster;
import org.mifosplatform.portfolio.property.domain.PropertyMasterRepository;
import org.mifosplatform.portfolio.property.exceptions.PropertyMasterNotFoundException;
import org.mifosplatform.portfolio.property.serialization.PropertyCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropertyWriteplatformServiceImpl implements PropertyWriteplatformService {

	private final static Logger LOGGER = LoggerFactory.getLogger(PropertyWriteplatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final PropertyCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final PropertyMasterRepository propertyMasterRepository;

	@Autowired
	public PropertyWriteplatformServiceImpl(final PlatformSecurityContext context,
			final PropertyCommandFromApiJsonDeserializer apiJsonDeserializer,
			final PropertyMasterRepository propertyMasterRepository) {

		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.propertyMasterRepository = propertyMasterRepository;
	}

	@Transactional
	@Override
	public CommandProcessingResult createProperty(final JsonCommand command) {

		try {
			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			PropertyMaster propertyMaster = PropertyMaster.fromJson(command);
			this.propertyMasterRepository.save(propertyMaster);
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
					    .withEntityId(propertyMaster.getId()).build();

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(1L));
		}

	}
	
	@Transactional
	@Override
	public CommandProcessingResult updateProperty(final Long entityId,final JsonCommand command) {
		
		try{
			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			PropertyMaster propertyMaster=this.propertyRetrieveById(entityId);
			final Map<String,Object> changes=propertyMaster.update(command);
			if (!changes.isEmpty()) {
				this.propertyMasterRepository.saveAndFlush(propertyMaster);
			}
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
				       .withEntityId(propertyMaster.getId()).with(changes).build();
		}catch(DataIntegrityViolationException dve){
			
			if (dve.getCause() instanceof ConstraintViolationException) {
				handleCodeDataIntegrityIssues(command, dve);
			}
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	 }

	@Transactional
	@Override
	public CommandProcessingResult deleteProperty(final Long entityId) {

		PropertyMaster propertyMaster = null;
		try {
			this.context.authenticatedUser();
			propertyMaster = this.propertyRetrieveById(entityId);
			if(propertyMaster.getStatus()!=null){
			  if (propertyMaster.getStatus().equalsIgnoreCase(CodeNameConstants.CODE_PROPERTY_VACANT)) {
				propertyMaster.delete();
				this.propertyMasterRepository.save(propertyMaster);
			  } else {
				throw new PropertyMasterNotFoundException(entityId,propertyMaster.getStatus());
			  }
		   }
		 return new CommandProcessingResult(entityId);
	   } catch (final DataIntegrityViolationException dve) {
			throw new PlatformDataIntegrityException("error.msg.could.unknown.data.integrity.issue",
					"Unknown data integrity issue with resource: "+ dve.getMessage());
		}

	}

	private void handleCodeDataIntegrityIssues(final JsonCommand command,final DataIntegrityViolationException dve) {
		
		final Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("property_code_constraint")) {
			final String code = command.stringValueOfParameterNamed("propertyCode");
			throw new PlatformDataIntegrityException("error.msg.property.duplicate.code",
					"A property with Code'" + code + "'already exists","propertyCode", code);
		}

		LOGGER.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException("error.msg.could.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: "+ realCause.getMessage());

	}

	private PropertyMaster propertyRetrieveById(final Long entityId) {

		PropertyMaster propertyMaster = this.propertyMasterRepository.findOne(entityId);
		if (propertyMaster == null) {
			throw new PropertyMasterNotFoundException(entityId);
		}
		return propertyMaster;
	}

}
