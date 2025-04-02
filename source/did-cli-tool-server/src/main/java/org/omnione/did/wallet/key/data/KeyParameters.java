package org.omnione.did.wallet.key.data;

public class KeyParameters {

    /**
     * 공개키(검증) 알고리즘
     */
    private String pubAlg;

    /**
     * 서명 알고리즘
     */
    private String signAlg;

    /**
     * Sync Private IWKey(ex, AES) 암호화 알고리즘
     */
    private String encAlg;

    /**
     * Sync Private IWKey(ex, AES) 암호화용 salt
     */
    private byte[] salt;

    /**
     * Sync Private IWKey(ex, AES) 암호화용 iterator
     */
    private int iterator;

    public String getPubAlg() {
        return pubAlg;
    }

    public void setPubAlg(String pubAlg) {
        this.pubAlg = pubAlg;
    }

    public String getSignAlg() {
        return signAlg;
    }

    public void setSignAlg(String signAlg) {
        this.signAlg = signAlg;
    }

    public String getEncAlg() {
        return encAlg;
    }

    public void setEncAlg(String encAlg) {
        this.encAlg = encAlg;
    }

    public byte[] getSalt() {
    	
    	if(salt == null){
    		return null;
    	}
    	
    	byte [] data = new byte[salt.length];
		System.arraycopy(salt, 0, data, 0, salt.length);
		
        return data;
    }

    public void setSalt(byte[] salt) {
    	
    	if(salt != null){
    		this.salt = new byte[salt.length];
			System.arraycopy(salt, 0, this.salt, 0, salt.length);
    	}
    	else{
    		this.salt = null;
    	}
    	
    }

    public int getIterator() {
        return iterator;
    }

    public void setIterator(int iterator) {
        this.iterator = iterator;
    }
}
