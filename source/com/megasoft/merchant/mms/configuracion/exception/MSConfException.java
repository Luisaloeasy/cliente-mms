package com.megasoft.merchant.mms.configuracion.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class MSConfException extends Exception{

	private static final long serialVersionUID = 4775284622608614253L;

	public  MSConfException(String msg) {
        super(msg);
    }

    public  MSConfException(String msg, Throwable cause) {
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

