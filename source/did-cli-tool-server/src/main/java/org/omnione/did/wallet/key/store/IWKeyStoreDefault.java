package org.omnione.did.wallet.key.store;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.spongycastle.util.encoders.Hex;

import org.omnione.did.wallet.crypto.GDPCryptoHelperClient;
import org.omnione.did.wallet.eoscommander.crypto.util.Base58;
import org.omnione.did.wallet.exception.IWErrorCode;
import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.key.data.IWHeadElement;
import org.omnione.did.wallet.key.data.IWKeyStoreData;
import org.omnione.did.wallet.key.store.IWKeyStore.IWKeyStoreHepler;
import org.omnione.did.wallet.key.store.IWKeyStore.OnResultListener;
import org.omnione.did.wallet.util.GDPLogger;

public class IWKeyStoreDefault implements IWKeyStoreHepler {
    
	private IWKeyFile iwF;
	private boolean isProxyKey;

    
    public IWKeyStoreDefault(IWKeyFile keyFile) throws IWException {
    	iwF = keyFile;
    	isProxyKey = iwF.getData().getHead().getProxyKey() != null ? true : false;
    }
    
    @Override
    public boolean isExistWrapKey() {
    	return isProxyKey;
    }
            
    @Override
    public void genWrapKey(char [] pwd) throws IWException {
    	        	
    	byte[] encData = null;    		
		try {
			
			IWKeyStoreData data = iwF.getData();
            IWHeadElement head = data.getHead();
            
            int keyLength = (head.getEncType().equals("AES128"))?16:32;
            
			// derive DK
			GDPCryptoHelperClient cryptoHelper = new GDPCryptoHelperClient();
			SecretKeySpec secret = cryptoHelper.getSecretKeySpecWithPBKDF2(pwd, 
                    Base58.decode(iwF.getData().getHead().getSalt()), 
                    iwF.getData().getHead().getIterations(), (keyLength+16) * 8);  	
     
			// seperate K, IV
			byte[] dk = secret.getEncoded();
			SecretKey k = new SecretKeySpec(dk, 0, keyLength, "AES");
			byte[] iv = Arrays.copyOfRange(dk, keyLength, dk.length);
			GDPLogger.print("info", "[IWKeyStoreDefault] gen.k : " + Hex.toHexString(k.getEncoded()));
			GDPLogger.print("info", "[IWKeyStoreDefault] gen.iv : " + Hex.toHexString(iv));
			
            // encrypt the message
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "SC"); 
            cipher.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv)); 
            
            encData = cipher.doFinal("raonsecure".getBytes());
            
            // save
            
            head.setProxyKey(Base58.encode(encData));
            data.setHead(head);
            iwF.write(data);
            
		} catch (NoSuchAlgorithmException e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_KEYGEN_FAIL, e);
		} catch (NoSuchPaddingException e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_KEYGEN_FAIL, e);
		} catch (InvalidKeyException e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_KEYGEN_FAIL, e);
		} catch (IllegalBlockSizeException e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_KEYGEN_FAIL, e);
		} catch (BadPaddingException e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_KEYGEN_FAIL, e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_KEYGEN_FAIL, e);
		}
    }
    
    
    @Override
    public void authenticate(char [] pwd, OnResultListener listener) throws IWException {        	
       
    	byte[] data = null;
    	try {
    		
    		int keyLength = (iwF.getData().getHead().getEncType().equals("AES128"))?16:32;
    		
    		// derive DK
    		GDPCryptoHelperClient cryptoHelper = new GDPCryptoHelperClient();
			SecretKeySpec secret = cryptoHelper.getSecretKeySpecWithPBKDF2(pwd, 
                    Base58.decode(iwF.getData().getHead().getSalt()), 
                    iwF.getData().getHead().getIterations(), (keyLength+16) * 8);
            
			// seperate K, IV
			byte[] dk = secret.getEncoded();
			SecretKey k = new SecretKeySpec(dk, 0, keyLength, "AES");		
			byte[] iv = Arrays.copyOfRange(dk, keyLength, dk.length);
			GDPLogger.print("info", "[IWKeyStoreDefault] auth.k : " + Hex.toHexString(k.getEncoded()));
			GDPLogger.print("info", "[IWKeyStoreDefault] auth.iv : " + Hex.toHexString(iv));
			
            // decrypt the secret value
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "SC"); 
            cipher.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));  
        
            data = cipher.doFinal(Base58.decode(iwF.getData().getHead().getProxyKey()));
            
            // verify
            if(Arrays.equals(data, "raonsecure".getBytes())) {
            	Arrays.fill(data, (byte)0x00); // secret value
            	listener.onSuccess(dk);
            }
            else
            	listener.onFail(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_AUTHENTICATE_FAIL.getCode());
            
        } catch (IllegalBlockSizeException e) {
        	throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_AUTHENTICATE_FAIL, e);
        } catch (BadPaddingException e) {
        	throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_AUTHENTICATE_FAIL, e);
        } catch (NoSuchAlgorithmException e) {
        	throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_AUTHENTICATE_FAIL, e);
        } catch (NoSuchPaddingException e) {
        	throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_AUTHENTICATE_FAIL, e);
        } catch (InvalidKeyException e) {
        	throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_AUTHENTICATE_FAIL, e);
        } catch (InvalidAlgorithmParameterException e) {
        	throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_AUTHENTICATE_FAIL, e);
        }
    }
    
    
    
