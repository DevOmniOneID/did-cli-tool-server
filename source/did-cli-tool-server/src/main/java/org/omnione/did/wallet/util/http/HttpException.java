package org.omnione.did.wallet.util.http;

public class HttpException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4965956830237475572L;

	private int httpErrorCode = 0;
	
	private String errorMsg;

	public HttpException(int httpErrorCode, String errorMsg) {
		super(errorMsg);
		this.httpErrorCode = httpErrorCode;
		this.errorMsg = errorMsg;
	}
	
	public HttpException(String errorMsg, Throwable throwable) {
		super(errorMsg, throwable);
		this.errorMsg = errorMsg;
	}


	public int getHttpErrorCode() {
		return httpErrorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
	

}
