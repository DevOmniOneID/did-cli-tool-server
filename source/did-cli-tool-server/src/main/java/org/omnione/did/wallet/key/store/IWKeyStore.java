package org.omnione.did.wallet.key.store;

import org.omnione.did.wallet.exception.IWException;

public class IWKeyStore {
    private static IWKeyStoreHepler keyStoreHelper;
    


    /*
     * (Key Store) AES-Wrap Key 존재여부
     */
    public boolean isExistWrapKey() {
    	return keyStoreHelper.isExistWrapKey();
    }
    
    /*
     * (Key Store) Key 생성 with Password
     */
    public void genWrapKey(char [] pwdOrAlias) throws IWException {
   		keyStoreHelper.genWrapKey(pwdOrAlias);
    }
    
    
    /*
     * (Key Structure:head) Key 복호화
     */
    public void decrypt(char [] password, byte[] encProxyKey, OnResultListener listener) throws IWException {     	
    	keyStoreHelper.decryptKEK(password, encProxyKey, listener);    	
    }
    
    /*
     * (Key Structure:head) Key 암호화
     */
    public void encrypt(char [] password, byte[] proxyKey, OnResultListener listener) throws IWException {
    	keyStoreHelper.encryptKEK(password, proxyKey, listener);
    }
    
    /*
     * (Key Structure:head) Key 복호화 with Biometric
     */
    protected void encryptWithBioMetric(byte[] proxyKey, OnResultListener listener){    	
        return;
    }    
    
    public static interface IWKeyStoreHepler {
    	boolean isExistWrapKey();
    	void genWrapKey(char [] pwdOrAlias) throws IWException; 	
    	void authenticate(char [] pwd, OnResultListener listener) throws IWException;  	
    	void encryptKEK(char [] pwdOrAlias, byte[] data, OnResultListener listener) throws IWException;
    	void decryptKEK(char [] pwdOrAlias, byte[] encData, OnResultListener listener) throws IWException;
    	
    }
    
    public static interface OnResultListener {
    	void onSuccess(byte[] result);
    	void onFail(int errCode);
    	void onCancel();
    }    
}
