package org.omnione.did.wallet.key;

import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class IWAppResult extends IWObject {
	
	private boolean result = false;
	private String resultMsg;
	private String transactionMsgBase64;
	private String errorEnum;
	private String claimBase64;
	private String didDocumentBase64;
	private String spProfileBase64;
	
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getResultMsg() {
		return resultMsg;
	}
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
	public String getTransactionMsgBase64() {
		return transactionMsgBase64;
	}
	public void setTransactionMsgBase64(String transactionMsgBase64) {
		this.transactionMsgBase64 = transactionMsgBase64;
	}
	public String getErrorEnum() {
		return errorEnum;
	}
	public void setErrorEnum(String errorEnum) {
		this.errorEnum = errorEnum;
	}
	public String getClaimBase64() {
		return claimBase64;
	}
	public void setClaimBase64(String claimBase64) {
		this.claimBase64 = claimBase64;
	}
	public String getDidDocumentBase64() {
		return didDocumentBase64;
	}
	public void setDidDocumentBase64(String didDocumentBase64) {
		this.didDocumentBase64 = didDocumentBase64;
	}
	public String getSpProfileBase64() {
		return spProfileBase64;
	}
	public void setSpProfileBase64(String spProfileBase64) {
		this.spProfileBase64 = spProfileBase64;
	}
	
	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		IWAppResult obj = gson.fromJson(val, IWAppResult.class);
		result = obj.result;
		resultMsg = obj.resultMsg;
		transactionMsgBase64 = obj.transactionMsgBase64;
		errorEnum = obj.errorEnum;
		claimBase64 = obj.claimBase64;
		didDocumentBase64 = obj.didDocumentBase64;
		spProfileBase64 = obj.spProfileBase64;
	}
}
