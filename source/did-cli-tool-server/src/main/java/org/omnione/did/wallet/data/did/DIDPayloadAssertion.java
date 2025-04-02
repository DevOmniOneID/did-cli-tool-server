package org.omnione.did.wallet.data.did;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class DIDPayloadAssertion extends DIDDefaultAssertion {

	@SerializedName("payload")
	@Expose
	private Payload payload;

	public DIDPayloadAssertion() {

	}

	public DIDPayloadAssertion(String didsJson) {
		fromJson(didsJson);
	}

	public Payload getPayload() {
		return payload;
	}

	public void setPayload(Payload payload) {
		this.payload = payload;
	}

	@Override
	public void fromJson(String val) {
		super.fromJson(val);

		// GsonWrapper gson = new GsonWrapper();
		Gson gson = new Gson();
		DIDPayloadAssertion data = gson.fromJson(val, DIDPayloadAssertion.class);
		payload = data.getPayload();
	}
}
