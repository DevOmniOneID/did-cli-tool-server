package org.omnione.did.wallet.data.iw.profile.result;

import java.util.List;

import org.omnione.did.wallet.data.iw.profile.result.VCVerifyProfileResult;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class ReqVPProfileResult extends VCVerifyProfileResult{

	private List<String> privacy;

	public List<String> getPrivacy() {
		return privacy;
	}

	public void setPrivacy(List<String> privacy) {
		this.privacy = privacy;
	}
	
	@Override
	public void fromJson(String val) {
		super.fromJson(val);
		GsonWrapper gson = new GsonWrapper();
		ReqVPProfileResult obj = gson.fromJson(val, ReqVPProfileResult.class);
		privacy = obj.privacy;
	}
}
