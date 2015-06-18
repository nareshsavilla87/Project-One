package org.mifosplatform.portfolio.property.domain;

import java.util.List;

import javax.ws.rs.QueryParam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

public interface PropertyDeviceMappingRepository extends JpaRepository<PropertyDeviceMapping, Long>,
JpaSpecificationExecutor<PropertyDeviceMapping>{

	@QueryParam("from PropertyDeviceMapping propertyDeviceMapping where propertyDeviceMapping.serialNumber =:serialNumber and propertyDeviceMapping.isDeleted = 'N'")
	PropertyDeviceMapping findBySerailNumber(@Param("serialNumber")String serialNumber);

	

}

