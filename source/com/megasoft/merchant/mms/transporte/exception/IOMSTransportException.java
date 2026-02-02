package com.megasoft.merchant.mms.transporte.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class IOMSTransportException extends MSTransportException {

	private static final long serialVersionUID = 2681486573513956881L;
	
	public IOMSTransportException() {
		super("ErrorIO en la libreria interna");
	}
	
	public IOMSTransportException(Exception e) {
		super("ErrorIO en la libreria interna", e);
	}
	
	public IOMSTransportException(String mensaje) {
		super(mensaje);
	}
	
	public IOMSTransportException(String mensaje, Exception e) {
		super(mensaje,e);
	}
	
	public IOMSTransportException(String mensaje, Throwable e) {
		super(mensaje,e);
	}
}
