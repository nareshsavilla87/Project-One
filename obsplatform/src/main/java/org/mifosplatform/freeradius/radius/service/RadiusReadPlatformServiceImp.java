package org.mifosplatform.freeradius.radius.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.freeradius.radius.data.RadiusServiceData;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.jobs.service.JobName;
import org.mifosplatform.portfolio.order.domain.RadServiceTemp;
import org.mifosplatform.portfolio.order.domain.RadServuceTempRepository;
import org.mifosplatform.portfolio.order.exceptions.RadiusDetailsNotFoundException;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.provisioning.processscheduledjobs.service.SheduleJobReadPlatformService;
import org.mifosplatform.provisioning.provisioning.api.ProvisioningApiConstants;
import org.mifosplatform.provisioning.provsionactions.domain.ProvisionActions;
import org.mifosplatform.provisioning.provsionactions.domain.ProvisioningActionsRepository;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.JobParameterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

/**
 * @author hugo
 * 
 */
@Service
public class RadiusReadPlatformServiceImp implements RadiusReadPlatformService {

	private final SheduleJobReadPlatformService sheduleJobReadPlatformService;
	private final JdbcTemplate jdbcTemplate;
    private final RadServuceTempRepository radServuceTempRepository;
	private final ProvisioningActionsRepository provisioningActionsRepository;
	private final ProcessRequestRepository processRequestRepository;
	
	@Autowired
	public RadiusReadPlatformServiceImp(final SheduleJobReadPlatformService sheduleJobReadPlatformService, final TenantAwareRoutingDataSource dataSource,
			final ProvisioningActionsRepository provisioningActionsRepository, final ProcessRequestRepository processRequestRepository,
			final RadServuceTempRepository radServuceTempRepository){
		
		this.sheduleJobReadPlatformService = sheduleJobReadPlatformService;
		this.provisioningActionsRepository = provisioningActionsRepository;
		this.radServuceTempRepository = radServuceTempRepository;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.processRequestRepository = processRequestRepository;
		
	}
	
	@Override
	public String retrieveAllNasDetails() {

		try {
			JobParameterData data = this.sheduleJobReadPlatformService.getJobParameters(JobName.RADIUS.toString());
			if(data == null){
				throw new RadiusDetailsNotFoundException();
			}
			String url = data.getUrl() + "nas";
			String credentials = data.getUsername().trim() + ":" + data.getPassword().trim();
			byte[] encoded = Base64.encodeBase64(credentials.getBytes());
			String encodedPassword = new String(encoded);
			String nasData = this.processRadiusGet(url, encodedPassword);
			return nasData;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}

	}
	
	
	@Override
	public String retrieveNasDetail(final Long nasId) {

		try {
			JobParameterData data = this.sheduleJobReadPlatformService.getJobParameters(JobName.RADIUS.toString());
			if(data == null){
				throw new RadiusDetailsNotFoundException();
			}
			String url = data.getUrl() + "nas/"+nasId;
			String credentials = data.getUsername().trim() + ":" + data.getPassword().trim();
			byte[] encoded = Base64.encodeBase64(credentials.getBytes());
			String encodedPassword = new String(encoded);
			String nasData = this.processRadiusGet(url, encodedPassword);
			return nasData;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}

	}
	
