package com.megasoft.merchant.mms.configuracion.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class EntityConfigurationSaveException extends MSConfException{

	private static final long serialVersionUID = 1127823131151730263L;

	public EntityConfigurationSaveException(String error) {
		super(error);
	}

	public EntityConfigurationSaveException(String error, Exception e) {
		super(error,e);
	}
	
}