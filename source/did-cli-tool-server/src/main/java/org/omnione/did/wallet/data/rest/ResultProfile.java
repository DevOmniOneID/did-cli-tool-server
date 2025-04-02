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
public class ResultProfile extends ResultJson {

	/**
	 * 증명서 정보(base64 인코딩)
	 */
	private String profileBase64;

	public String getProfileBase64() {
		return profileBase64;
	}

	public void setProfileBase64(String profileBase64) {
		this.profileBase64 = profileBase64;
	}

	public void setProfileJson(String profileJson) {
		try {
			this.profileBase64 = GDPBase64.encodeUrlString(profileJson.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "ResultProfile [profileBase64=" + profileBase64 + ", isResult()=" + isResult() + ", getCode()=" + getCode() + ", getMessage()=" + getMessage() + ", getErrorMsg()=" + getErrorMsg() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	
	@Override
	public void fromJson(String val) {
		super.fromJson(val);
		Gson gson = new Gson();
		ResultProfile obj = gson.fromJson(val, ResultProfile.class);
		profileBase64 = obj.getProfileBase64();
	}

}
