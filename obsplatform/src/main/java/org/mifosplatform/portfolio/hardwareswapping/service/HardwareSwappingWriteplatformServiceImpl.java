package org.mifosplatform.portfolio.hardwareswapping.service;

import java.util.LinkedHashSet;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.LocalDate;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.domain.ItemMaster;
import org.mifosplatform.logistics.item.domain.ItemRepository;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsAllocation;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsRepository;
import org.mifosplatform.logistics.itemdetails.exception.SerialNumberNotFoundException;
import org.mifosplatform.logistics.itemdetails.service.ItemDetailsWritePlatformService;
import org.mifosplatform.logistics.onetimesale.data.AllocationDetailsData;
import org.mifosplatform.logistics.ownedhardware.data.OwnedHardware;
import org.mifosplatform.logistics.ownedhardware.domain.OwnedHardwareJpaRepository;
import org.mifosplatform.portfolio.allocation.service.AllocationReadPlatformService;
import org.mifosplatform.portfolio.association.data.AssociationData;
import org.mifosplatform.portfolio.association.data.HardwareAssociationData;
import org.mifosplatform.portfolio.association.service.HardwareAssociationReadplatformService;
import org.mifosplatform.portfolio.association.service.HardwareAssociationWriteplatformService;
import org.mifosplatform.portfolio.hardwareswapping.exception.WarrantyEndDateExpireException;
import org.mifosplatform.portfolio.hardwareswapping.serialization.HardwareSwappingCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderHistory;
import org.mifosplatform.portfolio.order.domain.OrderHistoryRepository;
import org.mifosplatform.portfolio.order.domain.OrderLine;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.domain.UserActionStatusTypeEnum;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.planmapping.domain.PlanMapping;
import org.mifosplatform.portfolio.planmapping.domain.PlanMappingRepository;
import org.mifosplatform.portfolio.property.data.PropertyDeviceMappingData;
import org.mifosplatform.portfolio.property.domain.PropertyDeviceMapping;
import org.mifosplatform.portfolio.property.domain.PropertyDeviceMappingRepository;
import org.mifosplatform.portfolio.property.domain.PropertyHistoryRepository;
import org.mifosplatform.portfolio.property.domain.PropertyMaster;
import org.mifosplatform.portfolio.property.domain.PropertyMasterRepository;
import org.mifosplatform.portfolio.property.domain.PropertyTransactionHistory;
import org.mifosplatform.portfolio.property.service.PropertyReadPlatformService;
import org.mifosplatform.portfolio.service.domain.ServiceMaster;
import org.mifosplatform.portfolio.service.domain.ServiceMasterRepository;
import org.mifosplatform.portfolio.servicemapping.domain.ServiceMapping;
import org.mifosplatform.portfolio.servicemapping.domain.ServiceMappingRepository;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hugo
 *
 */
