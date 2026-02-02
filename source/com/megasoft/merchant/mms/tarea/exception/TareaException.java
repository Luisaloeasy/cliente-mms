package com.megasoft.merchant.mms.tarea.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class TareaException extends Exception{
	
	private static final long serialVersionUID = -7473903587175286164L;

	public  TareaException(String msg) {
        super(msg);
    }

    public  TareaException(String msg, Throwable cause) {
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
