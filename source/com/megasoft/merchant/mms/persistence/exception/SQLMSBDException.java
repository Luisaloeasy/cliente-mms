package com.megasoft.merchant.mms.persistence.exception;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class SQLMSBDException extends MSBDException {

	private static final long serialVersionUID = -5890353700386817670L;

	public SQLMSBDException(String error) {
		super(error);
	}

	public SQLMSBDException(String error, Throwable e) {
		super(error,e);
	}
}
