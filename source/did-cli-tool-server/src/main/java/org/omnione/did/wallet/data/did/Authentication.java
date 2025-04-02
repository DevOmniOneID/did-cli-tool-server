
package org.omnione.did.wallet.data.did;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.eoscommander.util.StringUtils;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class Authentication extends IWObject {

	@SerializedName("id")
    @Expose
    private String id;
	
    @SerializedName("type")
    @Expose
    private String type;
    
    @SerializedName("publicKey")
    @Expose
    private String publicKey;
    
    @SerializedName("publicKeyBase58")
    @Expose
    private String publicKeyBase58;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
    public String getPublicKeyBase58() {
        return publicKeyBase58;
    }

    public void setPublicKeyBase58(String publicKeyBase58) {
        this.publicKeyBase58 = publicKeyBase58;
    }
    
    public void checkEmpty() {
    	if(StringUtils.isEmpty(getPublicKey())) {
			publicKey = null;
		}
		if(StringUtils.isEmpty(getId())) {
			id = null;
		}
    }

    @Override
	public void fromJson(String val) {
    	GsonWrapper gson = new GsonWrapper();
		Authentication obj = gson.fromJson(val, Authentication.class);
		type = obj.getType();
		if(!StringUtils.isEmpty(obj.getPublicKey())) {
			publicKey = obj.getPublicKey();
		}
		if(!StringUtils.isEmpty(obj.getId())) {
			id = obj.getId();
		}
		
		
		publicKeyBase58 = obj.getPublicKeyBase58();
	}	
}
