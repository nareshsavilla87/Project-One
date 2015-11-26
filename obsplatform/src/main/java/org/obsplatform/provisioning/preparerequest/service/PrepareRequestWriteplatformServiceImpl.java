package org.obsplatform.provisioning.preparerequest.service;


import org.obsplatform.cms.eventorder.service.PrepareRequestWriteplatformService;
import org.obsplatform.infrastructure.core.data.CommandProcessingResult;
import org.obsplatform.infrastructure.security.service.PlatformSecurityContext;
import org.obsplatform.portfolio.order.domain.Order;
import org.obsplatform.portfolio.plan.domain.Plan;
import org.obsplatform.provisioning.preparerequest.domain.PrepareRequest;
import org.obsplatform.provisioning.preparerequest.domain.PrepareRequsetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class PrepareRequestWriteplatformServiceImpl implements PrepareRequestWriteplatformService{
	private final PrepareRequsetRepository prepareRequsetRepository; 

	@Autowired
	public PrepareRequestWriteplatformServiceImpl(final PrepareRequsetRepository prepareRequsetRepository	) {
		this.prepareRequsetRepository=prepareRequsetRepository;

	}

	@Override
	public CommandProcessingResult prepareNewRequest(final Order order, final Plan plan, final String requestType) {
  
		try{
		
			PrepareRequest prepareRequest=new PrepareRequest(order.getClientId(), order.getId(), requestType, plan.getProvisionSystem(), 'N', "NONE", plan.getPlanCode());
			this.prepareRequsetRepository.save(prepareRequest);
			
			
			return CommandProcessingResult.resourceResult(prepareRequest.getId(), order.getId());
          			
		} catch (DataIntegrityViolationException dve) {
			return CommandProcessingResult.empty();
		
	}
	}

	@Override
	public void prepareRequestForRegistration(Long clientId, String action,String provisioningSystem) {
		
		PrepareRequest prepareRequest=new PrepareRequest(clientId,Long.valueOf(0),action,provisioningSystem, 'N', "NONE",String.valueOf(0));
		this.prepareRequsetRepository.save(prepareRequest);
		
	}

	

}
