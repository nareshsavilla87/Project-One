package org.obsplatform.finance.depositandrefund.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DepositAndRefundRepository extends JpaRepository<DepositAndRefund, Long>, JpaSpecificationExecutor<DepositAndRefund>{

	
}



