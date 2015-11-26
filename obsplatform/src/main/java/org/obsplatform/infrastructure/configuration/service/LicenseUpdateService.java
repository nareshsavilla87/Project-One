package org.obsplatform.infrastructure.configuration.service;

import javax.servlet.ServletRequest;

import org.obsplatform.infrastructure.configuration.data.LicenseData;
import org.obsplatform.infrastructure.core.domain.MifosPlatformTenant;

public interface LicenseUpdateService {

	void updateLicenseKey(ServletRequest req, MifosPlatformTenant tenant);
	
	boolean checkIfKeyIsValid(String licenseKey, MifosPlatformTenant tenant);

	LicenseData getLicenseDetails(String licenseKey);

}
