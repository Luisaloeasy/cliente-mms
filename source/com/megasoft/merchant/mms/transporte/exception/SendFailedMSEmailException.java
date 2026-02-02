package com.megasoft.merchant.mms.transporte.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class SendFailedMSEmailException extends MSEmailException {

	private static final long serialVersionUID = 3957454938193069598L;

	public SendFailedMSEmailException(String error) {
		super(error);
	}
	
	public SendFailedMSEmailException(String error, Exception e) {
		super(error,e);
	}

}
