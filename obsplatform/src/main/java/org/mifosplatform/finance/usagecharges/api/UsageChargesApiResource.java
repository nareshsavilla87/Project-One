package org.mifosplatform.finance.usagecharges.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.usagecharges.data.UsageChargesData;
import org.mifosplatform.infrastructure.codes.data.CodeData;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Ranjith
 * 
 */
@Path("/charges")
@Component
@Scope("singleton")
public class UsageChargesApiResource {

	/**
	 * The set of parameters that are supported in response for {@link CodeData}
	 */
	private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id","number","clientId"));

	private final static String RESOURCENAMEFORPERMISSIONS = "CHARGES";

	private final DefaultToApiJsonSerializer<UsageChargesData> toApiJsonSerializer;

	private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;

	@Autowired
	public UsageChargesApiResource(
			final DefaultToApiJsonSerializer<UsageChargesData> toApiJsonSerializer,
			final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService) {

		this.toApiJsonSerializer = toApiJsonSerializer;
		this.commandSourceWritePlatformService = commandSourceWritePlatformService;

	}

	/**
	 * This method is using for posting raw data of customers usage charges
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createCdrRawData(final String apiRequestBodyAsJson) {
		
		final CommandWrapper wrapperRequest = new CommandWrapperBuilder().createUsageChargesRawData().withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(wrapperRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

}
