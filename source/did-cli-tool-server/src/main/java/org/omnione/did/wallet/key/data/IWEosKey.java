package org.omnione.did.wallet.key.data;

import org.omnione.did.wallet.eoscommander.crypto.ec.EosPrivateKey;
import org.omnione.did.wallet.eoscommander.crypto.ec.EosPublicKey;

public class IWEosKey implements IWKeyPairInterface {
	
	private EosPublicKey pubKey; // optional
    private EosPrivateKey priKey;
    
    public IWEosKey(EosPublicKey pubKey, EosPrivateKey priKey) {
    	this.pubKey = pubKey;
    	this.priKey = priKey;
    }
    
    @Override
	public Object getPubKey() {
		return pubKey;
	}

    @Override
	public void setPubKey(Object pubKey) {
		this.pubKey = (EosPublicKey) pubKey;
	}

    @Override
	public Object getPriKey() {
		return priKey; 
	}
	
    @Override
	public void setPriKey(Object priKey) {
		this.priKey = (EosPrivateKey) priKey;
	}	
}
