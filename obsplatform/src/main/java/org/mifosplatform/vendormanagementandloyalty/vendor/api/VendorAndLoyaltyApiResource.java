/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.vendormanagementandloyalty.vendor.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.emun.data.EnumValuesConstants;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.address.data.CountryDetails;
import org.mifosplatform.organisation.address.service.AddressReadPlatformService;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.organisation.monetary.service.CurrencyReadPlatformService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.organisation.priceregion.data.PriceRegionData;
import org.mifosplatform.organisation.priceregion.service.RegionalPriceReadplatformService;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.plan.data.BillRuleData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.mifosplatform.portfolio.service.service.ServiceMasterReadPlatformService;
import org.mifosplatform.useradministration.data.AppUserData;
import org.mifosplatform.useradministration.service.AppUserReadPlatformService;
import org.mifosplatform.vendormanagementandloyalty.vendor.data.VendorData;
import org.mifosplatform.vendormanagementandloyalty.vendor.service.VendorManagementReadPlatformService1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/vendorandloyalty")
@Component
@Scope("singleton")
public class VendorAndLoyaltyApiResource {

    /**
     * The set of parameters that are supported in response for
     * {@link AppUserData}.
     */
    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));

    private static final String RESOURCENAMEFORPERMISSIONS = "VENDOR";
    private final PlatformSecurityContext context;
    private final VendorManagementReadPlatformService1 readPlatformService;
    private final RegionalPriceReadplatformService regionalPriceReadplatformService;
    private final DefaultToApiJsonSerializer<VendorData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final PlanReadPlatformService planReadPlatformService;
    private final ServiceMasterReadPlatformService serviceMasterReadPlatformService;
    private final OrderReadPlatformService orderReadPlatformService;
    private final AddressReadPlatformService addressReadPlatformService;
    private final CurrencyReadPlatformService currencyReadPlatformService;

    @Autowired
    public VendorAndLoyaltyApiResource(final PlatformSecurityContext context, final VendorManagementReadPlatformService1 readPlatformService,
    		final RegionalPriceReadplatformService regionalPriceReadplatformService, final DefaultToApiJsonSerializer<VendorData> toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final PlanReadPlatformService planReadPlatformService, final ServiceMasterReadPlatformService serviceMasterReadPlatformService,
            final OrderReadPlatformService orderReadPlatformService, final AddressReadPlatformService addressReadPlatformService,
            final CurrencyReadPlatformService currencyReadPlatformService) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.regionalPriceReadplatformService = regionalPriceReadplatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.planReadPlatformService = planReadPlatformService;
        this.serviceMasterReadPlatformService = serviceMasterReadPlatformService;
        this.orderReadPlatformService = orderReadPlatformService;
        this.addressReadPlatformService = addressReadPlatformService;
        this.currencyReadPlatformService = currencyReadPlatformService;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createVendor(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createVendorManagement() //
                .withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
    
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveUsers(@Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);

        final List<VendorData> vendor = this.readPlatformService.retrieveAllVendorAndLoyalties();

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, vendor, RESPONSE_DATA_PARAMETERS);
    }
    
    @GET
    @Path("{vendorId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveUser(@PathParam("vendorId") final Long vendorId, @Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        List<VendorData> singleVendorData = this.readPlatformService.retrieveVendor(vendorId);
        List<VendorData> vendorDetailsData = this.readPlatformService.retrieveVendorDetails(vendorId);
        VendorData vendor=handleTemplateData();
        vendor.setSingleVendorData(singleVendorData);
        vendor.setVendorDetailsData(vendorDetailsData);
        /*if (settings.isTemplate()) {
            final Collection<OfficeData> offices = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
            vendor = VendorData.template(vendor, offices);
        }*/

        return this.toApiJsonSerializer.serialize(settings, vendor, RESPONSE_DATA_PARAMETERS);
    }
    
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String newUserTemplateDetails(@Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
        VendorData vendor=handleTemplateData();
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, vendor, RESPONSE_DATA_PARAMETERS);
    }
    
    private VendorData handleTemplateData() {
		
    	final List<PriceRegionData> priceRegionData = this.regionalPriceReadplatformService.getPriceRegionsDetails();
        final List<EnumOptionData> statusData = this.planReadPlatformService.retrieveNewStatus();
        final List<ServiceData> servicesData = this.serviceMasterReadPlatformService.retrieveAllServices("N");
        final List<PlanCodeData> planDatas = this.orderReadPlatformService.retrieveAllPlatformData((long)0, null);
        final List<CountryDetails> countryData = this.addressReadPlatformService.retrieveCountries();
        final Collection<CurrencyData> currencyOptions = this.currencyReadPlatformService.retrieveAllPlatformCurrencies();
		 
		return new VendorData(priceRegionData, statusData, servicesData,
					planDatas, countryData, currencyOptions);
			
	}
    
    @PUT
    @Path("{vendorId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateVendor(@PathParam("vendorId") final Long vendorId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateVendorManagement(vendorId) //
                .withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

}
