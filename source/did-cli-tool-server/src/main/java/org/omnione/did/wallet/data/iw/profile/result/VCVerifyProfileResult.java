package org.omnione.did.wallet.data.iw.profile.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class VCVerifyProfileResult extends ProfileResult {

	@SerializedName("vcId")
	@Expose
	private String vcId;

	public String getVcId() {
		return vcId;
	}

	public void setVcId(String vcId) {
		this.vcId = vcId;
	}

	@Override
	public void fromJson(String val) {
		super.fromJson(val);

		GsonWrapper gson = new GsonWrapper();
		VCVerifyProfileResult obj = gson.fromJson(val, VCVerifyProfileResult.class);
		vcId = obj.vcId;
	}

}
