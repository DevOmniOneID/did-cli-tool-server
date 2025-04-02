package org.omnione.did.wallet.key.data;

import org.omnione.did.wallet.util.json.GsonWrapper;

public class IWHeadElement {
    private String encType;
    private String salt; // base58
    private int iterations;
    private String proxyKey;
    private int version;
    
	
    private IWKeyStoreEncodingElement encoding;
    
    
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
    
    public String getEncType() {
        return encType;
    }

    public void setEncType(String encType) {
        this.encType = encType;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
    
    public String getProxyKey() {
        return proxyKey;
    }

    public void setProxyKey(String proxyKey) {
        this.proxyKey = proxyKey;
    }
    
    public IWKeyStoreEncodingElement getEncoding() {
        return encoding;
    }

    public void setEncoding(IWKeyStoreEncodingElement encoding) {
        this.encoding = encoding;
    }
    
    public String toJson() {
		GsonWrapper gson = new GsonWrapper();
		return gson.toJson(this);
	}
}
