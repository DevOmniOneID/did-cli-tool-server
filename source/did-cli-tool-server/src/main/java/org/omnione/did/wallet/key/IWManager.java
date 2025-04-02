package org.omnione.did.wallet.key;

import java.util.Date;
import java.util.List;

import org.spongycastle.util.encoders.Hex;

import org.omnione.did.wallet.data.did.Proof;
import org.omnione.did.wallet.data.iw.Unprotected;
import org.omnione.did.wallet.data.iw.VerifiableClaim;
import org.omnione.did.wallet.eoscommander.crypto.util.Base58;
import org.omnione.did.wallet.exception.IWException;

public class IWManager {
		
	protected String getKeyDId(String did, String keyId) {
        return did + "#" + keyId;
    }
	
	protected Proof getProof(String signKeyDid, byte[] nonce) {
		Proof proof = new Proof();
		
		// proof.creator
    	proof.setCreator(signKeyDid);
    	
    	// proof.created
    	proof.setCreated(VerifiableClaim.dateToString(new Date()));
    	
    	// proof.nonce
//    	proof.setNonce(Base58.encode(nonce));
    	proof.setNonce(new String(Hex.encode(nonce)));
    	
    	return proof;
	}
	
//	protected byte[] getSignatureUsingKeyInKeyFile(byte[] data, String signKeyId, IWKeyManager keyManager) throws IWException {
	protected byte[] getSignatureUsingKeyInKeyFile(byte[] data, String signKeyId, IWKeyManagerInterface keyManager) throws IWException {
		// KeyFile에 key가 존재하는 경우
		List<String> keyIds = keyManager.getKeyIdList();
			if(keyIds != null){
		    	for(String keyId : keyManager.getKeyIdList()) {
		    		if(keyId.equals(signKeyId)) {    			
		    			return keyManager.getSign(signKeyId, data);   
		    		}
		    	}
			}	
		return null;
	}

}
