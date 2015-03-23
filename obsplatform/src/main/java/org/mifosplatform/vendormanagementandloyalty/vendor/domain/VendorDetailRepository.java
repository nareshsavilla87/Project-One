package org.mifosplatform.vendormanagementandloyalty.vendor.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VendorDetailRepository  extends
JpaRepository<VendorDetail, Long>,
JpaSpecificationExecutor<VendorDetail>{

}


