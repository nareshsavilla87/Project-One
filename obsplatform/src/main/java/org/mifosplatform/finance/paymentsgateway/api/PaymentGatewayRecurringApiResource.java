/*package org.mifosplatform.finance.paymentsgateway.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.paymentsgateway.data.PaymentGatewayData;
import org.mifosplatform.finance.paymentsgateway.data.RecurringPaymentTransactionTypeConstants;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayRepository;
import org.mifosplatform.finance.paymentsgateway.domain.PaypalRecurringBilling;
import org.mifosplatform.finance.paymentsgateway.domain.PaypalRecurringBillingRepository;
import org.mifosplatform.finance.paymentsgateway.service.PaymentGatewayReadPlatformService;
import org.mifosplatform.finance.paymentsgateway.service.PaymentGatewayRecurringWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.EventActionData;
import org.mifosplatform.workflow.eventaction.domain.EventAction;
import org.mifosplatform.workflow.eventaction.domain.EventActionRepository;
import org.mifosplatform.workflow.eventaction.service.EventActionConstants;
import org.mifosplatform.workflow.eventaction.service.EventActionReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutReq;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutRequestType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.BillingAgreementDetailsType;
import urn.ebay.apis.eBLBaseComponents.BillingCodeType;
import urn.ebay.apis.eBLBaseComponents.CurrencyCodeType;
import urn.ebay.apis.eBLBaseComponents.PaymentActionCodeType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsType;
import urn.ebay.apis.eBLBaseComponents.SetExpressCheckoutRequestDetailsType;

@Path("/recurringpayments")
@Component
@Scope("singleton")

*//**
 * The class <code>PaymentGatewayRecurringApiResource</code> is developed for
 * the processing of Third party PaymentGateway's Recurring Payments.
 * Using the below API to Communicate OBS with Adapters/Third-Party servers. 
 * 
 * @author ashokreddy
 *
 *//*
public class PaymentGatewayRecurringApiResource {

	private final PlatformSecurityContext context;
	private final PaymentGatewayReadPlatformService readPlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final DefaultToApiJsonSerializer<PaymentGatewayData> toApiJsonSerializer;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final PaymentGatewayRecurringWritePlatformService paymentGatewayRecurringWritePlatformService;
	private final PaymentGatewayRepository paymentGatewayRepository;
	private final PaypalRecurringBillingRepository paypalRecurringBillingRepository;
	private final EventActionReadPlatformService eventActionReadPlatformService;
	private final EventActionRepository eventActionRepository;

	
	@Autowired
	public PaymentGatewayRecurringApiResource(final PlatformSecurityContext context,final PaymentGatewayReadPlatformService readPlatformService,
			final DefaultToApiJsonSerializer<PaymentGatewayData> toApiJsonSerializer,final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
    		final PaymentGatewayRecurringWritePlatformService paymentGatewayRecurringWritePlatformService,
    		final PaymentGatewayRepository paymentGatewayRepository, 
    		final PaypalRecurringBillingRepository paypalRecurringBillingRepository,
    		final EventActionReadPlatformService eventActionReadPlatformService,
    		final EventActionRepository eventActionRepository) {

		this.toApiJsonSerializer = toApiJsonSerializer;
		this.context=context;
		this.readPlatformService=readPlatformService;
		this.apiRequestParameterHelper=apiRequestParameterHelper;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    	this.paymentGatewayRecurringWritePlatformService = paymentGatewayRecurringWritePlatformService;
    	this.paymentGatewayRepository = paymentGatewayRepository;
    	this.paypalRecurringBillingRepository = paypalRecurringBillingRepository;
    	this.eventActionReadPlatformService = eventActionReadPlatformService;
    	this.eventActionRepository = eventActionRepository;

	}
	
	
	*//**
	 * This method is using for posting data to create payment using paypal
	 *//*
	@POST
	@Path("ipnhandler")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.TEXT_HTML })
	public void paypalRecurringPayment(final @Context HttpServletRequest request) {

		try {
			String verifiyMessage = this.paymentGatewayRecurringWritePlatformService.paypalRecurringVerification(request);

			if (RecurringPaymentTransactionTypeConstants.RECURRING_VERIFIED.equals(verifiyMessage)) {

				String txnType = request.getParameter(RecurringPaymentTransactionTypeConstants.RECURRING_TXNTYPE);
				System.out.println(txnType);
				switch (txnType) {

				case RecurringPaymentTransactionTypeConstants.SUBSCR_SIGNUP:
				case RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_PROFILE_CREATED:
					
					this.paymentGatewayRecurringWritePlatformService.recurringSubscriberSignUp(request);
					break;
					
				case RecurringPaymentTransactionTypeConstants.SUBSCR_PAYMENT:
				case RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT:
					
					this.paymentGatewayRecurringWritePlatformService.recurringEventUpdate(request);
					
					break;
					
				case RecurringPaymentTransactionTypeConstants.SUBSCR_EOT:
				case RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_EXPIRED:
					
					break;
					
				case RecurringPaymentTransactionTypeConstants.SUBSCR_FAILED:
				case RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILED:
					
					break;
	
				case RecurringPaymentTransactionTypeConstants.SUBSCR_CANCELLED:
					
					break;
					
				case RecurringPaymentTransactionTypeConstants.SUBSCR_MODIFY:
					
					break;
				
				case RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_SKIPPED:
					
					break;
				
				case RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_SUSPENDED:
					
					break;
				
				case RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_SUSPENDED_DUR_TO_MAX_FAILED_PAYMENT:
					
					break;

				default:
					
					break;
				}

			} else {
				System.out.println("Failure");
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
	@POST
	@Path("getaccestoken")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.TEXT_HTML })
	public String paypalGetToken(final String apiRequestBodyAsJson) {
		 
		return this.paymentGatewayRecurringWritePlatformService.getAccessToken(apiRequestBodyAsJson);
	}
	 
	 

}
*/