package org.omnione.did.wallet.key.data;

import java.security.PrivateKey;
import java.security.PublicKey;

public class IWJavaKey implements IWKeyPairInterface {
	
    private PublicKey pubKey; // optional
    private PrivateKey priKey;
    
    public IWJavaKey(PublicKey pubKey, PrivateKey priKey) {
    	this.pubKey = pubKey;
    	this.priKey = priKey;
    }
    
    @Override
	public Object getPubKey() {
		return pubKey;
	}

    @Override
	public void setPubKey(Object pubKey) {
		this.pubKey = (PublicKey) pubKey;
	}

    @Override
	public Object getPriKey() {
		return priKey; 
	}
	
    @Override
	public void setPriKey(Object priKey) {
		this.priKey = (PrivateKey) priKey;
	}
}
