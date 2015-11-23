package org.mifosplatform.portfolio.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.chargecode.domain.ChargeCodeMaster;
import org.mifosplatform.billing.chargecode.domain.ChargeCodeRepository;
import org.mifosplatform.billing.planprice.exceptions.ChargeCodeAndContractPeriodException;
import org.mifosplatform.finance.billingorder.commands.BillingOrderCommand;
import org.mifosplatform.finance.billingorder.data.BillingOrderData;
import org.mifosplatform.finance.billingorder.domain.Invoice;
import org.mifosplatform.finance.billingorder.service.BillingOrderWritePlatformService;
import org.mifosplatform.finance.billingorder.service.GenerateBillingOrderService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.allocation.domain.HardwareAssociationRepository;
import org.mifosplatform.portfolio.association.data.AssociationData;
import org.mifosplatform.portfolio.association.domain.HardwareAssociation;
import org.mifosplatform.portfolio.association.service.HardwareAssociationReadplatformService;
import org.mifosplatform.portfolio.association.service.HardwareAssociationWriteplatformService;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepositoryWrapper;
import org.mifosplatform.portfolio.contract.domain.Contract;
import org.mifosplatform.portfolio.contract.domain.ContractRepository;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderAddons;
import org.mifosplatform.portfolio.order.domain.OrderAddonsRepository;
import org.mifosplatform.portfolio.order.domain.OrderPrice;
import org.mifosplatform.portfolio.order.domain.OrderPriceRepository;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.domain.StatusTypeEnum;
import org.mifosplatform.portfolio.order.domain.UserActionStatusTypeEnum;
import org.mifosplatform.portfolio.order.exceptions.AddonEndDateValidationException;
import org.mifosplatform.portfolio.order.serialization.OrderAddOnsCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.servicemapping.domain.ServiceMapping;
import org.mifosplatform.portfolio.servicemapping.domain.ServiceMappingRepository;
import org.mifosplatform.provisioning.provisioning.service.ProvisioningWritePlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


@Service
public class OrderAddOnsWritePlatformServiceImpl implements OrderAddOnsWritePlatformService{
	
	private final static Logger LOGGER = LoggerFactory.getLogger(OrderAddOnsWritePlatformServiceImpl.class);
	
