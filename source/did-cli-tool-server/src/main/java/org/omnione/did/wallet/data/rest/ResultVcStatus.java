package org.omnione.did.wallet.data.rest;

import com.google.gson.Gson;

/**
 * VC 결과 정보
 * 
 * @author tykim
 *
 */
public class ResultVcStatus extends ResultJson {

	private String vcStatus;

	private String vcId;

	public String getVcStatus() {
		return vcStatus;
	}

	public void setVcStatus(String vcStatus) {
		this.vcStatus = vcStatus;
	}

	public String getVcId() {
		return vcId;
	}

	public void setVcId(String vcId) {
		this.vcId = vcId;
	}

	@Override
	public void fromJson(String val) {
		super.fromJson(val);
		Gson gson = new Gson();
		ResultVcStatus obj = gson.fromJson(val, ResultVcStatus.class);
		vcStatus = obj.getVcStatus();
		vcId = obj.getVcId();
	}

}
