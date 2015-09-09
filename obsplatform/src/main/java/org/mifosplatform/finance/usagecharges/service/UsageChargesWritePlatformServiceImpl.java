
package org.mifosplatform.finance.usagecharges.service;

import java.math.BigDecimal;
import java.util.List;

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
	private final UsageChargesReadPlatformService usageChargesReadPlatformService;
	private final UsageRaWDataRepository usageRawDataRepository;
	private final UsageChargeRepository usageChargeRepository;

	@Autowired
	public UsageChargesWritePlatformServiceImpl(
			final PlatformSecurityContext context,
			final UsageChargesCommandFromApiJsonDeserializer apiJsonDeserializer,
			final UsageChargesReadPlatformService usageChargesReadPlatformService,
			final UsageRaWDataRepository usageRawDataRepository,
			final UsageChargeRepository usageChargeRepository) {

		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.usageChargesReadPlatformService = usageChargesReadPlatformService;
		this.usageRawDataRepository = usageRawDataRepository;
		this.usageChargeRepository = usageChargeRepository;

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
	 * @see processCustomerUsageRawData(java.lang.Long)
	 */
	@Transactional
	@Override
	public void processCustomerUsageRawData(final UsageChargesData customerData) {

		try {
			BigDecimal totalCost = BigDecimal.ZERO;
			BigDecimal totalDuration = BigDecimal.ZERO;

			List<UsageRaw> rawDatas = this.usageRawDataRepository.findUsageRawDataByCustomerId(customerData.getClientId(),customerData.getNumber());

			if (rawDatas.size() != 0) {
				UsageCharge charge = new UsageCharge(customerData.getClientId(),customerData.getNumber(),DateUtils.getLocalDateTimeOfTenant(), totalCost,totalDuration);
				
				for (UsageRaw rawData : rawDatas) {
					totalDuration = totalDuration.add(rawData.getDuration());
					totalCost = totalCost.add(rawData.getCost());
					charge.addUsageRaw(rawData);
				}
				charge.setTotalDuration(totalDuration);
				charge.setTotalCost(totalCost);
				this.usageChargeRepository.save(charge);
			}
		} catch (DataIntegrityViolationException dve) {
			LOGGER.error(dve.getMessage(), dve);
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
}
	


