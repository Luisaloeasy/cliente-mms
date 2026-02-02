package com.megasoft.merchant.mms.comunicacion.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class ConfigurationLoadErrorMSComunicacionException extends
		MSComunicacionException {

	private static final long serialVersionUID = -4556608917454320578L;

	public ConfigurationLoadErrorMSComunicacionException(String error) {
		super(error);
	}
	
	public ConfigurationLoadErrorMSComunicacionException(String error, Exception e) {
		super(error, e);
	}
}
