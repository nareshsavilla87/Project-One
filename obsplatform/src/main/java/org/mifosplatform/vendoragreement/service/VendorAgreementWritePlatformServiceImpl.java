package org.mifosplatform.vendoragreement.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.vendoragreement.data.VendorAgreementData;
import org.mifosplatform.vendoragreement.domain.VendorAgreement;
import org.mifosplatform.vendoragreement.domain.VendorAgreementDetail;
import org.mifosplatform.vendoragreement.domain.VendorAgreementDetailRepository;
import org.mifosplatform.vendoragreement.domain.VendorAgreementRepository;
import org.mifosplatform.vendoragreement.exception.VendorNotFoundException;
import org.mifosplatform.vendoragreement.serialization.VendorAgreementCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

	
@Service
public class VendorAgreementWritePlatformServiceImpl implements VendorAgreementWritePlatformService{
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(VendorAgreementWritePlatformServiceImpl.class);	
	
	private PlatformSecurityContext context;
	private VendorAgreementRepository vendorRepository; 
	private VendorAgreementCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final FromJsonHelper fromApiJsonHelper;
	private final VendorAgreementDetailRepository vendorDetailRepository;
	 
	@Autowired
	public VendorAgreementWritePlatformServiceImpl(final PlatformSecurityContext context, 
			final VendorAgreementRepository vendorRepository, 
			final VendorAgreementCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final FromJsonHelper fromApiJsonHelper,
			final VendorAgreementDetailRepository vendorDetailRepository) {
		this.context = context;
		this.vendorRepository = vendorRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.vendorDetailRepository = vendorDetailRepository;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult createVendorAndLoyaltyCalucation(
			JsonCommand command) throws JSONException, IOException {
		
		try{
			
		 this.context.authenticatedUser();
		// this.fromApiJsonDeserializer.validateForCreate(command.json());
		 JSONObject object = new JSONObject(command.json());
		 final VendorAgreementData vendorData = (VendorAgreementData)object.get("vendorAgreementData");
		 
		 String fileLocation=null;
		 fileLocation = FileUtils.saveToFileSystem(vendorData.getInputStream(), vendorData.getFileUploadLocation(),vendorData.getFileName());
			
		 final VendorAgreement vendor=VendorAgreement.fromJson(command);
		 
		 
		 final JsonArray vendorDetailsArray = command.arrayOfParameterNamed("vendorDetails").getAsJsonArray();
			String[] vendorDetails = null;
			vendorDetails = new String[vendorDetailsArray.size()];
			
			for(int i = 0; i < vendorDetailsArray.size(); i++){
				vendorDetails[i] = vendorDetailsArray.get(i).toString();
			}
			
			//For VendorDetails
			for (final String vendorDetailsData : vendorDetails) {
							 
				final JsonElement element = fromApiJsonHelper.parse(vendorDetailsData);
				
				final String contentCode = fromApiJsonHelper.extractStringNamed("contentCode", element);
				final String loyaltyType = fromApiJsonHelper.extractStringNamed("loyaltyType", element);
				final BigDecimal loyaltyShare = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("loyaltyShare", element);
				final Long priceRegion = fromApiJsonHelper.extractLongNamed("priceRegion", element);
				final BigDecimal contentCost = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("contentCost", element);
				
				final VendorAgreementDetail vendorDetail = new VendorAgreementDetail(contentCode, loyaltyType, loyaltyShare, priceRegion, contentCost);
				vendor.addVendorDetails(vendorDetail);
			}		 
			
			this.vendorRepository.save(vendor);
			return new CommandProcessingResult(vendor.getId());
		} catch (DataIntegrityViolationException dve) {
			 handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
	

	private void handleCodeDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {

		final Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("uvendor_code_key")) {
			final String vendorCode = command.stringValueOfParameterNamed("vendorCode");
			throw new PlatformDataIntegrityException("error.msg.vendor.code.duplicate", "A code with name '" + vendorCode + "' already exists");
		} else if (realCause.getMessage().contains("uvendor_mobileno_key")) {
			final String vendormobileNo = command.stringValueOfParameterNamed("vendormobileNo");
			throw new PlatformDataIntegrityException("error.msg.vendor.mobileno.duplicate", "A code with name '" + vendormobileNo + "' already exists");
		} else if (realCause.getMessage().contains("uvendor_telephoneno_key")) {
			final String vendorTelephoneNo = command.stringValueOfParameterNamed("vendorTelephoneNo");
			throw new PlatformDataIntegrityException("error.msg.vendor.telephoneno.duplicate", "A code with name '" + vendorTelephoneNo + "' already exists");
		} else if (realCause.getMessage().contains("uvendor_emailid_key")) {
			final String vendorEmailId = command.stringValueOfParameterNamed("vendorEmailId");
			throw new PlatformDataIntegrityException("error.msg.vendor.emailid.duplicate", "A code with name '" + vendorEmailId + "' already exists");
		}

		LOGGER.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());

	}

	@Override
	public CommandProcessingResult updateUser(Long vendorId, JsonCommand command) {
		
		this.context.authenticatedUser();
		this.fromApiJsonDeserializer.validateForCreate(command.json());
		VendorAgreement vendor=retrieveCodeBy(vendorId);
		
		final Map<String, Object> changes = vendor.update(command);
		 
		 final JsonArray vendorDetailsArray = command.arrayOfParameterNamed("vendorDetails").getAsJsonArray();
		 final JsonArray removevendorDetailsArray = command.arrayOfParameterNamed("removeVendorDetails").getAsJsonArray();
		
		 String[] vendorDetails = new String[vendorDetailsArray.size()];
		
		 for(int i = 0; i < vendorDetailsArray.size(); i++){
			 vendorDetails[i] = vendorDetailsArray.get(i).toString();
		 }
		 
		 for (final String vendorDetailsData : vendorDetails) {
						 
			final JsonElement element = fromApiJsonHelper.parse(vendorDetailsData);
			
			final Long vendorDetailId = fromApiJsonHelper.extractLongNamed("id", element);
			final String contentCode = fromApiJsonHelper.extractStringNamed("contentCode", element);
			final String loyaltyType = fromApiJsonHelper.extractStringNamed("loyaltyType", element);
			final BigDecimal loyaltyShare = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("loyaltyShare", element);
			final Long priceRegion = fromApiJsonHelper.extractLongNamed("priceRegion", element);
			final BigDecimal contentCost = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("contentCost", element);
			
			if(vendorDetailId != null){
				VendorAgreementDetail vendordetail =this.vendorDetailRepository.findOne(vendorDetailId);
				vendordetail.setContentCode(contentCode);
				vendordetail.setLoyaltyType(loyaltyType);
				vendordetail.setLoyaltyShare(loyaltyShare);
				vendordetail.setPriceRegion(priceRegion);
				if("NONE".equalsIgnoreCase(loyaltyType)){
					vendordetail.setContentCost(contentCost);
				}else{
					vendordetail.setContentCost(null);
				}
				this.vendorDetailRepository.saveAndFlush(vendordetail);
 				
			}else{
				
				final VendorAgreementDetail vendordetail = new VendorAgreementDetail(contentCode, loyaltyType, loyaltyShare, priceRegion, contentCost);
				vendor.addVendorDetails(vendordetail);
			}

		 }	
		 
		 if(removevendorDetailsArray.size() != 0){
				 
				String[] removedvendorDetails = new String[removevendorDetailsArray.size()];
	 			
	 			 for(int i = 0; i < removevendorDetailsArray.size(); i++){
	 				removedvendorDetails[i] = removevendorDetailsArray.get(i).toString();
	 			 }
	 			 
	 			 for (final String removedvendorDetailsData : removedvendorDetails) {
	 							 
	 				final JsonElement element = fromApiJsonHelper.parse(removedvendorDetailsData);
	 				final Long vendorDetailId = fromApiJsonHelper.extractLongNamed("id", element);
	 				final String contentCode = fromApiJsonHelper.extractStringNamed("contentCode", element);
		 			
	 				if(vendorDetailId != null){
	 					VendorAgreementDetail vendorDetail =this.vendorDetailRepository.findOne(vendorDetailId);
	 					vendorDetail.setContentCode(contentCode+"_"+vendorDetailId+"_"+"Y");
	 	 				vendorDetail.setIsDeleted("Y");
	 	 				vendorDetailRepository.saveAndFlush(vendorDetail);
	 				}	
	 			 }	
			 }
		
		this.vendorRepository.save(vendor);
		return new CommandProcessingResultBuilder() //
	       .withCommandId(command.commandId()) //
	       .withEntityId(vendorId) //
	       .with(changes) //
	       .build();
	}
	
	private VendorAgreement retrieveCodeBy(final Long vendorId) {
        final VendorAgreement vendor = this.vendorRepository.findOne(vendorId);
        if (vendor == null) { throw new VendorNotFoundException(vendorId.toString()); }
        return vendor;
    }

}
