package org.mifosplatform.integrationtests;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.regions.RegionHelper;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class RegionTest {


	private ResponseSpecification responseSpec;
	private RequestSpecification requestSpec;

	@Before
	public void setup() {
		Utils.initializeRESTAssured();
		this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
		this.requestSpec.header("Authorization","Basic "+ Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
		this.requestSpec.header("X-Obs-Platform-TenantId", "default");
		this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
	}
	
	
	@Test
	public void testRegionElements() {
		
        final String regionId="1";
		HashMap regionDetail = RegionHelper.getRegionById(requestSpec,responseSpec,regionId);
		System.out.println(regionDetail);
		
        ArrayList<HashMap>  regionsList = RegionHelper.getAllRegions(requestSpec, responseSpec);
		System.out.println(regionsList);

	}
	
	
	@Ignore
	@Test
	public void testCreateRegion() {

		Integer region = RegionHelper.createRegion(requestSpec,responseSpec);
		Assert.assertNotNull(region);

	}


}
