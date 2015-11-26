package org.obsplatform.provisioning.preparerequest.exception;

import org.obsplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class PrepareRequestActivationException extends AbstractPlatformDomainRuleException {

    public PrepareRequestActivationException() {
        super("error.msg.request.sent.for.activation", "Request is already sent for activation");
    }
    
}
