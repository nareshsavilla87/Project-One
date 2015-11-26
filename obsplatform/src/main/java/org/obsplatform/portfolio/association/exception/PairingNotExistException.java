package org.obsplatform.portfolio.association.exception;

import org.obsplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class PairingNotExistException extends AbstractPlatformDomainRuleException {


	public PairingNotExistException(Long orderId) {
		 super("error.msg.please.pair.hardware.with.plan", "Please pair hardware with plan",orderId);
		 
	}
}
