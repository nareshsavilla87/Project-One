package org.mifosplatform.organisation.partneragreement.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

public class AgreementData {
	
	private Long id;
	private String agreementStatus;
	private Long officeId;
	private LocalDate startDate;
	private LocalDate endDate;
	private String shareType;
	private BigDecimal shareAmount;
	private String source;
	private Long detailId;
	private Long partnerId;
	private Collection<MCodeData> shareTypes;
	private Collection<MCodeData> sourceData;
	private List<EnumOptionData> statusData;
	private Collection<MCodeData> agreementTypes;


	public AgreementData(Collection<MCodeData> shareTypes,Collection<MCodeData> sourceData, 
			 Collection<MCodeData> agreementTypes) {

		this.shareTypes = shareTypes;
		this.sourceData = sourceData;
		this.agreementTypes = agreementTypes;

	}


	public AgreementData(Long id,String agreementStatus, Long officeId, LocalDate startDate,LocalDate endDate,  
			   String shareType, BigDecimal shareAmount,String source, Long detailId) {
		
		this.id=id;
		this.agreementStatus = agreementStatus;
		this.officeId = officeId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.shareType = shareType;
		this.shareAmount = shareAmount;
		this.source = source;
		this.detailId = detailId;
		
	}

	public AgreementData(Long id, String agreementStatus, Long officeId,
			LocalDate startDate, LocalDate endDate) {
		
		this.id=id;
		this.agreementStatus = agreementStatus;
		this.officeId = officeId;
		this.startDate = startDate;
		this.endDate = endDate;
		
	}


	public Long getId() {
		return id;
	}
	
	public Long getOfficeId() {
		return officeId;
	}

	public String getAgreementStatus() {
		return agreementStatus;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public String getShareType() {
		return shareType;
	}

	public BigDecimal getShareAmount() {
		return shareAmount;
	}

	public String getSource() {
		return source;
	}

	public  Long getDetailId() {
		return detailId;
	}
	
	public Long getPartnerId() {
		return partnerId;
	}

	public Collection<MCodeData> getShareTypes() {
		return shareTypes;
	}

	public Collection<MCodeData> getSourceData() {
		return sourceData;
	}

	public List<EnumOptionData> getStatusData() {
		return statusData;
	}

	public Collection<MCodeData> getAgreementTypes() {
		return agreementTypes;
	}
	
}
