package org.mifosplatform.portfolio.plan.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class PlanNotFundException extends AbstractPlatformResourceNotFoundException {

public PlanNotFundException() {
super("error.msg.depositproduct.id.invalid",
		"Charge Code already exists with same plan");
}

public PlanNotFundException(final Long id) {
	super("error.msg.plan.not.found",
			"Plan with this Identifier `"+id+"` not found ",id );
}

}
