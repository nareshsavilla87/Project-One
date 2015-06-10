package org.mifosplatform.billing.payterms.data;

import java.math.BigDecimal;

public class PaytermData {

	private final Long id;
	private final String paytermtype;
	private final String duration;
	private final String planType;
	private final BigDecimal price;
public PaytermData(final Long id,final String paytermtype, String duration, String planType, BigDecimal price)
{
this.id=id;
this.paytermtype=paytermtype;
this.duration=duration;
this.planType=planType;
this.price = price;
}
public Long getId() {
	return id;
}
public String getPaytermtype() {
	return paytermtype;
}
/**
 * @return the duration
 */
public String getDuration() {
	return duration;
}
/**
 * @return the planType
 */
public String getPlanType() {
	return planType;
}


}
