package org.mifosplatform.finance.paymentsgateway.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.paymentsgateway.data.RecurringPaymentTransactionTypeConstants;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfiguration;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfigurationRepository;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayRepository;
import org.mifosplatform.finance.paymentsgateway.domain.PaypalRecurringBilling;
import org.mifosplatform.finance.paymentsgateway.domain.PaypalRecurringBillingRepository;
import org.mifosplatform.finance.paymentsgateway.exception.PaymentGatewayConfigurationException;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.message.domain.BillingMessageRepository;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateRepository;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.EventActionData;
import org.mifosplatform.workflow.eventaction.domain.EventAction;
import org.mifosplatform.workflow.eventaction.domain.EventActionRepository;
import org.mifosplatform.workflow.eventaction.service.EventActionConstants;
import org.mifosplatform.workflow.eventaction.service.EventActionReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutReq;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutRequestType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.BillingAgreementDetailsType;
import urn.ebay.apis.eBLBaseComponents.BillingCodeType;
import urn.ebay.apis.eBLBaseComponents.CurrencyCodeType;
import urn.ebay.apis.eBLBaseComponents.ErrorType;
import urn.ebay.apis.eBLBaseComponents.PaymentActionCodeType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsType;
import urn.ebay.apis.eBLBaseComponents.SetExpressCheckoutRequestDetailsType;

@Service
public class PaymentGatewayRecurringWritePlatformServiceImpl implements PaymentGatewayRecurringWritePlatformService {

	private final PlatformSecurityContext context;
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final FromJsonHelper fromApiJsonHelper;
    private final PortfolioCommandSourceWritePlatformService writePlatformService;
    private final PaymentGatewayConfigurationRepository paymentGatewayConfigurationRepository;
    private final BillingMessageTemplateRepository billingMessageTemplateRepository;
	private final BillingMessageRepository messageDataRepository;
	private final ClientRepository clientRepository;
	private final ConfigurationRepository configurationRepository;
	private final PaypalRecurringBillingRepository paypalRecurringBillingRepository;
	private final EventActionReadPlatformService eventActionReadPlatformService;
	private final EventActionRepository eventActionRepository;
   
   
    @Autowired
    public PaymentGatewayRecurringWritePlatformServiceImpl(final PlatformSecurityContext context,final PaymentGatewayRepository paymentGatewayRepository,
    		final FromJsonHelper fromApiJsonHelper,final PortfolioCommandSourceWritePlatformService writePlatformService,
    		final PaymentGatewayConfigurationRepository paymentGatewayConfigurationRepository,
    		final BillingMessageTemplateRepository billingMessageTemplateRepository,final BillingMessageRepository messageDataRepository,
    		final ClientRepository clientRepository, final EventActionRepository eventActionRepository,
    		final ConfigurationRepository configurationRepository,final EventActionReadPlatformService eventActionReadPlatformService,
    		final PaypalRecurringBillingRepository paypalRecurringBillingRepository){
    	
    	this.context=context;
    	this.paymentGatewayRepository=paymentGatewayRepository;
    	this.fromApiJsonHelper=fromApiJsonHelper;
    	this.writePlatformService = writePlatformService;
    	this.paymentGatewayConfigurationRepository = paymentGatewayConfigurationRepository;
    	this.billingMessageTemplateRepository = billingMessageTemplateRepository;
    	this.messageDataRepository = messageDataRepository;
    	this.clientRepository = clientRepository;
    	this.eventActionRepository = eventActionRepository;
    	this.configurationRepository = configurationRepository;
    	this.eventActionReadPlatformService = eventActionReadPlatformService;
    	this.paypalRecurringBillingRepository = paypalRecurringBillingRepository;
    }
    
