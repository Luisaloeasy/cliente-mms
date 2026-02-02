package com.megasoft.merchant.mms.transporte.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class EmailLibraryMSEmailException extends MSEmailException {

	private static final long serialVersionUID = 5444607287767511876L;

	public EmailLibraryMSEmailException(String error) {
		super(error);
	}
	
	public EmailLibraryMSEmailException(String error, Exception e) {
		super(error, e);
	}

}
