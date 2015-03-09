package org.mifosplatform.freeradius.radius.service;

import java.util.List;

import org.mifosplatform.freeradius.radius.data.RadiusServiceData;


/**
 * @author hugo
 * 
 */
public interface RadiusReadPlatformService {

	String retrieveAllNasDetails();
	
	String createNas(String Json);

	String retrieveNasDetail(Long nasId);
	
	String deleteNasDetail(Long nasId);
	
	String retrieveAllRadServiceDetails(String attribute);

	String createRadService(String Json);

	String retrieveRadServiceDetail(Long radServiceId);
	
	String deleteRadService(Long radServiceId);

	List<RadiusServiceData> retrieveRadServiceTemplateData();



}
