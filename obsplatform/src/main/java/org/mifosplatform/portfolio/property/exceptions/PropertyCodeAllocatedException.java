package org.mifosplatform.portfolio.property.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class PropertyCodeAllocatedException extends AbstractPlatformDomainRuleException {

    public PropertyCodeAllocatedException(String msg) {
        super("error.msg.property.code.already.allocated", " property Code is already allocated to client", msg);
    }

	
}
