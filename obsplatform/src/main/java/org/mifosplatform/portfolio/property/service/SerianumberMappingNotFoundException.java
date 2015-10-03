package org.mifosplatform.portfolio.property.service;
import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class SerianumberMappingNotFoundException extends AbstractPlatformResourceNotFoundException  {
	
	private static final long serialVersionUID = 1L;
	
	public SerianumberMappingNotFoundException (final String serialNumber,final String oldPropertyCode)
	{
		super("error.msg.serianumber.isnot.mapped", serialNumber + "Please map the device with proprtycode: "+ oldPropertyCode);
	}

}
