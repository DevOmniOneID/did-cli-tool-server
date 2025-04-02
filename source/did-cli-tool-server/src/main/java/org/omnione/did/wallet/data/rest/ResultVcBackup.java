package org.omnione.did.wallet.data.rest;

import java.util.List;

import com.google.gson.Gson;

/**
 * VC 결과 정보(백업 정보 포함)
 * 
 * @author crHam
 *
 */
public class ResultVcBackup extends ResultVc {

	
	/**
	 *  암호화된 VC를 분할한 정보(base64 인코딩)
	 */
	private List<String> splitClaimBase64;
	
	private List<byte[]> splitClaim;
	
	private String nonce;

	public List<String> getSplitClaimBase64() {
		return splitClaimBase64;
	}

	public void setSplitClaimBase64(List<String> splitClaimBase64) {
		this.splitClaimBase64 = splitClaimBase64;
	}
	
	public String getNonce() {
		return nonce;
	}

	public List<byte[]> getSplitClaim() {
		return splitClaim;
	}

	public void setSplitClaim(List<byte[]> splitClaim) {
		this.splitClaim = splitClaim;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	@Override
	public String toString() {
		return "ResultVcBackup [claimBase64=" + super.getClaimBase64() + ", claim=" + super.getClaim() + ", splitClaimBase64=" + splitClaimBase64+ ", nonce=" + nonce+ ", isResult()=" + isResult() + ", getCode()=" + getCode() + ", getMessage()=" + getMessage() + ", getErrorMsg()=" + getErrorMsg() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	
	
	
	@Override
	public void fromJson(String val) {
		super.fromJson(val);
		Gson gson = new Gson();
		ResultVcBackup obj = gson.fromJson(val, ResultVcBackup.class);	
		splitClaimBase64 = obj.getSplitClaimBase64();
	//	splitClaim = obj.getSplitClaim();
		nonce = obj.getNonce();
		
	}

}
