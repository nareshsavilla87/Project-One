package org.mifosplatform.crm.ticketmaster.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.service.DateUtils;

@Entity
@Table(name = "b_ticket_details")
public class TicketDetail {
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "ticket_id", length = 65536)
	private Long ticketId;

	@Column(name = "comments")
	private String comments;

	@Column(name = "attachments")
	private String attachments;
	
	@Column(name = "assigned_to")
	private Long assignedTo;
	
	@Column(name = "created_date")
	private Date createdDate;
	
	@Column(name = "createdby_id")
	private Long createdbyId;
	
	@Column(name="Assign_from")
	private String assignFrom;

	private String status;

	
	public static TicketDetail fromJson(final JsonCommand command) throws ParseException {
	
		final Long assignedTo = command.longValueOfParameterNamed("assignedTo");
		final String description  = command.stringValueOfParameterNamed("description");
		final LocalDate startDate = command.localDateValueOfParameterNamed("ticketDate");
		final String startDateString = startDate.toString() + command.stringValueOfParameterNamed("ticketTime");
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final Date ticketDate = df.parse(startDateString);
		final String assignFrom = command.stringValueOfParameterNamed("assignFrom");
		return new TicketDetail(assignedTo, description, ticketDate, assignFrom);
	}

	public TicketDetail(final Long assignedTo, final String description, final Date ticketDate, final String assignFrom ) {
		this.assignedTo = assignedTo;
		this.comments = null;
		this.createdDate = ticketDate;
		this.assignFrom = assignFrom;
	}

	public TicketDetail(final Long ticketId, final String comments, final String fileLocation,
			final Long assignedTo, final Long createdbyId, final String assignFrom, final String status) {
    
		this.ticketId = ticketId;
        if(comments==""||comments=="undefined"||comments==null){
        	   this.comments = "undefined";
        }else{        
        	this.comments = comments;
        	}
        this.attachments = fileLocation;
        this.assignedTo = assignedTo;
        this.createdDate = DateUtils.getLocalDateOfTenant().toDate();
        this.createdbyId = createdbyId;	
        this.assignFrom = assignFrom;
        this.setStatus(status);
	}

	public TicketDetail(Long id, Long assignedTo, String assignFrom) {
		
		this.id=id;
		this.assignedTo = assignedTo;
		this.assignFrom = assignFrom;
	}

	public Long getId() {
		return id;
	}
	
	public void setTicketId(final Long ticketId) {
		this.ticketId = ticketId;
	}

	public Long getTicketId() {
		return ticketId;
	}

	public String getComments() {
		return comments;
	}

	public String getAttachments() {
		return attachments;
	}

	public Long getAssignedTo() {
		return assignedTo;
	}

	public void setCreatedbyId(final Long createdbyId) {
		this.createdbyId = createdbyId;
	}
	
	public Long getCreatedbyId() {
		return createdbyId;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}
	

	public String getAssignFrom() {
		return assignFrom;
	}

	public void setAssignFrom(String assignFrom) {
		this.assignFrom = assignFrom;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
}