package org.omnione.did.wallet.data.rest;

import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;
import org.omnione.did.wallet.util.GDPBase64;

/**
 * SP 결과 정보
 * 
 * @author tykim
 *
 */
public class ResultSpProfile extends ResultJson {

	/**
	 * SP Profile 정보(Base64)
	 */
	private String spProfileBase64;

	public String getSpProfileBase64() {
		return spProfileBase64;
	}

	public void setSpProfileBase64(String spProfileBase64) {
		this.spProfileBase64 = spProfileBase64;
	}

	public void setSpProfileJson(String spProfileJson) {
		try {
			this.spProfileBase64 = GDPBase64.encodeUrlString(spProfileJson.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "ResultSpProfile [spProfileBase64=" + spProfileBase64 + ", isResult()=" + isResult() + ", getCode()=" + getCode() + ", getMessage()=" + getMessage() + ", getErrorMsg()=" + getErrorMsg() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	
	@Override
	public void fromJson(String val) {
		super.fromJson(val);
		Gson gson = new Gson();
		ResultSpProfile obj = gson.fromJson(val, ResultSpProfile.class);
		spProfileBase64 = obj.getSpProfileBase64();
	}

}
