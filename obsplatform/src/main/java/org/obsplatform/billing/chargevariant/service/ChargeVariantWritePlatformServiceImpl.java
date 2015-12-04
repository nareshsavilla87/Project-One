package org.obsplatform.billing.chargevariant.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.obsplatform.billing.chargevariant.domain.ChargeVariant;
import org.obsplatform.billing.chargevariant.domain.ChargeVariantDetails;
import org.obsplatform.billing.chargevariant.domain.ChargeVariantDetailsRepository;
import org.obsplatform.billing.chargevariant.domain.ChargeVariantRepository;
import org.obsplatform.billing.chargevariant.exception.ChargeVariantNotFountException;
import org.obsplatform.billing.chargevariant.serialization.ChargeVariantCommandFromApiJsonDeserializer;
import org.obsplatform.infrastructure.core.api.JsonCommand;
import org.obsplatform.infrastructure.core.data.CommandProcessingResult;
import org.obsplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.obsplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.obsplatform.infrastructure.core.serialization.FromJsonHelper;
import org.obsplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class ChargeVariantWritePlatformServiceImpl implements ChargeVariantWritePlatformService{
	
	 private final static Logger LOGGER = LoggerFactory.getLogger(ChargeVariantWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final ChargeVariantCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final FromJsonHelper fromApiJsonHelper;
	private final ChargeVariantRepository  chargeVariantRepository; 
	private final ChargeVariantDetailsRepository chargeVariantDetailsRepository; 
	
	
@Autowired
public ChargeVariantWritePlatformServiceImpl(final PlatformSecurityContext context,final ChargeVariantCommandFromApiJsonDeserializer  fromApiJsonDeserializer,
		final ChargeVariantRepository chargeVariantRepository,final FromJsonHelper fromApiJsonHelper,final ChargeVariantDetailsRepository chargeVariantDetailsRepository) {

   this.context = context;
   this.fromApiJsonDeserializer = fromApiJsonDeserializer;
   this.chargeVariantRepository = chargeVariantRepository;
   this.fromApiJsonHelper = fromApiJsonHelper;
   this.chargeVariantDetailsRepository = chargeVariantDetailsRepository;
   
}

@Transactional
@Override
public CommandProcessingResult createChargeVariant(JsonCommand command) {
   try{
	   
	   this.context.authenticatedUser();
	   this.fromApiJsonDeserializer.validateForCreate(command.json());
	   
	   ChargeVariant chargeVariant=ChargeVariant.fromJson(command);
	   final JsonArray chargeVariantDetails = command.arrayOfParameterNamed("chargeVariantDetails").getAsJsonArray();
	   chargeVariant=assembleDiscountDetails(chargeVariantDetails,chargeVariant); 
		this.chargeVariantRepository.save(chargeVariant);
		return new CommandProcessingResult(chargeVariant.getId());
	   
   }catch(DataIntegrityViolationException dve){
	   handleCodeDataIntegrityIssues(command,dve);
	   return new CommandProcessingResult(Long.valueOf(-1));
   }
	}

private ChargeVariant assembleDiscountDetails(JsonArray chargeVariantDetailsArray, ChargeVariant chargeVariant) {
	
	String[]  chargeVariantDetails = null;
	chargeVariantDetails = new String[chargeVariantDetailsArray.size()];
	if(chargeVariantDetailsArray.size() > 0){
		for(int i = 0; i < chargeVariantDetailsArray.size(); i++){
			chargeVariantDetails[i] = chargeVariantDetailsArray.get(i).toString();
		}

	for (final String VariantDetail : chargeVariantDetails) {
		Long from=Long.valueOf(0);
		Long  to=Long.valueOf(0);
		final JsonElement element = fromApiJsonHelper.parse(VariantDetail);
		final String variantType = fromApiJsonHelper.extractStringNamed("variantType", element);
		if("Range".equalsIgnoreCase(variantType)){
			from=fromApiJsonHelper.extractLongNamed("from", element);
			to=fromApiJsonHelper.extractLongNamed("to", element);
		}
		final String amountType = fromApiJsonHelper.extractStringNamed("amountType", element);
		final BigDecimal amount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amount", element);
		ChargeVariantDetails chargeVariantDetail = new ChargeVariantDetails(variantType,from,to,amountType,amount);
		chargeVariant.addDetails(chargeVariantDetail);
		
	}	 
}	

return chargeVariant;
}

private void handleCodeDataIntegrityIssues(final JsonCommand command,final DataIntegrityViolationException dve) {
	
	 final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("chargevariantcode")) {
            final String name = command.stringValueOfParameterNamed("chargevariantCode");
            throw new PlatformDataIntegrityException("error.msg.code.duplicate.name", "A code with name '" + name + "' already exists");
        }
        LOGGER.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
}


@Transactional
@Override
public CommandProcessingResult updateChargeVariant(Long entityId,JsonCommand command) {

	   try{
		   this.context.authenticatedUser();
		   this.fromApiJsonDeserializer.validateForCreate(command.json());
		   ChargeVariant chargeVariant=retriebeChargeVariantById(entityId);
		   List<ChargeVariantDetails> details=new ArrayList<>(chargeVariant.getChargeVariantDetails());
			final JsonArray chargeVariantDetailsArray = command.arrayOfParameterNamed("chargeVariantDetails").getAsJsonArray();
			    String[] chargeVariantDetails =null;
			    chargeVariantDetails=new String[chargeVariantDetailsArray.size()];
			    for(int i=0; i<chargeVariantDetailsArray.size();i++){
			    	chargeVariantDetails[i] =chargeVariantDetailsArray.get(i).toString();
			    }
			    for (String VariantDetail : chargeVariantDetails) {
					  
					 final JsonElement element = fromApiJsonHelper.parse(VariantDetail);
						final Long id = fromApiJsonHelper.extractLongNamed("id", element);
						if(id != null){
						ChargeVariantDetails variantDetails =this.chargeVariantDetailsRepository.findOne(id);
						
						if(variantDetails != null){
							variantDetails.update(element,fromApiJsonHelper);
							this.chargeVariantDetailsRepository.saveAndFlush(variantDetails);
							if(details.contains(variantDetails)){
							   details.remove(variantDetails);
							}
						}
						}else {
							Long from=Long.valueOf(0);
							Long  to=Long.valueOf(0);
							final String variantType = fromApiJsonHelper.extractStringNamed("variantType", element);
							if("Range".equalsIgnoreCase(variantType)){
								from=fromApiJsonHelper.extractLongNamed("from", element);
								to=fromApiJsonHelper.extractLongNamed("to", element);
							}
							final String amountType = fromApiJsonHelper.extractStringNamed("amountType", element);
							final BigDecimal amount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amount", element);
							ChargeVariantDetails chargeVariantDetail = new ChargeVariantDetails(variantType,from,to,amountType,amount);
							chargeVariant.addDetails(chargeVariantDetail);
						}
						
				  }
					 chargeVariant.getChargeVariantDetails().removeAll(details);
					 final Map<String, Object> changes = chargeVariant.update(command);
						this.chargeVariantRepository.saveAndFlush(chargeVariant);
						return new CommandProcessingResultBuilder().withCommandId(command.commandId())
							       .withEntityId(chargeVariant.getId()).with(changes).build();
						
	   }catch(DataIntegrityViolationException dve){
		   handleCodeDataIntegrityIssues(command, dve);
                return new CommandProcessingResult(Long.valueOf(-1));
	   }
}

private ChargeVariant retriebeChargeVariantById(Long entityId) {
	
	ChargeVariant chargeVariant=this.chargeVariantRepository.findOne(entityId);
	if(chargeVariant == null){
		throw new ChargeVariantNotFountException(entityId);
	}
	return chargeVariant;
}

@Override
public CommandProcessingResult deleteChargeVariant(Long entityId) {
	
	try{
		this.context.authenticatedUser();
		ChargeVariant chargeVariant=retriebeChargeVariantById(entityId);
		chargeVariant.delete();
	}catch(DataIntegrityViolationException dve){
		handleCodeDataIntegrityIssues(null, dve);
	}
	// TODO Auto-generated method stub
	return null;
}


}
