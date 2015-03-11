package org.mifosplatform.finance.adjustment.service;

import java.math.BigDecimal;

import org.mifosplatform.finance.adjustment.domain.Adjustment;
import org.mifosplatform.finance.adjustment.domain.AdjustmentRepository;
import org.mifosplatform.finance.adjustment.serializer.AdjustmentCommandFromApiJsonDeserializer;
import org.mifosplatform.finance.clientbalance.domain.ClientBalance;
import org.mifosplatform.finance.clientbalance.domain.ClientBalanceRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdjustmentWritePlatformServiceImpl implements AdjustmentWritePlatformService {

	private final PlatformSecurityContext context;
	private final AdjustmentRepository adjustmentRepository;
	private final ClientBalanceRepository clientBalanceRepository;
	private final AdjustmentCommandFromApiJsonDeserializer fromApiJsonDeserializer;

	@Autowired
	public AdjustmentWritePlatformServiceImpl(final PlatformSecurityContext context,final AdjustmentRepository adjustmentRepository,
			final ClientBalanceRepository clientBalanceRepository,final AdjustmentCommandFromApiJsonDeserializer fromApiJsonDeserializer) {
		
		this.context = context;
		this.adjustmentRepository = adjustmentRepository;
		this.clientBalanceRepository = clientBalanceRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
	}


	@Transactional
	@Override
	public CommandProcessingResult createAdjustment(final JsonCommand command) {

		try {
			
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			Adjustment adjustment = Adjustment.fromJson(command);
			ClientBalance clientBalance = null;
			BigDecimal balance=BigDecimal.ZERO;
			clientBalance = clientBalanceRepository.findByClientId(adjustment.getClientId());
			this.adjustmentRepository.saveAndFlush(adjustment);
			
			boolean isWalletPayment=command.booleanPrimitiveValueOfParameterNamed("isWalletPayment");
			if (clientBalance != null) {

				clientBalance.updateBalance(adjustment.getAdjustmentType(),adjustment.getAmountPaid(),isWalletPayment?'Y':'N');
			} else if (clientBalance == null) {
				
				   if("CREDIT".equalsIgnoreCase(adjustment.getAdjustmentType())){
				       balance=BigDecimal.ZERO.subtract(adjustment.getAmountPaid());
				   }else{
					    balance=BigDecimal.ZERO.add(adjustment.getAmountPaid());
				   }
					   
					clientBalance =ClientBalance.create(adjustment.getClientId(),balance,isWalletPayment?'Y':'N');
			}
                     this.clientBalanceRepository.saveAndFlush(clientBalance);
			return new CommandProcessingResultBuilder()
			.withCommandId(command.commandId()).withEntityId(adjustment.getId()).withClientId(command.entityId())
			.build();
			
		} catch (DataIntegrityViolationException dve) {
			return CommandProcessingResult.empty();
		}
	}
}
