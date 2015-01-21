package org.mifosplatform.organisation.office.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mifosplatform.organisation.partneragreement.data.AgreementData;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "b_office_commission")
public class OfficeCommision extends AbstractPersistable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "charge_id")
	private final List<Office> children = new LinkedList<Office>();

	@Column(name = "charge_id", nullable = false)
	private Long chargeId;

	@Column(name = "office_id",nullable = false)
	private Long officeId;

	@Column(name = "invoice_date",nullable = false)
	private Date invoiceDate;

	@Column(name = "source",nullable = false)
	private Long source;

	@Column(name = "share_amount",nullable = false)
	private BigDecimal shareAmount;

	@Column(name = "share_type",nullable = false)
	private String shareType;

	@Column(name = "comm_source",nullable = false)
	private String commisionSource;

	@Column(name = "amt",nullable = false)
	private BigDecimal commisionAmount;

	@Column(name = "created_dt", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date createdDate;

	public OfficeCommision(final AgreementData data) {
		
		this.chargeId = data.getChargeId();
		this.officeId = data.getOfficeId();
		this.invoiceDate = data.getStartDate().toDate();
		this.source = data.getSourceId();
		this.shareType = data.getShareType();
		this.shareAmount = data.getShareAmount();
		this.commisionSource = data.getSource();
		this.commisionAmount = data.getCommisionAmount();
		this.createdDate = new Date();
				
	}

	public static OfficeCommision fromJson(AgreementData data) {
		
		return new OfficeCommision(data);
	}
}
