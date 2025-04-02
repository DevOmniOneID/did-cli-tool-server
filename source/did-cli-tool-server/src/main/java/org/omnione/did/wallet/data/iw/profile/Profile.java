package org.omnione.did.wallet.data.iw.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class Profile extends IWObject {

	/**
	 * Profile Type: ISSUE, VERIFY, DATA_HUB, DID_AUTH
	 */
	@SerializedName("type")
	@Expose
	private String type; // ISSUE, VERIFY, DATA_HUB, DID_AUTH

	/**
	 * 응답 제출 URL 
	 */
	@SerializedName("callBackUrl")
	@Expose
	private String callBackUrl;

	@SerializedName("nonce")
	@Expose
	private String nonce;

	/**
	 * 암호화 타입 EncryptTypeEnum 참조
	 */
	@SerializedName("encryptType")
	@Expose
	private Integer encryptType = EncryptTypeEnum.NONE.getVal();

	/**
	 * DID의 공개키 (암호용) 
	 */
	@SerializedName("publicKey")
	@Expose
	private String publicKey;

	/**
	 * 요청자명 
	 */
	@SerializedName("name")
	@Expose
	private String name;
	
	
	@SerializedName("spName")
	@Expose
	private String spName;

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getSpName() {
		return spName;
	}

	public void setSpName(String spName) {
		this.spName = spName;
	}
	
	public Integer getEncryptType() {
		return encryptType;
	}

	public void setEncryptType(Integer encryptType) {
		this.encryptType = encryptType;
	}

	public void setEncryptType(EncryptTypeEnum encryptTypeEnum) {
		this.encryptType = encryptTypeEnum.getVal();
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	
	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		Profile obj = gson.fromJson(val, Profile.class);
		type = obj.type;
		callBackUrl = obj.callBackUrl;
		nonce = obj.nonce;
		name = obj.name;
		spName = obj.spName;
		encryptType = obj.encryptType;
		publicKey = obj.publicKey;
	}

}
