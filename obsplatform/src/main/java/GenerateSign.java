import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;


public class GenerateSign {
	
	public static void main(String []arg) {
		LocalDate date=new LocalDate().plusMonths(1);
		System.out.println(date.dayOfMonth().withMaximumValue());
	}
	
}