	@SuppressWarnings("rawtypes")
	@Override
	public String paypalRecurringVerification(HttpServletRequest request) throws IllegalStateException, ClientProtocolException, IOException, JSONException {

		PaymentGatewayConfiguration pgConfig = this.paymentGatewayConfigurationRepository.findOneByName(ConfigurationConstants.PAYPAL_PAYMENTGATEWAY);
		
		if (null == pgConfig || null == pgConfig.getValue() || !pgConfig.isEnabled()) {
			throw new PaymentGatewayConfigurationException(ConfigurationConstants.PAYPAL_PAYMENTGATEWAY);
		}
		
		JSONObject pgConfigJsonObj = new JSONObject(pgConfig.getValue());
		String paypalUrl = pgConfigJsonObj.getString("paypalUrl");
		//String paypalEmailId = pgConfigJsonObj.getString("paypalEmailId");
		
		String[] urlData = paypalUrl.split("\\?");
		
		//2. Prepare 'notify-validate' command with exactly the same parameters
		Enumeration en = request.getParameterNames();
		StringBuilder cmd = new StringBuilder("cmd=_notify-validate");
		String paramName;
		String paramValue;
		while (en.hasMoreElements()) {

			paramName = (String) en.nextElement();
			paramValue = request.getParameter(paramName);
			
			if (!"password".equalsIgnoreCase(paramName) && !"username".equalsIgnoreCase(paramName) && !"rm".equalsIgnoreCase(paramName)) {
				cmd.append("&").append(paramName).append("=").append(URLEncoder.encode(paramValue, request.getParameter("charset")));
				
			} /*else {
				cmd.append("&").append(paramName).append("=").append(URLEncoder.encode(paramValue, request.getParameter("charset")));
			}*/
		}
		 
		//3. Post above command to Paypal IPN URL {@link IpnConfig#ipnUrl}
		URL u = new URL(urlData[0].trim());
		HttpsURLConnection uc = (HttpsURLConnection) u.openConnection();
		uc.setDoOutput(true);
		uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		uc.setRequestProperty("Host", u.getHost());
		//uc.setRequestProperty("Host", "www.sandbox.paypal.com");
		uc.setRequestMethod("POST");
		PrintWriter pw = new PrintWriter(uc.getOutputStream());
		pw.println(cmd.toString());
		pw.close();
		 
		//4. Read response from Paypal
		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		String res = in.readLine();
		in.close(); 
		return res;

	}