	private final PlatformSecurityContext context;
	private final FromJsonHelper fromJsonHelper;
	private final ServiceMappingRepository serviceMappingRepository;
	private final OrderAddOnsCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final ContractRepository contractRepository;
	private final ProvisioningWritePlatformService provisioningWritePlatformService;
	private final OrderAssembler orderAssembler;
	private final OrderRepository orderRepository;
	private final OrderPriceRepository orderPriceRepository;
	private final HardwareAssociationRepository hardwareAssociationRepository;
	private final OrderAddonsRepository addonsRepository;
	private final GenerateBillingOrderService generateBillingOrderService;
	private final BillingOrderWritePlatformService billingOrderWritePlatformService;
	private final PlanRepository planRepository;
	private final ChargeCodeRepository chargeCodeRepository;
	private final ClientRepositoryWrapper clientRepository;
	private final HardwareAssociationReadplatformService associationReadplatformService;
	private final HardwareAssociationWriteplatformService associationWriteplatformService;
	
@Autowired
 public OrderAddOnsWritePlatformServiceImpl(final PlatformSecurityContext context,final OrderAddOnsCommandFromApiJsonDeserializer fromApiJsonDeserializer,
		 final FromJsonHelper fromJsonHelper,final ContractRepository contractRepository,final OrderAssembler orderAssembler,final OrderRepository orderRepository,
		 final ServiceMappingRepository serviceMappingRepository,final OrderAddonsRepository addonsRepository,final PlanRepository  planRepository,
		 final ProvisioningWritePlatformService provisioningWritePlatformService,final HardwareAssociationRepository associationRepository,
		 final OrderPriceRepository orderPriceRepository,final GenerateBillingOrderService generateBillingOrderService,
		 final BillingOrderWritePlatformService billingOrderWritePlatformService,final ChargeCodeRepository chargeCodeRepository,
		 final ClientRepositoryWrapper clientRepository,final HardwareAssociationReadplatformService associationReadplatformService,
		 final HardwareAssociationWriteplatformService associationWriteplatformService){
		
	this.context=context;
	this.fromJsonHelper=fromJsonHelper;
	this.fromApiJsonDeserializer=fromApiJsonDeserializer;
	this.contractRepository=contractRepository;
	this.orderRepository=orderRepository;
	this.provisioningWritePlatformService=provisioningWritePlatformService;
	this.orderPriceRepository=orderPriceRepository;
	this.orderAssembler=orderAssembler;
	this.hardwareAssociationRepository=associationRepository;
	this.planRepository = planRepository;
	this.addonsRepository=addonsRepository;
	this.serviceMappingRepository=serviceMappingRepository;
	this.billingOrderWritePlatformService = billingOrderWritePlatformService;
	this.generateBillingOrderService = generateBillingOrderService;
	this.chargeCodeRepository = chargeCodeRepository;
	this.clientRepository = clientRepository;
	this.associationReadplatformService = associationReadplatformService;
	this.associationWriteplatformService = associationWriteplatformService;
	
}

@Transactional
@Override
public CommandProcessingResult createOrderAddons(JsonCommand command,Long orderId) {

		try {

			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			final JsonElement element = fromJsonHelper.parse(command.json());
			final JsonArray addonServices = fromJsonHelper.extractJsonArrayNamed("addonServices", element);
			final String planName = command.stringValueOfParameterNamed("planName");
			final Long contractId = command.longValueOfParameterNamed("contractId");
			final LocalDate startDate = command.localDateValueOfParameterNamed("startDate");
			Order order = this.orderRepository.findOne(orderId);
			Contract contract = this.contractRepository.findOne(contractId);
			Date addonEndDate = null;
			LocalDate endDate = this.orderAssembler.calculateEndDate(new LocalDate(startDate), contract.getSubscriptionType(),contract.getUnits());

			if (endDate == null && order.getEndDate() != null) {
				throw new AddonEndDateValidationException(orderId);
			}
			if (order.getEndDate() != null && endDate.isAfter(new LocalDate(order.getEndDate()))) {
				throw new AddonEndDateValidationException(orderId);

			}

			Client client = this.clientRepository.findOneWithNotFoundDetection(order.getClientId());
			List<HardwareAssociation> association = this.hardwareAssociationRepository.findOneByOrderId(orderId);

			for (JsonElement jsonElement : addonServices) {
				OrderAddons addons = this.assembleOrderAddons(jsonElement,fromJsonHelper, order, startDate, endDate, 
						               contractId,association, planName);
				/*
				 * this.addonsRepository.saveAndFlush(addons);
				
				 if(!"None".equalsIgnoreCase(addons.getProvisionSystem())){
				   this.provisioningWritePlatformService. postOrderDetailsForProvisioning(order,planName,UserActionStatusTypeEnum.ADDON_ACTIVATION.toString(),
				   Long.valueOf(0),null,association.isEmpty()?null:association.get(0).getSerialNo(),orderId,addons.getProvisionSystem(),addons.getId()); }
				 */
				OrderPrice orderPrice = this.orderPriceRepository.findOne(addons.getPriceId());
				List<BillingOrderData> billingOrderDatas = new ArrayList<BillingOrderData>();
				if (endDate != null) {
					addonEndDate = endDate.toDate();
				} else {
					addonEndDate = startDate.plusYears(100).toDate();
				}

				// if(order.getNextBillableDay() != null){

				billingOrderDatas.add(new BillingOrderData(orderId, addons.getPriceId(), order.getPlanId(), order.getClientId(),startDate.toDate(), 
						orderPrice.getNextBillableDay(),addonEndDate, "", orderPrice.getChargeCode(),orderPrice.getChargeType(),Integer.valueOf(orderPrice.getChargeDuration()), 
						orderPrice.getDurationType(), orderPrice.getInvoiceTillDate(), orderPrice.getPrice(),"N", orderPrice.getBillStartDate(),addonEndDate,order.getStatus(), 
						orderPrice.isTaxInclusive() ? 1	: 0, String.valueOf(client.getTaxExemption())));

				List<BillingOrderCommand> billingOrderCommands = this.generateBillingOrderService.generatebillingOrder(billingOrderDatas);
				
				Invoice invoice = this.generateBillingOrderService.generateInvoice(billingOrderCommands);

				// Update Client Balance
				this.billingOrderWritePlatformService.updateClientBalance(invoice.getInvoiceAmount(), order.getClientId(), false);

				// Update order-price
				this.billingOrderWritePlatformService.updateBillingOrder(billingOrderCommands);
				System.out.println("---------------------"+ billingOrderCommands.get(0).getNextBillableDate());
			}
			// }
			return new CommandProcessingResult(orderId);

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

}


	private OrderAddons assembleOrderAddons(JsonElement jsonElement,FromJsonHelper fromJsonHelper, Order order, LocalDate startDate,
			LocalDate endDate, Long contractId,List<HardwareAssociation> orderAssociation, String planName) {

		OrderAddons orderAddon = OrderAddons.fromJson(jsonElement,fromJsonHelper, order.getId(), startDate, contractId);
		final BigDecimal price = fromJsonHelper.extractBigDecimalWithLocaleNamed("price", jsonElement);

		ChargeCodeMaster chargeCodeMaster = chargeCodeRepository.findOne(fromJsonHelper.extractLongNamed("chargeCodeId",jsonElement));
		Contract contract = contractRepository.findOne(orderAddon.getContractId());

		if (endDate != null	 && chargeCodeMaster.getChargeDuration() != contract.getUnits().intValue()
				&& chargeCodeMaster.getDurationType().equalsIgnoreCase(contract.getSubscriptionType())) {

			throw new ChargeCodeAndContractPeriodException(chargeCodeMaster.getBillFrequencyCode(), "addon");
		}

		OrderPrice orderPrice = new OrderPrice(orderAddon.getServiceId(),chargeCodeMaster.getChargeCode(),chargeCodeMaster.getChargeType(), price, null,
				chargeCodeMaster.getChargeType(), chargeCodeMaster.getChargeDuration().toString(),
				chargeCodeMaster.getDurationType(), startDate.toDate(),endDate, chargeCodeMaster.getTaxInclusive() == 1 ? true : false);
	
		orderPrice.update(order);
		orderPrice.setIsAddon('Y');
		order.addOrderDeatils(orderPrice);

		this.orderPriceRepository.saveAndFlush(orderPrice);
		this.orderRepository.saveAndFlush(order);
		Plan plan = this.planRepository.findOne(order.getPlanId());
		String serialNo = orderAssociation.get(0).getSerialNo();
		List<ServiceMapping> serviceMapping = this.serviceMappingRepository.findOneByServiceId(orderAddon.getServiceId());
		if (!plan.getProvisionSystem().equalsIgnoreCase("None") && serviceMapping.isEmpty()) {
			
			throw new AddonEndDateValidationException(orderAddon.getServiceId().toString());
		} else {
               
			if ('Y' == plan.isHardwareReq() && 'Y' == serviceMapping.get(0).getIsHwReq()) {

				List<AssociationData> clientOrderAllocations = this.associationReadplatformService.retrieveCustomerHardwareAllocationData(order.getClientId(),order.getId(),serviceMapping.get(0).getItemId());
				if (!clientOrderAllocations.isEmpty()) {
					for(AssociationData clientOrderAllocation:clientOrderAllocations){
						if(serviceMapping.get(0).getItemId().equals(clientOrderAllocation.getItemId())){
							serialNo = clientOrderAllocation.getSerialNum();
							break;
						}
					}
					
				} else {
					List<AssociationData> clientAllocations = this.associationReadplatformService.retrieveCustomerHardwareAllocationData(order.getClientId(),null,serviceMapping.get(0).getItemId());
                   if(clientAllocations.isEmpty()){
                	   throw new AddonEndDateValidationException();
                   }else{
                	   //Association
                	   this.associationWriteplatformService.createNewHardwareAssociation(order.getClientId(),order.getPlanId(),
                			 clientAllocations.get(0).getSerialNum(),order.getId(),"ALLOT",orderAddon.getServiceId());
                	   serialNo = clientAllocations.get(0).getSerialNum();
                   }
				}
			}
		}
		String status = StatusTypeEnum.ACTIVE.toString();
		if (!"None".equalsIgnoreCase(serviceMapping.get(0).getProvisionSystem())) {
			status = StatusTypeEnum.PENDING.toString();
		}
		if (endDate != null) {
			orderAddon.setEndDate(endDate.toDate());
		} else {
			orderAddon.setEndDate(null);
		}
		orderAddon.setProvisionSystem(serviceMapping.get(0).getProvisionSystem());
		orderAddon.setStatus(status);
		orderAddon.setPriceId(orderPrice.getId());

		this.addonsRepository.saveAndFlush(orderAddon);

		if (!"None".equalsIgnoreCase(orderAddon.getProvisionSystem())) {

			this.provisioningWritePlatformService.postOrderDetailsForProvisioning(order, planName,UserActionStatusTypeEnum.ADDON_ACTIVATION.toString(), Long.valueOf(0), null,
					serialNo, order.getId(),orderAddon.getProvisionSystem(), orderAddon.getId());
		}

		return orderAddon;
	}

	@Override
	public CommandProcessingResult disconnectOrderAddon(JsonCommand command,Long entityId) {
		try {

			// this.context.authenticatedUser();
			OrderAddons orderAddons = this.addonsRepository.findOne(entityId);
			List<ServiceMapping> serviceMapping = this.serviceMappingRepository.findOneByServiceId(orderAddons.getServiceId());

			if (!serviceMapping.isEmpty()) {
				if (serviceMapping.get(0).getProvisionSystem().equalsIgnoreCase("None")) {
					orderAddons.setStatus(StatusTypeEnum.DISCONNECTED.toString());
				} else {
					Order order = this.orderRepository.findOne(orderAddons.getOrderId());
					Plan plan = this.planRepository.findOne(order.getPlanId());
					OrderPrice orderAddonPrice = this.orderPriceRepository.findOne(orderAddons.getPriceId());
					List<HardwareAssociation> association = this.hardwareAssociationRepository.findOneByOrderId(orderAddons.getOrderId());
					String serialNo = association.get(0).getSerialNo();
					if ('Y' == plan.isHardwareReq() && 'Y' == serviceMapping.get(0).getIsHwReq()) {

						List<AssociationData> clientOrderAllocations = this.associationReadplatformService.retrieveCustomerHardwareAllocationData(order.getClientId(), order.getId(),
										serviceMapping.get(0).getItemId());
						if (!clientOrderAllocations.isEmpty()) {
							for (AssociationData clientOrderAllocation : clientOrderAllocations) {
								if (serviceMapping.get(0).getItemId().equals(clientOrderAllocation.getItemId())) {
									serialNo = clientOrderAllocation.getSerialNum();
									break;
								}
							}
						}
					}
					orderAddonPrice.setBillEndDate(DateUtils.getLocalDateOfTenant());
					orderAddonPrice.delete();
					this.orderPriceRepository.saveAndFlush(orderAddonPrice);
					orderAddons.setStatus(StatusTypeEnum.PENDING.toString());
					this.provisioningWritePlatformService.postOrderDetailsForProvisioning(order,plan.getPlanCode(),UserActionStatusTypeEnum.ADDON_DISCONNECTION.toString(), Long.valueOf(0), null,
							serialNo, order.getId(), serviceMapping.get(0).getProvisionSystem(), orderAddons.getId());
				}

				this.addonsRepository.save(orderAddons);

			} else {
				throw new AddonEndDateValidationException(orderAddons.getServiceId().toString());
			}
			return new CommandProcessingResult(entityId);

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}

	private void handleCodeDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {
		
		LOGGER.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException("error.msg.could.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: "+ dve.getCause().getMessage());

	}

}
