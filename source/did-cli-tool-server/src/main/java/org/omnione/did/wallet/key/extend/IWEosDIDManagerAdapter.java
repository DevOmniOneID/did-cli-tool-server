package org.omnione.did.wallet.key.extend;

import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.key.IWDIDManagerImpl;
import org.omnione.did.wallet.key.IWKeyManagerInterface;
import org.omnione.did.wallet.key.data.IWKey;

public class IWEosDIDManagerAdapter extends IWDIDManagerImpl {

	public IWEosDIDManagerAdapter(String pathWithName) throws IWException {
		super(pathWithName);
	}


	@Override
	protected byte[] getPublicKeyBytes(IWKey iwKey) {
    	return ((IWKey)iwKey).getEosPubKey().getBytes();
    }
	
	@Override
	protected byte[] getSignature(byte[] didsJsonBytes, String signKeyId, IWKeyManagerInterface keyManager) throws IWException {
		
		// key file 안의 key를 사용해서 서명을 한다.
		byte[] signature = null;
		if(keyManager.isExistKey(signKeyId)) {
			signature = keyManager.getSign(signKeyId, didsJsonBytes);   
		}


    	return signature;
    }	
	
	@Override
	protected void verifySignature(String signKeyId, byte[] source, byte[] sign, IWKeyManagerInterface keyManager) throws IWException {		
		keyManager.verifySign(signKeyId, source, sign);		
    }
}
