package org.obsplatform.provisioning.processrequest.service;

import java.util.List;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.obsplatform.finance.billingorder.domain.Invoice;
import org.obsplatform.finance.billingorder.service.InvoiceClient;
import org.obsplatform.infrastructure.configuration.domain.EnumDomainService;
import org.obsplatform.infrastructure.configuration.domain.EnumDomainServiceRepository;
import org.obsplatform.infrastructure.core.api.JsonCommand;
import org.obsplatform.infrastructure.core.data.CommandProcessingResult;
import org.obsplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.obsplatform.infrastructure.core.service.DateUtils;
import org.obsplatform.infrastructure.security.service.PlatformSecurityContext;
import org.obsplatform.organisation.ippool.domain.IpPoolManagementDetail;
import org.obsplatform.organisation.ippool.domain.IpPoolManagementJpaRepository;
import org.obsplatform.portfolio.client.domain.Client;
import org.obsplatform.portfolio.client.domain.ClientRepository;
import org.obsplatform.portfolio.client.domain.ClientStatus;
import org.obsplatform.portfolio.order.data.OrderStatusEnumaration;
import org.obsplatform.portfolio.order.domain.Order;
import org.obsplatform.portfolio.order.domain.OrderAddons;
import org.obsplatform.portfolio.order.domain.OrderAddonsRepository;
import org.obsplatform.portfolio.order.domain.OrderRepository;
import org.obsplatform.portfolio.order.domain.StatusTypeEnum;
import org.obsplatform.portfolio.order.domain.UserActionStatusTypeEnum;
import org.obsplatform.portfolio.order.service.OrderAssembler;
import org.obsplatform.portfolio.order.service.OrderReadPlatformService;
import org.obsplatform.portfolio.plan.domain.Plan;
import org.obsplatform.portfolio.plan.domain.PlanRepository;
import org.obsplatform.provisioning.preparerequest.domain.PrepareRequsetRepository;
import org.obsplatform.provisioning.processrequest.domain.ProcessRequest;
import org.obsplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.obsplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.obsplatform.provisioning.provisioning.api.ProvisioningApiConstants;
import org.obsplatform.provisioning.provisioning.domain.ServiceParameters;
import org.obsplatform.provisioning.provisioning.domain.ServiceParametersRepository;
import org.obsplatform.workflow.eventaction.data.ActionDetaislData;
import org.obsplatform.workflow.eventaction.service.ActionDetailsReadPlatformService;
import org.obsplatform.workflow.eventaction.service.ActiondetailsWritePlatformService;
import org.obsplatform.workflow.eventaction.service.EventActionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service(value = "processRequestWriteplatformService")
public class ProcessRequestWriteplatformServiceImpl implements ProcessRequestWriteplatformService{

	  private static final Logger logger =LoggerFactory.getLogger(ProcessRequestReadplatformServiceImpl.class);
	  private final PlanRepository planRepository;
	  private final IpPoolManagementJpaRepository ipPoolManagementJpaRepository;
	  private final ActionDetailsReadPlatformService actionDetailsReadPlatformService;
	  private final ActiondetailsWritePlatformService actiondetailsWritePlatformService; 
	  private final PlatformSecurityContext context;
	  private final OrderRepository orderRepository;
	  private final ClientRepository clientRepository;
	  private final OrderReadPlatformService orderReadPlatformService;
	  private final ProcessRequestRepository processRequestRepository;
	  private final EnumDomainServiceRepository enumDomainServiceRepository;
	  private final ServiceParametersRepository serviceParametersRepository;
	  private final OrderAddonsRepository orderAddonsRepository;
      private final OrderAssembler orderAssembler;
      private final InvoiceClient invoiceClient;
	  
	  

