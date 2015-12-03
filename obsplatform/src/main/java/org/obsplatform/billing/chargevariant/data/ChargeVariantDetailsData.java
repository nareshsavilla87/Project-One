package org.obsplatform.billing.chargevariant.data;

import java.math.BigDecimal;

public class ChargeVariantDetailsData {
	
	private Long id;
	private Long chargeVariantId;
	private String variantType;
	private Long fromRange;
	private Long toRange;
	private String amountType;
	private BigDecimal amount;

	public ChargeVariantDetailsData(Long id, Long chargeVariantId,String variantType, Long fromRange, Long toRange,
			String amountType, BigDecimal amount) {

	                this.id=id;
	                this.chargeVariantId = chargeVariantId;
	                this.variantType = variantType;
	                this.fromRange = fromRange;
	                this.toRange = toRange;
	                this.amountType = amountType;
	                this.amount = amount;
	}

}
