package com.megasoft.merchant.mms.transporte.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class MSEmailException extends MSTransportException{

	private static final long serialVersionUID = -4721794739973683692L;

	public MSEmailException(String error, Exception e){
		super(error,e);
	}

	public MSEmailException( String error) {
		super(error);
	}
}
