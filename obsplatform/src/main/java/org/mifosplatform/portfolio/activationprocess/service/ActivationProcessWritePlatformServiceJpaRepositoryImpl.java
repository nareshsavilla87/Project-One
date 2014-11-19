/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.activationprocess.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.selfcare.domain.SelfCare;
import org.mifosplatform.billing.selfcare.domain.SelfCareTemporary;
import org.mifosplatform.billing.selfcare.domain.SelfCareTemporaryRepository;
import org.mifosplatform.billing.selfcare.exception.PaymentStatusAlreadyActivatedException;
import org.mifosplatform.billing.selfcare.exception.SelfCareNotVerifiedException;
import org.mifosplatform.billing.selfcare.exception.SelfCareTemporaryEmailIdNotFoundException;
import org.mifosplatform.billing.selfcare.service.SelfCareRepository;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepository;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.domain.ItemMaster;
import org.mifosplatform.logistics.item.domain.ItemRepository;
import org.mifosplatform.logistics.item.exception.ItemNotFoundException;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsRepository;
import org.mifosplatform.logistics.itemdetails.exception.SerialNumberAlreadyExistException;
import org.mifosplatform.logistics.itemdetails.exception.SerialNumberNotFoundException;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleWritePlatformService;
import org.mifosplatform.logistics.ownedhardware.service.OwnedHardwareWritePlatformService;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.address.service.AddressReadPlatformService;
import org.mifosplatform.organisation.message.domain.BillingMessageRepository;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateRepository;
import org.mifosplatform.organisation.message.service.MessagePlatformEmailService;
import org.mifosplatform.portfolio.activationprocess.exception.ClientAlreadyCreatedException;
import org.mifosplatform.portfolio.activationprocess.serialization.ActivationProcessCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.client.service.ClientWritePlatformService;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
@Service
public class ActivationProcessWritePlatformServiceJpaRepositoryImpl implements ActivationProcessWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(ActivationProcessWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final FromJsonHelper fromJsonHelper;
    private final ItemRepository itemRepository;
    private final SelfCareRepository selfCareRepository;
    private final ClientWritePlatformService clientWritePlatformService;
    private final OneTimeSaleWritePlatformService oneTimeSaleWritePlatformService;
    private final OrderWritePlatformService orderWritePlatformService;
    private final ConfigurationRepository configurationRepository;
	private final OwnedHardwareWritePlatformService ownedHardwareWritePlatformService;
	private final AddressReadPlatformService addressReadPlatformService;
	private final ActivationProcessCommandFromApiJsonDeserializer commandFromApiJsonDeserializer;
	private final ItemDetailsRepository itemDetailsRepository;
	private final SelfCareTemporaryRepository selfCareTemporaryRepository;
	private final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	private final CodeValueRepository codeValueRepository;
	
	
	
    @Autowired
    public ActivationProcessWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,final FromJsonHelper fromJsonHelper,
    		final ClientWritePlatformService clientWritePlatformService,final OneTimeSaleWritePlatformService oneTimeSaleWritePlatformService,
    		final OrderWritePlatformService orderWritePlatformService,final ConfigurationRepository globalConfigurationRepository,
    		final OwnedHardwareWritePlatformService ownedHardwareWritePlatformService, final AddressReadPlatformService addressReadPlatformService,
    		final ActivationProcessCommandFromApiJsonDeserializer commandFromApiJsonDeserializer, final ItemDetailsRepository itemDetailsRepository,
    		final SelfCareTemporaryRepository selfCareTemporaryRepository,final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
    		final CodeValueRepository codeValueRepository, final SelfCareRepository selfCareRepository,final ItemRepository itemRepository,
    		final BillingMessageTemplateRepository billingMessageTemplateRepository,final MessagePlatformEmailService messagePlatformEmailService,
    		final BillingMessageRepository messageDataRepository) {

        
    	this.context = context;
    	this.itemRepository=itemRepository;
        this.fromJsonHelper = fromJsonHelper;
        this.selfCareRepository = selfCareRepository;
        this.clientWritePlatformService = clientWritePlatformService;
        this.oneTimeSaleWritePlatformService = oneTimeSaleWritePlatformService;
        this.orderWritePlatformService = orderWritePlatformService;
        this.configurationRepository = globalConfigurationRepository;
        this.ownedHardwareWritePlatformService = ownedHardwareWritePlatformService;
        this.addressReadPlatformService = addressReadPlatformService;
        this.commandFromApiJsonDeserializer = commandFromApiJsonDeserializer;
        this.itemDetailsRepository = itemDetailsRepository;
        this.selfCareTemporaryRepository = selfCareTemporaryRepository;
        this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
        this.codeValueRepository = codeValueRepository;
    }

    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("external_id")) {

            final String externalId = command.stringValueOfParameterNamed("externalId");
            throw new PlatformDataIntegrityException("error.msg.client.duplicate.externalId", "Client with externalId `" + externalId
                    + "` already exists", "externalId", externalId);
        } else if (realCause.getMessage().contains("account_no_UNIQUE")) {
            final String accountNo = command.stringValueOfParameterNamed("accountNo");
            throw new PlatformDataIntegrityException("error.msg.client.duplicate.accountNo", "Client with accountNo `" + accountNo
                    + "` already exists", "accountNo", accountNo);
        }else if (realCause.getMessage().contains("email_key")) {
            final String email = command.stringValueOfParameterNamed("email");
            throw new PlatformDataIntegrityException("error.msg.client.duplicate.email", "Client with email `" + email
                    + "` already exists", "email", email);
        }

        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    @Transactional
    @Override
    public CommandProcessingResult activationProcess(final JsonCommand command) {

        try {
            context.authenticatedUser();
            CommandProcessingResult resultClient=null;
            CommandProcessingResult resultSale=null;
          ///  CommandProcessingResult resultAllocate=null;
            CommandProcessingResult resultOrder=null;
            final JsonElement element = fromJsonHelper.parse(command.json());
	        JsonArray clientData = fromJsonHelper.extractJsonArrayNamed("client", element);
	        JsonArray saleData = fromJsonHelper.extractJsonArrayNamed("sale", element);
	        JsonArray owndevices= fromJsonHelper.extractJsonArrayNamed("owndevice", element);
	       // JsonArray allocateData = fromJsonHelper.extractJsonArrayNamed("allocate", element);
	        JsonArray bookOrder = fromJsonHelper.extractJsonArrayNamed("bookorder", element);
	        
	        
	       
	        for(JsonElement j:clientData){
           
	        	JsonCommand comm=new JsonCommand(null, j.toString(),j, fromJsonHelper, null, null, null, null, null, null, null, null, null, null, null,null);
	        	resultClient=this.clientWritePlatformService.createClient(comm,true);
	        }

	        Configuration configuration=configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_DEVICE_AGREMENT_TYPE);
	        if(configuration.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_SALE)){
	             
	        	for(JsonElement sale:saleData){
	        	  JsonCommand comm=new JsonCommand(null, sale.toString(),sale, fromJsonHelper, null, null, null, null, null, null, null, null, null, null, null,null);
	        	  resultSale=this.oneTimeSaleWritePlatformService.createOneTimeSale(comm,resultClient.getClientId());
	           }
	        }else if(configuration.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)){
	        	for(JsonElement ownDevice:owndevices){
	        		
	        		  JsonCommand comm=new JsonCommand(null, ownDevice.toString(),ownDevice, fromJsonHelper, null, null, null, null, null, null, null, null, null, null, null,null);
		        	  resultSale=this.ownedHardwareWritePlatformService.createOwnedHardware(comm,resultClient.getClientId());
	        	}
	        	
	        }
	       
	         for(JsonElement order:bookOrder){
		        	
		        	JsonCommand comm=new JsonCommand(null, order.toString(),order, fromJsonHelper, null, null, null, null, null, null, null, null, null, null, null,null);
		        	resultOrder=this.orderWritePlatformService.createOrder(resultClient.getClientId(),comm);
		        
		        }
	        return resultClient;

           
        } catch (DataIntegrityViolationException dve) {
        	
            handleDataIntegrityIssues(command, dve);
            return new CommandProcessingResult(-1l).empty();
        }
	
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }

	//@SuppressWarnings("unused")
	@Override
	public CommandProcessingResult selfRegistrationProcess(JsonCommand command) {

		try {
			context.authenticatedUser();
			Configuration deviceStatusConfiguration = configurationRepository.
					findOneByName(ConfigurationConstants.CONFIR_PROPERTY_REGISTRATION_DEVICE);
			
			commandFromApiJsonDeserializer.validateForCreate(command.json(),deviceStatusConfiguration.isEnabled());
			Long id = new Long(1);		
			CommandProcessingResult resultClient = null;
			CommandProcessingResult resultSale = null;
			CommandProcessingResult resultOrder = null;
			String device = null;
			String dateFormat = "dd MMMM yyyy";
			String activationDate = new SimpleDateFormat(dateFormat).format(new Date());

			

			String fullname = command.stringValueOfParameterNamed("fullname");
			String firstName = command.stringValueOfParameterNamed("firstname");
			String city = command.stringValueOfParameterNamed("city");
			String address = command.stringValueOfParameterNamed("address");
			Long phone = command.longValueOfParameterNamed("phone");	
			Long homePhoneNumber = command.longValueOfParameterNamed("homePhoneNumber");	
			String email = command.stringValueOfParameterNamed("email");
			String nationalId = command.stringValueOfParameterNamed("nationalId");
			String deviceId = command.stringValueOfParameterNamed("device");
			String kortaToken = command.stringValueOfParameterNamed("kortaToken");
			
			SelfCareTemporary temporary = selfCareTemporaryRepository.findOneByEmailId(email);
			
			if(temporary == null){
				throw new SelfCareTemporaryEmailIdNotFoundException(email);
			}

			if(temporary.getPaymentStatus().equalsIgnoreCase("ACTIVE")){
				throw new PaymentStatusAlreadyActivatedException(email);
			}
			
			if (temporary.getStatus().equalsIgnoreCase("ACTIVE")) {
				
                  throw new ClientAlreadyCreatedException();
			}
			
			if (temporary.getStatus().equalsIgnoreCase("PENDING")){
				
				String zipCode = command.stringValueOfParameterNamed("zipCode");
				// client creation
				AddressData addressData = this.addressReadPlatformService.retrieveAdressBy(city);
				CodeValue codeValue=this.codeValueRepository.findOneByCodeValue("Normal");
				JSONObject clientcreation = new JSONObject();
				clientcreation.put("officeId", new Long(1));
				clientcreation.put("clientCategory", codeValue.getId());
				clientcreation.put("firstname",firstName);
				clientcreation.put("lastname", fullname);
				clientcreation.put("phone", phone);
				clientcreation.put("homePhoneNumber", homePhoneNumber);
				clientcreation.put("entryType","IND");// new Long(1));
				clientcreation.put("addressNo", address);
				clientcreation.put("city", addressData.getCity());
				clientcreation.put("state", addressData.getState());
				clientcreation.put("country", addressData.getCountry());
				clientcreation.put("email", email);
				clientcreation.put("locale", "en");
				clientcreation.put("active", true);
				clientcreation.put("dateFormat", dateFormat);
				clientcreation.put("activationDate", activationDate);
				clientcreation.put("flag", false);
				clientcreation.put("zipCode", zipCode);
				clientcreation.put("device", deviceId);

				final JsonElement element = fromJsonHelper.parse(clientcreation.toString());
				JsonCommand clientCommand = new JsonCommand(null,clientcreation.toString(), element, fromJsonHelper,
						null, null, null, null, null, null, null, null, null, null, 
						null, null);
				resultClient = this.clientWritePlatformService.createClient(clientCommand,false);

				if (resultClient == null) {
					throw new PlatformDataIntegrityException("error.msg.client.creation.failed", "Client Creation Failed","Client Creation Failed");
				}
				
				temporary.setStatus("ACTIVE");
				
				//book device
				if(deviceStatusConfiguration != null){
					
					if(deviceStatusConfiguration.isEnabled()){
						
						JSONObject bookDevice = new JSONObject();
						deviceStatusConfiguration = configurationRepository.
								findOneByName(ConfigurationConstants.CONFIG_PROPERTY_DEVICE_AGREMENT_TYPE);
						 
						if(deviceStatusConfiguration != null && deviceStatusConfiguration.isEnabled() &&
								 deviceStatusConfiguration.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_SALE)){
						
						device = command.stringValueOfParameterNamed("device");
						ItemDetails detail = itemDetailsRepository.getInventoryItemDetailBySerialNum(device);

						if (detail == null) {
							throw new SerialNumberNotFoundException(device);
						}
						ItemMaster itemMaster=this.itemRepository.findOne(detail.getItemMasterId());
						if(itemMaster == null){
							throw new ItemNotFoundException(deviceId);
						}

						if (detail != null && detail.getStatus().equalsIgnoreCase("Used")) {
							throw new SerialNumberAlreadyExistException(device);
						}
						JSONObject serialNumberObject = new JSONObject();
						serialNumberObject.put("serialNumber", device);
						serialNumberObject.put("clientId", resultClient.getClientId());
						serialNumberObject.put("status", "allocated");
						serialNumberObject.put("itemMasterId", detail.getItemMasterId());
						serialNumberObject.put("isNewHw", "Y");
						JSONArray serialNumber = new JSONArray();
						serialNumber.put(0, serialNumberObject);
						
						bookDevice.put("chargeCode", itemMaster.getChargeCode());
						bookDevice.put("unitPrice", itemMaster.getUnitPrice());
						bookDevice.put("itemId", itemMaster.getId());
						bookDevice.put("discountId", id);
						bookDevice.put("officeId",detail.getOfficeId());
						bookDevice.put("totalPrice", itemMaster.getUnitPrice());

						bookDevice.put("quantity", id);
						bookDevice.put("locale", "en");
						bookDevice.put("dateFormat", dateFormat);
						bookDevice.put("saleType", "SALE");
						bookDevice.put("saleDate", activationDate);
						bookDevice.put("serialNumber",serialNumber);

						final JsonElement deviceElement = fromJsonHelper.parse(bookDevice.toString());
						JsonCommand comm = new JsonCommand(null, bookDevice.toString(),
								deviceElement, fromJsonHelper, null, null, null, null,
								null, null, null, null, null, null, null, null);
						resultSale = this.oneTimeSaleWritePlatformService.createOneTimeSale(comm, resultClient.getClientId());
						
						if (resultSale == null) {
							throw new PlatformDataIntegrityException("error.msg.client.device.assign.failed","Device Assign Failed for ClientId :"
											+ resultClient.getClientId(),"Device Assign Failed");
						}
						
					}else{
						
						List<ItemMaster> itemMaster=this.itemRepository.findAll(); 
						bookDevice.put("locale", "en");
						bookDevice.put("dateFormat", dateFormat);
						bookDevice.put("allocationDate",activationDate);
						bookDevice.put("provisioningSerialNumber",deviceId);
						bookDevice.put("itemType",itemMaster.get(0).getId());
						bookDevice.put("serialNumber",deviceId);
						bookDevice.put("status","ACTIVE");
						CommandWrapper commandWrapper = new CommandWrapperBuilder().createOwnedHardware(resultClient.getClientId()).withJson(bookDevice.toString()).build();
						final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(commandWrapper);
						if (result == null) {
							throw new PlatformDataIntegrityException("error.msg.client.device.assign.failed","Device Assign Failed for ClientId :"
											+ resultClient.getClientId(),"Device Assign Failed");
						}		
					}
					}
					
				}
				
				// book order
				Configuration selfregistrationconfiguration = configurationRepository.findOneByName(ConfigurationConstants.CONFIR_PROPERTY_SELF_REGISTRATION);
				
				if (selfregistrationconfiguration != null) {
					
					if (selfregistrationconfiguration.isEnabled()) {
						
						JSONObject ordeJson = new JSONObject(selfregistrationconfiguration.getValue());
						if (ordeJson.getString("paytermCode") != null && Long.valueOf(ordeJson.getLong("planCode")) != null
								&& Long.valueOf(ordeJson.getLong("contractPeriod")) != null) {
							ordeJson.put("locale", "en");
							ordeJson.put("isNewplan", true);
							ordeJson.put("dateFormat", dateFormat);
							ordeJson.put("start_date", activationDate);
							
							CommandWrapper commandRequest = new CommandWrapperBuilder().createOrder(resultClient.getClientId()).withJson(ordeJson.toString()).build();
							resultOrder = this.portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);
							
							if (resultOrder == null) {
								throw new PlatformDataIntegrityException("error.msg.client.order.creation","Book Order Failed for ClientId:"
												+ resultClient.getClientId(),"Book Order Failed");
							}
						}
					} else{
						
						JSONObject beeniusOrderJson = new JSONObject();
						String paytermCode = command.stringValueOfParameterNamed("paytermCode");
						Long contractPeriod = command.longValueOfParameterNamed("contractPeriod");
						Long planCode = command.longValueOfParameterNamed("planCode");
						
						beeniusOrderJson.put("planCode", planCode);
						beeniusOrderJson.put("contractPeriod", contractPeriod);
						beeniusOrderJson.put("paytermCode", paytermCode);
						beeniusOrderJson.put("billAlign", false);
						beeniusOrderJson.put("locale", "en");
						beeniusOrderJson.put("isNewplan", true);
						beeniusOrderJson.put("dateFormat", dateFormat);
						beeniusOrderJson.put("start_date", activationDate);
											
						CommandWrapper commandRequest = new CommandWrapperBuilder().createOrder(resultClient.getClientId()).withJson(beeniusOrderJson.toString()).build();
						resultOrder = this.portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);
						
						if (resultOrder == null) {
							throw new PlatformDataIntegrityException("error.msg.client.order.creation","Book Order Failed for ClientId:"
											+ resultClient.getClientId(),"Book Order Failed");
						}		
					}		
				}
				
				// payment Processing
				if(temporary.getPaymentStatus().equalsIgnoreCase("PENDING")){
					  temporary.setPaymentStatus("ACTIVE");
					  JSONObject json= new JSONObject(temporary.getPaymentData());
					  
				   	  String orderNumber = json.getString("order_num");				   	  
				   	  String amount = json.getString("total_amount");
				   	  BigDecimal totalAmount = new BigDecimal(amount);
				   	  	   	  
					  JsonObject object=new JsonObject();
					  object.addProperty("txn_id", orderNumber);
					  object.addProperty("dateFormat",dateFormat);
					  object.addProperty("locale","en");
					  object.addProperty("paymentDate",activationDate);
					  object.addProperty("amountPaid",totalAmount);
					  object.addProperty("isChequeSelected","no");
					  object.addProperty("receiptNo",orderNumber);
					  object.addProperty("remarks",email);
					  object.addProperty("paymentCode",27);
					  
					  final CommandWrapper paymentCommandRequest = new CommandWrapperBuilder().createPayment(resultClient.getClientId()).withJson(object.toString()).build();
					  final CommandProcessingResult result = this.portfolioCommandSourceWritePlatformService.logCommandSource(paymentCommandRequest);
					  if (result == null) {
							throw new PlatformDataIntegrityException("error.msg.client.payment.creation","Payment Failed for ClientId:"
											+ resultClient.getClientId(),"Payment Failed");
					  }
				}
				
				SelfCare selfcare =  this.selfCareRepository.findOneByClientId(resultClient.getClientId());
				
				if(selfcare != null && resultClient !=null && resultClient.getClientId() > 0 ){
						
					if(nationalId != null){
						selfcare.setNationalId(nationalId);
					}
					
					if(kortaToken !=null && !(kortaToken.equalsIgnoreCase(""))){
							selfcare.setToken(kortaToken);
					}			
		
				}
				
				return resultClient;
				
			} else if (temporary.getStatus().equalsIgnoreCase("INACTIVE")) {
				throw new SelfCareNotVerifiedException(email);			

			} else {
				return new CommandProcessingResult(-1l).empty();
			}	


		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(-1l).empty();
		} catch (JSONException e) {
			return new CommandProcessingResult(-1l).empty();
		}

	}
}
