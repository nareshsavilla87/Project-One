
package org.mifosplatform.finance.usagecharges.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.finance.billingorder.commands.BillingOrderCommand;
import org.mifosplatform.finance.billingorder.commands.InvoiceTaxCommand;
import org.mifosplatform.finance.billingorder.data.BillingOrderData;
import org.mifosplatform.finance.billingorder.domain.BillingOrder;
import org.mifosplatform.finance.billingorder.domain.Invoice;
import org.mifosplatform.finance.billingorder.service.GenerateBill;
import org.mifosplatform.finance.usagecharges.data.UsageChargesData;
import org.mifosplatform.finance.usagecharges.domain.UsageCharge;
import org.mifosplatform.finance.usagecharges.domain.UsageChargeRepository;
import org.mifosplatform.finance.usagecharges.domain.UsageRaWDataRepository;
import org.mifosplatform.finance.usagecharges.domain.UsageRaw;
import org.mifosplatform.finance.usagecharges.serialization.UsageChargesCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.allocation.domain.HardwareAssociationRepository;
import org.mifosplatform.portfolio.association.domain.HardwareAssociation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Ranjith
 * 
 */
@Service
public class UsageChargesWritePlatformServiceImpl implements UsageChargesWritePlatformService {

	private final static Logger LOGGER = LoggerFactory.getLogger(UsageChargesWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final UsageChargesCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final UsageRaWDataRepository usageRawDataRepository;
	private final UsageChargeRepository usageChargeRepository;
	private final HardwareAssociationRepository associationRepository;
	private final GenerateBill generateBill;

	@Autowired
	public UsageChargesWritePlatformServiceImpl(final PlatformSecurityContext context,
			final UsageChargesCommandFromApiJsonDeserializer apiJsonDeserializer,
			final UsageRaWDataRepository usageRawDataRepository,
			final UsageChargeRepository usageChargeRepository,
			final HardwareAssociationRepository associationRepository,
			final GenerateBill generateBill) {

		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.usageRawDataRepository = usageRawDataRepository;
		this.usageChargeRepository = usageChargeRepository;
		this.associationRepository = associationRepository;
		this.generateBill = generateBill;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see createUsageChargesRawData(JsonCommand)
	 */
	@Override
	public CommandProcessingResult createUsageChargesRawData(final JsonCommand command) {

		try {

			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			UsageRaw rawData = UsageRaw.fromJson(command);
			this.usageRawDataRepository.save(rawData);

			return new CommandProcessingResultBuilder().withEntityId(rawData.getId()).build();

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see processCustomerUsageRawData(Long)
	 * Here Transactional annotation help to handling of 
	 * JPA/Hibernate: detached entity passed to persist execption(persistenceexception)
	 */
	@Transactional
	@Override
	public void processCustomerUsageRawData(final UsageChargesData customerData) {

		try {
			BigDecimal totalCost = BigDecimal.ZERO;
			BigDecimal totalDuration = BigDecimal.ZERO;

			List<UsageRaw> rawDatas = this.usageRawDataRepository.findUsageRawDataByCustomerId(customerData.getClientId(),customerData.getNumber());

			if (rawDatas.size() != 0) {
				UsageCharge chargeData = new UsageCharge(customerData.getClientId(),customerData.getNumber(),DateUtils.getDateTimeOfTenant(), totalCost,totalDuration);
				
				for (UsageRaw rawData : rawDatas) {
					totalDuration = totalDuration.add(rawData.getDuration());
					totalCost = totalCost.add(rawData.getCost());
					chargeData.addUsageRaw(rawData);
				}
				chargeData.setTotalDuration(totalDuration);
				chargeData.setTotalCost(totalCost);
				this.usageChargeRepository.save(chargeData);
			}
		} catch (DataIntegrityViolationException dve) {
			LOGGER.error("usage rawData process failed........\r\n" +dve.getMessage());
		}

	}

	/**
	 * @param command
	 * @param dve
	 */
	private void handleCodeDataIntegrityIssues(final JsonCommand command,final Exception dve) {

		LOGGER.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException("error.msg.could.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: "+ dve.getMessage());

	}

	/* (non-Javadoc)
	 * @see #checkOrderUsageCharges(Long, BillingOrderData)
	 */
	@Override
	public BillingOrderCommand checkOrderUsageCharges(BillingOrderData billingOrderData) {
		
	 BigDecimal chargeAmount=BigDecimal.ZERO; 
	 LocalDate chargeStartDate = null;
	 LocalDate chargeEndDate = null;
	 BillingOrderCommand billingOrderCommand=null;
	 List<InvoiceTaxCommand> listOfTaxes = new ArrayList<InvoiceTaxCommand>();
	 List<UsageCharge> usageCharges = new ArrayList<UsageCharge>();
	 
	 List<HardwareAssociation> associations = this.associationRepository.findOneByOrderId(billingOrderData.getClientOrderId());
	 
	 if(!associations.isEmpty()){
		 List<UsageCharge> usages = new ArrayList<UsageCharge>();
		 for(HardwareAssociation association:associations){
			 
			 usages = this.usageChargeRepository.findCustomerUsageCharges(billingOrderData.getClientId(),association.getSerialNo());
			 usageCharges.addAll(usages);
		 }
	 }
	 
	 if(!usageCharges.isEmpty()){
		 
		 for(UsageCharge usageCharge:usageCharges){
			   chargeAmount = chargeAmount.add(usageCharge.getTotalCost());
		      if(chargeStartDate == null || chargeEndDate == null){
		    	  chargeStartDate = usageCharge.getChargeDate();
		    	  chargeEndDate = usageCharge.getChargeDate();
		      }else if(chargeStartDate.toDate().after(usageCharge.getChargeDate().toDate())){
		    	  chargeStartDate = usageCharge.getChargeDate();
		      }else if(chargeEndDate.toDate().before(usageCharge.getChargeDate().toDate())){
		    	  chargeEndDate = usageCharge.getChargeDate();
		      }
		 }
		 
		 chargeAmount = chargeAmount.add(billingOrderData.getPrice()).setScale(Integer.parseInt(this.generateBill.roundingDecimal()),RoundingMode.HALF_UP);
		 
         billingOrderCommand= new BillingOrderCommand(billingOrderData.getClientOrderId(),billingOrderData.getOderPriceId(),
        		 billingOrderData.getClientId(), chargeStartDate.toDate(),chargeEndDate.plusDays(1).toDate(),chargeEndDate.toDate(),
        		    billingOrderData.getBillingFrequency(),billingOrderData.getChargeCode(),billingOrderData.getChargeType(),
					billingOrderData.getChargeDuration(),billingOrderData.getDurationType(),chargeEndDate.toDate(),
					chargeAmount, billingOrderData.getBillingFrequency(), listOfTaxes,billingOrderData.getStartDate(), 
					billingOrderData.getEndDate(),null, billingOrderData.getTaxInclusive(),usageCharges);
		 
	   }
		
		return billingOrderCommand;
	}

	/* (non-Javadoc)
	 * @see updateUsageCharges(List,Invoice)
	 */
	@Override
	public void updateUsageCharges(List<BillingOrderCommand> commands,Invoice invoice) {

		List<BillingOrder> charges = invoice.getCharges();
		for (BillingOrderCommand billingOrderCommand : commands) {

			if ("UC".equalsIgnoreCase(billingOrderCommand.getChargeType()) && !billingOrderCommand.getUsageCharges().isEmpty()) {

				for (BillingOrder charge : charges) {
					if (charge.getOrderlineId().equals(billingOrderCommand.getOrderPriceId())) {
						for (UsageCharge usage : billingOrderCommand.getUsageCharges()) {
							usage.setChargeId(charge.getId());
						}
						this.usageChargeRepository.save(billingOrderCommand.getUsageCharges());
					}

				}

			}

		}
		
	}
	
}
	


