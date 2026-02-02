package com.megasoft.merchant.mms.tarea.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class ConfigurationErrorTareaException extends TareaException{

	private static final long serialVersionUID = -4507922359751689857L;

	public ConfigurationErrorTareaException(String error, Exception e) {
		super(error, e);
	}

}