@Service
public class HardwareSwappingWriteplatformServiceImpl implements HardwareSwappingWriteplatformService {
	
	
	private final static Logger LOGGER = LoggerFactory.getLogger(HardwareSwappingWriteplatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final HardwareAssociationWriteplatformService associationWriteplatformService;
	private final ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService;
	private final OrderRepository orderRepository;
	private final PlanRepository  planRepository;
	private final HardwareSwappingCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
	private final OrderHistoryRepository orderHistoryRepository;
	private final ConfigurationRepository globalConfigurationRepository;
	private final OwnedHardwareJpaRepository hardwareJpaRepository;
	private final HardwareAssociationReadplatformService associationReadplatformService;
	private final ItemRepository itemRepository;
	private final ItemDetailsRepository itemDetailsRepository;
	private final PropertyDeviceMappingRepository propertyDeviceMappingRepository;
	private final HardwareSwappingReadplatformService hardwareSwappingReadplatformService;
	private final PropertyHistoryRepository propertyHistoryRepository;
	private final ProcessRequestRepository processRequestRepository;
	private final PlanMappingRepository planMappingRepository;
	private final ServiceMappingRepository serviceMappingRepository;
	private final ServiceMasterRepository serviceMasterRepository;
	private final AllocationReadPlatformService allocationReadPlatformService;
	private final PropertyReadPlatformService propertyReadPlatformService;
	private final PropertyMasterRepository propertyMasterRepository;
	  
	@Autowired
	public HardwareSwappingWriteplatformServiceImpl(final PlatformSecurityContext context,final HardwareAssociationWriteplatformService associationWriteplatformService,
			final ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService,final OrderRepository orderRepository,final PlanRepository planRepository,
			final HardwareSwappingCommandFromApiJsonDeserializer apiJsonDeserializer,
			final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,final OrderHistoryRepository orderHistoryRepository,
			final ConfigurationRepository configurationRepository,final OwnedHardwareJpaRepository hardwareJpaRepository,
			final HardwareAssociationReadplatformService associationReadplatformService,final ItemRepository itemRepository,
			final ItemDetailsRepository itemDetailsRepository,final PropertyDeviceMappingRepository propertyDeviceMappingRepository,
			final HardwareSwappingReadplatformService hardwareSwappingReadplatformService,final PropertyHistoryRepository propertyHistoryRepository,
			final ProcessRequestRepository processRequestRepository,final PlanMappingRepository planMappingRepository,
			final ServiceMappingRepository serviceMappingRepository,final ServiceMasterRepository serviceMasterRepository,
			final AllocationReadPlatformService allocationReadPlatformService,final PropertyReadPlatformService propertyReadPlatformService,
			final PropertyMasterRepository propertyMasterRepository) {
 
		this.context=context;
		this.associationWriteplatformService=associationWriteplatformService;
		this.inventoryItemDetailsWritePlatformService=inventoryItemDetailsWritePlatformService;
		this.orderRepository=orderRepository;
		this.planRepository=planRepository;
		this.propertyDeviceMappingRepository = propertyDeviceMappingRepository;
		this.fromApiJsonDeserializer=apiJsonDeserializer;
		this.commandSourceWritePlatformService=commandSourceWritePlatformService;
		this.orderHistoryRepository=orderHistoryRepository;
		this.globalConfigurationRepository=configurationRepository;
		this.hardwareJpaRepository=hardwareJpaRepository;
		this.associationReadplatformService=associationReadplatformService;
		this.itemRepository=itemRepository;
		this.itemDetailsRepository = itemDetailsRepository;
		this.hardwareSwappingReadplatformService = hardwareSwappingReadplatformService;
		this.propertyHistoryRepository = propertyHistoryRepository;
		this.processRequestRepository = processRequestRepository;
		this.planMappingRepository = planMappingRepository;
		this.serviceMappingRepository = serviceMappingRepository;
		this.serviceMasterRepository = serviceMasterRepository;
		this.allocationReadPlatformService = allocationReadPlatformService;
		this.propertyReadPlatformService = propertyReadPlatformService;
		this.propertyMasterRepository = propertyMasterRepository;

	}
	
	
	
/* (non-Javadoc)
 * @see #doHardWareSwapping(java.lang.Long, org.mifosplatform.infrastructure.core.api.JsonCommand)
 */
@Transactional
@Override
public CommandProcessingResult doHardWareSwapping(final Long entityId,final JsonCommand command) {
		
	try{
		final Long userId=this.context.authenticatedUser().getId();
		this.fromApiJsonDeserializer.validateForCreate(command.json());
		final String serialNo=command.stringValueOfParameterNamed("serialNo");
		final String deviceAgrementType=command.stringValueOfParameterNamed("deviceAgrementType");
		final Long saleId=command.longValueOfParameterNamed("saleId");
		final String provisionNum=command.stringValueOfParameterNamed("provisionNum");
		
		if(this.hardwareSwappingReadplatformService.retrieveingDisconnectionOrders(serialNo)){
			throw new PlatformDataIntegrityException("error.msg.serialNumber.unpaire.already.discon.orders", 
					"Disconnection Orders already Associated with this serialNumber `" + serialNo
                    + "` Please unpaire first", "Disconnection Orders Already Associated with this serialNumber", serialNo);
		}
		
		if(this.hardwareSwappingReadplatformService.retrieveingPendingOrders(serialNo)){
			throw new PlatformDataIntegrityException("error.msg.serialNumber.pend.orders.request.alreadysent", 
					"Please wait until pending state orders to be active", "Provisioning Request was sent to activation ", serialNo);
		}
		
		//getting new serial number item details data 
		ItemDetails newSerailNoItemData = this.itemDetailsRepository.getInventoryItemDetailBySerialNum(provisionNum);
		
		if(newSerailNoItemData == null){
			throw new SerialNumberNotFoundException(provisionNum);
		}
		
		//getting old serial number item details data 
		ItemDetails oldSerailNoItemData = this.itemDetailsRepository.getInventoryItemDetailBySerialNum(serialNo);
		

		if(!newSerailNoItemData.getItemMasterId().equals(oldSerailNoItemData.getItemMasterId())){
			throw new PlatformDataIntegrityException("error.msg.device.types.are.not.same", 
					"Please choose same category devices", "Device swap not possiable-Please choose same category devices", "serialNumber");
		}
		
		List<AssociationData> associationData = this.hardwareSwappingReadplatformService.retrievingAllAssociations(entityId,serialNo,Long.valueOf(0));
		LinkedHashSet<Long> associationOrderList = new  LinkedHashSet<Long>();
		
		//DeAssociate Hardware
		for(AssociationData association : associationData){
			
			this.associationWriteplatformService.deAssociationHardware(association.getId());
			associationOrderList.add(association.getOrderId());
		}
	    
	    LocalDate newWarrantyDate = new LocalDate(newSerailNoItemData.getWarrantyDate());
		
		if(deviceAgrementType.equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)){
			
			OwnedHardware ownedHardware=this.hardwareJpaRepository.findBySerialNumber(serialNo);
			ownedHardware.updateSerialNumbers(provisionNum);
			this.hardwareJpaRepository.saveAndFlush(ownedHardware);
			
			final ItemMaster itemMaster=this.itemRepository.findOne(Long.valueOf(ownedHardware.getItemType()));
	        List<HardwareAssociationData> allocationDetailsDatas=this.associationReadplatformService.retrieveClientAllocatedPlan(ownedHardware.getClientId(),itemMaster.getItemCode());
	        if(!allocationDetailsDatas.isEmpty()){
	    				this.associationWriteplatformService.createNewHardwareAssociation(ownedHardware.getClientId(),allocationDetailsDatas.get(0).getPlanId(),
	    						ownedHardware.getSerialNumber(),allocationDetailsDatas.get(0).getorderId(),"ALLOT",null);
	        }
	        
	   }else{
		
		//DeAllocate HardWare
		ItemDetailsAllocation inventoryItemDetailsAllocation=this.inventoryItemDetailsWritePlatformService.deAllocateHardware(serialNo, entityId);
		
		 JSONObject allocation = new JSONObject();
		 JSONObject allocation1 = new JSONObject();
		 JSONArray  serialNumber=new JSONArray();
		  
		 allocation.put("itemMasterId",inventoryItemDetailsAllocation.getItemMasterId());
		 allocation.put("clientId",entityId);
		 allocation.put("orderId",saleId);
		 allocation.put("serialNumber",provisionNum);
		 allocation.put("status","allocated");
		 allocation.put("isNewHw","N");
		 
		 serialNumber.put(allocation);
		 allocation1.put("quantity",1);
		 allocation1.put("itemId",inventoryItemDetailsAllocation.getItemMasterId());
		 allocation1.put("serialNumber",serialNumber);
		 
		//ReAllocate HardWare
			CommandWrapper commandWrapper = new CommandWrapperBuilder().allocateHardware().withJson(allocation1.toString()).build();
			this.commandSourceWritePlatformService.logCommandSource(commandWrapper);
		}
		Long resouceId=Long.valueOf(0);
		String provisionSystem="None";
		
			JSONObject jsonObj = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			 
			//for Reassociation With New SerialNumber
			for(AssociationData association : associationData){
				
				List<AssociationData> existingAssociations = this.hardwareSwappingReadplatformService.retrievingAllAssociations(entityId,provisionNum,association.getOrderId());
				if(existingAssociations.isEmpty()){		
					this.associationWriteplatformService.createNewHardwareAssociation(entityId,association.getPlanId(),
						provisionNum,association.getOrderId(),"ALLOT",association.getServiceId());
				}
			}
			
			//PrePair Provisioning Request
			for(Long orderId : associationOrderList){
				final Order order=this.orderRepository.findOne(orderId);
				
				String orderNo = order.getOrderNo();
				if(orderNo != null){
					orderNo = orderNo.replaceFirst("^0*", "");
				}
				//For Order History
				OrderHistory orderHistory=new OrderHistory(Long.valueOf(orderNo),DateUtils.getLocalDateOfTenant(),DateUtils.getLocalDateOfTenant(),resouceId,"DEVICE SWAP",userId,null);
				this.orderHistoryRepository.saveAndFlush(orderHistory);
				
				 final Plan plan  = this.planRepository.findOne(order.getPlanId());
				if(plan.isHardwareReq() == 'Y' && !plan.getProvisionSystem().equalsIgnoreCase("None")){
					provisionSystem = plan.getProvisionSystem();
					List<AllocationDetailsData> detailsData=this.allocationReadPlatformService.getTheHardwareItemDetails(orderId,provisionNum);
					PlanMapping planMapping= this.planMappingRepository.findOneByPlanId(order.getPlanId());
					List<OrderLine> orderLineData=order.getServices();

					JSONObject innerJsonObj = new JSONObject();
					JSONArray innerJsonArray = new JSONArray();
					if(planMapping != null){
						 innerJsonObj.put("planIdentification", planMapping.getPlanIdentification());
						 
					 }
					if(!detailsData.isEmpty()&&1==detailsData.size()&&detailsData.get(0).getServiceId()==null){ //plan level map
						
						 for(OrderLine orderLine:orderLineData){
							 
							 List<ServiceMapping> serviceMappingDetails=this.serviceMappingRepository.findOneByServiceId(orderLine.getServiceId());
						 		ServiceMaster service=this.serviceMasterRepository.findOne(orderLine.getServiceId());
						 		JSONObject subjson = new JSONObject();
						 		subjson.put("serviceName", service !=null ?service.getServiceCode():" ");
							 if(!serviceMappingDetails.isEmpty()){
								 subjson.put("serviceIdentification", serviceMappingDetails.get(0).getServiceIdentification());
							 }
							 innerJsonArray.put(subjson);
						 }
						
					}else if(!detailsData.isEmpty()){
						for(OrderLine orderLine:orderLineData){
							
							for (AllocationDetailsData detail : detailsData) {
								if (detail.getServiceId().equals(orderLine.getServiceId())) {
									List<ServiceMapping> serviceMappingDetails=this.serviceMappingRepository.findOneByServiceId(orderLine.getServiceId());
									ServiceMaster service=this.serviceMasterRepository.findOne(orderLine.getServiceId());
									JSONObject subjson = new JSONObject();
									subjson.put("serviceName", service !=null ?service.getServiceCode():" ");
									if(!serviceMappingDetails.isEmpty()){
										subjson.put("serviceIdentification", serviceMappingDetails.get(0).getServiceIdentification());
									}
									innerJsonArray.put(subjson);
									break;
								}
							}
						}
					}
					innerJsonObj.put("services" ,innerJsonArray);
					jsonArray.put(innerJsonObj);
					 
				}
			}
		
			   LocalDate oldWarrantyDate = new LocalDate(oldSerailNoItemData.getWarrantyDate());
				oldSerailNoItemData.setWarrantyDate(newWarrantyDate);
				newSerailNoItemData.setWarrantyDate(oldWarrantyDate);
				
			 this.itemDetailsRepository.save(oldSerailNoItemData);
			 this.itemDetailsRepository.save(newSerailNoItemData);
			 
			 Configuration globalConfiguration=this.globalConfigurationRepository.findOneByName(ConfigurationConstants.CONFIG_IS_PROPERTY_MASTER);
			 
			 if(globalConfiguration.isEnabled()){
				 
				 List<PropertyDeviceMappingData> deviceMapping = this.propertyReadPlatformService.retrievePropertyDeviceMappingData(serialNo);
				 for (PropertyDeviceMappingData mappingdata : deviceMapping){
					 PropertyDeviceMapping propertyDeviceMapping = this.propertyDeviceMappingRepository.findOne(mappingdata.getId());
					 PropertyMaster propertyMaster = this.propertyMasterRepository.findoneByPropertyCode(mappingdata.getPropertycode());
					 propertyDeviceMapping.setSerialNumber(provisionNum);
					 this.propertyDeviceMappingRepository.save(propertyDeviceMapping);
					 PropertyTransactionHistory propertyHistory = new PropertyTransactionHistory(DateUtils.getLocalDateOfTenant(),propertyMaster.getId(),"DEVICE SWAP",entityId,propertyDeviceMapping.getPropertyCode());
					 this.propertyHistoryRepository.save(propertyHistory);
				 }
			 }
			 
			 jsonObj.put("clientId", entityId);
			 jsonObj.put("OldHWId", oldSerailNoItemData.getProvisioningSerialNumber());
			 jsonObj.put("NewHWId", newSerailNoItemData.getProvisioningSerialNumber());
			 jsonObj.put("plans", jsonArray);
			 
			 ProcessRequest processRequest=new ProcessRequest(Long.valueOf(0),entityId,Long.valueOf(0), provisionSystem,UserActionStatusTypeEnum.DEVICE_SWAP.toString(),'N','N');
			   ProcessRequestDetails processRequestDetails=new ProcessRequestDetails(Long.valueOf(0),Long.valueOf(0),jsonObj.toString(),"Recieved",
			     provisionNum,DateUtils.getDateOfTenant(),DateUtils.getDateOfTenant(),null,null,'N',UserActionStatusTypeEnum.DEVICE_SWAP.toString(),null);
			   processRequest.add(processRequestDetails);
			   this.processRequestRepository.save(processRequest);
 			
				
		return new CommandProcessingResult(entityId);	
		
	   }catch(final WarrantyEndDateExpireException e){
		   Object[] obj = e.getDefaultUserMessageArgs();
		   throw new WarrantyEndDateExpireException(obj[0].toString());
	  }catch(final SerialNumberNotFoundException e){
		   	throw new SerialNumberNotFoundException(command.stringValueOfParameterNamed("provisionNum"));
	  
	  }catch(final JSONException e){
		   	e.printStackTrace();
		   	return new CommandProcessingResult(Long.valueOf(-1));
	  }catch(final DataIntegrityViolationException e){
		  handleDataIntegrityIssues(command,e);
		return new CommandProcessingResult(Long.valueOf(-1));
	  }
	
	}

	private void handleDataIntegrityIssues(final JsonCommand command,final DataIntegrityViolationException dve) {

		LOGGER.error(dve.getMessage(), dve);
		final Throwable realCause = dve.getCause();
		throw new PlatformDataIntegrityException("error.msg.could.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: "+ realCause.getMessage());
	}

}
