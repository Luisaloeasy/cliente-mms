package com.megasoft.merchant.mms.configuracion.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class FileNotFoundMSConfException extends MSConfException{

	private static final long serialVersionUID = -7056868051096876514L;

	public FileNotFoundMSConfException(String error) {
		super(error);
	}
	
	public FileNotFoundMSConfException(String error, Exception e) {
		super(error, e);
	}

}