	@Override
	public String createNas(final String jsonData) {
		
		try {
			JobParameterData data = this.sheduleJobReadPlatformService.getJobParameters(JobName.RADIUS.toString());
			if(data == null){
				throw new RadiusDetailsNotFoundException();
			}
			String url = data.getUrl() + "nas";
			String credentials = data.getUsername().trim() + ":" + data.getPassword().trim();
			byte[] encoded = Base64.encodeBase64(credentials.getBytes());
			String encodedPassword = new String(encoded);
			String nasData = this.processRadiusPost(url, encodedPassword,jsonData);
			
			ProvisionActions provisionActions = this.provisioningActionsRepository.findOneByProvisionType(ProvisioningApiConstants.PROV_EVENT_CREATE_NAS);
			
			if(provisionActions.getIsEnable() == 'Y'){
				
				 ProcessRequest processRequest = new ProcessRequest(Long.valueOf(0), Long.valueOf(0), Long.valueOf(0),
						 provisionActions.getProvisioningSystem(),provisionActions.getAction(), 'N', 'N');

				 ProcessRequestDetails processRequestDetails = new ProcessRequestDetails(Long.valueOf(0),
						 Long.valueOf(0), jsonData, "Recieved",
						 null, new Date(), null, null, null, 'N', provisionActions.getAction(), null);

				 processRequest.add(processRequestDetails);
				 this.processRequestRepository.save(processRequest);
				
			}
			return nasData;
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	
	@Override
	public String deleteNasDetail(final Long nasId) {

		try {
			JobParameterData data = this.sheduleJobReadPlatformService.getJobParameters(JobName.RADIUS.toString());
			if(data == null){
				throw new RadiusDetailsNotFoundException();
			}
			String url = data.getUrl() + "nas/"+nasId;
			String credentials = data.getUsername().trim() + ":" + data.getPassword().trim();
			byte[] encoded = Base64.encodeBase64(credentials.getBytes());
			String encodedPassword = new String(encoded);
			String nasData = this.processRadiusDelete(url, encodedPassword);
			return nasData;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}

	}
	
	@Override
	public String retrieveAllRadServiceDetails(final String attribute) {

		try {
			JobParameterData data = this.sheduleJobReadPlatformService.getJobParameters(JobName.RADIUS.toString());
			if(data == null){
				throw new RadiusDetailsNotFoundException();
			}
			String url ="";
			if(data.getProvSystem().equalsIgnoreCase("version-1")){
				if(attribute!=null){
					url= data.getUrl() + "radservice?attribute="+attribute;
				}else{
					url= data.getUrl() + "radservice";
				}
			}else if(data.getProvSystem().equalsIgnoreCase("version-2")){
				url= data.getUrl() + "service2";
			}
			String credentials = data.getUsername().trim() + ":" + data.getPassword().trim();
			byte[] encoded = Base64.encodeBase64(credentials.getBytes());
			String encodedPassword = new String(encoded);
			String radServiceData = this.processRadiusGet(url, encodedPassword);
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("radiusVersion", data.getProvSystem().toLowerCase());
			jsonObj.put("radServiceData", new JSONArray(radServiceData));
			radServiceData = jsonObj.toString();
			return radServiceData;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		} catch (JSONException e) {
			e.printStackTrace();
			return e.getMessage();
		}

	}
	
	@Override
	public CommandProcessingResult createRadService(final String Json) {
		
		try {
			JobParameterData data = this.sheduleJobReadPlatformService.getJobParameters(JobName.RADIUS.toString());
			if(data == null){
				throw new RadiusDetailsNotFoundException();
			}
			String url = "";
			if(data.getProvSystem().equalsIgnoreCase("version-1")){
				 url = data.getUrl() + "radservice";
			}
			else if(data.getProvSystem().equalsIgnoreCase("version-2")){
				 url = data.getUrl() + "service2";
			}
			String credentials = data.getUsername().trim() + ":" + data.getPassword().trim();
			byte[] encoded = Base64.encodeBase64(credentials.getBytes());
			String encodedPassword = new String(encoded);
			String radServiceData = this.processRadiusPost(url, encodedPassword,Json);
			JSONObject jsonObject = new JSONObject(Json); 
			RadServiceTemp radServiceTemp=RadServiceTemp.fromJson(jsonObject);
			jsonObject.put("srvid", radServiceTemp.getId());
			this.radServuceTempRepository.saveAndFlush(radServiceTemp);
			
			
			
			ProvisionActions provisionActions=this.provisioningActionsRepository.findOneByProvisionType(ProvisioningApiConstants.PROV_EVENT_CREATE_RADSERVICE);
			if(provisionActions.getIsEnable() == 'Y'){
				
				 ProcessRequest processRequest = new ProcessRequest(Long.valueOf(0), Long.valueOf(0), Long.valueOf(0),
						 provisionActions.getProvisioningSystem(),provisionActions.getAction(), 'N', 'N');

				 ProcessRequestDetails processRequestDetails = new ProcessRequestDetails(Long.valueOf(0),
						 Long.valueOf(0), Json, "Recieved",
						 null, new Date(), null, null, null, 'N', provisionActions.getAction(), null);

				 processRequest.add(processRequestDetails);
				 this.processRequestRepository.save(processRequest);
				
			}
			return new CommandProcessingResult(radServiceTemp.getId());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (IOException e) {
			e.printStackTrace();
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (JSONException e) {
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
	
	
	@Override
	public String retrieveRadServiceDetail(final Long radServiceId) {

		try {
			JobParameterData data = this.sheduleJobReadPlatformService.getJobParameters(JobName.RADIUS.toString());		
			if(data == null){
				throw new RadiusDetailsNotFoundException();
			}
			String url = data.getUrl() + "radservice/"+radServiceId;
			String credentials = data.getUsername().trim() + ":" + data.getPassword().trim();
			byte[] encoded = Base64.encodeBase64(credentials.getBytes());
			String encodedPassword = new String(encoded);
			String radServiceData = this.processRadiusGet(url, encodedPassword);
			return radServiceData;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}

	}
	
	@Override
	public String deleteRadService(final Long radServiceId) {

		try {
			JobParameterData data = this.sheduleJobReadPlatformService.getJobParameters(JobName.RADIUS.toString());
			if(data == null){
				throw new RadiusDetailsNotFoundException();
			}
			String url = "";
			if(data.getProvSystem().equalsIgnoreCase("version-1")){
			  url = data.getUrl() + "radservice/"+radServiceId;
			}
			/*else if(data.getProvSystem().equalsIgnoreCase("version-2")){
				url = data.getUrl() + "radservice/"+radServiceId;
			}*/
			String credentials = data.getUsername().trim() + ":" + data.getPassword().trim();
			byte[] encoded = Base64.encodeBase64(credentials.getBytes());
			String encodedPassword = new String(encoded);
			String radServiceData = this.processRadiusDelete(url, encodedPassword);
			return radServiceData;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}

	}
	
	//get
	private  String processRadiusGet(String url, String encodePassword) throws ClientProtocolException, IOException{
		
		 HttpClient httpClient = new DefaultHttpClient();
		 HttpGet getRequest = new HttpGet(url);
		 getRequest.setHeader("Authorization", "Basic " +encodePassword);
		 getRequest.setHeader("Content-Type", "application/json");
		 HttpResponse response=httpClient.execute(getRequest);
		 
		 if (response.getStatusLine().getStatusCode() == 404) {
				return "ResourceNotFoundException";

			} else if (response.getStatusLine().getStatusCode() == 401) {	
				return "UnauthorizedException"; 

			} else if (response.getStatusLine().getStatusCode() != 200) {
				System.out.println("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			} else{
				System.out.println("Execute Successfully:" + response.getStatusLine().getStatusCode());
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			String output,output1="";
			
			while ((output = br.readLine()) != null) {
				output1 = output1 + output;
			}
			br.close();
			return output1;
		 
		}
	
	//post
	private  String processRadiusPost(String url, String encodePassword, String data) throws IOException{
		
		HttpClient httpClient = new DefaultHttpClient();
		StringEntity se = new StringEntity(data.trim());
		HttpPost postRequest = new HttpPost(url);
		postRequest.setHeader("Authorization", "Basic " + encodePassword);
		postRequest.setHeader("Content-Type", "application/json");
		postRequest.setEntity(se);
		HttpResponse response = httpClient.execute(postRequest);

		if (response.getStatusLine().getStatusCode() == 404) {
			return "ResourceNotFoundException";

		} else if (response.getStatusLine().getStatusCode() == 401) {
			return "UnauthorizedException"; 

		} else if (response.getStatusLine().getStatusCode() != 200) {
			System.out.println("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
		} else{
			System.out.println("Execute Successfully:" + response.getStatusLine().getStatusCode());
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		String output,output1="";
		
		while ((output = br.readLine()) != null) {
			output1 = output1 + output;
		}
		
		System.out.println(output1);
		br.close();
		
		return output1;
		
	}
	
	//delete
	private  String processRadiusDelete(String url, String encodePassword) throws ClientProtocolException, IOException{
		
		 HttpClient httpClient = new DefaultHttpClient();
		 HttpDelete deleteRequest = new HttpDelete(url);
		 deleteRequest.setHeader("Authorization", "Basic " +encodePassword);
		 deleteRequest.setHeader("Content-Type", "application/json");
		 HttpResponse response=httpClient.execute(deleteRequest);
		 
		 if (response.getStatusLine().getStatusCode() == 404) {
				return "ResourceNotFoundException";

			} else if (response.getStatusLine().getStatusCode() == 401) {	
				return "UnauthorizedException"; 

			} else if (response.getStatusLine().getStatusCode() != 200) {
				System.out.println("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			} else{
				System.out.println("Execute Successfully:" + response.getStatusLine().getStatusCode());
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			String output,output1="";
			
			while ((output = br.readLine()) != null) {
				output1 = output1 + output;
			}
			br.close();
			return output1;
		 
		}

	@Override
	public List<RadiusServiceData> retrieveRadServiceTemplateData() {
		
		try {
			/*JobParameterData data = this.sheduleJobReadPlatformService.getJobParameters(JobName.RADIUS.toString());
			if(data == null){
				throw new RadiusDetailsNotFoundException();
			}
			String url ="";
			url= data.getUrl() + "raduser2/template";
			String credentials = data.getUsername().trim() + ":" + data.getPassword().trim();
			byte[] encoded = Base64.encodeBase64(credentials.getBytes());
			String encodedPassword = new String(encoded);
			String radServiceTemplateData = this.processRadiusGet(url, encodedPassword);
			return radServiceTemplateData;*/
			



			
			ServiceDetailsMapper mapper = new ServiceDetailsMapper();

			String sql = "select " + mapper.schema();

			return this.jdbcTemplate.query(sql, mapper, new Object[] {});

		

			
		} catch (EmptyResultDataAccessException e) {
			return null;
		} 
	}
	
	private static final class ServiceDetailsMapper implements RowMapper<RadiusServiceData> {

		public String schema() {
			return " rs.srvid as id,rs.srvname as serviceName from rm_services rs;";

		}

		@Override
		public RadiusServiceData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String serviceName = rs.getString("serviceName");
			return new RadiusServiceData(id,serviceName);

		}
	}

}

