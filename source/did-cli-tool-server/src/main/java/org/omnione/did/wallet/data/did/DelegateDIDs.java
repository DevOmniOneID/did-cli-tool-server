
package org.omnione.did.wallet.data.did;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class DelegateDIDs extends IWObject {

	/**
	 *
	 * (Required)
	 *
	 */
	@SerializedName("delegateDIDs")
	@Expose
	private List<String> delegateDIDs;

	public List<String> getDelegateDIDs() {
		return delegateDIDs;
	}

	public void setDelegateDIDs(List<String> delegateDIDs) {
		this.delegateDIDs = delegateDIDs;
	}

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		DelegateDIDs obj = gson.fromJson(val, DelegateDIDs.class);
		delegateDIDs = obj.delegateDIDs;
	}

}
