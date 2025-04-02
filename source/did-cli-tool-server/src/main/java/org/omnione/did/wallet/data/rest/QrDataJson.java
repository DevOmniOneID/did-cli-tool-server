package org.omnione.did.wallet.data.rest;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.data.iw.profile.EncryptTypeEnum;

public class QrDataJson extends IWObject {

	/**
	 * VC, DID_AUTH
	 */
	@Expose
	private String type;

	@Expose
	private String profileUrl;
	
	@SerializedName("sp_did")
	@Expose
	private String spDid;
	
	@SerializedName("service_code")
	@Expose
	private String serviceCode;
	
	/**
	 * 응답 제출 URL 
	 */
	@SerializedName("callback_url")
	@Expose
	private String callBackUrl;

	@SerializedName("nonce")
	@Expose
	private String nonce;
	
	/**
	 * 암호화 타입 EncryptTypeEnum 참조
	 */
	@SerializedName("encrypt_type")
	@Expose
	private Integer encryptType;
	
	
	@SerializedName("sessionId")
	@Expose
	private String sessionId;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}
	
	

	public String getSpDid() {
		return spDid;
	}

	public void setSpDid(String spDid) {
		this.spDid = spDid;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public Integer getEncryptType() {
		return encryptType;
	}

	public void setEncryptType(EncryptTypeEnum encryptType) {
		this.encryptType = encryptType.getVal();
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public void fromJson(String val) {
		Gson gson = new Gson();
		QrDataJson obj = gson.fromJson(val, QrDataJson.class);
		type = obj.getType();
		profileUrl = obj.getProfileUrl();
		spDid = obj.getSpDid();
		serviceCode = obj.getServiceCode();
		nonce = obj.getNonce();
		callBackUrl = obj.getCallBackUrl();
		encryptType = obj.getEncryptType();
		sessionId = obj.getSessionId();


	}

}
