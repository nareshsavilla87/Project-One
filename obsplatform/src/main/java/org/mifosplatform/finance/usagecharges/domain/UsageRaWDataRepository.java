
package org.mifosplatform.finance.usagecharges.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Ranjith
 *
 */
public interface UsageRaWDataRepository extends JpaRepository<UsageRaw, Long>,JpaSpecificationExecutor<UsageRaw> {
	
	
	@Query("from UsageRaw usageRaw where usageRaw.clientId =:clientId and usageRaw.number =:number and usageRaw.usageCharge is null")
	List<UsageRaw> findUsageRawDataByCustomerId(@Param("clientId") Long clientId, @Param("number") String number);

}
