package org.omnione.did.wallet.key.extend;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.HashMap;
import java.util.Map;

import org.spongycastle.util.Arrays;
import org.spongycastle.util.encoders.Base64;

import org.omnione.did.wallet.crypto.GDPCryptoHelperClient;
import org.omnione.did.wallet.crypto.GDPCryptoHelperServer;
import org.omnione.did.wallet.crypto.GDPCryptoHelperServer.CurveParamEnum;
import org.omnione.did.wallet.eoscommander.crypto.digest.Sha256;
import org.omnione.did.wallet.eoscommander.crypto.digest.Sha512;
import org.omnione.did.wallet.eoscommander.crypto.ec.CurveParam;
import org.omnione.did.wallet.eoscommander.crypto.ec.EcDsa;
import org.omnione.did.wallet.eoscommander.crypto.ec.EcSignature;
import org.omnione.did.wallet.eoscommander.crypto.ec.EcTools;
import org.omnione.did.wallet.eoscommander.crypto.ec.EosPrivateKey;
import org.omnione.did.wallet.eoscommander.crypto.ec.EosPublicKey;
import org.omnione.did.wallet.eoscommander.crypto.util.Base58;
import org.omnione.did.wallet.eoscommander.crypto.util.HexUtils;
import org.omnione.did.wallet.exception.IWErrorCode;
import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.key.IWKeyManagerImpl;
import org.omnione.did.wallet.key.data.AESType;
import org.omnione.did.wallet.key.data.IWKey;
import org.omnione.did.wallet.key.data.IWKey.ALGORITHM_TYPE;
import org.omnione.did.wallet.key.data.IWKeyElement;
import org.omnione.did.wallet.key.store.IWKeyFile;
import org.omnione.did.wallet.util.GDPBase58;
import org.omnione.did.wallet.util.GDPLogger;
import org.omnione.did.wallet.util.RsaKeyPair;

public class IWEosKeyManagerAdapter extends IWKeyManagerImpl {

	/**
	 * 서버에서 사용할 키 맵
	 * tykim: 서버쪽 매번 키파일을 로드하지 않도록 수정함
	 */
	public Map<String, EosPrivateKey> privateKeyMap = new HashMap<String, EosPrivateKey>();
	public Map<String, EosPublicKey> publicKeyMap = new HashMap<String, EosPublicKey>();
	// mDL - RSA 추가
	public Map<String, PrivateKey> rsaPrivateKeyMap = new HashMap<String, PrivateKey>();
	public Map<String, PublicKey> rsaPublicKeyMap = new HashMap<String, PublicKey>();

	public IWEosKeyManagerAdapter(String keyFile_pathWithName, char [] pwd) throws IWException {
		super(keyFile_pathWithName, pwd);
	}

	//For KTFC
	public IWEosKeyManagerAdapter(String keyFile_pathWithName, char [] pwd, AESType aesType) throws IWException {
		super(keyFile_pathWithName, pwd, aesType);
	}

	// for android
	public IWEosKeyManagerAdapter(String keyFile_pathWithName) throws IWException {
		super(keyFile_pathWithName);
	}

	@Override
	protected IWKey getIWKeyFromIWKeyElement(IWKeyElement iwKeyElement) throws IWException {
		String keyId = iwKeyElement.getKeyId();

		if(iwKeyElement.getAlgString().equals(ALGORITHM_TYPE.ALGORITHM_TYPE_RSA.toString())) {
			PublicKey pubKey = getPublicKeyObject(iwKeyElement);
			PrivateKey priKey =  getPrivateKey(iwKeyElement);
			IWKey iwKey = new IWKey(keyId, ALGORITHM_TYPE.ALGORITHM_TYPE_RSA.getValue(), pubKey, priKey);
			return iwKey;

		}else {
			EosPublicKey pubKey = getEosPublicKeyObject(keyId);
			EosPrivateKey priKey = getPrivateKey(keyId);
			IWKey iwKey = new IWKey(keyId, ALGORITHM_TYPE.ALGORITHM_SECP256k1.getValue(), pubKey, priKey);//
			return iwKey;
		}

	}

	@Override
	protected byte[] getPrivateKeyBytes(IWKey iwKey) {
		byte[] privateBytes = null;
		if(iwKey.getAlg() == ALGORITHM_TYPE.ALGORITHM_SECP256k1.getValue()) {
			privateBytes =  iwKey.getEosPriKey().getBytes();
		}else if(iwKey.getAlg() == ALGORITHM_TYPE.ALGORITHM_TYPE_RSA.getValue()) {
			privateBytes =  iwKey.getPriKey().getEncoded();
		}
		return privateBytes;
	}

