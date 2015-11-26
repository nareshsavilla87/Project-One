package org.obsplatform.crm.ticketmaster.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketDetailsRepository  extends
	JpaRepository<TicketDetail, Long>,
	JpaSpecificationExecutor<TicketDetail>{
	}


