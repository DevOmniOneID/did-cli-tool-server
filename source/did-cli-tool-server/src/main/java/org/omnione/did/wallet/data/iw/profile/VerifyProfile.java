package org.omnione.did.wallet.data.iw.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class VerifyProfile extends AbstractProfile {

	@SerializedName("profile")
	@Expose
	protected VerifyInnerProfile verifyInnerProfile;

	public VerifyInnerProfile getVerifyInnerProfile() {
		return verifyInnerProfile;
	}

	public void setVerifyInnerProfile(VerifyInnerProfile verifyInnerProfile) {
		this.verifyInnerProfile = verifyInnerProfile;
	}

	@Override
	public void fromJson(String val) {
		super.fromJson(val);

		GsonWrapper gson = new GsonWrapper();
		VerifyProfile obj = gson.fromJson(val, VerifyProfile.class);
		verifyInnerProfile = obj.verifyInnerProfile;
	}
}
