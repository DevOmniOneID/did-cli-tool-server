package org.omnione.did.wallet.exception;

public class IWException extends IWCommonException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2584536544817080786L;

	public IWException(int code) {
		super(IWErrorCode.getEnumByCode(code));
	}
	
	public IWException(int code, String msg) {
		super(code, msg);
	}
	
	
	public IWException(int code, Throwable throwable)  {
		super(IWErrorCode.getEnumByCode(code), throwable);
	}
	
	public IWException(IWErrorCode iwErrorCode)  {
		super(iwErrorCode);
	}
	
	public IWException(IWErrorCode iwErrorCode, Throwable throwable)  {
		super(iwErrorCode, throwable);
	}


	public int getStatusCode() {
		return errorCode;
	}

	@Override
	public int getErrorCode(){		
		return errorCode;
	}

	@Override
	public String getErrorReason() {
		return errorReason;
	}
}
