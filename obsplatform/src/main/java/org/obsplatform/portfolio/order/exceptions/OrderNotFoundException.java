package org.obsplatform.portfolio.order.exceptions;

import org.obsplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class OrderNotFoundException extends AbstractPlatformDomainRuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OrderNotFoundException(final Long orderId) {
		super("error.msg.Order.not.found.with.this.identifier","Order not found with this identifier",orderId);
		
	}

}
