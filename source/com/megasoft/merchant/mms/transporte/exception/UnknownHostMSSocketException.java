package com.megasoft.merchant.mms.transporte.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class UnknownHostMSSocketException extends MSSocketException {
	
	private static final long serialVersionUID = 5656887757940879768L;

	public UnknownHostMSSocketException(Exception e) {
		super("Servidor no alcanzable", e);
	}
	
	public UnknownHostMSSocketException() {
		super("Servidor no alcanzable");
	}
}
