
package org.omnione.did.wallet.data.did;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class PublicKey extends IWObject {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("publicKeyPem")
    @Expose
    private String publicKeyPem;
    @SerializedName("publicKeyBase58")
    @Expose
    private String publicKeyBase58;
    @SerializedName("publicKeyJwk")
    @Expose
    private String publicKeyJwk;
    @SerializedName("publicKeyHex")
    @Expose
    private String publicKeyHex;

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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPublicKeyPem() {
        return publicKeyPem;
    }

    public void setPublicKeyPem(String publicKeyPem) {
        this.publicKeyPem = publicKeyPem;
    }

    public String getPublicKeyBase58() {
        return publicKeyBase58;
    }

    public void setPublicKeyBase58(String publicKeyBase58) {
        this.publicKeyBase58 = publicKeyBase58;
    }

    public String getPublicKeyJwk() {
        return publicKeyJwk;
    }

    public void setPublicKeyJwk(String publicKeyJwk) {
        this.publicKeyJwk = publicKeyJwk;
    }

    public String getPublicKeyHex() {
        return publicKeyHex;
    }

    public void setPublicKeyHex(String publicKeyHex) {
        this.publicKeyHex = publicKeyHex;
    }

    @Override
	public void fromJson(String val) {
    	GsonWrapper gson = new GsonWrapper();
		PublicKey obj = gson.fromJson(val, PublicKey.class);
		id = obj.getId();
		type = obj.getType();
		owner = obj.getOwner();
		publicKeyPem = obj.getPublicKeyPem();
		publicKeyBase58 = obj.getPublicKeyBase58();
		publicKeyJwk = obj.getPublicKeyJwk();
		publicKeyHex = obj.getPublicKeyHex();
	}	
}
