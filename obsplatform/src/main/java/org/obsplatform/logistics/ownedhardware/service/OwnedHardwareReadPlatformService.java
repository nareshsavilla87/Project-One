package org.obsplatform.logistics.ownedhardware.service;

import java.util.List;

import org.obsplatform.logistics.item.data.ItemData;
import org.obsplatform.logistics.ownedhardware.data.OwnedHardwareData;

public interface OwnedHardwareReadPlatformService {

	public List<OwnedHardwareData> retriveOwnedHardwareData(Long clientId);
	
	public List<ItemData> retriveTemplate();
	
	public List<String> retriveSerialNumbers();
	
	public List<OwnedHardwareData> retriveSingleOwnedHardwareData(Long id);

	public int retrieveClientActiveDevices(Long clientId);

	public int retrieveNoOfActiveUsers(Long clientId);
	
}
