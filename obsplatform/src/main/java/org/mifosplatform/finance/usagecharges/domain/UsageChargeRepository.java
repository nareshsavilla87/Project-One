
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
public interface UsageChargeRepository extends JpaRepository<UsageCharge, Long>,
    JpaSpecificationExecutor<UsageCharge> {

	@Query("from UsageCharge usageCharge where usageCharge.clientId =:clientId and usageCharge.number =:number and usageCharge.usageCharge is null")
	List<UsageCharge> findCustomerUsageCharges(@Param("clientId") Long clientId,@Param("number") String number);

}
