package org.mifosplatform.billing.selfcare.service;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

@SuppressWarnings("serial")
public class SelfcarePasswordNotFoundException extends AbstractPlatformDomainRuleException {
	
	public SelfcarePasswordNotFoundException(final String password){
		super("error.msg.selfcare.currentpassword.not.found", "Email Address not found with this " + password, password);
	}
	

}
