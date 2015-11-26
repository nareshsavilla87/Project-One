package org.obsplatform.finance.depositandrefund.service;

import org.obsplatform.finance.billingorder.service.BillingOrderWritePlatformService;
import org.obsplatform.finance.depositandrefund.domain.DepositAndRefund;
import org.obsplatform.finance.depositandrefund.domain.DepositAndRefundRepository;
import org.obsplatform.finance.depositandrefund.exception.InvalidDepositException;
import org.obsplatform.finance.depositandrefund.serialization.DepositeCommandFromApiJsonDeserializer;
import org.obsplatform.infrastructure.core.api.JsonCommand;
import org.obsplatform.infrastructure.core.data.CommandProcessingResult;
import org.obsplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.obsplatform.infrastructure.core.service.DateUtils;
import org.obsplatform.infrastructure.security.service.PlatformSecurityContext;
import org.obsplatform.organisation.feemaster.data.FeeMasterData;
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
public class DepositeWritePlatformServiceImp implements DepositeWritePlatformService {

	private final static Logger LOGGER = LoggerFactory.getLogger(DepositeWritePlatformServiceImp.class);

	private final PlatformSecurityContext context;
	private final DepositeCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final DepositAndRefundRepository depositAndRefundRepository;
	private final DepositeReadPlatformService depositeReadPlatformService;
	private final BillingOrderWritePlatformService billingOrderWritePlatformService;

	
	
	@Autowired
	public DepositeWritePlatformServiceImp(final PlatformSecurityContext context, final DepositAndRefundRepository depositAndRefundRepository,
			final DepositeCommandFromApiJsonDeserializer apiJsonDeserializer,final DepositeReadPlatformService depositeReadPlatformService,
			final BillingOrderWritePlatformService billingOrderWritePlatformService) {
		
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.billingOrderWritePlatformService = billingOrderWritePlatformService;
		this.depositAndRefundRepository = depositAndRefundRepository;
		this.depositeReadPlatformService = depositeReadPlatformService;

	}

	@Transactional
	@Override
	public CommandProcessingResult createDeposite(final JsonCommand command) {
		
		try{
			context.authenticatedUser();
			this.apiJsonDeserializer.validaForCreate(command.json());
			final String feeId = command.stringValueOfParameterNamed("feeId");
			final String clientId = command.stringValueOfParameterNamed("clientId");
		    FeeMasterData  feeMasterData= this.depositeReadPlatformService.retrieveDepositDetails(Long.valueOf(feeId),Long.valueOf(clientId));
		    if(feeMasterData == null){
		    	throw new InvalidDepositException(Long.valueOf(feeId));
		    }
		    DepositAndRefund depositfund=new DepositAndRefund(Long.valueOf(clientId),Long.valueOf(feeId),feeMasterData.getDefaultFeeAmount(),DateUtils.getDateOfTenant(),feeMasterData.getTransactionType());
			this.depositAndRefundRepository.save(depositfund);
			
			// Update Client Balance
			this.billingOrderWritePlatformService.updateClientBalance(feeMasterData.getDefaultFeeAmount(),Long.valueOf(clientId),false);
			
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(depositfund.getId()).build();
			
		}catch(final DataIntegrityViolationException dve){
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1L));
		}
	
	}

	private void handleDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {
	
		
	}
	
}
