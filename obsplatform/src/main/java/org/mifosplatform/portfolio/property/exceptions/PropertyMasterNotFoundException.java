package org.mifosplatform.portfolio.property.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when a code is not found.
 */
@SuppressWarnings("serial")
public class PropertyMasterNotFoundException extends AbstractPlatformResourceNotFoundException {

    /**
     * @param propertyId
     */
    public PropertyMasterNotFoundException(final Long propertyId) {
        super("error.msg.property.not.found", "Property Code with this id"+propertyId+"not exist",propertyId);
        
    }
    
    public PropertyMasterNotFoundException(final Long propertyId,final String status) {
        super("error.msg.please.free.this.property. from assigned client", "please free this property from assigned client");
    }
   
}