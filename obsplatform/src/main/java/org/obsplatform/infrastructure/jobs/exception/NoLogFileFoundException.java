package org.obsplatform.infrastructure.jobs.exception;

import org.obsplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class NoLogFileFoundException extends AbstractPlatformDomainRuleException {

    public NoLogFileFoundException() {
        super("error.msg.billing.Job.logfile .found", "Log Files are Not created ");
    }
    
   
}