//    @Override
//    public void authenticateByAES128(char [] pwd, OnResultListener listener) throws IWException {        	
//       
//    	byte[] data = null;
//    	try {
//    		// derive DK
//    		GDPCryptoHelperClient cryptoHelper = new GDPCryptoHelperClient();
//			SecretKeySpec secret = cryptoHelper.getSecretKeySpecWithPBKDF2(pwd, 
//                    Base58.decode(iwF.getData().getHead().getSalt()), 
//                    iwF.getData().getHead().getIterations(), (16+16) * 8);
//            
//			// seperate K, IV
//			byte[] dk = secret.getEncoded();
//			SecretKey k = new SecretKeySpec(dk, 0, 16, "AES");		
//			byte[] iv = Arrays.copyOfRange(dk, 16, dk.length);
//			GDPLogger.print("info", "[IWKeyStoreDefault] auth.k : " + Hex.toHexString(k.getEncoded()));
//			GDPLogger.print("info", "[IWKeyStoreDefault] auth.iv : " + Hex.toHexString(iv));
//			
//            // decrypt the secret value
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
////			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "SC"); 
//            cipher.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));  
//        
//            data = cipher.doFinal(Base58.decode(iwF.getData().getHead().getProxyKey()));
//            
//            // verify
//            if(Arrays.equals(data, "raonsecure".getBytes())) {
//            	Arrays.fill(data, (byte)0x00); // secret value
//            	listener.onSuccess(dk);
//            }
//            else
//            	listener.onFail(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_AUTHENTICATE_FAIL.getCode());
//            
//        } catch (IllegalBlockSizeException e) {
//        	throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_AUTHENTICATE_FAIL, e);
//        } catch (BadPaddingException e) {
//        	throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_AUTHENTICATE_FAIL, e);
//        } catch (NoSuchAlgorithmException e) {
//        	throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_AUTHENTICATE_FAIL, e);
//        } catch (NoSuchPaddingException e) {
//        	throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_AUTHENTICATE_FAIL, e);
//        } catch (InvalidKeyException e) {
//        	throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_AUTHENTICATE_FAIL, e);
//        } catch (InvalidAlgorithmParameterException e) {
//        	throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_AUTHENTICATE_FAIL, e);
//        }
//    }
    @Override
	public void encryptKEK(char [] password, byte[] data, OnResultListener listener) throws IWException { 
    	
    }

    @Override
    public void decryptKEK(char [] password, byte[] encData, OnResultListener listener) throws IWException {
    	
    }
}
