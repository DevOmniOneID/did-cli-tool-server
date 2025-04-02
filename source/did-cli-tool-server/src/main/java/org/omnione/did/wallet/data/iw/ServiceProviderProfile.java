package org.omnione.did.wallet.data.iw;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.data.did.Proof;
import org.omnione.did.wallet.data.iw.profile.Profile;
import org.omnione.did.wallet.util.json.GsonWrapper;

@Deprecated 
public class ServiceProviderProfile extends IWObject {
	
	@SerializedName("id")
	@Expose
	private String id;
	
	@SerializedName("profile")
	@Expose
	private Profile profile;
	
	@SerializedName("proof")
	@Expose
	private Proof proof;

	@SerializedName("issuerProof")
	@Expose
	private Proof issuerProof;
	
	public String getId() {
		return id;
	}

	public ServiceProviderProfile() {
		
	}
	
	public ServiceProviderProfile(String serviceProviderProfileJson) {
		this.fromJson(serviceProviderProfileJson);
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Proof getProof() {
		return proof;
	}

	public void setProof(Proof proof) {
		this.proof = proof;
	}
	
	
	public Proof getIssuerProof() {
		return issuerProof;
	}

	public void setIssuerProof(Proof proof) {
		this.issuerProof = proof;
	}
	
	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		ServiceProviderProfile obj = gson.fromJson(val, ServiceProviderProfile.class);
		id = obj.id;
		profile = obj.profile;
		proof = obj.proof;
		issuerProof = obj.issuerProof;
	}

}
