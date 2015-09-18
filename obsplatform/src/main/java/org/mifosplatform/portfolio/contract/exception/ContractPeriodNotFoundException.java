package org.mifosplatform.portfolio.contract.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class ContractPeriodNotFoundException extends AbstractPlatformDomainRuleException {

    public ContractPeriodNotFoundException(final String duration,final Long clientId) {
        super("error.msg.duration.not.found", "Duration not found for this " + duration + " and clientId = " + clientId, duration,clientId);
    }

}
