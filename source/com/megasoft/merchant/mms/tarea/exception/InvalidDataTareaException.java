package com.megasoft.merchant.mms.tarea.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class InvalidDataTareaException extends TareaException{

	private static final long serialVersionUID = -6002792936469939020L;

	public InvalidDataTareaException(String error) {
		super(error);
	}
	
	public InvalidDataTareaException(String error, Exception e) {
		super(error, e);
	}

}