	@Override
	protected byte[] getPublicKeyBytes(IWKey iwKey) {
		byte[] publicKeyBytes = null;
		if(iwKey.getAlg() == ALGORITHM_TYPE.ALGORITHM_SECP256k1.getValue()) {
			publicKeyBytes =  iwKey.getEosPubKey().getBytes();
		}else if(iwKey.getAlg() == ALGORITHM_TYPE.ALGORITHM_TYPE_RSA.getValue()) {
			publicKeyBytes =  iwKey.getPubKey().getEncoded();
		}
		return publicKeyBytes;
	}

	@Override
	protected byte[] getSignBytes(String keyId, byte[] source) throws IWException {
		EosPrivateKey key = getPrivateKey(keyId);
		if(key == null) {
			GDPLogger.debug("ERR_CODE_KEYMANAGER_KEYID_NOT_EXIST - " + keyId);
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_KEYID_NOT_EXIST.getCode(), keyId);
		}
		return sign(key, source);
	}

	@Override
	protected void verify(byte[] pubkey_rw, byte[] source, byte[] signature) throws IWException {
		CurveParam curveParam = EcTools.getCurveParam(CurveParam.SECP256_K1);

		EcSignature ecSignature = new EcSignature(signature);

		Sha256 hash = Sha256.from(source);
		byte[] data = hash.getBytes();

		EosPublicKey pubKey = new EosPublicKey(pubkey_rw);

		for (int i = 0; i < 4; i++) {
			EosPublicKey recovered = EcDsa.recoverPubKey(curveParam, data, ecSignature, i);
			if (pubKey.equals(recovered)) {
				ecSignature.setRecid(i);
				// success
				return;
			}
		}

		if (ecSignature.recId < 0) {
			throw new IllegalStateException("could not find recid. Was this data signed with this key?");
		}
		throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_VERIFY_SIGN_FAIL);
	}

	private byte[] sign(EosPrivateKey key, byte[] source) {
		// ref com.raonsecure.gdp.key.GDPIssueReg
		Sha256 sha256 = Sha256.from(source);
		EcSignature ecSignature = EcDsa.sign(sha256, key);
		byte[] signature = ecSignature.eosEncoding(true);
		return signature;
	}

	private EosPrivateKey getPrivateKey(String keyId) throws IWException {
		if(!isUnLock())
			return null;

		/**
		 * 서버에서 사용할 키 맵
		 * tykim: 서버쪽 매번 키파일을 로드하지 않도록 수정함
		 */
		if(IWKeyFile.ONETIME_LOAD) {
			EosPrivateKey privKey = privateKeyMap.get(keyId);
			if(privKey != null) {
				return privKey;
			}
		}


		IWKeyElement key = getIWKey(keyId);
		if(key == null)
			return null;

		byte[] encPrivKey = Base58.decode(key.getPrivateKey());
		byte[] decPrivKey = symDecPrivateKey(encPrivKey);

		EosPrivateKey privKey = new EosPrivateKey(decPrivKey);

		if(IWKeyFile.ONETIME_LOAD) {
			privateKeyMap.put(keyId, privKey);
		}

		return privKey;
	}


	//mDL - RSA
	private PrivateKey getPrivateKey(IWKeyElement iwKeyElement) throws IWException {
		if(!isUnLock())
			return null;

		/**
		 * 서버에서 사용할 키 맵
		 * tykim: 서버쪽 매번 키파일을 로드하지 않도록 수정함
		 */

		if(IWKeyFile.ONETIME_LOAD) {
			PrivateKey privKey = rsaPrivateKeyMap.get(iwKeyElement.getKeyId());
			if(privKey != null) {
				return privKey;
			}
		}


		IWKeyElement key = getIWKey(iwKeyElement.getKeyId());
		if(key == null)
			return null;

		byte[] encPrivKey = Base58.decode(key.getPrivateKey());
		byte[] decPrivKey = symDecPrivateKey(encPrivKey);


		PrivateKey privKey = new RsaKeyPair().getPrivateKey(decPrivKey);

		if(IWKeyFile.ONETIME_LOAD) {
			rsaPrivateKeyMap.put(iwKeyElement.getKeyId(), privKey);
		}

		return privKey;
	}

	private EosPublicKey getEosPublicKeyObject(String keyId) throws IWException {
		if(!isUnLock())
			return null;

		/**
		 * 서버에서 사용할 키 맵
		 * tykim: 서버쪽 매번 키파일을 로드하지 않도록 수정함
		 */
		if(IWKeyFile.ONETIME_LOAD) {
			EosPublicKey pubKey = publicKeyMap.get(keyId);
			if(pubKey != null) {
				return pubKey;
			}
		}


		IWKeyElement key = getIWKey(keyId);
		if(key == null)
			return null;

		byte[] pubKey0;
		try {
			pubKey0 = GDPBase58.decode(key.getPublicKey());
			EosPublicKey pubKey = new EosPublicKey(pubKey0);

			if(IWKeyFile.ONETIME_LOAD) {
				publicKeyMap.put(keyId, pubKey);
			}


			return pubKey;
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}

	//mDL - RSA
	private PublicKey getPublicKeyObject(IWKeyElement iwKeyElement) throws IWException {
		if(!isUnLock())
			return null;

		/**
		 * 서버에서 사용할 키 맵
		 * tykim: 서버쪽 매번 키파일을 로드하지 않도록 수정함
		 */
		if(IWKeyFile.ONETIME_LOAD) {
			PublicKey pubKey = rsaPublicKeyMap.get(iwKeyElement.getKeyId());
			if(pubKey != null) {
				return pubKey;
			}
		}


		IWKeyElement key = getIWKey(iwKeyElement.getKeyId());
		if(key == null)
			return null;

		byte[] pubKey0;
		try {
			pubKey0 = GDPBase58.decode(key.getPublicKey());
			PublicKey pubKey = new RsaKeyPair().getPublicKey(pubKey0);

			if(IWKeyFile.ONETIME_LOAD) {
				rsaPublicKeyMap.put(iwKeyElement.getKeyId(), pubKey);
			}


			return pubKey;
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}
	@Override
	public byte[] getECIESEncryptBytes(String keyId, byte[] nonce, String publicKey, byte[] source, AESType aesType) throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		EosPrivateKey eosPrivateKey = getPrivateKey(keyId);
		return getInnerECIESEncryptBytes(nonce, publicKey, eosPrivateKey, source, aesType);
	}

	@Override
	public byte[] getECIESDecryptBytes(String keyId, byte[] nonce, String publicKey, byte[] source, AESType aesType) throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		EosPrivateKey eosPrivateKey = getPrivateKey(keyId);

		if(eosPrivateKey == null) {
			GDPLogger.debug("ERR_CODE_KEYMANAGER_KEYID_NOT_EXIST - " + keyId);
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_KEYID_NOT_EXIST.getCode(), keyId);
		}

		return getInnerECIESDecryptBytes(nonce, publicKey, eosPrivateKey, source, aesType);

	}

	@Override
	public byte[] getECIESEncryptBytes(byte[] nonce, String publicKey, String privateKey, byte[] source, AESType aesType) throws IWException {
		byte[] decodedPrivateKey = Base58.decode(privateKey);
		EosPrivateKey eosPrivateKey = new EosPrivateKey(decodedPrivateKey);
		return getInnerECIESEncryptBytes(nonce, publicKey, eosPrivateKey, source, aesType);
	}

	@Override
	public byte[] getECIESDecryptBytes(byte[] nonce, String publicKey, String privateKey, byte[] source, AESType aesType) throws IWException {
		byte[] decodedPrivateKey = Base58.decode(privateKey);
		EosPrivateKey eosPrivateKey = new EosPrivateKey(decodedPrivateKey);
		return getInnerECIESDecryptBytes(nonce, publicKey, eosPrivateKey, source, aesType);
	}


	private byte[] getInnerECIESEncryptBytes(byte[] nonce, String publicKey, EosPrivateKey eosPrivateKey, byte[] source, AESType aesType) throws IWException {

		if(aesType != AESType.AES128 && aesType != AESType.AES256) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_OUT_OF_RANGE_VALUE);
		}

		GDPCryptoHelperServer cryptoHelperServer = new GDPCryptoHelperServer();
		ECPrivateKey ecPrivateKey = cryptoHelperServer.eosPriKeyToECPriKey(eosPrivateKey.getBytes(), CurveParamEnum.SECP256_K1);
		ECPublicKey otherPublicKey = null;
		try {
			byte[] decodedPublicKey = Base58.decode(publicKey);
			otherPublicKey = cryptoHelperServer.getEcPublicKey(decodedPublicKey, CurveParamEnum.SECP256_K1);
		} catch (Exception e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_INVALID_PUBLIC_KEY);
		}

		byte[] sharedSecret = null;
		byte[] nonceAndSecret = null;
		byte[] hash = null;
		byte[] k = null;
		byte[] iv = null;
		byte[] enc = null;
		try {
			sharedSecret = cryptoHelperServer.getSharedSecret(ecPrivateKey, otherPublicKey);


			nonceAndSecret = Arrays.concatenate(nonce, sharedSecret);

			if (aesType == AESType.AES128) {
				Sha256 sha256 = Sha256.from(nonceAndSecret);
				hash = sha256.getBytes();
			}
			else {
				Sha512 sha512 = Sha512.from(nonceAndSecret);
				hash = sha512.getBytes();
			}

			GDPCryptoHelperClient cryptoHelperClient = new GDPCryptoHelperClient();

			int length = aesType == AESType.AES256 ? 32 : 16;
			k = Arrays.copyOfRange(hash, 0, length);
			iv = Arrays.copyOfRange(hash, length, length + 16);



			enc = cryptoHelperClient.encrypt(k, iv, source);



		} catch (InvalidKeyException e) {
			e.printStackTrace();
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_INVALID_PRIVATE_KEY);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_GEN_SECRET_FAIL);
		}catch (NoSuchProviderException e) {
			e.printStackTrace();
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_GEN_SECRET_FAIL);

		}

		if (sharedSecret != null) {
			Arrays.fill(sharedSecret, (byte) 0);
		}
		else {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_GEN_SECRET_FAIL);
		}

		if (nonceAndSecret != null)
			Arrays.fill(nonceAndSecret, (byte) 0);

		if (hash != null)
			Arrays.fill(hash, (byte) 0);

		if (k != null)
			Arrays.fill(k, (byte) 0);

		if (iv != null)
			Arrays.fill(iv, (byte) 0);

		if(enc == null) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_AES_ENCRYPT_FAIL);
		}

		return enc;
	}

	private byte[] getInnerECIESDecryptBytes(byte[] nonce, String publicKey, EosPrivateKey eosPrivateKey, byte[] source, AESType aesType) throws IWException {
		if(aesType != AESType.AES128 && aesType != AESType.AES256) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_OUT_OF_RANGE_VALUE);
		}

		GDPLogger.debug("nonce:" + HexUtils.toHex(nonce));
		GDPLogger.debug("publicKey:" + publicKey);
		GDPLogger.debug("eosPrivateKey:" + eosPrivateKey.toString());



		GDPCryptoHelperServer cryptoHelperServer = new GDPCryptoHelperServer();
		ECPrivateKey ecPrivateKey = cryptoHelperServer.eosPriKeyToECPriKey(eosPrivateKey.getBytes(), CurveParamEnum.SECP256_K1);
		ECPublicKey otherPublicKey = null;
		try {
			byte[] decodedPublicKey = Base58.decode(publicKey);
			otherPublicKey = cryptoHelperServer.getEcPublicKey(decodedPublicKey, CurveParamEnum.SECP256_K1);
		} catch (Exception e) {
//			e.printStackTrace();
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_INVALID_PUBLIC_KEY);
		}

		byte[] sharedSecret = null;
		byte[] nonceAndSecret = null;
		byte[] hash = null;
		byte[] k = null;
		byte[] iv = null;
		byte[] dec = null;
		try {
			sharedSecret = cryptoHelperServer.getSharedSecret(ecPrivateKey, otherPublicKey);


			if(nonce !=null) {

			}
			nonceAndSecret = Arrays.concatenate(nonce, sharedSecret);

			if (aesType == AESType.AES128) {
				Sha256 sha256 = Sha256.from(nonceAndSecret);
				hash = sha256.getBytes();
			}
			else {
				Sha512 sha512 = Sha512.from(nonceAndSecret);
				hash = sha512.getBytes();
			}



			GDPCryptoHelperClient cryptoHelperClient = new GDPCryptoHelperClient();

			int length = aesType == AESType.AES256 ? 32 : 16;
			k = Arrays.copyOfRange(hash, 0, length);
			iv = Arrays.copyOfRange(hash, length, length + 16);



			dec = cryptoHelperClient.decrypt(k, iv, source);

		} catch (InvalidKeyException e) {
			e.printStackTrace();
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_INVALID_PRIVATE_KEY);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_GEN_SECRET_FAIL);
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_GEN_SECRET_FAIL);

		}

		if (sharedSecret != null) {
			Arrays.fill(sharedSecret, (byte) 0);
		}
		else {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_GEN_SECRET_FAIL);
		}

		if (nonceAndSecret != null)
			Arrays.fill(nonceAndSecret, (byte) 0);

		if (hash != null)
			Arrays.fill(hash, (byte) 0);

		if (k != null)
			Arrays.fill(k, (byte) 0);

		if (iv != null)
			Arrays.fill(iv, (byte) 0);

		if(dec == null) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_AES_DECRYPT_FAIL);
		}

		return dec;
	}

	/**
	 * ECDH 암호화시 Curve 값을 secp256r1, secp256k1 선택하여 암호화.
	 */
	public byte[] getECIESEncryptBytes(byte[] nonce, String publicKey, String privateKey, byte[] source, AESType aesType, int curveParam) throws IWException {
		if (curveParam == CurveParam.SECP256_K1) {
			return getECIESEncryptBytes(nonce, publicKey, privateKey, source, aesType);
		} else {
			byte[] decodedPrivateKey = Base58.decode(privateKey);
			EosPrivateKey eosPrivateKey = new EosPrivateKey(curveParam, decodedPrivateKey);
			return getInnerECIESEncryptBytesV2(nonce, publicKey, eosPrivateKey, source, aesType, curveParam);
		}
	}

	/**
	 * ECDH 복호화시 Curve 값을 secp256r1, secp256k1 선택하여 복호화.
	 */
	public byte[] getECIESDecryptBytes(byte[] nonce, String publicKey, String privateKey, byte[] source, AESType aesType, int curveParam) throws IWException {
		if (curveParam == CurveParam.SECP256_K1) {
			return getECIESDecryptBytes(nonce, publicKey, privateKey, source, aesType);
		} else {
			byte[] decodedPrivateKey = Base58.decode(privateKey);
			EosPrivateKey eosPrivateKey = new EosPrivateKey(curveParam, decodedPrivateKey);

			return getInnerECIESDecryptBytesV2(nonce, publicKey, eosPrivateKey, source, aesType, curveParam);
		}
	}

	/**
	 * ECDH 를 이용하여 암호화.
	 */
	private byte[] getInnerECIESEncryptBytesV2(byte[] nonce, String publicKey, EosPrivateKey eosPrivateKey, byte[] source, AESType aesType, int curveParam) throws IWException {

		if(aesType != AESType.AES128 && aesType != AESType.AES256) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_OUT_OF_RANGE_VALUE);
		}

		GDPCryptoHelperServer cryptoHelperServer = new GDPCryptoHelperServer();
		CurveParamEnum curveParamEnum = curveParam == CurveParam.SECP256_K1 ? CurveParamEnum.SECP256_K1 : CurveParamEnum.SECP256_R1;
		ECPrivateKey ecPrivateKey = cryptoHelperServer.eosPriKeyToECPriKeyV2(eosPrivateKey.getBytes(), curveParamEnum);
		ECPublicKey otherPublicKey = null;
		try {
			byte[] decodedPublicKey = Base58.decode(publicKey);
			otherPublicKey = cryptoHelperServer.getEcPublicKeyV2(decodedPublicKey, curveParamEnum);
		} catch (Exception e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_INVALID_PUBLIC_KEY);
		}

		byte[] sharedSecret = null;
		byte[] nonceAndSecret = null;
		byte[] hash = null;
		byte[] k = null;
		byte[] iv = null;
		byte[] enc = null;
		try {
			sharedSecret = cryptoHelperServer.getSharedSecret(ecPrivateKey, otherPublicKey);
			GDPLogger.printHex("enc sharedSecret", sharedSecret);
			nonceAndSecret = Arrays.concatenate(nonce, sharedSecret);

			if (aesType == AESType.AES128) {
				Sha256 sha256 = Sha256.from(nonceAndSecret);
				hash = sha256.getBytes();
			}
			else {
				Sha512 sha512 = Sha512.from(nonceAndSecret);
				hash = sha512.getBytes();
			}

			GDPCryptoHelperClient cryptoHelperClient = new GDPCryptoHelperClient();

			int length = aesType == AESType.AES256 ? 32 : 16;
			k = Arrays.copyOfRange(hash, 0, length);
			iv = Arrays.copyOfRange(hash, length, length + 16);

			enc = cryptoHelperClient.encrypt(k, iv, source);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_INVALID_PRIVATE_KEY);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_GEN_SECRET_FAIL);
		}catch (NoSuchProviderException e) {
			e.printStackTrace();
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_GEN_SECRET_FAIL);
		}

		if (sharedSecret != null) {
			Arrays.fill(sharedSecret, (byte) 0);
		}
		else {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_GEN_SECRET_FAIL);
		}

		if (nonceAndSecret != null)
			Arrays.fill(nonceAndSecret, (byte) 0);

		if (hash != null)
			Arrays.fill(hash, (byte) 0);

		if (k != null)
			Arrays.fill(k, (byte) 0);

		if (iv != null)
			Arrays.fill(iv, (byte) 0);

		if(enc == null) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_AES_ENCRYPT_FAIL);
		}
		GDPLogger.printHex("new enc ", enc);
		return enc;
	}

	/**
	 * ECDH 를 이용하여 복호화.
	 */
	private byte[] getInnerECIESDecryptBytesV2(byte[] nonce, String publicKey, EosPrivateKey eosPrivateKey, byte[] source, AESType aesType, int curveParam) throws IWException {
		if(aesType != AESType.AES128 && aesType != AESType.AES256) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_OUT_OF_RANGE_VALUE);
		}

		GDPLogger.debug("nonce:" + HexUtils.toHex(nonce));
		GDPLogger.debug("publicKey:" + publicKey);
		GDPLogger.debug("eosPrivateKey:" + eosPrivateKey.toString());

		GDPCryptoHelperServer cryptoHelperServer = new GDPCryptoHelperServer();
		CurveParamEnum curveParamEnum = curveParam == CurveParam.SECP256_K1 ? CurveParamEnum.SECP256_K1 : CurveParamEnum.SECP256_R1;
		ECPrivateKey ecPrivateKey = cryptoHelperServer.eosPriKeyToECPriKeyV2(eosPrivateKey.getBytes(), curveParamEnum);
		ECPublicKey otherPublicKey = null;
		try {
			byte[] decodedPublicKey = Base58.decode(publicKey);
			otherPublicKey = cryptoHelperServer.getEcPublicKeyV2(decodedPublicKey, curveParamEnum);
		} catch (Exception e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_INVALID_PUBLIC_KEY);
		}

		byte[] sharedSecret = null;
		byte[] nonceAndSecret = null;
		byte[] hash = null;
		byte[] k = null;
		byte[] iv = null;
		byte[] dec = null;
		try {
			sharedSecret = cryptoHelperServer.getSharedSecret(ecPrivateKey, otherPublicKey);
			GDPLogger.printHex("dec sharedSecret", sharedSecret);
			GDPLogger.print("dec sharedSecret hex : " + HexUtils.toHex(sharedSecret));
			nonceAndSecret = Arrays.concatenate(nonce, sharedSecret);

			if (aesType == AESType.AES128) {
				Sha256 sha256 = Sha256.from(nonceAndSecret);
				hash = sha256.getBytes();
			}
			else {
				Sha512 sha512 = Sha512.from(nonceAndSecret);
				hash = sha512.getBytes();
			}

			GDPCryptoHelperClient cryptoHelperClient = new GDPCryptoHelperClient();

			int length = aesType == AESType.AES256 ? 32 : 16;
			k = Arrays.copyOfRange(hash, 0, length);
			iv = Arrays.copyOfRange(hash, length, length + 16);

			dec = cryptoHelperClient.decrypt(k, iv, source);

		} catch (InvalidKeyException e) {
			e.printStackTrace();
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_INVALID_PRIVATE_KEY);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_GEN_SECRET_FAIL);
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_GEN_SECRET_FAIL);
		}

		if (sharedSecret != null) {
			Arrays.fill(sharedSecret, (byte) 0);
		}
		else {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_ECIES_GEN_SECRET_FAIL);
		}

		if (nonceAndSecret != null)
			Arrays.fill(nonceAndSecret, (byte) 0);

		if (hash != null)
			Arrays.fill(hash, (byte) 0);

		if (k != null)
			Arrays.fill(k, (byte) 0);

		if (iv != null)
			Arrays.fill(iv, (byte) 0);

		if(dec == null) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_AES_DECRYPT_FAIL);
		}

		return dec;
	}

	@Override
	public void setSlot(int slot) {
		// TODO Auto-generated method stub

	}

}
