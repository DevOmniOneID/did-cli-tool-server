
package org.omnione.did.wallet.data.did;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class DIDDefaultAssertion extends DIDAssertion {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("proof")
    @Expose
    private Proof proof;

    public DIDDefaultAssertion() {
    	
    }
    
    public DIDDefaultAssertion(String didsJson) {
    	fromJson(didsJson);
    }
    
    public String getId() {
        return id;
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
    	super.fromJson(val);
    	
    	GsonWrapper gson = new GsonWrapper();
    	DIDDefaultAssertion data = gson.fromJson(val, DIDDefaultAssertion.class);
    	id = data.getId();
    	proof = data.getProof();
    }
}
