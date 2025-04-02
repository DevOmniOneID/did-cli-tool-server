package org.omnione.did.wallet.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RsaKeyPair {
	
	private String privateKey;
	
	private String publicKey;
	

	public RsaKeyPair() {
		super();
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public PublicKey getPublicKey(byte[] publicKey) {

		KeyFactory keyFactory = null;
		PublicKey pubKey = null;
		try {
			X509EncodedKeySpec ukeySpec = new X509EncodedKeySpec(publicKey);
			keyFactory = KeyFactory.getInstance("RSA");
			pubKey = keyFactory.generatePublic(ukeySpec);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pubKey;
	}

	public PrivateKey getPrivateKey(byte[] decPrivKey){
		PrivateKey privateKey = null;
		try{
			PKCS8EncodedKeySpec rkeySpec = new PKCS8EncodedKeySpec(decPrivKey);
			KeyFactory rkeyFactory = KeyFactory.getInstance("RSA");
			privateKey = rkeyFactory.generatePrivate(rkeySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return privateKey;
	}
}
