package org.obsplatform.finance.paymentsgateway.exception;

import org.obsplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class PaymentGatewayConfigurationException extends AbstractPlatformDomainRuleException {

	public PaymentGatewayConfigurationException(String paymentGateway) {
		super("error.msg.paymentgateway.configure.details.not.found", paymentGateway + " PaymentGateway details are not Configured Properly","");
	}
}
