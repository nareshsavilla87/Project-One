package org.mifosplatform.portfolio.servicemapping.service;

import java.util.Map;

import org.mifosplatform.infrastructure.codes.exception.CodeNotFoundException;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.servicemapping.domain.ServiceMapping;
import org.mifosplatform.portfolio.servicemapping.domain.ServiceMappingRepository;
import org.mifosplatform.portfolio.servicemapping.serialization.ServiceMappingCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

	
@Service
public class ServiceMappingWritePlatformServiceImpl implements ServiceMappingWritePlatformService{
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ServiceMappingWritePlatformServiceImpl.class);	
	
	private final PlatformSecurityContext platformSecurityContext;
	private final ServiceMappingRepository serviceMappingRepository; 
	private final ServiceMappingCommandFromApiJsonDeserializer serviceMappingCommandFromApiJsonDeserializer;
	private final ConfigurationRepository globalConfigurationRepository;
//	private ServiceMappingReadPlatformService serviceMappingReadPlatformService; 
	
	@Autowired
	public ServiceMappingWritePlatformServiceImpl(final PlatformSecurityContext platformSecurityContext, 
			final ServiceMappingRepository serviceMappingRepository, 
			final ServiceMappingCommandFromApiJsonDeserializer serviceMappingCommandFromApiJsonDeserializer,
			final ConfigurationRepository globalConfigurationRepository) {
		this.platformSecurityContext = platformSecurityContext;
		this.serviceMappingRepository = serviceMappingRepository;
		this.serviceMappingCommandFromApiJsonDeserializer = serviceMappingCommandFromApiJsonDeserializer;
		this.globalConfigurationRepository = globalConfigurationRepository;
	//	this.serviceMappingReadPlatformService = serviceMappingReadPlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult createServiceMapping(JsonCommand command) {
		
		try{
			platformSecurityContext.authenticatedUser();
			Configuration property = this.globalConfigurationRepository.findOneByName(ConfigurationConstants.CONFIG_IS_SERVICE_DEVICE_MAPPING);
			this.serviceMappingCommandFromApiJsonDeserializer.validateForCreate(command.json(), command.hasParameter("sortBy"),property !=null ? property.isEnabled():false);
			final ServiceMapping serviceMapping = ServiceMapping.fromJson(command,property !=null ? property.isEnabled():false);
			serviceMappingRepository.save(serviceMapping);
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(serviceMapping.getId()).build();
		}catch(DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}
	}

	@Override
	public CommandProcessingResult updateServiceMapping(Long serviceMapId,
			JsonCommand command) {
		try {

			this.platformSecurityContext.authenticatedUser();
			Map<String, Object> changes = null;
			Configuration property = this.globalConfigurationRepository.findOneByName(ConfigurationConstants.CONFIG_IS_SERVICE_DEVICE_MAPPING);
			this.serviceMappingCommandFromApiJsonDeserializer.validateForCreate(command.json(),command.hasParameter("sortBy"),property !=null ? property.isEnabled():false);
			final ServiceMapping serviceMapping = retrieveServiceMappingById(serviceMapId);
			
			if(command.hasParameter("sortBy")){
				serviceMapping.setSortBy(command.integerValueOfParameterNamed("sortBy"));
				this.serviceMappingRepository.save(serviceMapping);
			}else{
				changes = serviceMapping.update(command,property !=null ? property.isEnabled():false);
				if (!changes.isEmpty()) {
					this.serviceMappingRepository.saveAndFlush(serviceMapping);
				}
			}
			
			return new CommandProcessingResultBuilder() //
					.withCommandId(command.commandId()) //
					.withEntityId(serviceMapId) //
					.with(changes) //
					.build();
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}

	private void handleCodeDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {

		final Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("serviceCode")) {
			final String name = command.stringValueOfParameterNamed("serviceId");
			throw new PlatformDataIntegrityException("error.msg.service.mapping.duplicate", "A code with name '" + name + "' already exists","serviceId");
			// throw new
			// PlatformDataIntegrityException("error.msg.code.duplicate.name",
			// "A code with name '" + name + "' already exists");
		}else	if (realCause.getMessage().contains("service_identification_uq")) {
			final String name = command.stringValueOfParameterNamed("serviceId");
			final String serviceIdentification = command.stringValueOfParameterNamed("serviceIdentification");
			throw new PlatformDataIntegrityException("error.msg.service.already.mapped.with.serviceIdentification", "A code with name '" + name + "' already mapped with '" + serviceIdentification + "'","serviceIdentification");
			
		}
		
		LOGGER.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());

	}

	private ServiceMapping retrieveServiceMappingById(Long serviceMapId) {
		final ServiceMapping serviceMapping = this.serviceMappingRepository.findOne(serviceMapId);
		if (serviceMapping == null) {
			throw new CodeNotFoundException(serviceMapId.toString());
		}
		return serviceMapping;
	}
	}


