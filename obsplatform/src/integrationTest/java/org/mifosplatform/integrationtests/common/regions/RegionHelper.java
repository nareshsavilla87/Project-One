package org.mifosplatform.integrationtests.common.regions;

import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.LocalDate;
import org.mifosplatform.integrationtests.common.Utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class RegionHelper {
	
	 private static final String REGIONS_URL = "/obsplatform/api/v1/regions";

	@SuppressWarnings("rawtypes")
	public static HashMap getRegionById(RequestSpecification requestSpec,ResponseSpecification responseSpec, String regionId) {
		
		  final String GET_REGIONS_URL= REGIONS_URL + "/"+ regionId + "?" + Utils.TENANT_IDENTIFIER;
		  System.out.println("------------------------ RETRIEVING  REGION DATA -------------------------");
		  final String jsonData = new Gson().toJson(Utils.performServerGet(requestSpec, responseSpec, GET_REGIONS_URL, ""));
	      return new Gson().fromJson(jsonData, new TypeToken<HashMap>() {}.getType());
		
	}

	@SuppressWarnings("rawtypes")
	public static ArrayList<HashMap> getAllRegions(RequestSpecification requestSpec,ResponseSpecification responseSpec) {
		
		    final String GET_ALL_REGIONS_URL = REGIONS_URL + "?" + Utils.TENANT_IDENTIFIER;
	        System.out.println("------------------------ RETRIEVING ALL REGIONS -------------------------");
	        final ArrayList<HashMap> response = Utils.performServerGet(requestSpec, responseSpec, GET_ALL_REGIONS_URL, "");
	        final String jsonData = new Gson().toJson(new ArrayList<HashMap>(response));//remove spaces, convert java object to JSON format and returned as JSON formatted string   
	        System.out.println(jsonData);   
	        return new Gson().fromJson(jsonData,new TypeToken<ArrayList<HashMap>>(){}.getType());
	
	}

	public static Integer createRegion(RequestSpecification requestSpec,ResponseSpecification responseSpec) {
		
		   System.out.println("---------------------------------CREATING A DISCOUNT---------------------------------------------");
	       Integer resourceId = Utils.performServerPost(requestSpec, responseSpec, REGIONS_URL+"?"+ Utils.TENANT_IDENTIFIER, getTestRegionAsJSON(),"resourceId");
		   return resourceId;
	
	}

	private static String getTestRegionAsJSON() {
		
		final HashMap<String, String> map = new HashMap<>();
        map.put("discountCode", Utils.randomStringGenerator("DI",5));
        map.put("discountRate",Utils.randomNumberGenerator(1,100));
        map.put("discountDescription", "off");
        map.put("discountType","Percentage");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("locale", "en");
        map.put("startDate",Utils.convertDateToURLFormat(new LocalDate(), "dd MMMM yyyy"));
        map.put("discountStatus","ACTIVE");
        System.out.println("map : " + map);	
        return new Gson().toJson(map);
	}



}
