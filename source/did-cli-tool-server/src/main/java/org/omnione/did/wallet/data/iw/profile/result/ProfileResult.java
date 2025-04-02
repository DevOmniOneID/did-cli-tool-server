package org.omnione.did.wallet.data.iw.profile.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.data.iw.profile.EncryptTypeEnum;
import org.omnione.did.wallet.util.json.GsonWrapper;

/**
 * Profile 결과 데이터 
 * @author kimtayoon
 *
 */
public class ProfileResult extends IWObject {

	/**
	 * 요청자 DID 
	 */
	@SerializedName("did")
	@Expose
	private String did;

	/**
	 * 암호화타입 EncryptTypeEnum 값을 참조 하세요 
	 */
	@SerializedName("encryptType")
	@Expose
	private Integer encryptType = EncryptTypeEnum.NONE.getVal();

	/**
	 * 요청 데이터, encryptType==1 plaintext, encryptType!=1 암호된 byte[] Hex값
	 */
	@SerializedName("data")
	@Expose
	private String data;

	@SerializedName("nonce")
	@Expose
	private String nonce;
	
	@SerializedName("publicKey")
	@Expose
	private String publicKey;

	/**
	 * Profile Type: ISSUE, VERIFY, DATA_HUB, DID_AUTH
	 */
	@SerializedName("type")
	@Expose
	private String type;

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public Integer getEncryptType() {
		return encryptType;
	}

	public void setEncryptType(Integer encryptType) {
		this.encryptType = encryptType;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
		ProfileResult obj = gson.fromJson(val, ProfileResult.class);
		did = obj.did;
		encryptType = obj.encryptType;
		data = obj.data;
		nonce = obj.nonce;
		type = obj.type;
		publicKey = obj.publicKey;
	}

}
