/**
 * 
 */
package org.mifosplatform.infrastructure.dataqueries.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.dataqueries.data.ReportData;
import org.mifosplatform.infrastructure.dataqueries.service.ScheduledReportReadService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author hugo
 *
 */
@Path("/scheduledreport")
@Component
@Scope("singleton")
public class ScheduledReportsApiResource {


	private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "reportName", "reportType", "reportSubType",
					"reportCategory", "description", "reportSql", "coreReport","useReport", "reportParameters"));

	private final String resourceNameForPermissions = "REPORT";
	private final PlatformSecurityContext context;
	private final ToApiJsonSerializer<ReportData> toApiJsonSerializer;
	private final ScheduledReportReadService scheduledReportReadService;
	private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;

	@Autowired
	public ScheduledReportsApiResource(final PlatformSecurityContext context,final ScheduledReportReadService scheduledReportReadService,
			final ToApiJsonSerializer<ReportData> toApiJsonSerializer,PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,
			final ApiRequestParameterHelper apiRequestParameterHelper) {
		
		this.context = context;
		this.scheduledReportReadService = scheduledReportReadService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.commandSourceWritePlatformService = commandSourceWritePlatformService;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
	}

	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveScheduleReportTemplate(@Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final ReportData result = handleTemplateData();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, result,RESPONSE_DATA_PARAMETERS);
	}

	/**
	 * @return
	 */
	private ReportData handleTemplateData() {
	
		Collection<ReportData> reportNames = this.scheduledReportReadService.retrieveAllReportNames();
		List<String>  reportCategory = this.scheduledReportReadService.retrieveReportCategories();
		return new ReportData(reportNames,reportCategory);
	}
}
