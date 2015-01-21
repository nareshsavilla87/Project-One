package org.mifosplatform.organisation.partner.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PartnerBalanceRepository extends JpaRepository<PartnerControlBalance, Long>,
JpaSpecificationExecutor<PartnerControlBalance> {

	
@Query("from PartnerControlBalance balance where balance.officeId = ?1 and balance.accountType= ?2")
PartnerControlBalance findOneWithPartnerAccount(Long officeId,String accountType);

}
