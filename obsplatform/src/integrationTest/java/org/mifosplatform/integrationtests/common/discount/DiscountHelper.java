package org.mifosplatform.integrationtests.common.discount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mifosplatform.integrationtests.common.Utils;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class DiscountHelper {
	
	 private static final String DISCOUNTS_URL = "/obsplatform/api/v1/discount";

	@SuppressWarnings("unchecked")
	public static ArrayList<DiscountDomain> getAllDiscounts(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
		
		   final String GET_ALL_DISCOUNTS_URL = DISCOUNTS_URL + "?" + Utils.TENANT_IDENTIFIER;
	        System.out.println("------------------------ RETRIEVING ALL DISCOUNTS -------------------------");
	        final List<DiscountDomain> response = Utils.performServerGet(requestSpec, responseSpec, GET_ALL_DISCOUNTS_URL, "");
	        final String jsonData = new Gson().toJson(new ArrayList<DiscountDomain>(response));//remove spaces, convert java object to JSON format and returned as JSON formatted string
	        return new Gson().fromJson(jsonData, new ArrayList<DiscountDomain>().getClass());
	}
	
	
	 public static Integer createDiscount(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
		   Integer  resourceId=null;
		 
		 for(int i=0; i<=1; i++){
	        System.out.println("---------------------------------CREATING A DISCOUNT---------------------------------------------");
	        resourceId = Utils.performServerPost(requestSpec, responseSpec, DISCOUNTS_URL+"?"+ Utils.TENANT_IDENTIFIER, getTestDiscountAsJSON(i),"resourceId");
	    }
		 return resourceId;
	 }


	  public static String getTestDiscountAsJSON(int i) {
		  
	        final HashMap<String, String> map = new HashMap<>();
	        switch(i) {
	        case 0:
	        map.put("discountCode", Utils.randomStringGenerator("DI",5));
	        map.put("discountRate",Utils.randomNumberGenerator(1,100));
	        map.put("discountDescription", "discountflat");
	        map.put("discountType","Flat");
	        map.put("dateFormat", "dd MMMM yyyy");
	        map.put("locale", "en");
	        map.put("startDate", "13 February 2015");
	        map.put("discountStatus","ACTIVE");
	        System.out.println("map : " + map);
	        break;
	        
	    	default:
	    	 break;	
	        }	
	        return new Gson().toJson(map);
	  }

}
