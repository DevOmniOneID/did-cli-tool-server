package org.omnione.did.wallet.data.iw.profile.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.did.DIDDefaultAssertion;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class DIDAuthProfileResult extends ProfileResult {

	@SerializedName("didAuth")
	@Expose
	private DIDDefaultAssertion didAuth;

	public DIDDefaultAssertion getDidAuth() {
		return didAuth;
	}

	public void setDidAuth(DIDDefaultAssertion didAuth) {
		this.didAuth = didAuth;
	}

	@Override
	public void fromJson(String val) {
		super.fromJson(val);

		GsonWrapper gson = new GsonWrapper();
		DIDAuthProfileResult obj = gson.fromJson(val, DIDAuthProfileResult.class);
		didAuth = obj.didAuth;
	}

}
