package org.obsplatform.billing.chargevariant.domain;


import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.obsplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.google.gson.JsonElement;

@Entity
@Table(name = "b_chargevariant_detail")
public class ChargeVariantDetails extends AbstractPersistable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "variant_type")
	private String variantType;

	@Column(name = "from_range")
	private Long fromRange;
	
	@Column(name = "to_range")
	private Long toRange;
	
	
	@Column(name="amount_type")
	private String amountType;
	
	@Column(name = "amount")
	private BigDecimal amount;
	

	@ManyToOne
	@JoinColumn(name = "chargevariant_id")
	private ChargeVariant chargeVariant;
	
	@Column(name="is_deleted")
	private char isDeleted = 'N';

	public ChargeVariantDetails() {
	}

	public ChargeVariantDetails(String variantType, Long from, Long to,String amountType, BigDecimal amount) {
		
               this.variantType = variantType;
               this.fromRange =from;
               this.toRange=to;
               this.amountType=amountType;
               this.amount= amount;
	               
	
	  }

	public void update(ChargeVariant chargeVariant) {
		   this.chargeVariant=chargeVariant;
	}

	
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getVariantType() {
		return variantType;
	}

	public Long getFrom() {
		return fromRange;
	}

	public Long getTo() {
		return toRange;
	}

	public String getAmountType() {
		return amountType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public ChargeVariant getChargeVariant() {
		return chargeVariant;
	}

	public char getIsDeleted() {
		return isDeleted;
	}

	public Map<String, Object> update(JsonElement element, FromJsonHelper fromApiJsonHelper) {
		final Map<String, Object> actualChanges = new ConcurrentHashMap<String, Object>(1);
		final String variantType = fromApiJsonHelper.extractStringNamed("variantType", element);
		
		final String variantTypeParamName = "variantType";
		if (StringUtils.isNotBlank(this.variantType) && this.chargeVariant.equals(variantType)) {
			final String newValue = variantType;
			actualChanges.put(variantTypeParamName, newValue);
			this.variantType = StringUtils.defaultIfEmpty(newValue, null);
		}

		final String fromParamName = "from";
		final Long from = fromApiJsonHelper.extractLongNamed("from", element);
		if (StringUtils.isNotBlank(this.fromRange.toString()) && this.fromRange.equals(fromParamName)) {
			final String newValue = from.toString();
			actualChanges.put(from.toString(), newValue);
			this.fromRange = from;
		}

		final String toParamName = "to";
		final Long to = fromApiJsonHelper.extractLongNamed("to", element);
		if (StringUtils.isNotBlank(this.toRange.toString()) && this.toRange.equals(toParamName)) {
			final String newValue = to.toString();
			actualChanges.put(to.toString(), newValue);
			this.toRange = to;
		}
		
		final String amountTypeParamName = "amountType";
		if (StringUtils.isNotBlank(this.amountType) && this.chargeVariant.equals(amountType)) {
			final String newValue = amountType;
			actualChanges.put(amountTypeParamName, newValue);
			this.amountType = StringUtils.defaultIfEmpty(newValue, null);
		}

		
		final String amountParamName = "amount";
		final BigDecimal amount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amount", element);
		if (StringUtils.isNotBlank(this.toRange.toString()) && this.toRange.equals(amountParamName)) {
			final BigDecimal newValue = amount;
			actualChanges.put(amountParamName, newValue);
			this.amount = newValue;
		}


		return actualChanges;

	}

	public void delete() {
                this.isDeleted = 'Y';	
	}


}