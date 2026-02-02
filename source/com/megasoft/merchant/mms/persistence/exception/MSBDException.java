package com.megasoft.merchant.mms.persistence.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class MSBDException extends Exception{

	private static final long serialVersionUID = 2289719655936097474L;

    public  MSBDException(String msg, Throwable cause) {
        super(msg,cause);
    }

	public  MSBDException(String msg) {
        super(msg);
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
