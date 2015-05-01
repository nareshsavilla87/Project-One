package org.mifosplatform.portfolio.property.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PropertyCodeMasterRepository extends
		JpaRepository<PropertyCodeMaster, Long>,
		JpaSpecificationExecutor<PropertyCodeMaster> {

}
