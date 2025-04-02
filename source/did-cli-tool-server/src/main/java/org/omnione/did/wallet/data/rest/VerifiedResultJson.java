package org.omnione.did.wallet.data.rest;

import java.util.HashMap;

import com.google.gson.Gson;

public class VerifiedResultJson extends ResultJson{
	
	private HashMap<String, String> privacy;

	public HashMap<String, String> getPrivacy() {
		return privacy;
	}

	public void setPrivacy(HashMap<String, String> privacy) {
		this.privacy = privacy;
	}
	
	@Override
	public void fromJson(String val) {
		super.fromJson(val);
		Gson gson = new Gson();
		VerifiedResultJson obj = gson.fromJson(val, VerifiedResultJson.class);
		privacy = obj.getPrivacy();

	}
	

}
