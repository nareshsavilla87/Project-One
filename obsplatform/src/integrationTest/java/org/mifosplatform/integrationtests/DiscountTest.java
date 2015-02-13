package org.mifosplatform.integrationtests;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.discount.DiscountDomain;
import org.mifosplatform.integrationtests.common.discount.DiscountHelper;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class DiscountTest {

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

	@Ignore
	@Test
	public void testDiscountElements() {

		ArrayList<DiscountDomain> discountList = DiscountHelper.getAllDiscounts(requestSpec, responseSpec);
		System.out.println(discountList);
		/*
		 * for(DiscountDomain list:discountList){
		 * Assert.assertTrue(list.getDiscountCode().equals("DISRPVFX")); }
		 */
		//

	}

	@Test
	public void testCreateDiscount() {

		Integer discount = DiscountHelper.createDiscount(requestSpec,responseSpec);
		System.out.println(discount);
		Assert.assertNotNull(discount);

	}

	@After
	public void tearDown() {

		System.out.println("----------------Successfully executed--------------------");

	}

}
