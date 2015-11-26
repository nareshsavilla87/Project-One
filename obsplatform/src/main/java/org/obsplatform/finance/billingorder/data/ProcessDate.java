package org.obsplatform.finance.billingorder.data;

import org.joda.time.LocalDate;
import org.obsplatform.infrastructure.core.api.JsonCommand;
import org.springframework.stereotype.Service;

@Service
public class ProcessDate {
	
	public LocalDate processDate;
	
	public static LocalDate  fromJson(JsonCommand command){
		return command.localDateValueOfParameterNamed("systemDate");
	}

}
