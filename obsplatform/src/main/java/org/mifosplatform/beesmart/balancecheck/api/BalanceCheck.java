package org.mifosplatform.beesmart.balancecheck.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.mifosplatform.finance.clientbalance.data.ClientBalanceData;
import org.mifosplatform.finance.clientbalance.service.ClientBalanceReadPlatformService;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/beesmart")
@Component
@Scope("singleton")

/**
 * 
 * @author raghu
 *
 */
public class BalanceCheck {
	
	private final PlatformSecurityContext context;
	private final ClientBalanceReadPlatformService clientBalanceReadPlatformService;

	@Autowired
	public BalanceCheck(final PlatformSecurityContext context,final ClientBalanceReadPlatformService clientBalanceReadPlatformService) {

		this.context=context;
    	this.clientBalanceReadPlatformService = clientBalanceReadPlatformService;
	}

	
	@POST
	@Path("/checkAccountBalance")
	@Consumes({ MediaType.WILDCARD })
	@Produces({ MediaType.WILDCARD })
	public String onlinePayment(final String requestData){
		
	     try{
	    	 context.authenticatedUser();
	    	 String checkBalanceResponseOut = "";
	    	 if(requestData != null && !requestData.equals("")){
	    		
				/*String requestDataSB1 = requestData.toString().replaceAll("&lt;", "<");
				requestDataSB1 = requestDataSB1.replaceAll("&gt;", ">");*/

				JSONObject xmlJSONObj = XML.toJSONObject(requestData);

				JSONObject transactionResultset = xmlJSONObj.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
																						.getJSONObject("prep:checkAccountBalance");
				
				String subscriberId = transactionResultset.get("prep:subscriberId").toString();
				if(subscriberId != null){
					ClientBalanceData clientBalanceData = this.clientBalanceReadPlatformService.retrieveBalance(Long.parseLong(subscriberId));
					String balanceAmount = clientBalanceData.getBalanceAmount().toString();
					checkBalanceResponseOut = checkBalanceResponseOutSoap(balanceAmount);
				}
	    	 }
	    	 return checkBalanceResponseOut;   
		}catch (JSONException e) {
			    return e.getCause().toString();	 
		} catch (PlatformDataIntegrityException e) {
		        return null;
	    }
	}

	private String checkBalanceResponseOutSoap(String balanceAmount) {
		
		return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
				+ "xmlns:prep=\"http://prepaid.beesmart.tv/\">"
				+ "<soap:Body>" 
				+"<prep:checkAccountBalanceResponse>"
				+"<prep:checkAccountBalanceResult>"
				+"<prep:ReturnResult>"
				+"<prep:errorCode>200</prep:errorCode>"
				+"<prep:errorMessage>successfully returned balance Amount</prep:errorMessage>"
				+"<prep:value>"+balanceAmount+"</prep:value>"
				+"</prep:ReturnResult>"
				+"</prep:checkAccountBalanceResult>"
				+"</prep:checkAccountBalanceResponse>"
				+"</soapenv:Body>"
				+"</soapenv:Envelope>";
				
	}
	
	@POST
	@Path("/transaction")
	@Consumes({ MediaType.WILDCARD })
	@Produces({ MediaType.WILDCARD })
	public String transaction(final String requestData){
		
	    	 String checkBalanceResponseOut = "";
	    	System.out.println(requestData);
	    	 return checkBalanceResponseOut;   
	}
	 
}

