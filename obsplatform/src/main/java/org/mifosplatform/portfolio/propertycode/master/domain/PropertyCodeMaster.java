package org.mifosplatform.portfolio.propertycode.master.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_property_master",uniqueConstraints = { @UniqueConstraint(name = "property_code_type_with_its_code",columnNames = { "property_code_type", "code"}) })
public class PropertyCodeMaster extends AbstractPersistable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "property_code_type",nullable=false)
	private String propertyCodeType;
	
	@Column(name = "code",nullable=false)
	private String code;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "reference_value")
	private String referenceValue;

	public PropertyCodeMaster() {
	}

	public PropertyCodeMaster(final String propertyCodeType, final String code, final String description,final String referenceValue) {

		this.propertyCodeType = propertyCodeType;
		this.code = code;
		this.description = description;
		this.referenceValue = referenceValue;

	}

	
	public static PropertyCodeMaster fromJson(final JsonCommand command) {
	    final String propertyCodeType = command.stringValueOfParameterNamed("propertyCodeType");
	    final String code = command.stringValueOfParameterNamed("code");
	    final String description = command.stringValueOfParameterNamed("description");
	    final String referenceValue = command.stringValueOfParameterNamed("referenceValue");
	    return new PropertyCodeMaster(propertyCodeType,code,description,referenceValue);
	}

	public String getPropertyCodeType() {
		return propertyCodeType;
	}

	public void setPropertyCodeType(String propertyCodeType) {
		this.propertyCodeType = propertyCodeType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReferenceValue() {
		return referenceValue;
	}

	public void setReferenceValue(String referenceValue) {
		this.referenceValue = referenceValue;
	}


}