	    @Autowired
	    public ProcessRequestWriteplatformServiceImpl(final OrderReadPlatformService orderReadPlatformService,final OrderAssembler orderAssembler,
	    		final OrderRepository orderRepository,final ProcessRequestRepository processRequestRepository,final PrepareRequsetRepository prepareRequsetRepository,
	    		final ClientRepository clientRepository,final PlanRepository planRepository,final ActionDetailsReadPlatformService actionDetailsReadPlatformService,
	    		final ActiondetailsWritePlatformService actiondetailsWritePlatformService,final PlatformSecurityContext context,
	    		final EnumDomainServiceRepository enumDomainServiceRepository,final ServiceParametersRepository parametersRepository,
	    		final IpPoolManagementJpaRepository ipPoolManagementJpaRepository,final OrderAddonsRepository orderAddonsRepository,
	    	    final InvoiceClient invoiceClient) {

	    	
	    	    this.context = context;
	    	    this.planRepository=planRepository;
	    	    this.ipPoolManagementJpaRepository=ipPoolManagementJpaRepository;
	    	    this.actionDetailsReadPlatformService=actionDetailsReadPlatformService;
	    	    this.actiondetailsWritePlatformService=actiondetailsWritePlatformService;
	    	    this.orderAddonsRepository=orderAddonsRepository;
	    	    this.orderRepository=orderRepository;
	    	    this.clientRepository=clientRepository;
	    	    this.orderAssembler = orderAssembler;
	    	    this.serviceParametersRepository=parametersRepository;
	    	    this.processRequestRepository=processRequestRepository;
	    	    this.orderReadPlatformService=orderReadPlatformService;
	    	    this.enumDomainServiceRepository=enumDomainServiceRepository;
	    	    this.invoiceClient = invoiceClient;

	             
	    }

/*	    @Transactional
	    @Override
		public void ProcessingRequestDetails() {
	        
	        final MifosPlatformTenant tenant = this.tenantDetailsService.loadTenantById("default");
	        ThreadLocalContextUtil.setTenant(tenant);
            List<PrepareRequestData> data=this.prepareRequestReadplatformService.retrieveDataForProcessing();
            	for(PrepareRequestData requestData:data){
                       //Get the Order details
                     final List<Long> clientOrderIds = this.prepareRequestReadplatformService.retrieveRequestClientOrderDetails(requestData.getClientId());
                     	//Processing the request
                     	if(clientOrderIds!=null){
                                     this.processingClientDetails(clientOrderIds,requestData);
                                    //Update RequestData
                                     PrepareRequest prepareRequest=this.prepareRequsetRepository.findOne(requestData.getRequestId());
                                     prepareRequest.updateProvisioning('Y');
                                     this.prepareRequsetRepository.save(prepareRequest);
                     	}
            	}
	    	}
                    
		
	    private void processingClientDetails(List<Long> clientOrderIds,PrepareRequestData requestData) {
	    		for(Long orderId:clientOrderIds){
	    			final MifosPlatformTenant tenant = this.tenantDetailsService.loadTenantById("default");
			        ThreadLocalContextUtil.setTenant(tenant);
			        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
	    		}
			}*/

		@Override
		public void notifyProcessingDetails(ProcessRequest detailsData,char status) {
				try{
					if(detailsData!=null && !(detailsData.getRequestType().equalsIgnoreCase(ProvisioningApiConstants.REQUEST_TERMINATE)) && status != 'F'){
						
						Order order=null;
						Plan plan = null;
					
						if (detailsData.getOrderId() != null && detailsData.getOrderId() > 0) {
							order = this.orderRepository.findOne(detailsData.getOrderId());
							plan = this.planRepository.findOne(order.getPlanId());
						}
						
						Client client=this.clientRepository.findOne(detailsData.getClientId());
						
						switch(detailsData.getRequestType()){
						
						case ProvisioningApiConstants.REQUEST_ACTIVATION  :
							 
							if(detailsData.getRequestType().equalsIgnoreCase(UserActionStatusTypeEnum.ACTIVATION.toString())){
								order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId());
								ActionDetaislData provActionDetail=this.actionDetailsReadPlatformService.retrieveEventWithAction(EventActionConstants.EVENT_PROVISION_CONFIRM,EventActionConstants.ACTION_CHANGE_START);
								 if(provActionDetail != null){
									order.setStartDate(DateUtils.getLocalDateOfTenant());
									order=this.orderAssembler.setDatesOnOrderActivation(order,DateUtils.getLocalDateOfTenant());
								 }
								client.setStatus(ClientStatus.ACTIVE.getValue());
								this.orderRepository.saveAndFlush(order);
								List<ActionDetaislData> actionDetaislDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_ACTIVE_ORDER);
								if(actionDetaislDatas.size() != 0){
										this.actiondetailsWritePlatformService.AddNewActions(actionDetaislDatas,order.getClientId(), order.getId().toString(),null);
								}
							}
								
								break;
							case ProvisioningApiConstants.REQUEST_RECONNECTION :

								   order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId());
								   ActionDetaislData provReconnectActionDetail=this.actionDetailsReadPlatformService.retrieveEventWithAction(EventActionConstants.EVENT_PROVISION_CONFIRM,EventActionConstants.ACTION_CHANGE_START);
                                    if(provReconnectActionDetail != null){
	                                  order.setStartDate(DateUtils.getLocalDateOfTenant());
									  order=this.orderAssembler.setDatesOnOrderActivation(order,DateUtils.getLocalDateOfTenant());
                                    }
									client.setStatus(ClientStatus.ACTIVE.getValue());
									this.orderRepository.saveAndFlush(order);
									List<ActionDetaislData> actionDetaislDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_RECONNECTION_ORDER);
									if(actionDetaislDatas.size() != 0){
											this.actiondetailsWritePlatformService.AddNewActions(actionDetaislDatas,order.getClientId(), order.getId().toString(),null);
									}
									
