package org.obsplatform.billing.selfcare.exception;

import org.obsplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SelfCareEmailIdDuplicateException extends AbstractPlatformDomainRuleException{

	public SelfCareEmailIdDuplicateException(final String emailId){
		 super("error.msg.billing.emailId.duplicate.found", "EmailId already exist with this " + emailId);
	}
	
	
	

}