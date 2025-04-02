package org.omnione.did.wallet.data.rest;

import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;
import org.omnione.did.wallet.util.GDPBase64;

/**
 * VC 결과 정보
 * 
 * @author tykim
 *
 */
public class ResultVc extends ResultJson {

	/**
	 * 증명서 정보(base64 인코딩)
	 */
	private String claimBase64;

	private String claim;

	public void setVcJson(String vcJson) {
		this.claim = vcJson;
		try {
			this.claimBase64 = GDPBase64.encodeUrlString(vcJson.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void setEncVcJson(String encVcJson) {
		this.claimBase64 = encVcJson;
	}

	public String getClaimBase64() {
		return claimBase64;
	}

	public void setClaimBase64(String claimBase64) {
		this.claimBase64 = claimBase64;
	}

	public String getClaim() {
		return claim;
	}

	public void setClaim(String claim) {
		this.claim = claim;
	}

	@Override
	public String toString() {
		return "ResultVc [claimBase64=" + claimBase64 + ", claim=" + claim + ", isResult()=" + isResult() + ", getCode()=" + getCode() + ", getMessage()=" + getMessage() + ", getErrorMsg()=" + getErrorMsg() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	
	@Override
	public void fromJson(String val) {
		super.fromJson(val);
		Gson gson = new Gson();
		ResultVc obj = gson.fromJson(val, ResultVc.class);
		claimBase64 = obj.getClaimBase64();
		claim = obj.getClaim();
	}

}
