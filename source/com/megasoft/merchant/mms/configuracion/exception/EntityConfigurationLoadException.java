package com.megasoft.merchant.mms.configuracion.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class EntityConfigurationLoadException extends MSConfException{

	private static final long serialVersionUID = 7654739437433902866L;

	public EntityConfigurationLoadException(String error) {
		super(error);
	}

	public EntityConfigurationLoadException(String error, Throwable e) {
		super(error,e);
	}
	
}
