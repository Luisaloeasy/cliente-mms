package com.megasoft.merchant.mms.persistence.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class ConexionMSBDException extends MSBDException{

	private static final long serialVersionUID = -1898835482569680353L;

	public ConexionMSBDException(String error) {
		super(error);
	}
	
	public ConexionMSBDException(String error, Exception e) {
		super(error,e);
	}

}
