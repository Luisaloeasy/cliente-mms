package com.megasoft.merchant.mms.persistence.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class InvalidDataMSBDException extends MSBDException {

	private static final long serialVersionUID = -3080506901109981996L;

	public InvalidDataMSBDException(String error) {
		super(error);
	}
	
	public InvalidDataMSBDException(String error, Throwable e) {
		super(error,e);
	}

}
