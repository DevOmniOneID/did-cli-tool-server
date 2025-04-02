package org.omnione.did.wallet.key.data;

import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class IWRecoveryHeaderData extends IWObject {

	private int encryptionType;
    private int iterations;
    private String salt;
    private int version;
    
    public int getEncryptionType() {
        return encryptionType;
    }
    
    public void setEncryptionType(int encryptionType) {
        this.encryptionType = encryptionType;
    }

    
    public int getIterations() {
        return iterations;
    }
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
    
    
    public String getSalt() {
        return salt;
    }
    
    public void setSalt(String salt) {
        this.salt = salt;
    }
    
    public int getVersion() {
        return version;
    }
    
    public void setVersion(int version) {
        this.version = version;
    }
    
	@Override
	public void fromJson(String val) {
		
		GsonWrapper gson = new GsonWrapper();
		IWRecoveryHeaderData obj = gson.fromJson(val, IWRecoveryHeaderData.class);
		encryptionType = obj.encryptionType;
		iterations = obj.iterations;
		salt = obj.salt;	
		version = obj.version;
	}
	
	public String toJson() {
		GsonWrapper gson = new GsonWrapper();
		return gson.toJson(this);
	}

}