									break;
									
							case ProvisioningApiConstants.REQUEST_CHANGE_PLAN :
								
									order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId());
									ActionDetaislData provChangePlanActionDetail=this.actionDetailsReadPlatformService.retrieveEventWithAction(EventActionConstants.EVENT_PROVISION_CONFIRM,EventActionConstants.ACTION_CHANGE_START);
	                                 if(provChangePlanActionDetail != null){
		                                  order.setStartDate(DateUtils.getLocalDateOfTenant());
										  order=this.orderAssembler.setDatesOnOrderActivation(order,DateUtils.getLocalDateOfTenant());
	                                  }
									client.setStatus(ClientStatus.ACTIVE.getValue());
									this.orderRepository.saveAndFlush(order);
									actionDetaislDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_CHANGE_PLAN);
									if(actionDetaislDatas.size() != 0){
											this.actiondetailsWritePlatformService.AddNewActions(actionDetaislDatas,order.getClientId(), order.getId().toString(),null);
									}
									
									break;		
								
							case ProvisioningApiConstants.REQUEST_DISCONNECTION :
								
								order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.DISCONNECTED).getId());
								this.orderRepository.saveAndFlush(order);
								Long activeOrders=this.orderReadPlatformService.retrieveClientActiveOrderDetails(order.getClientId(), null);
								if(activeOrders == 0){
									client.setStatus(ClientStatus.DEACTIVE.getValue());
								}
								actionDetaislDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_DISCONNECTION_ORDER);
								if(actionDetaislDatas.size() != 0){
										this.actiondetailsWritePlatformService.AddNewActions(actionDetaislDatas,order.getClientId(), order.getId().toString(),null);
								}
								
								break;
								
	                      case ProvisioningApiConstants.REQUEST_SUSPENTATION :
								
	                    		
								EnumDomainService enumDomainService=this.enumDomainServiceRepository.findOneByEnumMessageProperty(StatusTypeEnum.SUSPENDED.toString());
								order.setStatus(enumDomainService.getEnumId());
								this.orderRepository.saveAndFlush(order);
								
								break;
								
	                      case ProvisioningApiConstants.REQUEST_REACTIVATION :
								
	                    		
								 enumDomainService=this.enumDomainServiceRepository.findOneByEnumMessageProperty(StatusTypeEnum.ACTIVE.toString());
								order.setStatus(enumDomainService.getEnumId());
								this.orderRepository.saveAndFlush(order);
								
								break;
								
							case ProvisioningApiConstants.REQUEST_TERMINATION :

								order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.TERMINATED).getId());
								this.orderRepository.saveAndFlush(order);
								if(plan.getProvisionSystem().equalsIgnoreCase(ProvisioningApiConstants.PROV_PACKETSPAN)){
									
									List<ServiceParameters> parameters=this.serviceParametersRepository.findDataByOrderId(order.getId());
									for(ServiceParameters serviceParameter:parameters){
										if(serviceParameter.getParameterName().equalsIgnoreCase(ProvisioningApiConstants.PROV_DATA_IPADDRESS)){
											JSONArray ipAddresses = new  JSONArray(serviceParameter.getParameterValue());
											for(int i=0;i<ipAddresses.length();i++){
												IpPoolManagementDetail ipPoolManagementDetail= this.ipPoolManagementJpaRepository.findAllocatedIpAddressData(ipAddresses.getString(i));
												if(ipPoolManagementDetail != null){
													ipPoolManagementDetail.setStatus('T');
													ipPoolManagementDetail.setClientId(null);
													this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
												}
											}
										}
									}
										}
							
								break;
								
                                case ProvisioningApiConstants.REQUEST_ADDON_ACTIVATION :
                                	
                                	List<ProcessRequestDetails> requestDetails=detailsData.getProcessRequestDetails();
                                	JSONObject jsonObject=new JSONObject(requestDetails.get(0).getSentMessage());
                                	JSONArray array=jsonObject.getJSONArray("services");
                                	for(int i=0;i<array.length();i++){
                                		JSONObject object=array.getJSONObject(i);
                                		Long addonId=object.getLong("addonId");
                                		OrderAddons addons=this.orderAddonsRepository.findOne(addonId);
                                		addons.setStatus(StatusTypeEnum.ACTIVE.toString());
                                		this.orderAddonsRepository.saveAndFlush(addons);
                                	}
                                	
								break;
                                case ProvisioningApiConstants.REQUEST_ADDON_DISCONNECTION :
                                	
                                	 requestDetails=detailsData.getProcessRequestDetails();
                                	 jsonObject=new JSONObject(requestDetails.get(0).getSentMessage());
                                	 array=jsonObject.getJSONArray("services");
                                	for(int i=0;i<array.length();i++){
                                		JSONObject object=array.getJSONObject(i);
                                		Long addonId=object.getLong("addonId");
                                		OrderAddons addons=this.orderAddonsRepository.findOne(addonId);
                                		addons.setStatus(StatusTypeEnum.DISCONNECTED.toString());
                                		this.orderAddonsRepository.saveAndFlush(addons);
                                	}
                                	
								break;
                                case ProvisioningApiConstants.REQUEST_RENEWAL_AE:
                                	
                                	if (detailsData.getOrderId() != null && detailsData.getOrderId() > 0) {
        								order = this.orderRepository.findOne(detailsData.getOrderId());
        								plan = this.planRepository.findOne(order.getPlanId());
        							}
        							
        							 client=this.clientRepository.findOne(detailsData.getClientId());
        								order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId());
        								ActionDetaislData provRenewalAfterActionDetail=this.actionDetailsReadPlatformService.retrieveEventWithAction(EventActionConstants.EVENT_PROVISION_CONFIRM,EventActionConstants.ACTION_CHANGE_START);
                                        if(provRenewalAfterActionDetail != null){
    									  order=this.orderAssembler.setDatesOnOrderActivation(order,DateUtils.getLocalDateOfTenant());
                                        }
        								client.setStatus(ClientStatus.ACTIVE.getValue());
        								this.orderRepository.saveAndFlush(order);

        							 if(plan.isPrepaid() == 'Y'){
        			        	  		Invoice invoice=this.invoiceClient.singleOrderInvoice(order.getId(),order.getClientId(),new LocalDate(order.getStartDate()).plusDays(1));
        			        	  		 if(invoice!=null){
        			          		    	List<ActionDetaislData> actionDetaislData=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_TOPUP_INVOICE_MAIL);
        			          				if(actionDetaislData.size() != 0){
        			          					this.actiondetailsWritePlatformService.AddNewActions(actionDetaislData,order.getClientId(), invoice.getId().toString(),null);
        			          				}
        			          		    }
        							 }
        							 
        							 actionDetaislDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_RECONNECTION_ORDER);
     								if(actionDetaislDatas.size() != 0){
     										this.actiondetailsWritePlatformService.AddNewActions(actionDetaislDatas,order.getClientId(), order.getId().toString(),null);
     								}
        							 
								break;
								
								default : 

									if(order != null){
										order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId());
										this.orderRepository.saveAndFlush(order);
									}
									
									client.setStatus(ClientStatus.ACTIVE.getValue());
									this.clientRepository.saveAndFlush(client);
								
									break;
						}
						
							
							/*if(detailsData.getRequestType().equalsIgnoreCase(UserActionStatusTypeEnum.ACTIVATION.toString())){}else if(detailsData.getRequestType().equalsIgnoreCase(UserActionStatusTypeEnum.DISCONNECTION.toString())){
					 
								order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.DISCONNECTED).getId());
								this.orderRepository.saveAndFlush(order);
								Long activeOrders=this.orderReadPlatformService.retrieveClientActiveOrderDetails(order.getClientId(), null);
								if(activeOrders == 0){
									client.setStatus(ClientStatus.DEACTIVE.getValue());
								}
								
							
							}else if(detailsData.getRequestType().equalsIgnoreCase(UserActionStatusTypeEnum.TERMINATION.toString())){}else if(detailsData.getRequestType().equalsIgnoreCase(UserActionStatusTypeEnum.SUSPENTATION.toString())){
								
								EnumDomainService enumDomainService=this.enumDomainServiceRepository.findOneByEnumMessageProperty(StatusTypeEnum.SUSPENDED.toString());
								order.setStatus(enumDomainService.getEnumId());
								this.orderRepository.saveAndFlush(order);
								
							}else if(detailsData.getRequestType().equalsIgnoreCase(UserActionStatusTypeEnum.REACTIVATION.toString())){
								
								EnumDomainService enumDomainService=this.enumDomainServiceRepository.findOneByEnumMessageProperty(StatusTypeEnum.ACTIVE.toString());
								order.setStatus(enumDomainService.getEnumId());
								this.orderRepository.saveAndFlush(order);
							}else{
								
								if(order != null){
									order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId());
									this.orderRepository.saveAndFlush(order);
								}
								client.setStatus(ClientStatus.ACTIVE.getValue());
								this.clientRepository.saveAndFlush(client);
<<<<<<< HEAD
								this.orderRepository.saveAndFlush(order);
<<<<<<< HEAD
							}	
								
							}	
							}	
*/
						//	this.orderRepository.saveAndFlush(order);
							this.clientRepository.saveAndFlush(client);
							detailsData.setNotify();
					}
						this.processRequestRepository.saveAndFlush(detailsData);
				}catch(Exception exception){
					exception.printStackTrace();
				}
		}
		
		@Transactional
		@Override
		public CommandProcessingResult addProcessRequest(JsonCommand command){
			
			try{
				this.context.authenticatedUser();
				ProcessRequest processRequest = ProcessRequest.fromJson(command);
				ProcessRequestDetails processRequestDetails = ProcessRequestDetails.fromJson(processRequest,command);	
				processRequest.add(processRequestDetails);
				this.processRequestRepository.save(processRequest);
				return	new CommandProcessingResult(Long.valueOf(processRequest.getPrepareRequestId()),processRequest.getClientId());

			}catch(DataIntegrityViolationException dve){
				handleCodeDataIntegrityIssues(command,dve);
				return CommandProcessingResult.empty();
			}
			
		}
		
		 private void handleCodeDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {
				 Throwable realCause = dve.getMostSpecificCause();
			        logger.error(dve.getMessage(), dve);
			        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
			                "Unknown data integrity issue with resource: " + realCause.getMessage());
			}

		
	/*	@Transactional
		@Override
		public void postProvisioningdetails(Client client,EventOrder eventOrder,String requestType,String provSystem, String response) {
			try{
				
				
				this.context.authenticatedUser();
				ProcessRequest processRequest=new ProcessRequest(Long.valueOf(0), eventOrder.getClientId(),eventOrder.getId(),ProvisioningApiConstants.PROV_BEENIUS,
						ProvisioningApiConstants.REQUEST_ACTIVATION_VOD);
				List<EventOrderdetials> eventDetails=eventOrder.getEventOrderdetials();
				EventMaster eventMaster=this.eventMasterRepository.findOne(eventOrder.getEventId());
				JSONObject jsonObject=new JSONObject();
				jsonObject.put("officeUid",client.getOffice().getExternalId());
				jsonObject.put("subscriberUid",client.getAccountNo());
				jsonObject.put("vodUid",eventMaster.getEventName());
						
					for(EventOrderdetials details:eventDetails){
						ProcessRequestDetails processRequestDetails=new ProcessRequestDetails(details.getId(),details.getEventDetails().getId(),jsonObject.toString(),
								response,null,eventMaster.getEventStartDate(), eventMaster.getEventEndDate(),new Date(),new Date(),'N',
								ProvisioningApiConstants.REQUEST_ACTIVATION_VOD,null);
						processRequest.add(processRequestDetails);
					}
				this.processRequestRepository.save(processRequest);
			}catch(DataIntegrityViolationException dve){
				handleCodeDataIntegrityIssues(null, dve);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}*/
}
