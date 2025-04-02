package org.omnione.did.wallet.data.iw.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class IssueProfile extends AbstractProfile {

	@SerializedName("profile")
	@Expose
	protected IssueInnerProfile issueInnerProfile;

	public IssueInnerProfile getIssueInnerProfile() {
		return issueInnerProfile;
	}

	public void setIssueInnerProfile(IssueInnerProfile issueInnerProfile) {
		this.issueInnerProfile = issueInnerProfile;
	}

	@Override
	public void fromJson(String val) {
		super.fromJson(val);

		GsonWrapper gson = new GsonWrapper();
		IssueProfile obj = gson.fromJson(val, IssueProfile.class);
		issueInnerProfile = obj.issueInnerProfile;
	}

}
