/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.vendormanagementandloyalty.vendor.service;

import java.util.List;

import org.mifosplatform.vendormanagementandloyalty.vendor.data.VendorData;

public interface VendorManagementReadPlatformService1 {

	List<VendorData> retrieveAllVendorAndLoyalties();

	List<VendorData> retrieveVendor(Long vendorId);

	List<VendorData> retrieveVendorDetails(Long vendorId);

}