package org.omnione.did.wallet.data.iw.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.data.did.Proof;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class AbstractProfile extends IWObject {

	@SerializedName("id")
	@Expose
	private String id;

	@SerializedName("proof")
	@Expose
	private Proof proof;

	public String getId() {
		return id;
	}

	public AbstractProfile() {

	}

	public AbstractProfile(String commonProfileJson) {
		this.fromJson(commonProfileJson);
	}

	public void setId(String id) {
		this.id = id;
	}

	public Proof getProof() {
		return proof;
	}

	public void setProof(Proof proof) {
		this.proof = proof;
	}

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		AbstractProfile obj = gson.fromJson(val, AbstractProfile.class);
		id = obj.id;
		proof = obj.proof;
	}

}