	@Override
	public String getAccessToken(String data) {
		// TODO Auto-generated method stub
		try {
			
			PaymentGatewayConfiguration pgConfig = this.paymentGatewayConfigurationRepository.findOneByName(ConfigurationConstants.PAYPAL_RECURRING_PAYMENT_DETAILS);
			
			if (null == pgConfig || null == pgConfig.getValue() || !pgConfig.isEnabled()) {
				throw new PaymentGatewayConfigurationException(ConfigurationConstants.PAYPAL_RECURRING_PAYMENT_DETAILS);
			}
			
			JSONObject pgConfigObject = new JSONObject(pgConfig);
			String username = pgConfigObject.getString("userName");
			String password = pgConfigObject.getString("password");
			String signature = pgConfigObject.getString("signature");
			String serverType = pgConfigObject.getString("serverType");
			
			JSONObject object = new JSONObject(data);
			
			String currencyCode = object.getString("currencyCode");
			Double amount = object.getDouble("amount");
			String returnUrl = object.getString("returnUrl");
			
			PaymentDetailsType paymentDetails = new PaymentDetailsType();
			paymentDetails.setPaymentAction(PaymentActionCodeType.fromValue("Sale"));
		
			BasicAmountType orderTotal = new BasicAmountType();
			orderTotal.setCurrencyID(CurrencyCodeType.fromValue(currencyCode));
			orderTotal.setValue(amount.toString());
			paymentDetails.setOrderTotal(orderTotal);
			List<PaymentDetailsType> paymentDetailsList = new ArrayList<PaymentDetailsType>();
			paymentDetailsList.add(paymentDetails);
		
			SetExpressCheckoutRequestDetailsType setExpressCheckoutRequestDetails = new SetExpressCheckoutRequestDetailsType();
			setExpressCheckoutRequestDetails.setReturnURL(returnUrl);
			setExpressCheckoutRequestDetails.setCancelURL(returnUrl);
			
			setExpressCheckoutRequestDetails.setPaymentDetails(paymentDetailsList);
			
			BillingAgreementDetailsType billingAgreement = new BillingAgreementDetailsType(BillingCodeType.fromValue("RecurringPayments"));
			billingAgreement.setBillingAgreementDescription("recurringbilling");
			List<BillingAgreementDetailsType> billList = new ArrayList<BillingAgreementDetailsType>();
			billList.add(billingAgreement);
			setExpressCheckoutRequestDetails.setBillingAgreementDetails(billList);
		
			SetExpressCheckoutRequestType setExpressCheckoutRequest = new SetExpressCheckoutRequestType(setExpressCheckoutRequestDetails);
			setExpressCheckoutRequest.setVersion("104.0");
		
			SetExpressCheckoutReq setExpressCheckoutReq = new SetExpressCheckoutReq();
			setExpressCheckoutReq.setSetExpressCheckoutRequest(setExpressCheckoutRequest);
		
			Map<String, String> sdkConfig = new HashMap<String, String>();
			
			
			sdkConfig.put(RecurringPaymentTransactionTypeConstants.SERVER_MODE, serverType);
			sdkConfig.put(RecurringPaymentTransactionTypeConstants.API_USERNAME, username);
			sdkConfig.put(RecurringPaymentTransactionTypeConstants.API_PASSWORD, password);
			sdkConfig.put(RecurringPaymentTransactionTypeConstants.API_SIGNATURE, signature);
			
			/*sdkConfig.put("mode", "sandbox");
			sdkConfig.put("acct1.UserName", "lingala.ashokreddy_api1.gmail.com");
			sdkConfig.put("acct1.Password", "1370339308");
			sdkConfig.put("acct1.Signature","AjSY..LirRQ9F92pKH.DNdiq3W1nAskOsq2sX19ZuiC6NSziuA2b.3Rr");*/
			/*sdkConfig.put("acct1.UserName", "jb-us-seller_api1.paypal.com");
			sdkConfig.put("acct1.Password", "WX4WTU3S8MY44S7F");
			sdkConfig.put("acct1.Signature","AFcWxV21C7fd0v3bYYYRCpSSRl31A7yDhhsPUU2XhtMoZXsWHFxu-RWy");*/
			/*sdkConfig.put("acct1.UserName", "ashokreddy3093_api1.gmail.com");
			sdkConfig.put("acct1.Password", "1369477475");
			sdkConfig.put("acct1.Signature","AFcWxV21C7fd0v3bYYYRCpSSRl31ATpEIl3frWOo7F.aflwIrVQt3.im");*/
			/*sdkConfig.put("acct1.UserName", "testashokreddy556_api1.gmail.com");
			sdkConfig.put("acct1.Password", "FREAHRHNLDFFT8ER");
			sdkConfig.put("acct1.Signature","Ax78nw1vKeDjANCxG.k6Rilr1EfvAdEDtszb7HfyP0QVLh5V6akyQAM5");*/
			
			PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(sdkConfig);
				
			SetExpressCheckoutResponseType setExpressCheckoutResponse = service.setExpressCheckout(setExpressCheckoutReq);
			
			String ack = setExpressCheckoutResponse.getAck().getValue();
			
			object = new JSONObject(); 
			
			if(setExpressCheckoutResponse != null && ack.equalsIgnoreCase(
					RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_SUCCESS)){
				
				object.put("result", ack);
				object.put("accessToken", setExpressCheckoutResponse.getToken());
				
				return object.toString();
			
			} else if (setExpressCheckoutResponse != null && ack.equalsIgnoreCase(
					RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE)) {
				
				ErrorType errorType = setExpressCheckoutResponse.getErrors().get(0);
				
				object.put("result", ack);
				object.put("error", errorType.getErrorCode());
				object.put("description", errorType.getLongMessage());
				
				return object.toString();
			
			} else{
				
			}
			
			System.out.println(setExpressCheckoutResponse.getToken());
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	@Override
	public void recurringEventUpdate(HttpServletRequest request) throws JSONException {
		
		/**
		 * Storing ProfileId, if not Stored in the b_recurring table 
		 */
		recurringSubscriberSignUp(request);
		
		/**
		 * @Param retrievePendingRecurringRequest() method is used for get the Pending Orders from b_event_action
		 */
		
		String jsonObj = request.getParameter(RecurringPaymentTransactionTypeConstants.CUSTOM);
		
		JSONObject obj = new JSONObject(jsonObj);
		
		Long clientId = obj.getLong(RecurringPaymentTransactionTypeConstants.CLIENTID);
		Long planId   = obj.getLong(RecurringPaymentTransactionTypeConstants.PLANID);
		String paytermCode = obj.getString(RecurringPaymentTransactionTypeConstants.PAYMENTCODE);
		Long contractPeriod = obj.getLong(RecurringPaymentTransactionTypeConstants.CONTRACTPERIOD);
		
		List<EventActionData> eventActionDatas = this.eventActionReadPlatformService.retrievePendingRecurringRequest(clientId);
		
		for(EventActionData eventActionData:eventActionDatas){
			
			System.out.println("EventAction Id:"+eventActionData.getId()+", PaymentGatewayId:"+eventActionData.getResourceId());
		
			EventAction eventAction = this.eventActionRepository.findOne(eventActionData.getId());
			
			if (eventAction.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_NEW)) {
				
				JSONObject createOrder = new JSONObject(eventAction.getCommandAsJson());
				
				Long planId1   = createOrder.getLong(RecurringPaymentTransactionTypeConstants.PLANID);
				String paytermCode1 = createOrder.getString(RecurringPaymentTransactionTypeConstants.PAYMENTCODE);
				Long contractPeriod1 = createOrder.getLong(RecurringPaymentTransactionTypeConstants.CONTRACTPERIOD);
				
				if(planId == planId1 && paytermCode.equalsIgnoreCase(paytermCode1) && contractPeriod == contractPeriod1){
					createOrder.remove("start_date");
					eventAction.updateStatus('N');
					eventAction.setTransDate(DateUtils.getLocalDateOfTenant().toDate());
					createOrder.put("start_date", DateUtils.getLocalDateOfTenant().toDate());
					eventAction.setCommandAsJson(createOrder.toString());
					this.eventActionRepository.save(eventAction);
				}
				
			} else{
				System.out.println("Does Not Implement the Code....");
			}
			
			this.eventActionRepository.save(eventAction);
		}

		
	}

	@Override
	public void recurringSubscriberSignUp(HttpServletRequest request) {
		// TODO Auto-generated method stub

		try {
			String ProfileId = request.getParameter(RecurringPaymentTransactionTypeConstants.SUBSCRID);
			String jsonObj = request.getParameter(RecurringPaymentTransactionTypeConstants.CUSTOM);

			PaypalRecurringBilling billing = this.paypalRecurringBillingRepository.findOneBySubscriberId(ProfileId);
			
			if (billing == null) {

				 JSONObject object = new JSONObject(jsonObj);
				 Long clientId = object.getLong(RecurringPaymentTransactionTypeConstants.CLIENTID);
				 
				 billing = new PaypalRecurringBilling(clientId, ProfileId);
				 this.paypalRecurringBillingRepository.save(billing);
			}

		} catch (JSONException e) {
			System.out.println("ProfileId Storing Failed");
			e.printStackTrace();
		}
	}

	@Override
	public String createJsonForOnlineMethod(HttpServletRequest request) throws JSONException {
		
		String status;
		
		String paymentStatus = request.getParameter(RecurringPaymentTransactionTypeConstants.PAYMENTSTATUS);
		String currency = request.getParameter(RecurringPaymentTransactionTypeConstants.MCCURRENCY);
		BigDecimal amount = new BigDecimal(request.getParameter(RecurringPaymentTransactionTypeConstants.MCGROSS));
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
		String date = dateFormat.format(new Date());
		
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("paymentDate", date);
		jsonObj.addProperty("payerEmail", request.getParameter("payer_email"));
		jsonObj.addProperty("customer_name", request.getParameter("first_name"));
		jsonObj.addProperty("receiverEmail", request.getParameter("receiver_email"));
		jsonObj.addProperty("payerStatus", request.getParameter("payer_status"));
		jsonObj.addProperty("currency", currency);
		jsonObj.addProperty("paymentStatus", paymentStatus);
		
		JSONObject custom = new JSONObject(request.getParameter(RecurringPaymentTransactionTypeConstants.CUSTOM));
		Long clientId = custom.getLong(RecurringPaymentTransactionTypeConstants.CLIENTID);
		
		JSONObject jsonObject = new JSONObject();
		
		if(paymentStatus.equalsIgnoreCase(RecurringPaymentTransactionTypeConstants.COMPLETED)){
			
			status = RecurringPaymentTransactionTypeConstants.SUCCESS;
			
		} else if (paymentStatus.equalsIgnoreCase(RecurringPaymentTransactionTypeConstants.PENDING)) {
			
			status = RecurringPaymentTransactionTypeConstants.PENDING;
			String error = request.getParameter(RecurringPaymentTransactionTypeConstants.PENDINGREASON);
			jsonObj.addProperty("pendingReason", error);
			jsonObject.put("error", error);
		
		} else{
			status = paymentStatus;
		}
				
		jsonObject.put("source", RecurringPaymentTransactionTypeConstants.PAYPAL);
		jsonObject.put("transactionId", request.getParameter(RecurringPaymentTransactionTypeConstants.TRANSACTIONID));
		jsonObject.put("currency", currency);
		jsonObject.put("clientId", clientId);
		jsonObject.put("total_amount", amount);
		jsonObject.put("locale", "en");
		jsonObject.put("dateFormat", "dd MMMM yyyy");
		jsonObject.put("otherData", jsonObj.toString());
		jsonObject.put("status", status);
	
		return jsonObject.toString();
		
	}
}
