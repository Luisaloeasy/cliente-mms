package com.megasoft.merchant.mms.transporte.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class BadDataMSEmailException extends MSEmailException {

	private static final long serialVersionUID = 8507401438440381599L;

	public BadDataMSEmailException(String error) {
		super(error);
	}
	
	public BadDataMSEmailException(String error, Exception e) {
		super(error, e);
	}

}
