package org.omnione.did.wallet.data.rest;

import com.google.gson.Gson;
import org.omnione.did.wallet.data.iw.profile.EncryptTypeEnum;

/**
 *VC 복원 결과 정보
 * 
 * @author crHam
 *
 */
public class ResultVcRestore extends ResultVc {

	private String nonce;

	private Integer encryptType = EncryptTypeEnum.NONE.getVal();

	private String publicKey;

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
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
	public String toString() {
		return "ResultVcBackup [claimBase64=" + super.getClaimBase64() + ", claim=" + super.getClaim()
				+ ", encryptType=" + encryptType + ", nonce=" + nonce + ", publicKey=" + publicKey + ", isResult()="
				+ isResult() + ", getCode()=" + getCode() + ", getMessage()=" + getMessage() + ", getErrorMsg()="
				+ getErrorMsg() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

	@Override
	public void fromJson(String val) {
		super.fromJson(val);
		Gson gson = new Gson();
		ResultVcRestore obj = gson.fromJson(val, ResultVcRestore.class);
		nonce = obj.getNonce();
		encryptType = obj.getEncryptType();
		publicKey = obj.getPublicKey();
	}

}
