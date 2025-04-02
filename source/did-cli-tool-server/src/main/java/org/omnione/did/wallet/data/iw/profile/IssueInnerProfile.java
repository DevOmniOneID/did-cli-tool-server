package org.omnione.did.wallet.data.iw.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class IssueInnerProfile extends Profile {

	@SerializedName("vcType")
	@Expose
	protected VCType vcType;
	
	public IssueInnerProfile() {
		setType("ISSUE");
	}

	public VCType getVCType() {
		return vcType;
	}

	public void setVCType(VCType vcType) {
		this.vcType = vcType;
	}

	@Override
	public void fromJson(String val) {
		super.fromJson(val);

		GsonWrapper gson = new GsonWrapper();
		IssueInnerProfile obj = gson.fromJson(val, IssueInnerProfile.class);
		vcType = obj.vcType;
	}

}
