package com.megasoft.merchant.mms.comunicacion.exception;

/**
 * Excepcion padre de las excepciones procedentes del paquete de comunicacion del sistema MMS
 * @author Christian De Sousa
 *
 */
public class MSComunicacionException extends Exception{
	
	private static final long serialVersionUID = -7701633974214029371L;
	
	public  MSComunicacionException(String msg) {
        super(msg);
    }

    public  MSComunicacionException(String msg, Throwable cause) {
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
