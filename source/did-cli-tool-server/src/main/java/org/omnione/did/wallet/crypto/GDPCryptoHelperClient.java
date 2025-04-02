package org.omnione.did.wallet.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECParameterSpec;

import org.omnione.did.wallet.crypto.GDPCryptoHelperInterface;
import org.omnione.did.wallet.eoscommander.crypto.digest.Sha256;
import org.omnione.did.wallet.eoscommander.crypto.digest.Sha512;
import org.omnione.did.wallet.exception.IWErrorCode;
import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.key.data.AESType;


public class GDPCryptoHelperClient extends GDPCryptoHelperInterface {
			
	private static int dk_keySize = 256 + 128;
	private static int default_keySize = 256;
    private static int default_saltlength = default_keySize / 8;
    private static int default_noncelength = 20;
    
	@Override
	public KeyPair generateKeyPair() {
		try {
			KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "SC");
			ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");

			g.initialize(ecSpec, SecureRandom.getInstance("SHA1PRNG"));
			return g.generateKeyPair();
		}
		catch (Exception var2) {
//			var2.printStackTrace();
			return null;
		}
	}
	
	public KeyPair generateEosKeyPair() {
		try {
			KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "SC");
			ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");

			g.initialize(ecSpec, SecureRandom.getInstance("SHA1PRNG"));
			return g.generateKeyPair();
		}
		catch (Exception var2) {
//			var2.printStackTrace();
			return null;
		}
	}
		
	public SecretKeySpec getSecretKeySpecWithPBKDF2(char[] password, byte[] salt, int iterator) {
		return getSecretKeySpecWithPBKDF2(password, salt, iterator, default_keySize);
	}
	
	public SecretKeySpec getSecretKeySpecWithPBKDF2(char[] password, byte[] salt, int iterator, int keySize) {
		SecretKeySpec skeySpec = null;
		try {
			// Derive the key
	        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	        PBEKeySpec spec = new PBEKeySpec(
	        		password, 
	        		salt, 
	        		iterator, 
	                keySize
	                );
	        
	        // Secret Key
	        SecretKey secretKey = factory.generateSecret(spec);
	        skeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
	        
		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
//			e.printStackTrace();
		}
		
        return skeySpec;
	}
	
	/**
	 * [IWKeyStoreDefault 사용]
	 * @param dk
	 * @param data
	 * @return
	 * @throws IWException
	 */
	public byte[] encrypt(byte[] dk, byte[] data) throws IWException {		
		// seperate K, IV
		byte[] k = Arrays.copyOfRange(dk, 0, 32);		
		byte[] iv = Arrays.copyOfRange(dk, 32, dk.length);
		return encrypt(k, iv, data);					
	}
	
	/**
	 * [IWKeyStoreDefault 사용]
	 * @param dk
	 * @param data
	 * @param keySize
	 * @return
	 * @throws IWException
	 */
	public byte[] encrypt(byte[] dk, byte[] data, int keySize) throws IWException {		
		// seperate K, IV
		byte[] k = Arrays.copyOfRange(dk, 0, keySize);		
		byte[] iv = Arrays.copyOfRange(dk, keySize, dk.length);
		return encrypt(k, iv, data);					
	}
	
	/**
	 * [IWKeyStoreDefault 사용]
	 * @param dk
	 * @param data
	 * @return
	 * @throws IWException
	 */
	public byte[] decrypt(byte[] dk, byte[] data) throws IWException {		
		// seperate K, IV
		byte[] k = Arrays.copyOfRange(dk, 0, 32);		
		byte[] iv = Arrays.copyOfRange(dk, 32, dk.length);
		return decrypt(k, iv, data);					
	}
	
	
	/**
	 * [IWKeyStoreDefault 사용]
	 * @param dk
	 * @param data
	 * @param key size
	 * @return
	 * @throws IWException
	 */
	public byte[] decrypt(byte[] dk, byte[] data, int keySize) throws IWException {		
		// seperate K, IV
		byte[] k = Arrays.copyOfRange(dk, 0, keySize);		
		byte[] iv = Arrays.copyOfRange(dk, keySize, dk.length);
		return decrypt(k, iv, data);					
	}
		
	
	/**
	 * AES Key를 사용해서 암호화
	 * @param key
	 * @param data
	 * @return
	 */
	public byte[] encrypt(byte[] key, byte[] iv, byte[] data) throws IWException {
		byte[] encrypted = null;
		try {            
			// Secret Key
			SecretKey sKey = new SecretKeySpec(key, 0, key.length, "AES");
            
			// Secret Key IV
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
			
			// Secret Key Algorithm
//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "SC"); 
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");			
			cipher.init(Cipher.ENCRYPT_MODE, sKey, ivSpec);
	        encrypted = cipher.doFinal(data);
	        
		} catch (NoSuchAlgorithmException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_ENCRYPT, e);
		} catch (NoSuchPaddingException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_ENCRYPT, e);
		} catch (InvalidKeyException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_ENCRYPT, e);
		} catch (IllegalBlockSizeException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_ENCRYPT, e);
		} catch (BadPaddingException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_ENCRYPT, e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_ENCRYPT, e);
		}
        return encrypted;
	}
	
	/**
	 * AES Key를 사용해서 복호화
	 * @param key
	 * @param data
	 * @return
	 */
	public byte[] decrypt(byte[] key, byte[] iv, byte[] encData) throws IWException {
		byte[] decrypted = null;
		try {            
			// Secert Key
			SecretKey sKey = new SecretKeySpec(key, 0, key.length, "AES");
            
			// Secret Key IV
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
			
			// Secret Key Algorithm
//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "SC"); 
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");			
			cipher.init(Cipher.DECRYPT_MODE, sKey, ivSpec);
			decrypted = cipher.doFinal(encData);
	        
		} catch (NoSuchAlgorithmException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_DECRYPT, e);
		} catch (NoSuchPaddingException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_DECRYPT, e);
		} catch (InvalidKeyException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_DECRYPT, e);
		} catch (IllegalBlockSizeException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_DECRYPT, e);
		} catch (BadPaddingException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_DECRYPT, e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_DECRYPT, e);
		} 
        return decrypted;
	}
	
	
	/**
	 * AES Key를 PBKDF2로 유도한 뒤 암호화
	 * @param pwd
	 * @param data
	 * @param salt
	 * @param iterator
	 * @return
	 */
	public byte[] encryptWithPBKDF2(char[] pwd, byte[] data, byte[] salt, int iterator) throws IWException { 
		// Secert Key
		SecretKeySpec skeySpec = getSecretKeySpecWithPBKDF2(pwd, salt, iterator, dk_keySize);
		byte[] dk = skeySpec.getEncoded();
		
		// seperate K, IV
		byte[] k = Arrays.copyOfRange(dk, 0, 32);		
		byte[] iv = Arrays.copyOfRange(dk, 32, dk.length);
		return encrypt(k, iv, data);
	}
	
	/**
	 * AES Key를 PBKDF2로 유도한 뒤 복호화
	 * @param pwd
	 * @param encData
	 * @param salt
	 * @param iterator
	 * @return
	 */
	public byte[] decryptWithPBKDF2(char[] pwd, byte[] encData, byte[] salt, int iterator) throws IWException {
		// Secert Key
		SecretKeySpec skeySpec = getSecretKeySpecWithPBKDF2(pwd, salt, iterator, dk_keySize);
		byte[] dk = skeySpec.getEncoded();
		
		// seperate K, IV
		byte[] k = Arrays.copyOfRange(dk, 0, 32);		
		byte[] iv = Arrays.copyOfRange(dk, 32, dk.length);
		return decrypt(k, iv, encData);	
	}
	
	public byte[] generateNonce() throws IWException {
		return generateSecureRandom(default_noncelength);
    }
	
	public byte[] generateSalt() throws IWException {
        return generateSecureRandom(default_saltlength);
    }
	
	public byte[] generateSecureRandom(int size) throws IWException {
		try {
			SecureRandom random;
			random = SecureRandom.getInstance("SHA1PRNG");				
	        byte bytes[] = new byte[size];
	        random.nextBytes(bytes);
	        return bytes;
	        
		} catch (NoSuchAlgorithmException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_GENSRANDOM_FAIL, e);
		}
	}
	
	static public byte[] aesEncrypt(byte[] data, byte[] key, AESType aesType) throws IWException {
		
		byte[] hash = null;
		
		if (aesType == AESType.AES128) {
			Sha256 sha256 = Sha256.from(key);
			hash = sha256.getBytes();
		}
		else {
			Sha512 sha512 = Sha512.from(key);
			hash = sha512.getBytes();
		}

		
		int length = aesType == AESType.AES256 ? 32 : 16;
		byte[] k = Arrays.copyOfRange(hash, 0, length);
		byte[] iv = Arrays.copyOfRange(hash, length, length + 16);

		byte[] encrypted = null;
		try {            
			// Secret Key
			SecretKey sKey = new SecretKeySpec(k, 0, k.length, "AES");
            
			// Secret Key IV
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
			
			// Secret Key Algorithm
//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "SC"); 
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");			
			cipher.init(Cipher.ENCRYPT_MODE, sKey, ivSpec);
	        encrypted = cipher.doFinal(data);
	        
		} catch (NoSuchAlgorithmException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_ENCRYPT, e);
		} catch (NoSuchPaddingException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_ENCRYPT, e);
		} catch (InvalidKeyException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_ENCRYPT, e);
		} catch (IllegalBlockSizeException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_ENCRYPT, e);
		} catch (BadPaddingException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_ENCRYPT, e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_ENCRYPT, e);
		} 	
        return encrypted;		
	}
	
	static public byte[] aesDecrypt(byte[] data, byte[] key, AESType aesType) throws IWException {
		
//		Sha512 sha512 = Sha512.from(key);
//		byte[] hash = sha512.getBytes();
//		
//		byte[] k = Arrays.copyOfRange(hash, 0, 32);		
//		byte[] iv = Arrays.copyOfRange(hash, 32, 32 + 16);
		
		byte[] hash = null;
		
		if (aesType == AESType.AES128) {
			Sha256 sha256 = Sha256.from(key);
			hash = sha256.getBytes();
		}
		else {
			Sha512 sha512 = Sha512.from(key);
			hash = sha512.getBytes();
		}
		
		int length = aesType == AESType.AES256 ? 32 : 16;
		byte[] k = Arrays.copyOfRange(hash, 0, length);
		byte[] iv = Arrays.copyOfRange(hash, length, length + 16);
		
		
		byte[] decrypted = null;
		try {            
			// Secret Key
			SecretKey sKey = new SecretKeySpec(k, 0, k.length, "AES");
            
			// Secret Key IV
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
			
			// Secret Key Algorithm
//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "SC"); 
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");			
			cipher.init(Cipher.DECRYPT_MODE, sKey, ivSpec);
			decrypted = cipher.doFinal(data);
	        
		} catch (NoSuchAlgorithmException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_DECRYPT, e);
		} catch (NoSuchPaddingException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_DECRYPT, e);
		} catch (InvalidKeyException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_DECRYPT, e);
		} catch (IllegalBlockSizeException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_DECRYPT, e);
		} catch (BadPaddingException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_DECRYPT, e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_DECRYPT, e);
		} 	
        return decrypted;
	}
}
