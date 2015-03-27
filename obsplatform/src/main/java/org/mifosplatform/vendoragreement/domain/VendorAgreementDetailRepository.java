package org.mifosplatform.vendoragreement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VendorAgreementDetailRepository  extends
JpaRepository<VendorAgreementDetail, Long>,
JpaSpecificationExecutor<VendorAgreementDetail>{

}


