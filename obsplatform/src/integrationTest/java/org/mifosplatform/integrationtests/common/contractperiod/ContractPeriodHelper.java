package org.mifosplatform.integrationtests.common.contractperiod;

import java.util.HashMap;

import org.junit.Assert;
import org.mifosplatform.integrationtests.common.Utils;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class ContractPeriodHelper {

	private static final String CREATE_CONTRACT_PERIOD_URL = "/obsplatform/api/v1/subscriptions?tenantIdentifier=default";

	public static Integer createContractPeriod(final RequestSpecification requestSpec,final ResponseSpecification responseSpec) {
		Integer contract = null;
		for (int i = 0; i <= 1; i++) {
			System.out.println("---------------------------------CREATING A CONTRACT PERIOD["+ i+ "]---------------------------------------------");
			contract = Utils.performServerPost(requestSpec, responseSpec,CREATE_CONTRACT_PERIOD_URL, getContractPeriodBodyAsJSON(i),"resourceId");
			Assert.assertNotNull(contract);
		}
		return contract;

	}

	public static String getContractPeriodBodyAsJSON(int i) {
		HashMap<String, String> map = new HashMap<String, String>();
		switch (i) {
		case 0:
			map.put("subscriptionType", "Week(s)");
			map.put("subscriptionPeriod", "ABCDF");
			map.put("units", "6");
			break;

		default:
			break;
		}
		return new Gson().toJson(map);
	}
}