package org.mifosplatform.integrationtests.common.Invoice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.integrationtests.common.Utils;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class InvoiceHelper {

	private static final String INVOICE_URL = "/obsplatform/api/v1/billingorder/";

	public static Integer createInvoice(final RequestSpecification request,final ResponseSpecification response) {
		Integer resourceId = null;
		System.out.println("---------------------------------CREATING A INVOICE FOR CLIENTS---------------------------------------------");
		List<Long> clientIds = new ArrayList<>();
		for (Long clientId : clientIds) {
			resourceId = Utils.performServerPost(request, response, INVOICE_URL+ clientId + "?" + Utils.TENANT_IDENTIFIER,getTestInvoiceAsJSON(), "resourceId");
		}
		return resourceId;
	}

	public static String getTestInvoiceAsJSON() {

		final HashMap<String, String> map = new HashMap<>();
		map.put("systemDate", Utils.convertDateToURLFormat(new LocalDate(),"dd MMMM yyyy"));
		map.put("dateFormat", "dd MMMM yyyy");
		map.put("locale", "en");
		System.out.println("map : " + map);
		return new Gson().toJson(map);
	}
}
