package com.megasoft.merchant.mms.transporte.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class MSTransportException extends Exception{

	private static final long serialVersionUID = -5283938091429175259L;

	public  MSTransportException(String msg) {
        super(msg);
    }

    public  MSTransportException(String msg, Throwable cause) {
        super(msg,cause);
    }

	public String getMessage() {
	    	Throwable cause = super.getCause();
	    	if (cause == null) {
	            return super.getMessage();
	        } else {
	            return super.getMessage() + " : "+cause.getMessage();
	        }
	}
}
