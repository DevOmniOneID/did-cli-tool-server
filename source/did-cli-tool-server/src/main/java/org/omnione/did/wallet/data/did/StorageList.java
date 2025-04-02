package org.omnione.did.wallet.data.did;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class StorageList extends IWObject {

	@SerializedName("url")
	@Expose
	private String url;
	
	@SerializedName("did")
	@Expose
	private String did;
	
	
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getDid() {
		return this.did;
	}

	public void setDid(String did) {
		this.did = did;
	}
	

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		StorageList obj = gson.fromJson(val, StorageList.class);
		url = obj.getUrl();
		did = obj.getDid();
	
	}	
}
