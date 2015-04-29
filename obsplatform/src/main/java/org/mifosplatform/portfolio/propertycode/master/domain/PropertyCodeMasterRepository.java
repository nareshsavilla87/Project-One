package org.mifosplatform.portfolio.propertycode.master.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PropertyCodeMasterRepository extends JpaRepository<PropertyCodeMaster, Long>,
														 JpaSpecificationExecutor<PropertyCodeMaster>{

}
