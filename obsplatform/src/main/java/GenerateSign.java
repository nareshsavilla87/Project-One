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
import org.json.JSONException;
import org.json.JSONObject;


public class GenerateSign {
	
	static char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	
	public static void main(String []arg) throws ClientProtocolException, IOException, JSONException{
		
		HttpClient  httpClient=new DefaultHttpClient();  
		HttpGet request = new HttpGet("http://cms.ip888.tv:8088/iptv/ApiOBS?username=obs_integrator&operation=get_time");
		request.addHeader("User-Agent", "Mozilla/5.0");
		//request.
		HttpResponse response = httpClient.execute(request);
		 BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		  String output,output1="";

		  while ((output = br.readLine()) != null) {
		   output1 = output1.concat(output);
		  } 
		  System.out.println("time is"+output1);
		  JSONObject ob=new JSONObject(output1);
		  long time=ob.getLong("timestamp")+1;
		  System.out.println(time);
		  //System.out.println(time+1);
		 
		Map<String, Object>map = new HashMap<String, Object>();  
      map.put("timestamp", time);
		
	    map.put("operation", "user_list");
		/*map.put("operation", "edit_box");
		map.put("box_id", "5363");
		map.put("mac", "11:11:bd:b0:00:f9");
		map.put("sn", "11:11:bd:b0:00:f9");*/
		//map.put("memberType_id", "9");
		map.put("username", "obs_integrator");
		String signature= sign_post(map,"19dk3d!s22");
		System.out.println(signature);
		
		//System.out.println("http://catchup2.ip888.tv:8088/iptv/ApiOBS?username=obs_integrator&operation=user_list&timestamp="+time+"&sign="+signature);
		request = new HttpGet("http://cms.ip888.tv:8088/iptv/ApiOBS?username=obs_integrator&operation=user_list&timestamp="+time+"&sign="+signature);
		//request = new HttpGet("http://catchup2.ip888.tv:8088/iptv/ApiOBS?username=obs_integrator&operation=edit_box&box_id=5363&timestamp="+time+"&mac=11:11:bd:b0:00:f9&sn=11:11:bd:b0:00:f9&sign="+signature);
		//request = new HttpGet("http://catchup2.ip888.tv:8088/iptv/ApiOBS?username=obs_integrator&operation=user_detail&user_id=1389&timestamp="+time+"&sign="+signature);
		//request = new HttpGet("http://catchup2.ip888.tv:8088/iptv/ApiOBS?username=obs_integrator&operation=get_products&memberType_id=9&timestamp="+time+"&sign="+signature);
		//request = new HttpGet("http://catchup2.ip888.tv:8088/iptv/ApiOBS?username=obs_integrator&operation=get_products&memberType_id=9&timestamp="+time+"&sign="+signature);
		request.addHeader("User-Agent", "Mozilla/5.0");
		//request.
		 response = httpClient.execute(request);
		  br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		   output1="";

		  while ((output = br.readLine()) != null) {
			  
		   output1 = output1.concat(output);
		  } 
		  System.out.println(output1);

	
	
	}
	private static String sign_post(Map<String, Object> parms, String pass) {
		  String sig, data = "";
		  Set<String> set = parms.keySet();
		  String[] keys = new String[set.size()];
		  set.toArray(keys);
		  List<String> tmpkeyList = Arrays.asList(keys);

		  Collections.sort(tmpkeyList);
		  for (Object key : tmpkeyList) {
		   data = data + key.toString() + "=" + parms.get(key).toString()+ "&";
		  }
		  
		  data = data.substring(0, data.length() - 1);
		  try {
		   sig = SHA1(data + pass);
		   System.out.println("signing:"+data+pass+" and is:"+sig);
		   return sig;
		  } catch (NoSuchAlgorithmException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  } catch (UnsupportedEncodingException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  }

		  return "";
		 }

	
	private static String SHA1(String text) throws NoSuchAlgorithmException,
	UnsupportedEncodingException 
	{
		System.out.println(text);
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		byte[] sha1hash = md.digest();
		return convertToHex(sha1hash);
	}

	
	private static String convertToHex(byte[] data) {

		StringBuilder buf = new StringBuilder();
		for (int i = 0; i<data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte<= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return  buf.toString();
		 
	
}
}


