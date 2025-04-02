package org.omnione.did.wallet.exception;

/**
 * IW Exception - 확장가능
 * 
 * @author tykim
 *
 */
public class IWCommonException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6712733907628438694L;

	/**
	 * Error Code - int 범위에서 사용하셔야 합니다.
	 */
	protected int errorCode;

	/**
	 * Error 코드 메시지
	 */
	protected String errorMsg;

	/**
	 * Error 발생 사유
	 */
	protected String errorReason;

	public IWCommonException(IWErrorEnumInterface iwErrorEnum) {
		super("ErrorCode: " + iwErrorEnum.getCode() + ", Message: " + iwErrorEnum.getMsg());
		this.errorCode = iwErrorEnum.getCode();
		this.errorReason = iwErrorEnum.getMsg();
	}

	public IWCommonException(IWErrorEnumInterface iwErrorEnum, Throwable throwable) {
		super("ErrorCode: " + iwErrorEnum.getCode() + ", Message: " + iwErrorEnum.getMsg(), throwable);
		this.errorCode = iwErrorEnum.getCode();
		this.errorMsg = iwErrorEnum.getMsg();
	}

	public IWCommonException(IWErrorEnumInterface iwErrorEnum, String errorReason) {
		super("ErrorCode: " + iwErrorEnum.getCode() + ", Message: " + iwErrorEnum.getMsg() + ", Reason: " + errorReason);
		this.errorCode = iwErrorEnum.getCode();
		this.errorMsg = iwErrorEnum.getMsg();
		this.errorReason = errorReason;

	}

	public IWCommonException(IWErrorEnumInterface iwErrorEnum, String errorReason, Throwable throwable) {
		super("ErrorCode: " + iwErrorEnum.getCode() + ", Message: " + iwErrorEnum.getMsg() + ", Reason: " + errorReason, throwable);
		this.errorCode = iwErrorEnum.getCode();
		this.errorMsg = iwErrorEnum.getMsg();
		this.errorReason = errorReason;
	}

	public IWCommonException(int errorCode, String errorMsg) {
		super("ErrorCode: " + errorCode + ", Message: " + errorMsg);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public IWCommonException(int errorCode, String errorMsg, Throwable throwable) {
		super("ErrorCode: " + errorCode + ", Message: " + errorMsg, throwable);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public IWCommonException(int errorCode, String errorMsg, String errorReason) {
		super("ErrorCode: " + errorCode + ", Message: " + errorMsg + ", Reason: " + errorReason);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
		this.errorReason = errorReason;
	}

	public IWCommonException(int errorCode, String errorMsg, String errorReason, Throwable throwable) {
		super("ErrorCode: " + errorCode + ", Message: " + errorMsg + ", Reason: " + errorReason, throwable);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
		this.errorReason = errorReason;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

}
