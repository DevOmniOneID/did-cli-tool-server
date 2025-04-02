package org.omnione.did.wallet.data.did;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class AddSignData extends IWObject{
	
	@SerializedName("data")
	@Expose
	private String data;

	@SerializedName("signature")
	@Expose
	private String signature;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = GsonWrapper.getGson();
		
		AddSignData obj = gson.fromJson(val, AddSignData.class);
		data = obj.getData();
		signature = obj.getSignature();
	}
	
	
	

}
