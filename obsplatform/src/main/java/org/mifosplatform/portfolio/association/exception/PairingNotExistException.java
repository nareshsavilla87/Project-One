package org.mifosplatform.portfolio.association.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class PairingNotExistException extends AbstractPlatformDomainRuleException {

	private static final long serialVersionUID = 1L;

	public PairingNotExistException(Long orderId) {
		super("error.msg.please.pair.hardware.with.plan",	"Please pair hardware with plan", orderId);

	}

	public PairingNotExistException(Long orderId, String planName) {
		super("error.msg.hardware.device.for.is.not.available.for.pairing",
				"Hardware Device for " + planName+ " is not available for pairing", planName);

	}

	public PairingNotExistException(String planName) {
		super("error.msg.all.hardware.device.for.is.not.available.for.pairing",
				"All Hardware Device for "+ planName+ " is not available for pairing",
				planName);
	}
}
