package org.obsplatform.portfolio.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.obsplatform.billing.chargevariant.domain.ChargeVariant;
import org.obsplatform.billing.chargevariant.domain.ChargeVariantDetails;
import org.obsplatform.billing.chargevariant.domain.ChargeVariantRepository;
import org.obsplatform.billing.discountmaster.data.DiscountMasterData;
import org.obsplatform.billing.discountmaster.domain.DiscountDetails;
import org.obsplatform.billing.discountmaster.domain.DiscountMaster;
import org.obsplatform.billing.discountmaster.domain.DiscountMasterRepository;
import org.obsplatform.billing.discountmaster.exception.DiscountMasterNotFoundException;
import org.obsplatform.billing.planprice.data.PriceData;
import org.obsplatform.finance.billingorder.service.GenerateBill;
import org.obsplatform.infrastructure.configuration.domain.Configuration;
import org.obsplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.obsplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.obsplatform.infrastructure.core.api.JsonCommand;
import org.obsplatform.portfolio.client.domain.Client;
import org.obsplatform.portfolio.client.domain.ClientRepository;
import org.obsplatform.portfolio.contract.domain.Contract;
import org.obsplatform.portfolio.contract.domain.ContractRepository;
import org.obsplatform.portfolio.order.data.OrderStatusEnumaration;
import org.obsplatform.portfolio.order.domain.Order;
import org.obsplatform.portfolio.order.domain.OrderDiscount;
import org.obsplatform.portfolio.order.domain.OrderLine;
import org.obsplatform.portfolio.order.domain.OrderPrice;
import org.obsplatform.portfolio.order.domain.StatusTypeEnum;
import org.obsplatform.portfolio.order.domain.UserActionStatusTypeEnum;
import org.obsplatform.portfolio.order.exceptions.NoRegionalPriceFound;
import org.obsplatform.portfolio.plan.data.ServiceData;
import org.obsplatform.portfolio.plan.domain.Plan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderAssembler {
	
private final OrderDetailsReadPlatformServices orderDetailsReadPlatformServices;
private final ContractRepository contractRepository;
private final ConfigurationRepository configurationRepository;
private final DiscountMasterRepository discountMasterRepository;
private final ClientRepository clientRepository;
private final GenerateBill generateBill;
private final ChargeVariantRepository chargeVariantRepository;

@Autowired
public OrderAssembler(final OrderDetailsReadPlatformServices orderDetailsReadPlatformServices,final ContractRepository contractRepository,
		   final DiscountMasterRepository discountMasterRepository,final ConfigurationRepository configurationRepository,
		   final ClientRepository clientRepository, final GenerateBill generateBill, final ChargeVariantRepository chargeVariantRepository){
	
	this.orderDetailsReadPlatformServices=orderDetailsReadPlatformServices;
	this.contractRepository=contractRepository;
	this.discountMasterRepository=discountMasterRepository;
	this.configurationRepository = configurationRepository;
	this.clientRepository = clientRepository;
	this.generateBill = generateBill;
	this.chargeVariantRepository = chargeVariantRepository;
}

	public Order assembleOrderDetails(JsonCommand command, Long clientId, Plan plan) throws JSONException {
		
		List<OrderLine> serviceDetails = new ArrayList<OrderLine>();
		List<OrderPrice> orderprice = new ArrayList<OrderPrice>();
		List<PriceData> datas = new ArrayList<PriceData>();
		Long orderStatus=null;
		LocalDate endDate = null;
		BigDecimal discountRate=BigDecimal.ZERO;
		
        Order order=Order.fromJson(clientId, command);
			List<ServiceData> details =this.orderDetailsReadPlatformServices.retrieveAllServices(order.getPlanId());
			datas=this.orderDetailsReadPlatformServices.retrieveAllPrices(order.getPlanId(),order.getBillingFrequency(),clientId);
			/*if(datas.isEmpty()){
				datas=this.orderDetailsReadPlatformServices.retrieveDefaultPrices(order.getPlanId(),order.getBillingFrequency(),clientId);
			}*/
			if(datas.isEmpty()){
				throw new NoRegionalPriceFound();
			}
			
			Contract contractData = this.contractRepository.findOne(order.getContarctPeriod());
			LocalDate startDate=new LocalDate(order.getStartDate());
			
			if(plan.getProvisionSystem().equalsIgnoreCase("None")){
				orderStatus = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId();

			}else{
			orderStatus = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.PENDING).getId();
			}

			//Calculate EndDate
			endDate = calculateEndDate(startDate,contractData.getSubscriptionType(),contractData.getUnits());
			
			order=new Order(order.getClientId(),order.getPlanId(),orderStatus,null,order.getBillingFrequency(),startDate, endDate,
					 order.getContarctPeriod(), serviceDetails, orderprice,order.getbillAlign(),
					 UserActionStatusTypeEnum.ACTIVATION.toString(),plan.isPrepaid(),order.isAutoRenewal());
			

	        Configuration configuration = this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_ALIGN_BILLING_CYCLE);
			/*
			if(configuration != null && plan.isPrepaid() == 'N'){
				order.setBillingAlign(configuration.isEnabled()?'Y':'N');
				if(configuration.isEnabled() && endDate != null){
				order.setEndDate(endDate.dayOfMonth().withMaximumValue());
				}
			}*/

		  if (configuration != null && configuration.isEnabled() && plan.isPrepaid() == 'N') {

			JSONObject configValue = new JSONObject(configuration.getValue());
			if (endDate != null && configValue.getBoolean("fixed")) {
				order.setBillingAlign('Y');
				order.setEndDate(endDate.dayOfMonth().withMaximumValue());
			} else if (endDate == null && configValue.getBoolean("perpetual")) {
				order.setBillingAlign('Y');
			} else {
				order.setBillingAlign('N');
			}
		  } else {
			order.setBillingAlign('N');
		}
			
			BigDecimal priceforHistory=BigDecimal.ZERO;

			for (PriceData data : datas) {
				LocalDate billstartDate = startDate;
				LocalDate billEndDate = null;

				//end date is null for rc
				if (data.getChagreType().equalsIgnoreCase("RC")	&& endDate != null) {
					billEndDate = new LocalDate(order.getEndDate());
				} else if(data.getChagreType().equalsIgnoreCase("NRC")) {
					billEndDate = billstartDate;
				}
				
				final DiscountMaster discountMaster=this.discountMasterRepository.findOne(data.getDiscountId());
				if(discountMaster == null){
					throw new DiscountMasterNotFoundException();
				}
				
				//	If serviceId Not Exist
				OrderPrice price = new OrderPrice(data.getServiceId(),data.getChargeCode(), data.getChargingVariant(),data.getPrice(), 
						null, data.getChagreType(),
			    data.getChargeDuration(), data.getDurationType(),billstartDate.toDate(), billEndDate,data.isTaxInclusive());
				order.addOrderDeatils(price);
				priceforHistory=priceforHistory.add(data.getPrice());
				Client client=this.clientRepository.findOne(clientId);
				List<DiscountDetails> discountDetails=discountMaster.getDiscountDetails();
				for(DiscountDetails discountDetail:discountDetails){
					if(client.getCategoryType().equals(Long.valueOf(discountDetail.getCategoryType()))){
						discountRate = discountDetail.getDiscountRate();
					}else if(discountRate.equals(BigDecimal.ZERO) && Long.valueOf(discountDetail.getCategoryType()).equals(Long.valueOf(0))){
						discountRate = discountDetail.getDiscountRate();
					}
				}
				
				//discount Order
				OrderDiscount orderDiscount=new OrderDiscount(order,price,discountMaster.getId(),discountMaster.getStartDate(),null,discountMaster.getDiscountType(),
						discountRate);
				//price.addOrderDiscount(orderDiscount);
				order.addOrderDiscount(orderDiscount);
			}
			
			for (ServiceData data : details) {
				OrderLine orderdetails = new OrderLine(data.getPlanId(),data.getServiceType(), plan.getStatus(), 'n');
				order.addServiceDeatils(orderdetails);
			}
			
		  return order;
	
	}
	

    //Calculate EndDate
	public LocalDate calculateEndDate(LocalDate startDate,String durationType,Long duration) {

			LocalDate contractEndDate = null;
			 		if (durationType.equalsIgnoreCase("DAY(s)")) {
			 			contractEndDate = startDate.plusDays(duration.intValue() - 1);
			 		} else if (durationType.equalsIgnoreCase("MONTH(s)")) {
			 			contractEndDate = startDate.plusMonths(duration.intValue()).minusDays(1);
			 		} else if (durationType.equalsIgnoreCase("YEAR(s)")) {
			 		contractEndDate = startDate.plusYears(duration.intValue()).minusDays(1);
			 		} else if (durationType.equalsIgnoreCase("week(s)")) {
			 		contractEndDate = startDate.plusWeeks(duration.intValue()).minusDays(1);
			 		}
			 	return contractEndDate;
			}

	
	public Order setDatesOnOrderActivation(Order order, LocalDate startDate) {
		
		Contract contract = this.contractRepository.findOne(order.getContarctPeriod());
	    LocalDate endDate = this.calculateEndDate(startDate, contract.getSubscriptionType(), contract.getUnits());
	    order.setStartDate(startDate);
	    if(order.getbillAlign() == 'Y' && endDate != null){
	    	order.setEndDate(endDate.dayOfMonth().withMaximumValue());
		}else{
			order.setEndDate(endDate);
		}

		for (OrderPrice orderPrice : order.getPrice()) {
			LocalDate billstartDate = startDate;

			orderPrice.setBillStartDate(billstartDate);
			// end date is null for rc
			if ("RC".equalsIgnoreCase(orderPrice.getChargeType()) && endDate != null) {
				orderPrice.setBillEndDate(new LocalDate(order.getEndDate()));
			} else if ("RC".equalsIgnoreCase(orderPrice.getChargeType()) && endDate == null) {
				orderPrice.setBillEndDate(endDate);
			} else if ("NRC".equalsIgnoreCase(orderPrice.getChargeType())) {
				orderPrice.setBillEndDate(billstartDate);
			}
		}
		return order;
	}
	
	/*private BigDecimal calculateChargeVariantPrice(String chargingVariant,BigDecimal orderPrice, Long clientId, Long planId) {
		
   	 ChargeVariant chargeVariant = this.chargeVariantRepository.findOne(Long.valueOf(chargingVariant));
   	 Long orderActivationCount=this.orderDetailsReadPlatformServices.retrieveClientActivePlanOrderDetails(clientId,planId);
   	 if(chargeVariant != null){
   		   for(ChargeVariantDetails chargeVariantDetails:chargeVariant.getChargeVariantDetails()){
   			 
   			   DiscountMasterData discountMasterData = new DiscountMasterData(chargeVariant.getId(),null,null,chargeVariantDetails.getAmountType(),
		    				chargeVariantDetails.getAmount(), null,null);
   			     if(orderActivationCount > 1){
   			      if("ANY".equalsIgnoreCase(chargeVariantDetails.getVariantType())){
   			    	  orderPrice =  this.generateBill.calculateDiscount(discountMasterData,orderPrice).getDiscountedChargeAmount();  
   			    	   return orderPrice;
   			      }else if("Range".equalsIgnoreCase(chargeVariantDetails.getVariantType())){

   			    	  if (orderActivationCount >= chargeVariantDetails.getFrom() && orderActivationCount <= chargeVariantDetails.getTo()) {
   			    		  orderPrice =  this.generateBill.calculateDiscount(discountMasterData, orderPrice).getDiscountedChargeAmount();  
      			    	       return orderPrice;
   			    		}
   			    	     
   			      }
   			   
   		         }
   		   }
   	 }
   	 return orderPrice;
   	
	}*/
}
