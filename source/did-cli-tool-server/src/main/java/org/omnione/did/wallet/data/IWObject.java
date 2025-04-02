package org.omnione.did.wallet.data;

import org.omnione.did.wallet.util.GDPJsonSortUtil;
import org.omnione.did.wallet.util.json.GsonWrapper;

public abstract class IWObject {
	
	public String toJson() {
		GsonWrapper gson = new GsonWrapper();
		String json = gson.toJson(this);
		return GDPJsonSortUtil.sortJsonString(gson, json);
	}
	
	public abstract void fromJson(String val);	
}
