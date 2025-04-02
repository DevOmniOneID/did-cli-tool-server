package org.omnione.did.wallet.key.data;

import java.io.UnsupportedEncodingException;

import org.omnione.did.wallet.eoscommander.crypto.digest.Sha256;

public class IWKdf {

	protected byte[] key = null;
	protected byte[] iv = null;
	protected byte[] macKey = null;
	
	public byte [] getKey() {
		return key;
	}
	
	public byte [] getIv() {
		return iv;
	}
	
	public byte [] getMacKey() {
		return macKey;
	}
	
	public void deriveKeyWithSeed(byte[] seed, String salt, int iterations) {
		clean();
		
		byte[] saltByte = null;
		try {
			saltByte = salt.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] hashed = new byte[seed.length + saltByte.length];
		

		System.arraycopy(seed		,	0,	hashed,	0  	       ,	seed.length);
		System.arraycopy(saltByte	,	0,	hashed,	seed.length,	saltByte.length);
		
		
		int i = 0;
		do {
			Sha256 hash2 = Sha256.from(hashed);
			byte[] hashedByte2 = hash2.getBytes();
			hashed = hashedByte2;
			i++;
		}while(i < iterations);
		
		key = new byte[hashed.length];
		System.arraycopy(hashed, 0, key, 0, hashed.length);
		
		Sha256 ivHash = Sha256.from(hashed);
		iv = ivHash.getBytes();
		
		Sha256 macKeyHash = Sha256.from(iv);
		macKey = macKeyHash.getBytes();		
		
	}
	
	public void clean() {
		if(key != null) {
			key = null;
		}
		if(iv != null) {
			iv = null;
		}
		if(macKey != null) {
			macKey = null;
		}
	}
}
