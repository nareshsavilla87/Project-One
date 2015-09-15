/**
 * 
 */
package org.mifosplatform.portfolio.association.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Ranjith
 *
 */
public interface AssociationRepository extends JpaRepository<HardwareAssociation, Long>,
JpaSpecificationExecutor<HardwareAssociation>{
	
	
	@Query("from HardwareAssociation association where association.orderId =:orderId and isDeleted='N' and allocationType='ALLOT'")
	List<HardwareAssociation> findOrderAssocaitions(@Param("orderId")Long orderId);

}
