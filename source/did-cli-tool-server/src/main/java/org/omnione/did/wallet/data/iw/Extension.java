package org.omnione.did.wallet.data.iw;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class Extension extends IWObject{
	
	@SerializedName("vcAttribute")
	@Expose
	private VcAttribute vcAttribute;
	 
	

	public void setVcAttribute(VcAttribute vcAttribute) {
		this.vcAttribute = vcAttribute;
	}
	
	public VcAttribute getVcAttribute() {
		return vcAttribute;
	}

	
	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		Extension obj = gson.fromJson(val, Extension.class);

		vcAttribute = obj.vcAttribute;
		
	}	
	

}
