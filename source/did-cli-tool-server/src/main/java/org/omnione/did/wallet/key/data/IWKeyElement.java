package org.omnione.did.wallet.key.data;

import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.key.data.IWKey.ALGORITHM_TYPE;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class IWKeyElement extends IWObject {
    private String keyId;
    private int alg;
    private String algString;
    private String publicKey; // base58
    private String privateKey; // base58
	
    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public int getAlg() {
        return alg;
    }

    public void setAlg(int alg) {
        this.alg = alg;
        ALGORITHM_TYPE algType = ALGORITHM_TYPE.fromValue(alg);
        this.algString = algType.toString();
    }
    
    public String getAlgString() {
        return algString;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    
    public String toJson() {
    	GsonWrapper gson = new GsonWrapper();
		return gson.toJson(this);
	}
    
    @Override
    public void fromJson(String val) {
    	GsonWrapper gson = new GsonWrapper();
    	IWKeyElement data = gson.fromJson(val, IWKeyElement.class);
    	
    	keyId = data.getKeyId();
    	alg = data.getAlg();
    	algString = data.getAlgString();
    	publicKey = data.getPublicKey();
    	privateKey = data.getPrivateKey();
    }
    
}
