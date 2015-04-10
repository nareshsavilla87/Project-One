package org.mifosplatform.finance.paymentsgateway.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

public interface PaymentGatewayRecurringWritePlatformService {

	public String paypalRecurringVerification(HttpServletRequest request) 
			throws UnsupportedEncodingException, IllegalStateException, ClientProtocolException, IOException, JSONException;

	public String getAccessToken(String data);

	public void recurringEventUpdate(HttpServletRequest request) throws JSONException;

	public void recurringSubscriberSignUp(HttpServletRequest request);

	public String createJsonForOnlineMethod(HttpServletRequest request) throws JSONException;
	
	
}
