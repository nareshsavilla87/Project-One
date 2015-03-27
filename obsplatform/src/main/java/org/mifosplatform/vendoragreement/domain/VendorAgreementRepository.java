package org.mifosplatform.vendoragreement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VendorAgreementRepository  extends
JpaRepository<VendorAgreement, Long>,
JpaSpecificationExecutor<VendorAgreement>{

}

