package org.omnione.did.wallet.data.iw.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class VerifyInnerProfile extends Profile {

	@SerializedName("filter")
	@Expose
	protected Filter filter;
	
	public VerifyInnerProfile() {
		setType("VERIFY");
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		super.fromJson(val);

		VerifyInnerProfile obj = gson.fromJson(val, VerifyInnerProfile.class);
		filter = obj.filter;
	}

}
