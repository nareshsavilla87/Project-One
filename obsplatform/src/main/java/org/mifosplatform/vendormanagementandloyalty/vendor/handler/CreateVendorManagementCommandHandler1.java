/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.mifosplatform.vendormanagementandloyalty.vendor.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.vendormanagementandloyalty.vendor.service.VendorManagementWritePlatformService1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateVendorManagementCommandHandler1 implements NewCommandSourceHandler {

    private final VendorManagementWritePlatformService1 writePlatformService;

    @Autowired
    public CreateVendorManagementCommandHandler1(final VendorManagementWritePlatformService1 writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        return this.writePlatformService.createVendorAndLoyaltyCalucation(command);
    }
}
