package com.megasoft.merchant.mms.transporte.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class MSSocketException extends MSTransportException{

	private static final long serialVersionUID = -7009373859169442511L;

	public MSSocketException(String error, Exception e){
		super(error,e);
	}

	public MSSocketException(String error) {
		super(error);
	}
}
