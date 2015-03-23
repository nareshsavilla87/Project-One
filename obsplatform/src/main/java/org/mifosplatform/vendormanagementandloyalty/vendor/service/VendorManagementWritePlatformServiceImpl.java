/*package org.mifosplatform.vendormanagementandloyalty.vendor.service;

public class VendorManagementWritePlatformServiceImpl {

}
*/
package org.mifosplatform.vendormanagementandloyalty.vendor.service;

import java.math.BigDecimal;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.vendormanagementandloyalty.vendor.domain.Vendor;
import org.mifosplatform.vendormanagementandloyalty.vendor.domain.VendorDetail;
import org.mifosplatform.vendormanagementandloyalty.vendor.domain.VendorRepository;
import org.mifosplatform.vendormanagementandloyalty.vendor.serialization.VendorManagementCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

	
@Service
public class VendorManagementWritePlatformServiceImpl implements VendorManagementWritePlatformService{
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(VendorManagementWritePlatformServiceImpl.class);	
	
	private PlatformSecurityContext context;
	private VendorRepository vendorRepository; 
	private VendorManagementCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final FromJsonHelper fromApiJsonHelper;
	 
	@Autowired
	public VendorManagementWritePlatformServiceImpl(final PlatformSecurityContext context, 
			final VendorRepository vendorRepository, 
			final VendorManagementCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final FromJsonHelper fromApiJsonHelper) {
		this.context = context;
		this.vendorRepository = vendorRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.fromApiJsonHelper = fromApiJsonHelper;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult createVendorAndLoyaltyCalucation(
			JsonCommand command) {
		
		try{
			
		 this.context.authenticatedUser();
		 this.fromApiJsonDeserializer.validateForCreate(command.json());
		 final Vendor vendor=Vendor.fromJson(command);
		 
		 
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
				
				final VendorDetail mediaassetLocation = new VendorDetail(contentCode, loyaltyType, loyaltyShare, priceRegion, contentCost);
				vendor.addMediaLocations(mediaassetLocation);
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

}
