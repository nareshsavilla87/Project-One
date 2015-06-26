

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;

public class SampleGet {

	
	static Socket requestSocket = null;
	
	String message;
	private static HttpPost post;
	private static byte[] encoded;
	private static String tenantIdentifier;
	//private static HttpClient httpClient;
	private static String provisioningSystem;
	public int timePeriod;
	public static int wait;

	public static HttpClient wrapClient(HttpClient base) {

		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				@SuppressWarnings("unused")
				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				@SuppressWarnings("unused")
				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = base.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			return new DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
			return null;
		}
	}

	@SuppressWarnings("static-access")
	public SampleGet() {
		try{
			
			//httpClient = new DefaultHttpClient();
			//httpClient = wrapClient(httpClient);
			//httpClient =new DefaultHttpClient();
			
			String encodedpassword ="admin:demo@123";
			encoded = Base64.encodeBase64(encodedpassword.getBytes());
			
		} catch(Exception e){
		}
	
	}

	
	
	public static void main(String arg[]){
		
		try {
			HttpClient httpclient = HttpClients.createDefault();
			SampleGet sampleGet= new SampleGet();
			//post = new HttpPost(prop.getString("BSSServerQuery").trim() + "/" + id);
			HttpGet post = new HttpGet("https://istream.openbillingsystem.com/obsplatform/api/v1/offices");
			
			post.setHeader("Authorization", "Basic " + new String(encoded));
			post.setHeader("Content-Type", "application/json");
			post.addHeader("X-Obs-Platform-TenantId", "demo");
			String errorOutput= "";
			String output = "";
			HttpResponse response = httpclient.execute(post);
			//response.getEntity().consumeContent();
			if (response.getStatusLine().getStatusCode() != 200) {
				String error = String.valueOf(response.getStatusLine().getStatusCode());
				
				throw new Exception(error);
			}else{
				BufferedReader br2 = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
				while((output = br2.readLine()) != null){
					errorOutput = errorOutput + output;
				}
				System.out.println(errorOutput);
			//	br2.close();
				
			}
			

		}catch (Exception e) {
			e.printStackTrace();
	    }
	  
	}
}
