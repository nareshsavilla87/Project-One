package org.mifosplatform.integrationtests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.Invoice.InvoiceHelper;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class InvoiceTest {

private ResponseSpecification responseSpec;
private RequestSpecification requestSpec;

@Before
public void setUp() {
	Utils.initializeRESTAssured();
	this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
	this.requestSpec.header("Authorization","Basic "+ Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
	this.requestSpec.header("X-Obs-Platform-TenantId", "default");
	this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();	
	
}

@Test
public void testInvoice(){
	Integer invoiceId=InvoiceHelper.createInvoice(requestSpec, responseSpec);
	Assert.assertNotNull(invoiceId);
	
	
}


}
