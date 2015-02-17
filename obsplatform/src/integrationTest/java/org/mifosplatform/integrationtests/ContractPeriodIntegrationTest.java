package org.mifosplatform.integrationtests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.contractperiod.ContractPeriodHelper;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class ContractPeriodIntegrationTest {
    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;
    private ContractPeriodHelper accountHelper;
    
    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.requestSpec.header("X-Obs-Platform-TenantId", "default");
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }
    
    @Test
    public void flowContractPeriod(){
    	@SuppressWarnings("static-access")
		Integer contract = accountHelper.createContractPeriod(requestSpec, responseSpec);
    	System.out.println("----****----" +contract+ "----****----");
    	Assert.assertNotNull(contract);
    }
    
    @Ignore
    @After
    public void tearDown() {

       System.out.println("----------------Successfully executed--------------------");
        
    }

}

