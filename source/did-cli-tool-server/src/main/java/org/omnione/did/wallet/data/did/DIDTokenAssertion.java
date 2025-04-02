
package org.omnione.did.wallet.data.did;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class DIDTokenAssertion extends DIDDefaultAssertion {
    
    @SerializedName("transToken")
    @Expose
    private DIDToken transToken;
    

    public DIDTokenAssertion() {
    	
    }
    
    public DIDTokenAssertion(String didsJson) {
    	fromJson(didsJson);
    }
    
    
    
    public DIDToken getTransToken() {
		return transToken;
	}

	public void setTransToken(DIDToken transToken) {
		this.transToken = transToken;
	}

	@Override
    public void fromJson(String val) {    	
    	super.fromJson(val);
    	
    	GsonWrapper gson = new GsonWrapper();
    	DIDTokenAssertion data = gson.fromJson(val, DIDTokenAssertion.class);
    	transToken = data.getTransToken();
    }
}
