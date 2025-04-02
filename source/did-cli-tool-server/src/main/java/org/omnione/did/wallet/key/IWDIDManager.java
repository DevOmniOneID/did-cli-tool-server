package org.omnione.did.wallet.key;

import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.key.extend.IWEosDIDManagerAdapter;

public class IWDIDManager extends IWEosDIDManagerAdapter {
	
	
    /**
     * IWDIDManager Constructor
     * @param pathWithName
     * @throws IWException 
     */
    public IWDIDManager(String pathWithName) throws IWException {
        super(pathWithName);
    }
    

        
}
