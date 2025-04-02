package org.omnione.did.wallet.data.rest;

import com.google.gson.Gson;
import org.omnione.did.wallet.data.IWObject;

/**
 * 결과 정보
 * @author tykim
 *
 */
public class ResultJson extends IWObject{

	/**
	 * 성공여부 , 성공 = true, 실패 = false
	 */
	private boolean result = false;

	/**
	 * 에러 코드
	 */
	private int code = ErrorEnum.NONE.getCode();
	
	/**
	 * 에러 코드 성명
	 */
	private String message = ErrorEnum.NONE.getDesc();
	
	/**
	 * 에러 추가 정보 
	 */
	private String errorMsg;

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
		if(result) {
			this.code = ErrorEnum.SUCCESS.getCode();
			this.message = ErrorEnum.SUCCESS.getDesc();
		}
	}

	public int getCode() {
		return code;
	}

	public void setErrorCode(int errorCode) {
		this.code = errorCode;
	}
	
	public void setErrorCode(ErrorEnum errorEnum) {
		this.result = false;
		this.code = errorEnum.getCode();
		this.message = errorEnum.getDesc();
	}

	public String getMessage() {
		return message;
	}

	public void setErrorCodeDesc(String errorCodeDesc) {
		this.message = errorCodeDesc;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public void fromJson(String val) {
		Gson gson = new Gson();
		ResultJson obj = gson.fromJson(val, ResultJson.class);
		result = obj.isResult();
		code = obj.getCode();
		message = obj.getMessage();
		errorMsg = obj.getErrorMsg();
	}
	

}
