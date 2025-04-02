package org.omnione.did.wallet.key;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.spongycastle.util.encoders.Hex;

import com.google.gson.Gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.omnione.did.wallet.crypto.GDPCryptoHelperClient;
import org.omnione.did.wallet.data.did.AddSignData;
import org.omnione.did.wallet.data.did.DIDs;
import org.omnione.did.wallet.data.did.Proof;
import org.omnione.did.wallet.data.did.PublicKey;

import org.omnione.did.wallet.data.iw.*;
import org.omnione.did.wallet.data.iw.Claim;
import org.omnione.did.wallet.data.iw.Copy;
import org.omnione.did.wallet.data.iw.Extension;
import org.omnione.did.wallet.data.iw.Issuer;
import org.omnione.did.wallet.data.iw.Privacy;
import org.omnione.did.wallet.data.iw.Unprotected;
import org.omnione.did.wallet.data.iw.VerifiableClaim;
import org.omnione.did.wallet.data.iw.profile.AbstractProfile;
import org.omnione.did.wallet.data.iw.profile.IssueProfile;
import org.omnione.did.wallet.data.iw.profile.VerifyProfile;
import org.omnione.did.wallet.data.iw.v2.CredentialSubject;
import org.omnione.did.wallet.data.iw.v2.VerifiableCredential;
import org.omnione.did.wallet.data.iw.v2.VerifiablePresentation;

import org.omnione.did.wallet.eoscommander.crypto.digest.Sha256;
import org.omnione.did.wallet.eoscommander.crypto.ec.EosPrivateKey;
import org.omnione.did.wallet.eoscommander.crypto.ec.EosPublicKey;
import org.omnione.did.wallet.eoscommander.crypto.util.Base58;
import org.omnione.did.wallet.eoscommander.crypto.util.HexUtils;
import org.omnione.did.wallet.exception.IWErrorCode;
import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.key.data.*;
import org.omnione.did.wallet.key.data.IWKey.ALGORITHM_TYPE;
import org.omnione.did.wallet.key.data.IWKeyStoreEncodingElement.ENCODING_TYPE;
import org.omnione.did.wallet.key.store.IWKeyFile;
import org.omnione.did.wallet.key.store.IWKeyStore.IWKeyStoreHepler;
import org.omnione.did.wallet.key.store.IWKeyStore.OnResultListener;
import org.omnione.did.wallet.key.store.IWKeyStoreDefault;
import org.omnione.did.wallet.util.GDPBase58;
import org.omnione.did.wallet.util.GDPBase64;
import org.omnione.did.wallet.util.GDPLogger;
//import org.omnione.did.wallet.util.OmniOneLicenseChcker;
import org.omnione.did.wallet.util.RSAEncryptUtil;
import org.omnione.did.wallet.util.RsaKeyPair;

import org.omnione.did.wallet.zkp.data.Credential;
import org.omnione.did.wallet.zkp.data.MasterSecret;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.data.dto.*;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpLogger;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpSetting;
import org.omnione.did.wallet.zkp.util.gson.ZkpGsonWrapper;
import org.spongycastle.util.encoders.Hex;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class IWKeyManagerImpl extends IWManager implements IWKeyManagerInterface {

	private IWKeyStoreHepler iwK;
	protected IWKeyFile iwF;
	protected GDPCryptoHelperClient cryptoHelper;
	private byte[] proxyKey = null;


	private final static String ALGORITHM_VERIFICATION = "VerificationKey2018";

	/**
	 * (Default) KeyStore 생성자 : Password 인증
	 *
	 * @param walletfile_pathWithName
	 * @param pwd
	 * @throws Exception
	 */
	public IWKeyManagerImpl(String walletfile_pathWithName, char[] pwd) throws IWException {
		init(walletfile_pathWithName, pwd, AESType.AES256);
	}

	public IWKeyManagerImpl(String walletfile_pathWithName, byte[] pwd) throws IWException {
		char[] chars = new char[pwd.length];
		for (int i = 0; i < pwd.length; i++)
			chars[i] = (char) pwd[i];

		init(walletfile_pathWithName, chars, AESType.AES256);
	}

	//For KTFC
	public IWKeyManagerImpl(String walletfile_pathWithName, char[] pwd, AESType aesType) throws IWException {
		init(walletfile_pathWithName, pwd, aesType);
	}

	public IWKeyManagerImpl(String walletfile_pathWithName, byte[] pwd, AESType aesType) throws IWException {
		char[] chars = new char[pwd.length];
		for (int i = 0; i < pwd.length; i++)
			chars[i] = (char) pwd[i];

		init(walletfile_pathWithName, chars, aesType);
	}

	// for android
	public IWKeyManagerImpl(String walletfile_pathWithName) throws IWException {
		init(walletfile_pathWithName);
	}

	/**
	 * (Default) KeyStore 생성자 초기화
	 *
	 * @param walletfile_pathWithName
	 * @param pwd
	 * @throws IWException
	 */
	private void init(String walletfile_pathWithName, char[] pwd, AESType aesType) throws IWException {

//		OmniOneLicenseChcker.check();

		cryptoHelper = new GDPCryptoHelperClient();

		iwF = new IWKeyFile(walletfile_pathWithName);
		if (!iwF.isExist()) {
			// generate & save a symmetric key parameters
			IWKeyStoreData data = new IWKeyStoreData();
			IWHeadElement head = new IWHeadElement();

			if( aesType == AESType.AES128) {
				head.setEncType("AES128");
			}
			else {
				head.setEncType("AES256");
			}

			head.setIterations(2048);
			head.setSalt(Base58.encode(cryptoHelper.generateSalt()));

			IWKeyStoreEncodingElement encoding = new IWKeyStoreEncodingElement();

			encoding.setKey(ENCODING_TYPE.ENCODING_BASE58.getValue());
			encoding.setPrivacy(ENCODING_TYPE.ENCODING_HEXA_DECIMAL.getValue());

			head.setEncoding(encoding);

			data.setHead(head);
			iwF.write(data);
		}

		iwK = new IWKeyStoreDefault(iwF);
		if (!iwK.isExistWrapKey()) {
			// generate & save a wrapKey
			iwK.genWrapKey(pwd);
		}
	}

	// for android
	private void init(String walletfile_pathWithName) throws IWException {
//		OmniOneLicenseChcker.check();

		cryptoHelper = new GDPCryptoHelperClient();

		iwF = new IWKeyFile(walletfile_pathWithName);
		if (!iwF.isExist()) {
			// generate & save a symmetric key parameters
			IWKeyStoreData data = new IWKeyStoreData();
			IWHeadElement head = new IWHeadElement();
			head.setEncType("AES256");
			head.setIterations(2048);
			head.setSalt(Base58.encode(cryptoHelper.generateSalt()));

			IWKeyStoreEncodingElement encoding = new IWKeyStoreEncodingElement();

			encoding.setKey(ENCODING_TYPE.ENCODING_BASE58.getValue());
			encoding.setPrivacy(ENCODING_TYPE.ENCODING_HEXA_DECIMAL.getValue());

			head.setEncoding(encoding);

			data.setHead(head);
			iwF.write(data);
		}

	}

	// for android
	// modified by joshua
	private void generateWrapKey(char[] pwd) throws IWException {
		iwK = new IWKeyStoreDefault(iwF);
		if (!iwK.isExistWrapKey()) {
			// generate & save a wrapKey
			iwK.genWrapKey(pwd);

		}
	}

	// modified ends

	@Override
	public IWHeadElement getHeader() {
		IWKeyStoreData data = null;
		try {
			data = iwF.getData();
		} catch (IWException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return null;
		}
		IWHeadElement head = data.getHead();

		return head;
	}

	/**
	 * WrapKey를 Memory에서 로드 했는지 여부
	 *
	 * @return
	 */
	@Override
	public boolean isUnLock() {
		return (proxyKey != null ? true : false);
	}

	/**
	 * WrapKey를 Memory에서 제거
	 *
	 * @return
	 */
	@Override
	public boolean lock() {
		if (proxyKey != null) {
			Arrays.fill(proxyKey, (byte) 0x00);
			proxyKey = null;
		}

		// modified by joshua
		iwF.clearData();
		// modified ends

		return true;
	}

	/**
	 * 비밀번호 등록되어있는지 확인하기 (dlchoi)
	 *
	 *
	 */

	@Override
	public boolean isPasswordSet() {
		IWHeadElement header = getHeader();
		if (header == null) {
			return false;
		} else {
			if (header.getProxyKey() == null) {
				return false;
			} else {
				return true;
			}
		}

	}

	/**
	 * WrapKey를 Memory위에 로드
	 *
	 * @param pwdOrAlias
	 * @param listener
	 * @throws Exception
	 */

	@Override
	public void unLock(byte[] pwdOrAlias, final OnUnLockListener listener) throws IWException {
		char[] chars = new char[pwdOrAlias.length];
		for (int i = 0; i < pwdOrAlias.length; i++)
			chars[i] = (char) pwdOrAlias[i];

		unLock(chars, listener);
	}

	@Override
	public void unLock(char[] pwdOrAlias, final OnUnLockListener listener) throws IWException {
//		OmniOneLicenseChcker.check();

		if (pwdOrAlias == null || pwdOrAlias.length == 0) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_PASSWORD);
		}

		// 없으면 생성.. 있으면 셋팅..
		generateWrapKey(pwdOrAlias);

		if (proxyKey != null) {
			listener.onSuccess();
			return;
		}

		// AndroidKeyStore 사용하는 경우
		if (isAndroidKeyStore()) {
			if (isNeedGenerateProxyKey()) {
				// generate Proxy Key (=P)
				proxyKey = cryptoHelper.generateSecureRandom(32);

				// generate Encrypted Proxy Key & save in KeyFile
				iwK.encryptKEK(pwdOrAlias, proxyKey, new OnResultListener() {

					@Override
					public void onSuccess(byte[] result) {
						/*
						 * Key Structure - head field encType string salt string (base58, 32byte)
						 * iterator int proxyKey string (base58)
						 */
						String encProxyKey = Base58.encode(result);

						try {
							IWKeyStoreData data = iwF.getData();
							IWHeadElement head = data.getHead();
							head.setProxyKey(encProxyKey);
							data.setHead(head);
							iwF.write(data);
						} catch (IWException e) {
							lock();
							listener.onFail(e.getErrorCode());
						}
						listener.onSuccess();
					}

					@Override
					public void onFail(int errCode) {
						lock();
						listener.onFail(errCode);
					}

					@Override
					public void onCancel() {
						lock();
						listener.onCancel();
					}
				});
			} else {
				// get Decrypted Proxy Key
				byte[] encProxyKey = Base58.decode(iwF.getData().getHead().getProxyKey());

				iwK.decryptKEK(pwdOrAlias, encProxyKey, new OnResultListener() {

					@Override
					public void onSuccess(byte[] result) {
						GDPLogger.print("info", "decryptKEK->proxyKey : " + Hex.toHexString(result));

						if (result != null) {
							proxyKey = new byte[result.length];
							System.arraycopy(result, 0, proxyKey, 0, result.length);
						} else {
							proxyKey = null;
						}

						listener.onSuccess();
					}

					@Override
					public void onFail(int errCode) {
						lock();
						listener.onFail(errCode);
					}

					@Override
					public void onCancel() {
						lock();
						listener.onCancel();
					}
				});
			}
		}
		// 내장 기본 KeyStore 사용하는 경우 ?(server- unlock 포함 .. )
		else {

			iwK.authenticate(pwdOrAlias, new OnResultListener() {

				@Override
				public void onSuccess(byte[] result) {

					if (result != null) {
						proxyKey = new byte[result.length];
						System.arraycopy(result, 0, proxyKey, 0, result.length);
					} else {
						proxyKey = null;
					}

					try {
						checkHeaderVersion();
					} catch (IWException e) {
						lock();
						listener.onFail(e.getErrorCode());
					}

					listener.onSuccess();
				}

				@Override
				public void onFail(int errCode) {
					lock();
					listener.onFail(errCode);
				}

				@Override
				public void onCancel() {
					lock();
					listener.onCancel();
				}
			});


		}
	}

	@Override
	public byte[] rsaEncrypt(String keyId, byte[] plainData,  AESType aesType) throws IWException {
		byte[] sortedData = new byte[0];

		// 대칭키 생성 random to hash
		byte[] randomKey = new GDPCryptoHelperClient().generateNonce();
		byte[] randomHash = Sha256.from(randomKey).getBytes();
		// AES256(randomHash) 암호화
		byte[] encData = GDPCryptoHelperClient.aesEncrypt(plainData, randomHash, aesType);

		// wallet 에서 RSA 공개키 추출
		IWKey iwKey = this.getIWKeyFromIWKeyElement(getIWKey(keyId));
		// 대칭키(randomHash) RSA 암호화

		try {
			byte[] encKey = RSAEncryptUtil.encrypt(randomHash, iwKey.getPubKey(), "RSA");
			sortedData = new byte[2 + encKey.length + encData.length];
			System.arraycopy(intToBytes(encKey.length), 0, sortedData, 0, 2);
			System.arraycopy(encKey, 0, sortedData, 2, encKey.length);
			System.arraycopy(encData, 0, sortedData, 2 + encKey.length, encData.length);

			Arrays.fill(randomKey, (byte) 0x00);
			Arrays.fill(randomHash, (byte) 0x00);
			Arrays.fill(encKey, (byte) 0x00);
			Arrays.fill(encData, (byte) 0x00);
		} catch (Exception e) {
			new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_RSA_ENCRYPT_FAIL, e);
		}

		return sortedData;
	}

	@Override
	public byte[] rsaEncrypt(PublicKey publicKey, byte[] plainData,  AESType aesType) throws IWException {

		byte[] sortedData = new byte[0];

		// 대칭키 생성 random to hash
		byte[] randomKey = new GDPCryptoHelperClient().generateNonce();
		byte[] randomHash = Sha256.from(randomKey).getBytes();
		// AES256(randomHash) 암호화
		byte[] encData = GDPCryptoHelperClient.aesEncrypt(plainData, randomHash, aesType);
		// 대칭키(randomHash) RSA 암호화
		try {
			byte[] encKey = RSAEncryptUtil.encrypt(randomHash, new RsaKeyPair().getPublicKey(Base58.decode(publicKey.getPublicKeyBase58())), "RSA");
			sortedData = new byte[2 + encKey.length  + encData.length];
			System.arraycopy(intToBytes(encKey.length), 0, sortedData, 0, 2);
			System.arraycopy(encKey, 0, sortedData, 2, encKey.length);
			System.arraycopy(encData, 0, sortedData, 2 + encKey.length, encData.length);

			Arrays.fill(randomKey, (byte) 0x00);
			Arrays.fill(randomHash, (byte) 0x00);
			Arrays.fill(encKey, (byte) 0x00);
			Arrays.fill(encData, (byte) 0x00);
		} catch (Exception e) {
			new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_RSA_ENCRYPT_FAIL, e);
		}

		return sortedData;
	}

	@Override
	public byte[] rsaDecrypt(String keyId, byte[] encryptedData, AESType aesType) throws IWException {
		byte[] decMsg = new byte[0];

		// 대칭키 길이 조회.
		byte[] keyLen = new byte[2];
		System.arraycopy(encryptedData, 0, keyLen, 0, 2);
		// 대칭키 길이 조회 후, 해당 길이 만큼 대칭키 조회.
		byte[] encKey = new byte[byteToInt(keyLen)];
		System.arraycopy(encryptedData, 2, encKey, 0, byteToInt(keyLen));

		// 암호문 길이 조회 후, 해당 길이 만큼 암호문 조회.
		int encDataLen = encryptedData.length - 2 - byteToInt(keyLen);
		byte[] encData = new byte[encDataLen];
		System.arraycopy(encryptedData, 2 + byteToInt(keyLen), encData, 0, encDataLen);

		IWKey iwKey = this.getIWKeyFromIWKeyElement(getIWKey(keyId));
		try {

			byte[] key = RSAEncryptUtil.decrypt(encKey, iwKey.getPriKey(), "RSA");
			decMsg = GDPCryptoHelperClient.aesDecrypt(encData, key, aesType);
			Arrays.fill(key, (byte) 0);
			Arrays.fill(encKey, (byte) 0);
			Arrays.fill(encData, (byte) 0);
		} catch (Exception e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_RSA_DECRYPT_FAIL, e);
		}

		return decMsg;
	}

	private void checkHeaderVersion() throws IWException {
		IWHeadElement headElement = getHeader();
		int version = headElement.getVersion();

		if (version == 0) {
			List<VerifiableClaim> claims = new ArrayList<VerifiableClaim>();
			// privacy 복호화한 claim 얻기
			claims = getPreviousClaims();

			if (claims != null && claims.size() > 0) {
				for (int i = 0; i < claims.size(); i++) {
					VerifiableClaim vc = claims.get(i);
					addPreviousClaim(vc.toJson());
				}
				headElement.setVersion(1);
			}
		}
	}

	private byte[] generateDerivedKey(char[] source, AESType aesType) throws IWException {
		byte[] dk = null;

		int keyLength = (aesType == AESType.AES128)?16:32;
		// derive DK
		GDPCryptoHelperClient cryptoHelper = new GDPCryptoHelperClient();
		SecretKeySpec secret = cryptoHelper.getSecretKeySpecWithPBKDF2(source,
				Base58.decode(iwF.getData().getHead().getSalt()), iwF.getData().getHead().getIterations(),
				(keyLength + 16) * 8);

		// seperate K, IV
		dk = secret.getEncoded();

		return dk;

	}

	private String generateProxyKey(char[] source, AESType aesType) throws IWException {
		byte[] encData = null;
		try {

			int keyLength = (aesType == AESType.AES128)?16:32;

			// seperate K, IV
			byte[] dk = generateDerivedKey(source, aesType);
			SecretKey k = new SecretKeySpec(dk, 0, keyLength, "AES");
			byte[] iv = Arrays.copyOfRange(dk, keyLength, dk.length);

			// encrypt the message
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));

			encData = cipher.doFinal("raonsecure".getBytes());

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

		String proxyKey = Base58.encode(encData);

		return proxyKey;
	}

	@Override
	public void changePassword(byte[] oldPassword, byte[] newPassword, boolean autoLock, SuccessCallBack callBack) {

		if (oldPassword == null || oldPassword.length == 0 || newPassword == null || newPassword.length == 0) {
			callBack.failure(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_PASSWORD);
			return;
		}

		char[] oldPasswordChar = new char[oldPassword.length];
		for (int i = 0; i < oldPassword.length; i++) {
			oldPasswordChar[i] = (char) oldPassword[i];
		}

		char[] newPasswordChar = new char[newPassword.length];
		for (int i = 0; i < newPassword.length; i++) {
			newPasswordChar[i] = (char) newPassword[i];
		}

		changePassword(oldPasswordChar, newPasswordChar, autoLock, callBack);
	}

	@Override
	public void changePassword(char[] oldPassword, char[] newPassword, boolean autoLock, SuccessCallBack callBack) {

		// File Check
		if (!iwF.isExist()) {
			callBack.failure(IWErrorCode.ERR_CODE_KEYMANAGER_FILE_LOAD_FAIL);
		}

		IWKeyStoreData keyStoreData = null;
		try {
			keyStoreData = iwF.getData();
		} catch (IWException e) {
			IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
			callBack.failure(errorCode);
			return;
		}
		if (keyStoreData == null) {
			callBack.failure(IWErrorCode.ERR_CODE_KEYMANAGER_FILE_LOAD_FAIL);
			return;
		}

		// ProxyKey check
		String proxyKeyString = keyStoreData.getHead().getProxyKey();
		if (proxyKeyString == null || proxyKeyString.length() == 0) {
			callBack.failure(IWErrorCode.ERR_CODE_KEYMANAGER_PASSWORD_NOT_SET);
			return;
		}

		// Check the Given Passwords each
		if (oldPassword == null || oldPassword.length == 0 || newPassword == null || newPassword.length == 0) {
			callBack.failure(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_PASSWORD);
			return;
		}
//		else if(Arrays.equals(oldPassword, newPassword)) {
//			callBack.failure(IWErrorCode.ERR_CODE_KEYMANAGER_PASSWORD_SAME_AS_OLD);
//			return;
//		}

		AESType aesType = (keyStoreData.getHead().getEncType().equals("AES128"))?AESType.AES128:AESType.AES256;

		String oldProxyKeyString = null;
		try {
			oldProxyKeyString = generateProxyKey(oldPassword, aesType);
		} catch (IWException e) {
			IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
			callBack.failure(errorCode);
			return;
		}

		if (oldProxyKeyString == null) {
			callBack.failure(IWErrorCode.ERR_CODE_KEYMANAGER_DEFAULTKEYSTORE_KEYGEN_FAIL);
			return;
		}

		// Check the set password is the same with given one
//		if(!proxyKeyString.contentEquals(oldProxyKeyString)) {
//			callBack.failure(IWErrorCode.ERR_CODE_KEYMANAGER_PASSWORD_NOT_MATCH_WITH_THE_SET_ONE);
//			return;
//		}

		if (!proxyKeyString.contentEquals(oldProxyKeyString)) { // oldplassword wrong
			callBack.failure(IWErrorCode.ERR_CODE_KEYMANAGER_PASSWORD_NOT_MATCH_WITH_THE_SET_ONE);
			return;
		} else if (Arrays.equals(oldPassword, newPassword)) {
			callBack.failure(IWErrorCode.ERR_CODE_KEYMANAGER_PASSWORD_SAME_AS_OLD);
			return;
		}

		// header version check
		try {
			checkHeaderVersion();
		} catch (IWException e1) {
			IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e1.getErrorCode());
			callBack.failure(errorCode);
			return;
		}

		// Set the proxyKey to decode and decrypt
		byte[] proxyByte = null;
		try {
			proxyByte = generateDerivedKey(oldPassword, aesType);
		} catch (IWException e2) {
			IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e2.getErrorCode());
			callBack.failure(errorCode);
			return;
		}

		this.proxyKey = new byte[proxyByte.length];
		System.arraycopy(proxyByte, 0, this.proxyKey, 0, proxyByte.length);

		int encodingType = keyStoreData.getHead().getEncoding().getPrivacy();

		// decrypt Claims
		ArrayList<VerifiableClaim> claims = null;
		ArrayList<String> oldClaims = keyStoreData.getEncClaims();
		if (oldClaims != null) {
			try {
				claims = decryptVerifiableClaim(oldClaims, encodingType, callBack);
			} catch (IWException e) {
				IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
				callBack.failure(errorCode);
				return;
			}
		}

		// decrypt credentials
		ArrayList<VerifiableCredential> credentials = null;
		ArrayList<String> oldCredentials = keyStoreData.getEncCredentials();
		if (oldCredentials != null) {
			try {
				credentials = decryptVerifiableCredential(oldCredentials, encodingType, callBack);
			} catch (IWException e) {
				IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
				callBack.failure(errorCode);
				return;
			}
		}

		// privatekey
		int keyEncodingType = keyStoreData.getHead().getEncoding().getKey();

		ArrayList<IWKeyElement> keys = null;
		ArrayList<IWKeyElement> oldKeys = keyStoreData.getKeys();
		if (oldKeys != null) {
			keys = decryptPrivateKey(oldKeys, keyEncodingType, callBack);
		}

		String newProxyKey = null;
		try {
			newProxyKey = generateProxyKey(newPassword, aesType);
		} catch (IWException e) {
			IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
			callBack.failure(errorCode);
			return;
		}

		IWHeadElement head = keyStoreData.getHead();
		head.setProxyKey(newProxyKey);
		keyStoreData.setHead(head);

		try {
			Arrays.fill(proxyByte, (byte) 0x00);
			proxyByte = null;

			proxyByte = generateDerivedKey(newPassword, aesType);
		} catch (IWException e2) {
			IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e2.getErrorCode());
			callBack.failure(errorCode);
			return;
		}

		Arrays.fill(this.proxyKey, (byte) 0x00);
		this.proxyKey = null;

		this.proxyKey = new byte[proxyByte.length];
		System.arraycopy(proxyByte, 0, this.proxyKey, 0, proxyByte.length);

		// encrypt claim
		if (claims != null && claims.size() > 0) {
			try {
				keyStoreData.setEncClaims(getEncryptedVerifiableClaim(claims, encodingType, callBack));
			} catch (IWException e) {
				IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
				callBack.failure(errorCode);
				return;
			}
		}

		// encrypt credential
		if (credentials != null && credentials.size() > 0) {
			try {
				keyStoreData.setEncCredential(getEncryptedVerifiableCredential(credentials, encodingType, callBack));
			} catch (IWException e) {
				IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
				callBack.failure(errorCode);
				return;
			}
		}

		// encrypt privatekey
		if (keys != null && keys.size() > 0) {
			for (IWKeyElement element : keys) {

				byte[] decoded = null;
				try {
					decoded = GDPBase58.decode(element.getPrivateKey());
				} catch (Exception e) {
					callBack.failure(IWErrorCode.ERR_CODE_CRYPTOHELPER_ENCRYPT);
					return;
				}

				String encodedPrivateKey = null;
				try {

					encodedPrivateKey = encrypt(decoded, keyEncodingType);

				} catch (IWException e) {
					IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
					callBack.failure(errorCode);
					return;
				}

				if (encodedPrivateKey == null) {
					callBack.failure(IWErrorCode.ERR_CODE_CRYPTOHELPER_ENCRYPT);
					return;
				}

				element.setPrivateKey(encodedPrivateKey);

			}

			keyStoreData.setKeys(keys);
		}

		if (autoLock == true) {
			this.lock();
		}

		// save
		try {
			iwF.write(keyStoreData);
		} catch (IWException e) {
			IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
			callBack.failure(errorCode);
		}

		callBack.success();

	}

	/**
	 * (Key Structure:keys) Remove a IWKey by keyId.
	 *
	 * @param keyId
	 * @return
	 */
	@Override
	public void removeKey(String keyId) throws IWException {
		if (!isUnLock())
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);

		IWKeyElement key = getIWKey(keyId);
		if (key == null)
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_KEYID_NOT_EXIST);

		IWKeyStoreData data = iwF.getData();
		if (data == null) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILE_LOAD_FAIL);
		}

		boolean isChanged = false;

		ArrayList<IWKeyElement> keys = data.getKeys();
		for (IWKeyElement k : keys) {
			if (k.getKeyId().equals(keyId)) {
				if (keys.remove(k)) {
					isChanged = true;
					break;
				}
			}
		}

		if (isChanged == true) {
			data.setKeys(keys);
			iwF.write(data);
		}

	}

	/**
	 * (Key Structure:keys) Add a IWKey.
	 *
	 * @param key
	 * @return
	 */
	@Override
	public void addKey(IWKey key) throws IWException {
		if (!isUnLock())
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);

		if (key == null) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_IWKEY_IS_NULL);
		}

		if (key.getAlg() != ALGORITHM_TYPE.ALGORITHM_SECP256k1.getValue() && key.getAlg() != ALGORITHM_TYPE.ALGORITHM_TYPE_RSA.getValue() && key.getAlg() != ALGORITHM_TYPE.ALGORITHM_SECP256r1.getValue()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_ALGORITHM_TYPE);
		}

		if (key.getKeyId().equals("") || key.getKeyId() == null) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_KEYID_EMPTY_NAME);
		}

		if (!key.getKeyId().matches("^[0-9a-zA-Z.]+$"))
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_KEYID_NAME);

		// privateKey, publicKey check 추가 예정

		if(key.getAlg() == ALGORITHM_TYPE.ALGORITHM_SECP256k1.getValue()) {
			if (key.getEosPriKey().getBytes().length != 32) {
				throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_PRIVATE_KEY);
			}

			if (key.getEosPubKey().getBytes().length != 33) {
				throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_PUBLIC_KEY);
			}
		}

		// 기존 key 존재여부 검사
		if (iwF.getData() != null && iwF.getData().getKeys() != null) {
			ArrayList<IWKeyElement> keyEles = iwF.getData().getKeys();
			for (IWKeyElement keyEle : keyEles) {
				if (keyEle.getKeyId().equals(key.getKeyId())) {

					throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_KEYID_ALREADY_EXIST);
				}
			}
		}

		// 기존 key 읽기
		IWKeyStoreData data = iwF.getData();
		ArrayList<IWKeyElement> keys = data.getKeys();
		if (keys == null)
			keys = new ArrayList<IWKeyElement>();

		// 새로운 key 추가
		keys.add(convertToIWKeyElement(key));
		data.setKeys(keys);
		iwF.write(data);
	}

	/**
	 * (Key Structure:keys) Generate Random IWKey.
	 *
	 * @param keyId
	 * @param algType
	 * @return
	 */
	@Override
	public void generateRandomKey(String keyId, int algType) throws IWException {
		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		if (isExistKey(keyId)) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_KEYID_ALREADY_EXIST);
		}

		if (algType != ALGORITHM_TYPE.ALGORITHM_SECP256k1.getValue() && (algType != ALGORITHM_TYPE.ALGORITHM_TYPE_RSA.getValue())) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_ALGORITHM_TYPE);
		}

		if (keyId.equals("")) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_KEYID_EMPTY_NAME);
		}

		// algo type 에 따라서  키 생성
		if(algType == ALGORITHM_TYPE.ALGORITHM_SECP256k1.getValue()) {
			EosPrivateKey eosPriKey = new EosPrivateKey();
			EosPublicKey eosPubKey = eosPriKey.getPublicKey();
			IWKey iwKey = new IWKey(keyId, algType, eosPubKey, eosPriKey);
			addKey(iwKey);
		}

		if(algType == ALGORITHM_TYPE.ALGORITHM_TYPE_RSA.getValue()) {
			try {
				KeyPair keyPair= RSAEncryptUtil.generateKey();
				IWKey iwKey = new IWKey(keyId, algType, keyPair.getPublic(), keyPair.getPrivate());
				addKey(iwKey);
			} catch (NoSuchAlgorithmException e) {
				throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_ALGORITHM_TYPE);
			} catch (NoSuchProviderException e) {
				throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_ALGORITHM_TYPE);
			}
		}


	}

	/**
	 * (Key Structure:keys) Get a IWKey by keyId.
	 *
	 * @param keyId
	 * @return
	 */
	@Override
	public String getPublicKey(String keyId) throws IWException {
		IWKeyElement key = getIWKey(keyId);
		if (key == null)
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_KEYID_NOT_EXIST);

		return key.getPublicKey();
	}

	/**
	 * (Key Structure:keys) Get a IWKey by keyId.
	 *
	 * @param keyId
	 * @return
	 */
	// 2020.04.07 dlchoi public에서 private로 변경
	private IWKey getKeyElement(String keyId) throws IWException {
		if (!isUnLock())
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);

		IWKeyElement key = getIWKey(keyId);
		if (key == null)
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_KEYID_NOT_EXIST);

		return this.getIWKeyFromIWKeyElement(key);
	}

	// dlchoi 추가
	@Override
	public String getAlgoType(String keyId) throws IWException {
		IWKey key = getKeyElement(keyId);
		ALGORITHM_TYPE algType = ALGORITHM_TYPE.fromValue(key.getAlg());

		return algType.toString();
	}

	/**
	 * (Key Structure:keys) Check whether a IWKey is exist by keyId or not.
	 *
	 * @return
	 */
	@Override
	public boolean isExistKey(String keyId) throws IWException {
		if (!isUnLock())
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);

		if (getKeyIdList() != null && getKeyIdList().contains(keyId))
			return true;
		return false;
	}

	private boolean genKeyFile() throws IWException {
		if (!iwF.isExist()) {
			// generate & save a symmetric key parameters
			IWKeyStoreData data = new IWKeyStoreData();
			IWHeadElement head = new IWHeadElement();
			head.setEncType("AES256");
			head.setIterations(2048);
			head.setSalt(Base58.encode(cryptoHelper.generateSalt()));

			IWKeyStoreEncodingElement encoding = new IWKeyStoreEncodingElement();

			encoding.setKey(ENCODING_TYPE.ENCODING_BASE58.getValue());
			encoding.setPrivacy(ENCODING_TYPE.ENCODING_HEXA_DECIMAL.getValue());

			head.setEncoding(encoding);

			data.setHead(head);
			iwF.write(data);
			return true;
		}
		return false;
	}

	/**
	 * (Key Structure:keys) Get all of the keyIds.
	 *
	 * @return
	 */
	@Override
	public List<String> getKeyIdList() throws IWException {
		List<IWKeyElement> keyEles = getIWKeyList();
		if (keyEles == null || keyEles.size() == 0) {
			return null;
		}

		List<String> keyIds = new ArrayList<String>();
		for (IWKeyElement keyId : keyEles) {
			keyIds.add(keyId.getKeyId());
		}

		return keyIds;
	}

	@Override
	public byte[] getAllKeyElementsEncrypted() throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<IWKeyElement> keys = data.getKeys();

		if (keys == null || keys.size() == 0) {
			return null;
		}

		int encodingType = data.getHead().getEncoding().getKey();

		JsonArray jsonArray = new JsonArray();
		for (IWKeyElement element : keys) {

			byte[] decrypted = decrypt(element.getPrivateKey(), encodingType);

			String encodedPrivateKey = GDPBase58.encode(decrypted);
			element.setPrivateKey(encodedPrivateKey);
			jsonArray.add(element.toJson());
		}
		String jsonString = jsonArray.toString();
		byte[] jsonByte = null;
		try {
			jsonByte = jsonString.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IWException(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
		}

		String proxyKey = iwF.getData().getHead().getProxyKey();

		if (proxyKey == null || proxyKey.length() == 0) {
			// TODO: error
		}

		byte[] keyData = null;
		try {
			keyData = proxyKey.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IWException(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
		}

		byte[] encrypted = GDPCryptoHelperClient.aesEncrypt(jsonByte, keyData, AESType.AES128);

		return encrypted;
	}


	/**
	 * (Key Structure:keys) Get a signature by keyId.
	 *
	 * @param keyId
	 * @param source
	 * @return
	 */
	@Override
	public byte[] getSign(String keyId, byte[] source) throws IWException {
		if (!isUnLock())
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);

		return getSignBytes(keyId, source);
	}

	/**
	 * (Key Structure:keys) Verify a signature by keyId.
	 *
	 * @param keyId
	 * @param source
	 * @param sign
	 * @return
	 */
	@Override
	public void verifySign(String keyId, byte[] source, byte[] sign) throws IWException {
		if (!isUnLock())
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);

		String pubkey_base58 = this.getPublicKey(keyId);
		byte[] pubkey_bytes = Base58.decode(pubkey_base58);

		if (pubkey_bytes.length != 33) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_PUBLIC_KEY);
		}

		if (sign == null || sign.length != 65) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_SIGN_VALUE);
		}

		try {
			verify(pubkey_bytes, source, sign);
		} catch (Exception e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_VERIFY_SIGN_FAIL);
		}
	}

	@Override
	public void verifySignWithPublicKey(String publicKey, byte[] source, byte[] sign) throws IWException {
		byte[] pubkey_bytes = Base58.decode(publicKey);

		if (pubkey_bytes.length != 33) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_PUBLIC_KEY);
		}

		if (sign == null || sign.length != 65) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_SIGN_VALUE);
		}

		try {
			verify(pubkey_bytes, source, sign);
		} catch (Exception e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_VERIFY_SIGN_FAIL);
		}
	}


	/**
	 * 금결원 - client
	 *
	 * @param didsJSON : 참여 기관의 DID Document String
	 * @param profile : 참여 기관서버에서 생성한 Profile
	 * @return verification result (true/false)
	 */
	@Override
	public boolean verifyProfile(String didsJSON, AbstractProfile profile) throws IWException {
		if (profile == null) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_PROFILE);
		}

		boolean isIssuerProfile = (profile instanceof IssueProfile);


		// proof - issueProfile, verifyProfile 공통
		Proof proof = profile.getProof();


		// signatureValue
		byte[] signatureValue = Base58.decode(proof.getSignatureValue());

		if (signatureValue == null || signatureValue.length != 65) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_SIGN_VALUE);
		}


		// publicKey
		DIDs dids = new DIDs();
		List<PublicKey> pubkey_str_arr = new ArrayList<PublicKey>();
		String pubkey_str = null;

		dids.fromJson(didsJSON);
		pubkey_str_arr = dids.getPublicKey();

		boolean isDetected = false;
		for(int i=0; i<pubkey_str_arr.size(); i++) {
			if(proof.getCreator().equals(pubkey_str_arr.get(i).getId())) {
				pubkey_str = dids.getPublicKey().get(i).getPublicKeyBase58();
				isDetected = true;
				break;
			}
		}

		if(isDetected == false) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_NOT_FOUND_PUBLIC_KEY);
		}


		byte[] pubkey_bytes = Base58.decode(pubkey_str);

		if (pubkey_bytes == null || pubkey_bytes.length != 33) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_PUBLIC_KEY);
		}


		// 원문 - signatureValue, type -> null
		byte[] source;
		proof.setSignatureValue(null);
		proof.setType(null);
		if (isIssuerProfile) {
			IssueProfile issueProfile = (IssueProfile) profile;
			issueProfile.setProof(proof);

			String source_str = issueProfile.toJson();
			source = source_str.getBytes();
		} else {
			VerifyProfile verifyProfile = (VerifyProfile) profile;

			String source_str = verifyProfile.toJson();
			source = source_str.getBytes();
		}

		try {
			verify(pubkey_bytes, source, signatureValue);
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}


	private boolean addPreviousClaim(String claimJSON) throws IWException {
		if (!isUnLock())
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);

		// passing new vc
		VerifiableClaim vc = new VerifiableClaim();
		vc.fromJson(claimJSON);

		IWKeyStoreData data = iwF.getData();

		ArrayList<String> encVcStore = data.getEncClaims();
		if (encVcStore == null) {
			encVcStore = new ArrayList<String>();
		}

		int encodingType = data.getHead().getEncoding().getPrivacy();

		Claim claim = vc.getClaim();

		String encVC = encrypt(vc.toJson().getBytes(), encodingType);
		encVcStore.add(encVC);

		data.setClaims(null);
		data.setEncClaims(encVcStore);

		iwF.write(data);

		return true;
	}

	/**
	 * (Key Structure:claims) Add a claim.
	 *
	 * @param claimJSON
	 * @return
	 * @throws IWException
	 */
	@Override
	public boolean addClaim(String claimJSON) throws IWException {
		if (!isUnLock())
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);

		VerifiableClaim new_vc = new VerifiableClaim();
		new_vc.fromJson(claimJSON);

		// privacy check
		Privacy privacy = new_vc.getClaim().getPrivacy();
		List<Unprotected> rawUnprotected = privacy.getUnprotected();
		for (Unprotected unprotected : rawUnprotected) {
			String value = unprotected.getValue();
			String type = unprotected.getType();
			if (value == null || type == null) {
				throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_PRIVACY);
			}
		}

		IWKeyStoreData data = iwF.getData();

		ArrayList<String> encVcStore = data.getEncClaims();
		if (encVcStore == null) {
			encVcStore = new ArrayList<String>();
		}

		int encodingType = data.getHead().getEncoding().getPrivacy();

		// wallet에 저장된 claim과 동일한 claim을 추가할 경우 exception 발생
		for (int i = 0; i < encVcStore.size(); i++) {
			String encData = encVcStore.get(i);
			byte[] valueByte = decrypt(encData, encodingType);

			VerifiableClaim vc = new VerifiableClaim();
			vc.fromJson(new String(valueByte));

			if (vc.getId().equals(new_vc.getId())) {
				throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DUPLICATED_VC_ID);
			}
		}

		// encrypt new VC
		String encrypted_New_VC = encrypt(new_vc.toJson().getBytes(), encodingType);

		// add new VC
		encVcStore.add(encrypted_New_VC);
		data.setEncClaims(encVcStore);

		iwF.write(data);

		return true;
	}

	/**
	 * (Key Structure:claims) Get all of the claims.
	 *
	 * @param
	 * @return
	 */

	private ArrayList<VerifiableClaim> getPreviousClaims() throws IWException {

		/// modified by joshua
		// 기존 scheme 읽기
		IWKeyStoreData data = iwF.getData();
		ArrayList<VerifiableClaim> claims = data.getClaims();

		if (isUnLock() && claims != null) {

			int encodingType = data.getHead().getEncoding().getPrivacy();

			for (VerifiableClaim vc : claims) {
				Claim claim = vc.getClaim();
				Privacy rawPrivacy = claim.getPrivacy();
				List<Unprotected> rawUnprotected = rawPrivacy.getUnprotected();

				for (Unprotected unprotected : rawUnprotected) {
					String encodedValue = unprotected.getValue();
					byte[] valueByte = decrypt(encodedValue, encodingType);
					unprotected.setValue(new String(valueByte));
				}
				rawPrivacy.setUnprotected(rawUnprotected);
				claim.setPrivacy(rawPrivacy);
				vc.setClaim(claim);
			}

		}

		return claims;
		// modified ends
	}

	@Override
	public List<VerifiableClaim> getClaims() throws IWException {
		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<String> encClaims = new ArrayList<String>();
		encClaims = data.getEncClaims();

		ArrayList<VerifiableClaim> claims = new ArrayList<VerifiableClaim>();
		int encodingType = data.getHead().getEncoding().getPrivacy();

		if (encClaims != null) {
			for (int i = 0; i < encClaims.size(); i++) {
				String encData = encClaims.get(i);

				byte[] valueByte = decrypt(encData, encodingType);

				VerifiableClaim vc = new VerifiableClaim();
				vc.fromJson(new String(valueByte));

				claims.add(vc);

			}
		}

		return claims;
		// modified ends
	}

	/**
	 * (Key Structure:claims) Get the filtered claims.
	 *
	 * @param filter
	 * @return
	 */
	@Override
	public List<VerifiableClaim> getClaims(String filter) throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		List<VerifiableClaim> claims = getClaims();

		if (claims == null || claims.size() == 0) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_EMPTY_VC);
		}

		List<VerifiableClaim> filtered = getFilteredClaims(claims, filter);

		if (filtered == null || filtered.size() == 0) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILTER_CONDITION_NOT_MATCH);
		}

		return filtered;
	}

	protected List<VerifiableClaim> getFilteredClaims(List<VerifiableClaim> claims, String filter) throws IWException {

		List<VerifiableClaim> filteredClaims = new ArrayList<VerifiableClaim>();
		try {
			if (claims != null && claims.size() > 0) {
				String[] inputCondition = filter.split("\\s+");
				if (inputCondition.length >= 3) {

					String objectCondition = inputCondition[0];
					String actionCondition = inputCondition[1];
					String compareCondition = inputCondition[2];

					if (inputCondition.length > 3) {
						for (int index = 3; index < inputCondition.length; index++) {
							compareCondition = compareCondition + " " + inputCondition[index];
						}
					}

					String separatedObject[] = objectCondition.split("\\.");
					String lastword = separatedObject[separatedObject.length - 1];

					String definedAction[] = { "==", "!=", ">", ">=", "<", "<=" };

					String boolObjects[] = { "encType" };
					String numberObjects[] = { "level" };

					Arrays.sort(definedAction);
					int isRightAction = Arrays.binarySearch(definedAction, actionCondition);
					if (isRightAction < 0) {
						// error
						// wrong action condition
						throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILTER_SYNTAX_ERROR);
					}

					Arrays.sort(boolObjects);
					int boolObjectIndex = Arrays.binarySearch(boolObjects, lastword);

					boolean isNumberObject = false;
					boolean isBoolObject = false;

					if (boolObjectIndex >= 0) {
						if (actionCondition.contentEquals("==") == false) {
							// bool action condition only can be ==
							throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILTER_SYNTAX_ERROR);
						}
						isBoolObject = true;

					} else {
						Arrays.sort(numberObjects);
						int numberObjectIndex = Arrays.binarySearch(numberObjects, lastword);

						if (numberObjectIndex < 0) {
							// remaining is just object
							if (actionCondition.contains("<") || actionCondition.contains(">")) {
								// object cannot be compared
								throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILTER_SYNTAX_ERROR);
							}
						} else {
							isNumberObject = true;
						}
					}

					for (VerifiableClaim claim : claims) {

						String vc = claim.toJson();

						JsonParser parser = new JsonParser();
						JsonElement ele = parser.parse(vc);

						for (int i = 0; i < separatedObject.length; i++) {

							String objectClass = separatedObject[i];

							if (i == separatedObject.length - 1) {
								if (isNumberObject) {
									int intValue = ele.getAsJsonObject().get(objectClass).getAsInt();

									int compareInt = Integer.parseInt(compareCondition);

									if (actionCondition.contentEquals("==")) {
										if (intValue == compareInt) {
											filteredClaims.add(claim);
										}

									} else if (actionCondition.contentEquals("!=")) {
										if (intValue != compareInt) {
											filteredClaims.add(claim);
										}
									} else {
										int correctInt = 0;
										if (actionCondition.contains("=")) {
											correctInt = 1;
										}

										if (actionCondition.contains("<")) {
											if (intValue < compareInt + correctInt) {
												filteredClaims.add(claim);
											}
										} else if (actionCondition.contains(">")) {
											if (intValue > compareInt - correctInt) {
												filteredClaims.add(claim);
											}
										}
									}

								} else if (isBoolObject) {

									if (actionCondition.contentEquals("==")) {

										boolean boolValue = ele.getAsJsonObject().get(objectClass).getAsBoolean();

										boolean compareValue = Boolean.parseBoolean(compareCondition);

										if (boolValue == compareValue) {
											filteredClaims.add(claim);
										}

									}
								} else {
									String stringValue = ele.getAsJsonObject().get(objectClass).getAsString();

									if (actionCondition.contentEquals("==")) {
										if (stringValue.contentEquals(compareCondition)) {
											filteredClaims.add(claim);
										}

									} else if (actionCondition.contentEquals("!=")) {
										if (!stringValue.contentEquals(compareCondition)) {
											filteredClaims.add(claim);
										}
									}
								}

							} else {
								ele = ele.getAsJsonObject().get(objectClass);
								if (ele == null) {
									break;
								}
							}

						}

					}

				} else {
					// error
					// wrong filter
					throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILTER_SYNTAX_ERROR);
				}

			} else {
				throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_EMPTY_VC);
			}

		} catch (UnsupportedOperationException e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILTER_SYNTAX_ERROR);
		}

		return filteredClaims;
	}

	/**
	 * (Key Structure:claims) Get all of the claims.
	 *
	 * @param
	 * @return
	 */
	@Override
	public List<VerifiableClaim> getClaimsInSPProfile(String serviceProviderProfile) throws IWException {

		// SPProfile
		VerifyProfile spProfile = new VerifyProfile();
		spProfile.fromJson(serviceProviderProfile);

		// SPProfile's allowList
		List<String> allowList = spProfile.getVerifyInnerProfile().getFilter().getAllowIssuerList();
		List<VerifiableClaim> vcList = getClaims();

		// VerifiableClaim in SPProfile's allowList
		List<VerifiableClaim> result = new ArrayList<VerifiableClaim>();
		for (VerifiableClaim vc : vcList) {
			Issuer issuer = vc.getIssuer();
			String id = issuer.getId();

			for (String allowId : allowList) {
				if (id.equals(allowId)) {
					result.add(vc);
				}
			}
		}

		return result;
	}

	/**
	 * (Key Structure:head) Read a head.
	 *
	 * @return
	 * @throws IWException
	 */
	@Override
	public String readKeyFileHeader() throws IWException {
		if (!isUnLock())
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);

		// 기존 scheme 읽기
		IWKeyStoreData data = iwF.getData();
		return data.getHead().toJson();
	}

	/**
	 * (Key Structure) Read a key file.
	 *
	 * @return
	 * @throws IWException
	 */
	@Override
	public String readKeyFile() throws IWException {
		if (!isUnLock())
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);

		// 기존 scheme 읽기
		IWKeyStoreData data = iwF.getData();
		return data.toJson();
	}

	@Override
	public String makeRequestVerifiableClaim(String did, Privacy privacy) {

		VerifiableClaim verifiableClaim = new VerifiableClaim();
		{
			Claim claim = new Claim();
			claim.setId(did);

			if (privacy == null) {
				Privacy emptyPrivacy = new Privacy();
				claim.setPrivacy(emptyPrivacy);
			} else {
				claim.setPrivacy(privacy);
			}

			verifiableClaim.setClaim(claim);
		}

		return verifiableClaim.toJson();
	}

	// client
	@Override
	public String makeRequestVerifiableClaimCopy(String verifiableClaimJSON, Privacy privacy, String spName,
												 String expires) throws IWException {

		VerifiableClaim verifiableClaim = new VerifiableClaim();
		verifiableClaim.fromJson(verifiableClaimJSON);

		Claim claim = verifiableClaim.getClaim();

		// claim
		// 사용자가 선택한 privacy 정보로 변경
		claim.setPrivacy(privacy);
		verifiableClaim.setClaim(claim);

		// copy
		// 서비스제공자가 보내준 spProfile 정보 추가
		Copy copy = new Copy();
		copy.setAuthorizedServiceProvider(spName);
		copy.setExpires(expires);
		verifiableClaim.setCopy(copy);

		// verifiableClaims
		// 반환되는 정보 : issuer, assertion, claim(변경후), copy
		return verifiableClaim.toJson();
	}


	// client
	@Override
	public String makeProof(String verifiableClaimJSON, String signKeyId, byte[] nonce) throws IWException {

		/*
		 * Verifiable Claim
		 *
		 * issuer 발급자 정보 assertion 신분증 정보 claim (소유용) 사용자정보 / 개인정보 copy (제출용) 사본정보 / 사용처
		 * proof (증명용) 서명
		 */
		VerifiableClaim claim_proof = new VerifiableClaim();
		claim_proof.fromJson(verifiableClaimJSON);
		Proof proof = new Proof();

		// proof.creator, proof.created, proof.nonce
		byte[] signature = null;
		{
			// proof.creator
			String signKeyDid = getKeyDId(claim_proof.getClaim().getId(), signKeyId);
			proof.setCreator(signKeyDid);
			proof.setCreated(VerifiableClaim.dateToString(new Date()));

//			proof.setNonce(new String(nonce));
			proof.setNonce(new String(Hex.encode(nonce)));

			claim_proof.setProof(proof);
		}

		// proof.sign
		signature = getSignatureUsingKeyInKeyFile(claim_proof.toJson().getBytes(), signKeyId, this);
		proof.setSignatureValue(Base58.encode(signature));

		// proof.type
		IWKey key = getKeyElement(signKeyId);
		ALGORITHM_TYPE algType = ALGORITHM_TYPE.fromValue(key.getAlg());
		proof.setType(algType.toString() + ALGORITHM_VERIFICATION);

		claim_proof.setProof(proof);
		return claim_proof.toJson();
	}

	// dlchoi - client
	@Override
	public String makeDelegateProof(String verifiableClaimCopyJSON, String did, String signKeyId, byte[] nonce)
			throws IWException {

		/*
		 * Verifiable Claim
		 *
		 * issuer 발급자 정보 assertion 신분증 정보 claim (소유용) 사용자정보 / 개인정보 copy (제출용) 사본정보 / 사용처
		 * proof (증명용) 서명
		 */
		VerifiableClaim claim_proof = new VerifiableClaim();
		claim_proof.fromJson(verifiableClaimCopyJSON);
		Proof delegateProof = new Proof();

		// proof.creator, proof.created, proof.nonce
		byte[] signature = null;
		{
			// proof.creator
			getKeyDId(claim_proof.getClaim().getId(), signKeyId);
			delegateProof.setCreator(did);
			delegateProof.setCreated(VerifiableClaim.dateToString(new Date()));

			delegateProof.setNonce(new String(Hex.encode(nonce)));

			claim_proof.setDelegateProof(delegateProof);
		}

		// proof.sign
		signature = getSignatureUsingKeyInKeyFile(claim_proof.toJson().getBytes(), signKeyId, this);
		delegateProof.setSignatureValue(Base58.encode(signature));

		// proof.type
		IWKey key = getKeyElement(signKeyId);
		ALGORITHM_TYPE algType = ALGORITHM_TYPE.fromValue(key.getAlg());
		delegateProof.setType(algType.toString() + ALGORITHM_VERIFICATION);

		claim_proof.setDelegateProof(delegateProof);
		return claim_proof.toJson();
	}

	/**
	 * (Key Structure:claims) remove claim with index.
	 *
	 */
	@Override
	public void removeClaim(int index) throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}
		IWKeyStoreData data = iwF.getData();

		ArrayList<String> claims = data.getEncClaims();

		if (claims == null || claims.size() == 0) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_EMPTY_VC);
		}

		if (index < 0 || index >= claims.size()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_OUT_OF_RANGE_VALUE);
		}

		claims.remove(index);

		data.setEncClaims(claims);
		iwF.write(data);

	}

	/**
	 * (Key Structure:claims) remove claim with Claim Object.
	 *
	 */

	@Override
	public void removeClaim(VerifiableClaim claim) throws IWException {
		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<VerifiableClaim> vcList = (ArrayList<VerifiableClaim>) this.getClaims();

		if (vcList == null || vcList.size() == 0) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_EMPTY_VC);
		}

		int deleteIndex = -1;
		String claimJson = claim.toJson();

		for (int index = 0; index < vcList.size(); index++) {
			String targetClaim = vcList.get(index).toJson();

			if (claimJson.equals(targetClaim)) {
				deleteIndex = index;
				break;
			}
		}

		if (deleteIndex == -1) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_VC);
		}

		ArrayList<String> encClaimList = data.getEncClaims();
		encClaimList.remove(deleteIndex);

		data.setEncClaims(encClaimList);

		iwF.write(data);

	}

//	@Override
//	public void removeClaim(VerifiableClaim claim) throws IWException {
//
//		if (!isUnLock()) {
//			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
//		}
//
//
//		IWKeyStoreData data = iwF.getData();
//		ArrayList<VerifiableClaim> claims = (ArrayList<VerifiableClaim>) this.getClaims();
//
//		if (claims == null || claims.size() == 0) {
//			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_EMPTY_CLAIM);
//		}
//
//		int claimIndex = -1;
//		String claimJson = claim.toJson();
//
//		for (int index = 0; index < claims.size(); index++) {
//			VerifiableClaim new_vc = claims.get(index);
//			String targetClaim = new_vc.toJson();
//
//			if (claimJson.equals(targetClaim)) {
//				claimIndex = index;
//				break;
//			}
//		}
//
//		if (claimIndex == -1) {
//			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_CLAIM);
//		}
//
//		claims.remove(claimIndex);
//
//

//		if (isUnLock()) {
//
//			int encodingType = data.getHead().getEncoding().getPrivacy();
//
//			ArrayList<VerifiableClaim> newClaims = new ArrayList<VerifiableClaim>();
//			for (int index = 0; index < claims.size(); index++) {
//
//				VerifiableClaim new_vc = claims.get(index);
//
//				Claim innerclaim = new_vc.getClaim();
//				Privacy rawPrivacy = innerclaim.getPrivacy();
//				List<Unprotected> rawUnprotected = rawPrivacy.getUnprotected();
//
//				for (Unprotected unprotected : rawUnprotected) {
//					String value = unprotected.getValue();
//					String base58Value = encrypt(value.getBytes(), encodingType);
//
//					unprotected.setValue(base58Value);
//				}
//				rawPrivacy.setUnprotected(rawUnprotected);
//				innerclaim.setPrivacy(rawPrivacy);
//				new_vc.setClaim(innerclaim);
//
//				newClaims.add(new_vc);
//
//			}
//
//			claims = newClaims;
//
//		}
//
//		data.setClaims(claims);
//		iwF.write(data);
//	}

	@Override
	public void removeClaims(String filter) throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData keyStoreData = iwF.getData();
		int encodingType = keyStoreData.getHead().getEncoding().getKey();
		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<VerifiableClaim> claims = data.getClaims();

		if (claims == null || claims.size() == 0) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_EMPTY_VC);
		}

		List<VerifiableClaim> filtered = getFilteredClaims(claims, filter);

		if (filtered == null || filtered.size() == 0) {

			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILTER_CONDITION_NOT_MATCH);

		}

		for (VerifiableClaim claim : filtered) {
			claims.remove(claim);
		}

		// dlchoi
		ArrayList<String> encClaimList = new ArrayList<String>();
		for (int i = 0; i < claims.size(); i++) {
			String encrypted_claim = encrypt(claims.get(i).toJson().getBytes(), encodingType);
			encClaimList.add(encrypted_claim);
		}

//		data.setClaims(claims);
		data.setEncClaims(encClaimList);
		iwF.write(data);
	}

	/**
	 * KeyFil 삭제
	 *
	 * @return
	 */
	@Override
	public void deleteWalletFile() {

		lock();

		iwF.delete();

	}

	@Override
	public void resetWalletFile() throws IWException {

		lock();

		if (cryptoHelper == null) {
			cryptoHelper = new GDPCryptoHelperClient();
		}




		// generate & save a symmetric key parameters
		IWKeyStoreData data = new IWKeyStoreData();
		IWHeadElement head = new IWHeadElement();

		if(iwF.isExist()) {
			head.setEncType(iwF.getData().getHead().getEncType());
		}
		else {
			head.setEncType("AES256");
		}


		head.setIterations(2048);
		head.setSalt(Base58.encode(cryptoHelper.generateSalt()));

		IWKeyStoreEncodingElement encoding = new IWKeyStoreEncodingElement();

		encoding.setKey(ENCODING_TYPE.ENCODING_BASE58.getValue());
		encoding.setPrivacy(ENCODING_TYPE.ENCODING_HEXA_DECIMAL.getValue());

		head.setEncoding(encoding);

		data.setHead(head);
		iwF.write(data);

	}

	@Override
	public void removeAllKeys() throws IWException {
		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		if (data == null) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILE_LOAD_FAIL);
		}

		ArrayList<IWKeyElement> keys = data.getKeys();

		if (keys != null && keys.size() > 0) {
			data.setKeys(null);
			iwF.write(data);
		}
	}

	@Override
	public void removeAllClaims() throws IWException {
		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		if (data == null) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILE_LOAD_FAIL);
		}

		ArrayList<String> claims = data.getEncClaims();

		if (claims != null && claims.size() > 0) {

			data.setEncClaims(null);
			iwF.write(data);
		}
	}

	private boolean isAndroidKeyStore() {
		// if helper is server, return false
		if (iwK instanceof IWKeyStoreDefault) {
			return false;
		}
		return false;
	}

	private boolean isNeedGenerateProxyKey() throws IWException {
		// if helper is android and the keyfile is not set a proxykey yet,
		// return true
		IWKeyStoreData data = iwF.getData();
		if (data == null) {
			genKeyFile();
		}
		String pk = iwF.getData().getHead().getProxyKey();
		return pk == null ? true : false;
	}

	/*
	 * (Key Structure:keys) SECP-Private Key 암호화
	 */
	private byte[] symEncPrivateKey(byte[] privateKey) throws IWException {
		return symEncPrivateKey(privateKey, proxyKey);
	}

	private byte[] symEncPrivateKey(byte[] privateKey, byte[] key) throws IWException {
		try {
			if (isAndroidKeyStore()) {
				IWHeadElement head = iwF.getData().getHead();
				return cryptoHelper.encryptWithPBKDF2(new String(key).toCharArray(), privateKey,
						Base58.decode(head.getSalt()), head.getIterations());
			} else {
				//  server
				if("AES128".equals(iwF.getData().getHead().getEncType())){
					return cryptoHelper.encrypt(key, privateKey, 16);
				}else{
					return cryptoHelper.encrypt(key, privateKey);
				}

			}
		} catch (IWException e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_AES_ENCRYPT_FAIL, e);
		}
	}

	/*
	 * (Key Structure:keys) SECP-Private Key 복호화
	 */

	protected byte[] symDecPrivateKey(byte[] encPrivateKey) throws IWException {
		return symDecPrivateKey(encPrivateKey, proxyKey);
	}

	protected byte[] symDecPrivateKey(byte[] encPrivateKey, byte[] key) throws IWException {
		try {

			if (isAndroidKeyStore()) {
				IWHeadElement head = iwF.getData().getHead();
				return cryptoHelper.decryptWithPBKDF2(new String(key).toCharArray(), encPrivateKey,
						Base58.decode(head.getSalt()), head.getIterations());
			} else {
				// server
				if("AES128".equals(iwF.getData().getHead().getEncType())){
					return cryptoHelper.decrypt(key, encPrivateKey, 16);
				}else {
					return cryptoHelper.decrypt(key, encPrivateKey);
				}

			}
		} catch (IWException e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_AES_DECRYPT_FAIL, e);
		}
	}

	protected IWKeyElement getIWKey(String keyId) throws IWException {
		IWKeyStoreData data = iwF.getData();
		ArrayList<IWKeyElement> keys = data.getKeys();
		if (keys == null)
			return null;

		for (IWKeyElement key : keys) {
			if (key.getKeyId().equals(keyId)) {
				return key;
			}
		}

		return null;
	}

	private List<IWKeyElement> getIWKeyList() throws IWException {
		IWKeyStoreData data = iwF.getData();
		if (data == null)
			return null;

		ArrayList<IWKeyElement> keys = data.getKeys();
		return keys;
	}


	protected IWKeyElement convertToIWKeyElement(IWKey key) throws IWException {
		IWKeyElement keyEle = new IWKeyElement();
		keyEle.setKeyId(key.getKeyId());
		keyEle.setAlg(key.getAlg());

		IWKeyStoreData keyStoreData = iwF.getData();

		int encodingType = keyStoreData.getHead().getEncoding().getKey();
		String encPriKey_encodedBase58 = encrypt(getPrivateKeyBytes(key), encodingType);

		keyEle.setPrivateKey(encPriKey_encodedBase58);
		keyEle.setPublicKey(Base58.encode(getPublicKeyBytes(key)));
		System.out.println("Base58! = " + Base58.encode(getPublicKeyBytes(key)));

		System.out.println("HEX! = " + HexUtils.toHex(getPublicKeyBytes(key)));

		return keyEle;
	}

	// modidied by joshua
	protected String encrypt(byte[] toBeEncoded) throws IWException {

		return encrypt(toBeEncoded, ENCODING_TYPE.ENCODING_BASE58.getValue());
	}

	protected String encrypt(byte[] toBeEncoded, int encodingType) throws IWException {

		// private key
		byte[] encrypted = symEncPrivateKey(toBeEncoded);

		return encodeByte(encrypted, encodingType);

	}

	protected String encodeByte(byte[] source, int encodingType) {
		String encodedString = null;

		switch (ENCODING_TYPE.fromValue(encodingType)) {
			case ENCODING_HEXA_DECIMAL: {

				encodedString = byteArrayToHexString(source);
				break;
			}
			case ENCODING_BASE58: {

				encodedString = GDPBase58.encode(source);
				break;
			}
			case ENCODING_BASE64: {

				encodedString = GDPBase64.encodeUrlString(source);
				break;
			}
			default: {
				break;
			}
		}

		return encodedString;
	}

	protected byte[] decrypt(String toBeDecoded) throws IWException {

		return decrypt(toBeDecoded, ENCODING_TYPE.ENCODING_BASE58.getValue());
	}

	protected byte[] decrypt(String toBeDecoded, int encodedType) throws IWException {

		byte[] decoded = decodeEncodedString(toBeDecoded, encodedType);

		byte[] decrypted = symDecPrivateKey(decoded);

		return decrypted;
	}

	protected byte[] decodeEncodedString(String source, int encodedType) {
		byte[] decoded = null;

		switch (ENCODING_TYPE.fromValue(encodedType)) {
			case ENCODING_HEXA_DECIMAL: {
				decoded = hexStringToByteArray(source);
				break;
			}
			case ENCODING_BASE58: {
				decoded = Base58.decode(source);
				break;
			}
			case ENCODING_BASE64: {

				decoded = GDPBase64.decodeUrl(source);
				break;
			}
			default: {
				break;
			}
		}

		return decoded;
	}

	protected byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	protected String byteArrayToHexString(byte[] bytes) {

		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02X", b & 0xff));
		}
		return sb.toString();
	}

	private ArrayList<VerifiableClaim> decryptVerifiableClaim(ArrayList<String> claims, int encodingType,
															  SuccessCallBack callBack) throws IWException {

		ArrayList<VerifiableClaim> vcList = new ArrayList<VerifiableClaim>();

		if (claims != null && claims.size() > 0) {
			for (int i = 0; i < claims.size(); i++) {
				String encVc = claims.get(i);
				byte[] valueByte = decrypt(encVc, encodingType);
				VerifiableClaim vc = new VerifiableClaim();
				vc.fromJson(new String(valueByte));
				vcList.add(vc);

			}
		}

		return vcList;

	}

	private ArrayList<VerifiableCredential> decryptVerifiableCredential(ArrayList<String> credentials, int encodingType,
																		SuccessCallBack callBack) throws IWException {

		ArrayList<VerifiableCredential> vcList = new ArrayList<VerifiableCredential>();

		if (credentials != null && credentials.size() > 0) {
			for (int i = 0; i < credentials.size(); i++) {
				String encVc = credentials.get(i);
				byte[] valueByte = decrypt(encVc, encodingType);
				VerifiableCredential vc = new VerifiableCredential();
				vc.fromJson(new String(valueByte));
				vcList.add(vc);

			}
		}

		return vcList;

	}

	private ArrayList<String> getEncryptedVerifiableClaim(ArrayList<VerifiableClaim> claims, int encodingType,
														  SuccessCallBack callBack) throws IWException {

		ArrayList<String> encVcList = new ArrayList<String>();

		if (claims != null && claims.size() > 0) {
			for (int i = 0; i < claims.size(); i++) {
				VerifiableClaim vc = claims.get(i);
				String encVC = encrypt(vc.toJson().getBytes(), encodingType);
				encVcList.add(encVC);

			}
		}

		return encVcList;

	}

	private ArrayList<String> getEncryptedVerifiableCredential(ArrayList<VerifiableCredential> credentials,
															   int encodingType, SuccessCallBack callBack) throws IWException {

		ArrayList<String> encVcList = new ArrayList<String>();

		if (credentials != null && credentials.size() > 0) {
			for (int i = 0; i < credentials.size(); i++) {
				VerifiableCredential vc = credentials.get(i);
				String encVC = encrypt(vc.toJson().getBytes(), encodingType);
				encVcList.add(encVC);

			}
		}

		return encVcList;

	}

	private ArrayList<IWKeyElement> decryptPrivateKey(ArrayList<IWKeyElement> keys, int keyEncodingType,
													  SuccessCallBack callback) {
		for (IWKeyElement element : keys) {
			String pKeyString = element.getPrivateKey();

			byte[] valueByte = null;
			try {
				valueByte = decrypt(pKeyString, keyEncodingType);
			} catch (IWException e) {

				IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
				callback.failure(errorCode);
			}

			if (valueByte == null) {
				callback.failure(IWErrorCode.ERR_CODE_CRYPTOHELPER_DECRYPT);
			}

			element.setPrivateKey(Base58.encode(valueByte));

		}

		return keys;
	}
	// modified ends

	/*
	 * depends on private key type
	 *
	 * 1. SECP private key of spongy castle 2. SECP private key of eos
	 */

	protected abstract IWKey getIWKeyFromIWKeyElement(IWKeyElement iwKeyElement) throws IWException;

	protected abstract byte[] getPrivateKeyBytes(IWKey iwKey);

	protected abstract byte[] getPublicKeyBytes(IWKey iwKey);

	protected abstract byte[] getSignBytes(String keyId, byte[] source) throws IWException;

	protected abstract void verify(byte[] pubkey_rw, byte[] srouce, byte[] signature) throws IWException;


	// client
	@Override
	public String makeRequestVerifiableCredential(String did, List<Unprotected> privacyList, String keyId, byte[] nonce)
			throws IWException {

		VerifiableCredential verifiableCredential = new VerifiableCredential();

		CredentialSubject credentialSubject = new CredentialSubject();
		credentialSubject.setId(did);

		if (privacyList == null) {
			List<Unprotected> newPrivacyList = new ArrayList<Unprotected>();
			credentialSubject.setPrivacyList(newPrivacyList);
		} else {
			credentialSubject.setPrivacyList(privacyList);
		}

		verifiableCredential.setCredentialSubject(credentialSubject);
		verifiableCredential.setContext();
		verifiableCredential.setType();

		Proof proof = new Proof();

		// proof.creator, proof.created, proof.nonce
		byte[] signature = null;
		{
			// proof.creator
			String signKeyDid = getKeyDId(verifiableCredential.getCredentialSubject().getId(), keyId);
			proof.setCreator(signKeyDid);
			proof.setCreated(VerifiableClaim.dateToString(new Date()));

//			proof.setNonce(new String(nonce));
			proof.setNonce(new String(Hex.encode(nonce)));

			verifiableCredential.setProof(proof);
		}

		// proof.sign
		signature = getSignatureUsingKeyInKeyFile(verifiableCredential.toJson().getBytes(), keyId, this);
		proof.setSignatureValue(Base58.encode(signature));

		// proof.type
		IWKey key = getKeyElement(keyId);
		ALGORITHM_TYPE algType = ALGORITHM_TYPE.fromValue(key.getAlg());
		proof.setType(algType.toString() + ALGORITHM_VERIFICATION);

		verifiableCredential.setProof(proof);

		return verifiableCredential.toJson();
	}

	// dlchoi, 금결원용 -  client
	// extension 추가 (vcAttribute)
	@Override
	public String makeRequestVerifiableCredential(String did, List<Unprotected> privacyList, String keyId, byte[] nonce,
												  Extension extension) throws IWException {

		VerifiableCredential verifiableCredential = new VerifiableCredential();

		CredentialSubject credentialSubject = new CredentialSubject();
		credentialSubject.setId(did);

		if (privacyList == null) {
			List<Unprotected> newPrivacyList = new ArrayList<Unprotected>();
			credentialSubject.setPrivacyList(newPrivacyList);
		} else {
			credentialSubject.setPrivacyList(privacyList);
		}

		verifiableCredential.setCredentialSubject(credentialSubject);
		verifiableCredential.setContext();
		verifiableCredential.setType();
		verifiableCredential.setExtension(extension);

		Proof proof = new Proof();

		// proof.creator, proof.created, proof.nonce
		byte[] signature = null;
		{
			// proof.creator
			String signKeyDid = getKeyDId(verifiableCredential.getCredentialSubject().getId(), keyId);
			proof.setCreator(signKeyDid);
			proof.setCreated(VerifiableClaim.dateToString(new Date()));

//			proof.setNonce(new String(nonce));
			proof.setNonce(new String(Hex.encode(nonce)));

			verifiableCredential.setProof(proof);
		}

		// proof.sign
		signature = getSignatureUsingKeyInKeyFile(verifiableCredential.toJson().getBytes(), keyId, this);
		proof.setSignatureValue(Base58.encode(signature));

		// proof.type
		IWKey key = getKeyElement(keyId);
		ALGORITHM_TYPE algType = ALGORITHM_TYPE.fromValue(key.getAlg());
		proof.setType(algType.toString() + ALGORITHM_VERIFICATION);

		verifiableCredential.setProof(proof);

		return verifiableCredential.toJson();
	}

	@Override
	public boolean addCredential(String credentialJSON) throws IWException {
		if (!isUnLock())
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);

		VerifiableCredential new_vc = new VerifiableCredential();
		new_vc.fromJson(credentialJSON);

		IWKeyStoreData data = iwF.getData();

		ArrayList<String> encVcStore = data.getEncCredentials();
		if (encVcStore == null) {
			encVcStore = new ArrayList<String>();
		}

		int encodingType = data.getHead().getEncoding().getPrivacy();

		// wallet에 저장된 claim과 동일한 claim을 추가할 경우 exception 발생
		for (int i = 0; i < encVcStore.size(); i++) {
			String encData = encVcStore.get(i);
			byte[] valueByte = decrypt(encData, encodingType);

			VerifiableCredential vc = new VerifiableCredential();
			vc.fromJson(new String(valueByte));

			if (vc.getId().equals(new_vc.getId())) {
				throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DUPLICATED_VC_ID);
			}
		}

		// encrypt new VC
		String encrypted_New_VC = encrypt(new_vc.toJson().getBytes(), encodingType);

		// add new VC
		encVcStore.add(encrypted_New_VC);
		data.setEncCredential(encVcStore);

		iwF.write(data);

		return true;
	}

	@Override
	public List<VerifiableCredential> getCredentials() throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<String> encCredential = new ArrayList<String>();
		encCredential = data.getEncCredentials();

		ArrayList<VerifiableCredential> credentials = new ArrayList<VerifiableCredential>();
		int encodingType = data.getHead().getEncoding().getPrivacy();

		if (encCredential != null) {
			for (int i = 0; i < encCredential.size(); i++) {
				String encData = encCredential.get(i);

				byte[] valueByte = decrypt(encData, encodingType);

				VerifiableCredential vc = new VerifiableCredential();
				vc.fromJson(new String(valueByte));

				credentials.add(vc);

			}
		}

		return credentials;
		// modified ends
	}

	@Override
	public List<VerifiableCredential> getCredentials(String filter) throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		List<VerifiableCredential> credentials = getCredentials();

		if (credentials == null || credentials.size() == 0) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_EMPTY_VC);
		}

		List<VerifiableCredential> filtered = getFilteredCredentials(credentials, filter);

		if (filtered == null || filtered.size() == 0) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILTER_CONDITION_NOT_MATCH);
		}

		return filtered;
	}

	@Override
	public void removeCredential(VerifiableCredential credential) throws IWException {
		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<VerifiableCredential> vcList = (ArrayList<VerifiableCredential>) this.getCredentials();

		if (vcList == null || vcList.size() == 0) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_EMPTY_VC);
		}

		int deleteIndex = -1;
		String credentialJson = credential.toJson();

		for (int index = 0; index < vcList.size(); index++) {
			String targetClaim = vcList.get(index).toJson();

			if (credentialJson.equals(targetClaim)) {
				deleteIndex = index;
				break;
			}
		}

		if (deleteIndex == -1) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_VC);
		}

		ArrayList<String> encCredentialList = data.getEncCredentials();
		encCredentialList.remove(deleteIndex);

		data.setEncCredential(encCredentialList);
		;

		iwF.write(data);

	}

	@Override
	public void removeCredentials(String filter) throws IWException {
		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData keyStoreData = iwF.getData();
		int encodingType = keyStoreData.getHead().getEncoding().getKey();
		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		List<VerifiableCredential> credentials = getCredentials();

		if (credentials == null || credentials.size() == 0) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_EMPTY_VC);
		}

		List<VerifiableCredential> filtered = getFilteredCredentials(credentials, filter);

		if (filtered == null || filtered.size() == 0) {

			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILTER_CONDITION_NOT_MATCH);

		}

		for (VerifiableCredential credential : filtered) {
			credentials.remove(credential);
		}

		// dlchoi
		ArrayList<String> encCredentialList = new ArrayList<String>();
		for (int i = 0; i < credentials.size(); i++) {
			String encrypted_credential = encrypt(credentials.get(i).toJson().getBytes(), encodingType);
			encCredentialList.add(encrypted_credential);
		}

//		data.setClaims(claims);
		data.setEncCredential(encCredentialList);
		iwF.write(data);
	}

	@Override
	public void removeAllCredentials() throws IWException {
		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		if (data == null) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILE_LOAD_FAIL);
		}

		ArrayList<String> credentials = data.getEncCredentials();

		if (credentials != null && credentials.size() > 0) {

			data.setEncCredential(null);
			iwF.write(data);
		}
	}

	// dlchoi -  client
	@Override
	public String makeRequestVerifiablePresentation(String expirationDate, ArrayList<VerifiableCredential> credentials,
													String did, String keyId, byte[] nonce) throws IWException {

		VerifiablePresentation verifiablePresentation = new VerifiablePresentation();
		verifiablePresentation.setContext();
		verifiablePresentation.setType();
		verifiablePresentation.setId();
		verifiablePresentation.setExpirationDate(expirationDate);
		verifiablePresentation.setVerifiableCredential(credentials);

		Proof proof = new Proof();

		// proof.creator, proof.created, proof.nonce
		byte[] signature = null;
		{
			// proof.creator
			String signKeyDid;
			if (did == null) {
				signKeyDid = getKeyDId(credentials.get(0).getCredentialSubject().getId(), keyId);
			} else {
				signKeyDid = getKeyDId(did, keyId);
			}
			proof.setCreator(signKeyDid);
			proof.setCreated(VerifiableClaim.dateToString(new Date()));

			proof.setNonce(new String(Hex.encode(nonce)));

			verifiablePresentation.setProof(proof);
		}

		// proof.sign
		signature = getSignatureUsingKeyInKeyFile(verifiablePresentation.toJson().getBytes(), keyId, this);
		proof.setSignatureValue(Base58.encode(signature));

		// proof.type
		IWKey key = getKeyElement(keyId);
		ALGORITHM_TYPE algType = ALGORITHM_TYPE.fromValue(key.getAlg());
		proof.setType(algType.toString() + ALGORITHM_VERIFICATION);

		verifiablePresentation.setProof(proof);

		return verifiablePresentation.toJson();
	}

	// dlchoi, 금결원용 -  client
	// data : addSignData의 평문
	@Override
	public String makeRequestVerifiablePresentation(String expirationDate, ArrayList<VerifiableCredential> credentials,
													String did, String keyId, byte[] nonce, String data) throws IWException {

		VerifiablePresentation verifiablePresentation = new VerifiablePresentation();
		verifiablePresentation.setContext();
		verifiablePresentation.setType();
		verifiablePresentation.setId();
		verifiablePresentation.setExpirationDate(expirationDate);
		verifiablePresentation.setVerifiableCredential(credentials);

		Proof proof = new Proof();

		// proof.creator, proof.created, proof.nonce
		byte[] signature = null;
		{
			// proof.creator
			String signKeyDid;
			if (did == null) {
				signKeyDid = getKeyDId(credentials.get(0).getCredentialSubject().getId(), keyId);
			} else {
				signKeyDid = getKeyDId(did, keyId);
			}
			proof.setCreator(signKeyDid);
			proof.setCreated(VerifiableClaim.dateToString(new Date()));

			proof.setNonce(new String(Hex.encode(nonce)));

			verifiablePresentation.setProof(proof);
		}

		// proof.sign
		signature = getSignatureUsingKeyInKeyFile(verifiablePresentation.toJson().getBytes(), keyId, this);
		proof.setSignatureValue(Base58.encode(signature));

		// proof.type
		IWKey key = getKeyElement(keyId);
		ALGORITHM_TYPE algType = ALGORITHM_TYPE.fromValue(key.getAlg());
		proof.setType(algType.toString() + ALGORITHM_VERIFICATION);

		verifiablePresentation.setProof(proof);

		// addSignData
		if (data != null) {
			AddSignData addSignData = new AddSignData();
			addSignData.setData(data);
			signature = getSignatureUsingKeyInKeyFile(data.getBytes(), keyId, this);
			addSignData.setSignature(Base58.encode(signature));

			verifiablePresentation.setAddSignData(addSignData);
		}

		return verifiablePresentation.toJson();
	}

	// dlchoi, 금결원용 -  client
	// data : addSignData의 평문
	@Override
	public String makeRequestVerifiablePresentation(String expirationDate, ArrayList<VerifiableCredential> credentials,
													String did, String keyId, byte[] nonce, List<String> data) throws IWException {

		VerifiablePresentation verifiablePresentation = new VerifiablePresentation();
		verifiablePresentation.setContext();
		verifiablePresentation.setType();
		verifiablePresentation.setId();
		verifiablePresentation.setExpirationDate(expirationDate);
		verifiablePresentation.setVerifiableCredential(credentials);

		Proof proof = new Proof();

		// proof.creator, proof.created, proof.nonce
		byte[] signature = null;
		{
			// proof.creator
			String signKeyDid;
			if (did == null) {
				signKeyDid = getKeyDId(credentials.get(0).getCredentialSubject().getId(), keyId);
			} else {
				signKeyDid = getKeyDId(did, keyId);
			}
			proof.setCreator(signKeyDid);
			proof.setCreated(VerifiableClaim.dateToString(new Date()));

			proof.setNonce(new String(Hex.encode(nonce)));

			verifiablePresentation.setProof(proof);
		}

		// proof.sign
		signature = getSignatureUsingKeyInKeyFile(verifiablePresentation.toJson().getBytes(), keyId, this);
		proof.setSignatureValue(Base58.encode(signature));

		// proof.type
		IWKey key = getKeyElement(keyId);
		ALGORITHM_TYPE algType = ALGORITHM_TYPE.fromValue(key.getAlg());
		proof.setType(algType.toString() + ALGORITHM_VERIFICATION);

		verifiablePresentation.setProof(proof);

		if (data != null && data.size() != 0) {
			// addSignDataList
			List<AddSignData> addSignDataList = new ArrayList<AddSignData>();

			// addSignData
			for (int i = 0; i < data.size(); i++) {
				AddSignData addSignData = new AddSignData();
				addSignData.setData(data.get(i));
				signature = getSignatureUsingKeyInKeyFile(data.get(i).getBytes(), keyId, this);
				addSignData.setSignature(Base58.encode(signature));
				addSignDataList.add(addSignData);
			}
			verifiablePresentation.setAddSignDataList(addSignDataList);
		}

		return verifiablePresentation.toJson();
	}

	// 	-  client
	protected List<VerifiableCredential> getFilteredCredentials(List<VerifiableCredential> credentials, String filter)
			throws IWException {

		List<VerifiableCredential> filteredCredentials = new ArrayList<VerifiableCredential>();
		try {
			if (credentials != null && credentials.size() > 0) {
				String[] inputCondition = filter.split("\\s+");
				if (inputCondition.length >= 3) {

					String objectCondition = inputCondition[0];
					String actionCondition = inputCondition[1];
					String compareCondition = inputCondition[2];

					if (inputCondition.length > 3) {
						for (int index = 3; index < inputCondition.length; index++) {
							compareCondition = compareCondition + " " + inputCondition[index];
						}
					}

					String separatedObject[] = objectCondition.split("\\.");
					String lastword = separatedObject[separatedObject.length - 1];

					String definedAction[] = { "==", "!=", ">", ">=", "<", "<=" };

					String boolObjects[] = { "encType" };
					String numberObjects[] = { "level" };

					Arrays.sort(definedAction);
					int isRightAction = Arrays.binarySearch(definedAction, actionCondition);
					if (isRightAction < 0) {
						// error
						// wrong action condition
						throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILTER_SYNTAX_ERROR);
					}

					Arrays.sort(boolObjects);
					int boolObjectIndex = Arrays.binarySearch(boolObjects, lastword);

					boolean isNumberObject = false;
					boolean isBoolObject = false;

					if (boolObjectIndex >= 0) {
						if (actionCondition.contentEquals("==") == false) {
							// bool action condition only can be ==
							throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILTER_SYNTAX_ERROR);
						}
						isBoolObject = true;

					} else {
						Arrays.sort(numberObjects);
						int numberObjectIndex = Arrays.binarySearch(numberObjects, lastword);

						if (numberObjectIndex < 0) {
							// remaining is just object
							if (actionCondition.contains("<") || actionCondition.contains(">")) {
								// object cannot be compared
								throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILTER_SYNTAX_ERROR);
							}
						} else {
							isNumberObject = true;
						}
					}

					for (VerifiableCredential credential : credentials) {

						String vc = credential.toJson();

						JsonParser parser = new JsonParser();
						JsonElement ele = parser.parse(vc);

						for (int i = 0; i < separatedObject.length; i++) {

							String objectClass = separatedObject[i];

							if (i == separatedObject.length - 1) {
								if (isNumberObject) {
									int intValue = ele.getAsJsonObject().get(objectClass).getAsInt();

									int compareInt = Integer.parseInt(compareCondition);

									if (actionCondition.contentEquals("==")) {
										if (intValue == compareInt) {
											filteredCredentials.add(credential);
										}

									} else if (actionCondition.contentEquals("!=")) {
										if (intValue != compareInt) {
											filteredCredentials.add(credential);
										}
									} else {
										int correctInt = 0;
										if (actionCondition.contains("=")) {
											correctInt = 1;
										}

										if (actionCondition.contains("<")) {
											if (intValue < compareInt + correctInt) {
												filteredCredentials.add(credential);
											}
										} else if (actionCondition.contains(">")) {
											if (intValue > compareInt - correctInt) {
												filteredCredentials.add(credential);
											}
										}
									}

								} else if (isBoolObject) {

									if (actionCondition.contentEquals("==")) {

										boolean boolValue = ele.getAsJsonObject().get(objectClass).getAsBoolean();

										boolean compareValue = Boolean.parseBoolean(compareCondition);

										if (boolValue == compareValue) {
											filteredCredentials.add(credential);
										}

									}
								} else {
									String stringValue = ele.getAsJsonObject().get(objectClass).getAsString();

									if (actionCondition.contentEquals("==")) {
										if (stringValue.contentEquals(compareCondition)) {
											filteredCredentials.add(credential);
										}

									} else if (actionCondition.contentEquals("!=")) {
										if (!stringValue.contentEquals(compareCondition)) {
											filteredCredentials.add(credential);
										}
									}
								}

							} else {
								ele = ele.getAsJsonObject().get(objectClass);
								if (ele == null) {
									break;
								}
							}

						}

					}

				} else {
					// error
					// wrong filter
					throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILTER_SYNTAX_ERROR);
				}

			} else {
				throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_EMPTY_VC);
			}

		} catch (UnsupportedOperationException e) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILTER_SYNTAX_ERROR);
		}

		return filteredCredentials;
	}


	private byte[] intToBytes(int keyLength) {
		byte [] data = new byte [2];
		data[0] = (byte) ((keyLength >> 8) & 0xFF);
		data[1] = (byte) (keyLength & 0xFF);

		return data;
	}

	private int byteToInt(byte[] data) {
		int ret = ((data[0] & 0xff) << 8) | (data[1] & 0xff);
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	// ZKP Extra Data
	/////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean addZkpCredential(String credentialId, Credential credential) throws IWException, ZkpException {

		if (!isUnLock())
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);

		CredentialInfo credentialInfo = new CredentialInfo(credentialId, credential);
		ZkpLogger.debug("addZkpCredential: "+ZkpGsonWrapper.getGsonPrettyPrinting().toJson(credentialInfo));
		IWKeyStoreData data = iwF.getData();
		ArrayList<String> encVcStore = data.getEncZkpCredentials();

		if (encVcStore == null) {
			encVcStore = new ArrayList<String>();
		}

		int encodingType = data.getHead().getEncoding().getPrivacy();

		for (int i = 0; i < encVcStore.size(); i++) {

			String encData = encVcStore.get(i);
			CredentialInfo vc = new CredentialInfo();

			if (ZkpSetting.getInstance().isWalletCrypto()) {
				byte[] valueByte = decrypt(encData, encodingType);
				vc.fromJson(new String(valueByte));
			} else {
				vc.fromJson(encData);
			}

			// credential Id 중복 체크
			if (credentialId.equals(vc.getCredentialId())) {
				throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_DUPLICATED, "duplicated key credentialId");
			}
		}

		String jsonString = ZkpGsonWrapper.getGson().toJson(credentialInfo);
		// encrypt new VC
		String encrypted_New_VC = encrypt(jsonString.getBytes(), encodingType);

		// add new VC
		if (ZkpSetting.getInstance().isWalletCrypto()) {
			encVcStore.add(encrypted_New_VC);
		} else {
			encVcStore.add(jsonString);
		}

		ZkpLogger.debug("save zkpCredential size: "+encVcStore.size());
		data.setEncZkpCredential(encVcStore);
		iwF.write(data);

		return true;
	}



	@Override
	public ArrayList<CredentialInfo> getZkpCredentials() throws IWException, ZkpException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<String> encCredential = data.getEncZkpCredentials();

		if (encCredential != null) {
			ZkpLogger.debug("load zkpCredential size: " + encCredential.size());
		}

		ArrayList<CredentialInfo> credentials = new ArrayList<CredentialInfo>();
		int encodingType = data.getHead().getEncoding().getPrivacy();

		if (encCredential != null) {

			for (int i = 0; i < encCredential.size(); i++) {

				String encData = encCredential.get(i);
				CredentialInfo info;

				if (ZkpSetting.getInstance().isWalletCrypto()) {
					byte[] valueByte = decrypt(encData, encodingType);
					info = new Gson().fromJson(new String(valueByte), CredentialInfo.class);
				} else {
					info = new Gson().fromJson(encData, CredentialInfo.class);
				}

				credentials.add(info);
			}
		}

		return credentials;
	}

	@Override
	public void removeCredential(String credentialId, Credential credential) throws IWException, ZkpException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<CredentialInfo> vcList = this.getZkpCredentials();

		if (vcList == null || vcList.size() == 0) {
			return;
		}

		ArrayList<String> encCredentialList = data.getEncZkpCredentials();
		encCredentialList.clear();
		data.setEncZkpCredential(encCredentialList);
		iwF.write(data);
	}


	@Override
	public boolean addCredDefInfos(String credDefInfos) throws IWException, ZkpException {
		if (!isUnLock())
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);

		CredentialDefinitionInfo new_vc = new CredentialDefinitionInfo();
		new_vc.fromJson(credDefInfos);
		IWKeyStoreData data = iwF.getData();
		ArrayList<String> credDefInfosStore = data.getCredDefInfos();

		if (credDefInfosStore == null) {
			credDefInfosStore = new ArrayList<String>();
		}

		int encodingType = data.getHead().getEncoding().getPrivacy();

		for (int i = 0; i < credDefInfosStore.size(); i++) {

			String encData = credDefInfosStore.get(i);
			byte[] valueByte = decrypt(encData, encodingType);
			CredentialDefinitionInfo vc = new CredentialDefinitionInfo();

			if (ZkpSetting.getInstance().isWalletCrypto()) {
				vc.fromJson(new String(valueByte));
			} else {
				vc.fromJson(encData);
			}

			// credentialdef Id 중복 체크
			if (vc.getCredentialDefinition().getId().equals(new_vc.getCredentialDefinition().getId())) {
				throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_DUPLICATED, "duplicated key credential definition Id");
			}

		}

		String jsonString = ZkpGsonWrapper.getGson().toJson(new_vc);
		// encrypt new VC
		String encrypted_New_VC = encrypt(jsonString.getBytes(), encodingType);

		// add new VC
		if (ZkpSetting.getInstance().isWalletCrypto()) {
			credDefInfosStore.add(encrypted_New_VC);
		} else {
			credDefInfosStore.add(jsonString);
		}

		data.setCredDefInfos(credDefInfosStore);
		iwF.write(data);
		return true;
	}
	@Override
	public void removeCredDefInfos() throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<String> infos = data.getCredDefInfos();

		if (infos == null || infos.size() == 0) {
			return;
		}

		infos.clear();
		data.setCredDefInfos(infos);
		iwF.write(data);
	}

	@Override
	public boolean isExistCredDefInfo(String credDefId) throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<String> infos = data.getCredDefInfos();
		int encodingType = data.getHead().getEncoding().getPrivacy();

		if (infos != null) {

			for (int i = 0; i < infos.size(); i++) {

				String encData = infos.get(i);
				byte[] valueByte = decrypt(encData, encodingType);
				CredentialDefinitionInfo info;

				if (ZkpSetting.getInstance().isWalletCrypto()) {
					info = new Gson().fromJson(new String(valueByte), CredentialDefinitionInfo.class);
				} else {
					info = new Gson().fromJson(encData, CredentialDefinitionInfo.class);
				}

				if (credDefId.equals(info.getCredentialDefinition().getId())) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public CredentialDefinitionInfo getCredDefInfo(String credDefId) throws IWException, ZkpException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		CredentialDefinitionInfo credentialDefinitionInfo = new CredentialDefinitionInfo();
		ArrayList<String> encCredentialDef = data.getCredDefInfos();
		int encodingType = data.getHead().getEncoding().getPrivacy();

		if (encCredentialDef != null) {
			for (int i = 0; i < encCredentialDef.size(); i++) {

				String encData = encCredentialDef.get(i);
				CredentialDefinitionInfo info;

				if (ZkpSetting.getInstance().isWalletCrypto()) {
					byte[] valueByte = decrypt(encData, encodingType);
					info = new Gson().fromJson(new String(valueByte), CredentialDefinitionInfo.class);
				} else {
					info = new Gson().fromJson(encData, CredentialDefinitionInfo.class);
				}

				if (credDefId.equals(info.getCredentialDefinition().getId())) {
					credentialDefinitionInfo = info;
				}
			}
		} else {
			throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_NO_COMPARE_CRED_DEF_ID);
		}

		if (credentialDefinitionInfo.getCredentialDefinition() == null) {
			throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_NO_COMPARE_CRED_DEF_ID);
		}

		return credentialDefinitionInfo;
	}

	@Override
	public ArrayList<CredentialDefinitionInfo> getCredDefInfos() throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<String> encCredentialDef = data.getCredDefInfos();
		ArrayList<CredentialDefinitionInfo> credentialDefinitionInfoList = new ArrayList<CredentialDefinitionInfo>();
		int encodingType = data.getHead().getEncoding().getPrivacy();

		if (encCredentialDef != null) {
			for (int i = 0; i < encCredentialDef.size(); i++) {

				String encData = encCredentialDef.get(i);
				CredentialDefinitionInfo credentialDefinitionInfo;

				if (ZkpSetting.getInstance().isWalletCrypto()) {
					byte[] valueByte = decrypt(encData, encodingType);
					credentialDefinitionInfo = new Gson().fromJson(new String(valueByte), CredentialDefinitionInfo.class);
				} else {
					credentialDefinitionInfo = new Gson().fromJson(encData, CredentialDefinitionInfo.class);
				}

				credentialDefinitionInfoList.add(credentialDefinitionInfo);
			}
		}

		return credentialDefinitionInfoList;
	}


	@Override
	public boolean addRevRegInfos(String revRegInfoJson) throws IWException, ZkpException {

		if (!isUnLock())
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);

		RevocationRegistryDefinitionInfo new_vc = new RevocationRegistryDefinitionInfo();
		new_vc.fromJson(revRegInfoJson);
		IWKeyStoreData data = iwF.getData();
		ArrayList<String> revRegInfosStore = data.getRevRegInfos();

		if (revRegInfosStore == null) {
			revRegInfosStore = new ArrayList<String>();
		}

		int encodingType = data.getHead().getEncoding().getPrivacy();

		for (int i = 0; i < revRegInfosStore.size(); i++) {

			String encData = revRegInfosStore.get(i);
			RevocationRegistryDefineInfo revRegDefInfo = new RevocationRegistryDefineInfo();

			if (ZkpSetting.getInstance().isWalletCrypto()) {
				byte[] valueByte = decrypt(encData, encodingType);
				revRegDefInfo.fromJson(new String(valueByte));
			} else {
				revRegDefInfo.fromJson(encData);
			}

			if (revRegDefInfo.getRevocationRegistryDefinition().getRevocationRegistryId().equals(new_vc.getRevocationRegistryDefinition().getRevocationRegistryId())) {
				throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_DUPLICATED, "duplicated key credential revocation registry definition Id");
			}
		}

		String jsonString = ZkpGsonWrapper.getGson().toJson(new_vc);
		// encrypt new VC
		String encrypted_New_VC = encrypt(jsonString.getBytes(), encodingType);
		// add new VC
		if (ZkpSetting.getInstance().isWalletCrypto()) {
			revRegInfosStore.add(encrypted_New_VC);
		} else {
			revRegInfosStore.add(jsonString);
		}

		data.setRevRegInfos(revRegInfosStore);
		iwF.write(data);
		return true;
	}

	@Override
	public void removeRevRegInfos() throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}
		IWKeyStoreData data = iwF.getData();
		ArrayList<String> infos = data.getRevRegInfos();

		if (infos == null || infos.size() == 0) {
			return;
		}

		infos.clear();
		data.setRevRegInfos(infos);
		iwF.write(data);
	}

	@Override
	public void removeRevRegInfo(String revRegDefId) throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}
		IWKeyStoreData data = iwF.getData();
		ArrayList<String> infos = data.getRevRegInfos();
		int encodingType = data.getHead().getEncoding().getPrivacy();

		if (infos == null || infos.size() == 0) {
			return;
		}

		for (int i = 0; i < infos.size(); i++) {
			String encData = infos.get(i);
			RevocationRegistryDefineInfo info;

			if (ZkpSetting.getInstance().isWalletCrypto()) {
				byte[] valueByte = decrypt(encData, encodingType);
				info = new Gson().fromJson(new String(valueByte), RevocationRegistryDefineInfo.class);
			} else {
				info = new Gson().fromJson(encData, RevocationRegistryDefineInfo.class);
			}

			if (revRegDefId.equals(info.getRevocationRegistryDefinition().getRevocationRegistryId())) {
				infos.remove(i);
			}
		}

		data.setRevRegInfos(infos);
		iwF.write(data);
	}


	@Override
	public boolean isExistRevRegInfo(String revRegId) throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<String> infos = data.getRevRegInfos();
		int encodingType = data.getHead().getEncoding().getPrivacy();

		if (infos != null) {
			for (int i = 0; i < infos.size(); i++) {

				String encData = infos.get(i);
				RevocationRegistryDefineInfo info;

				if (ZkpSetting.getInstance().isWalletCrypto()) {
					byte[] valueByte = decrypt(encData, encodingType);
					info = new Gson().fromJson(new String(valueByte), RevocationRegistryDefineInfo.class);
				} else {
					info = new Gson().fromJson(encData, RevocationRegistryDefineInfo.class);
				}

				if (revRegId.equals(info.getRevocationRegistryDefinition().getRevocationRegistryId())) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public ArrayList<RevocationRegistryDefinitionInfo> getRevRegInfos() throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<String> encRevRegInfos = data.getRevRegInfos();
		ArrayList<RevocationRegistryDefinitionInfo> revocationRegistryDefinitionList = new ArrayList<RevocationRegistryDefinitionInfo>();
		int encodingType = data.getHead().getEncoding().getPrivacy();

		if (encRevRegInfos != null) {
			for (int i = 0; i < encRevRegInfos.size(); i++) {

				String encData = encRevRegInfos.get(i);
				RevocationRegistryDefinitionInfo revocationRegistryDefinitionInfo;

				if (ZkpSetting.getInstance().isWalletCrypto()) {
					byte[] valueByte = decrypt(encData, encodingType);
					revocationRegistryDefinitionInfo = new Gson().fromJson(new String(valueByte), RevocationRegistryDefinitionInfo.class);
				} else {
					revocationRegistryDefinitionInfo = new Gson().fromJson(encData, RevocationRegistryDefinitionInfo.class);
				}

				revocationRegistryDefinitionList.add(revocationRegistryDefinitionInfo);
			}
		}

		return revocationRegistryDefinitionList;
	}



	@Override
	public RevocationRegistryDefinitionInfo getRevRegInfo(String revRegDefId) throws IWException, ZkpException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<String> encRevRegInfos = data.getRevRegInfos();
		RevocationRegistryDefinitionInfo revocationRegistryDefinition = new RevocationRegistryDefinitionInfo();
		int encodingType = data.getHead().getEncoding().getPrivacy();

		if (encRevRegInfos != null) {
			for (int i = 0; i < encRevRegInfos.size(); i++) {

				String encData = encRevRegInfos.get(i);
				// json str -> 역직렬화 속도 이슈로 revRegId만 사용하기 위해 임시 모델 (특히 key 연산속도 느림)
				RevocationRegistryDefineInfo revocationRegistryDefineInfo;

				if (ZkpSetting.getInstance().isWalletCrypto()) {
					byte[] valueByte = decrypt(encData, encodingType);
					revocationRegistryDefineInfo = new Gson().fromJson(new String(valueByte), RevocationRegistryDefineInfo.class);
				} else {
					revocationRegistryDefineInfo = new Gson().fromJson(encData, RevocationRegistryDefineInfo.class);
				}
				// id로 검출 시 역직렬화 진행
				if (revocationRegistryDefineInfo != null && revRegDefId.equals(revocationRegistryDefineInfo.getRevocationRegistryDefinition().getRevocationRegistryId())) {
					if (ZkpSetting.getInstance().isWalletCrypto()) {
						byte[] valueByte = decrypt(encData, encodingType);
						revocationRegistryDefinition = new Gson().fromJson(new String(valueByte), RevocationRegistryDefinitionInfo.class);
					} else {
						revocationRegistryDefinition = new Gson().fromJson(encData, RevocationRegistryDefinitionInfo.class);
					}
					break;
				}
			}
		} else {
			throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_NO_COMPARE_REV_REG_DEF_ID);
		}

		if (revocationRegistryDefinition.getRevocationRegistryDefinition() == null) {
			throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_NO_COMPARE_REV_REG_DEF_ID);
		}

		return revocationRegistryDefinition;
	}

	@Override
	public RevocationRegistryDefinitionInfo getRevRegInfoByCredDefId(String credRegDefId) throws IWException, ZkpException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<String> encRevRegInfos = data.getRevRegInfos();
		RevocationRegistryDefinitionInfo revocationRegistryDefinition = new RevocationRegistryDefinitionInfo();
		int encodingType = data.getHead().getEncoding().getPrivacy();

		if (encRevRegInfos != null) {
			for (int i = 0; i < encRevRegInfos.size(); i++) {

				String encData = encRevRegInfos.get(i);
				RevocationRegistryDefinitionInfo revocationRegistryDefinitionInfo;

				if (ZkpSetting.getInstance().isWalletCrypto()) {
					byte[] valueByte = decrypt(encData, encodingType);
					revocationRegistryDefinitionInfo = new Gson().fromJson(new String(valueByte), RevocationRegistryDefinitionInfo.class);
				} else {
					revocationRegistryDefinitionInfo = new Gson().fromJson(encData, RevocationRegistryDefinitionInfo.class);
				}

				if (credRegDefId.equals(revocationRegistryDefinitionInfo.getRevocationRegistryDefinition().getCredentialDefinitionId())) {
					revocationRegistryDefinition = revocationRegistryDefinitionInfo;
					break;
				}
			}
		} else {
			throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_NO_COMPARE_REV_REG_DEF_ID);
		}

		if (revocationRegistryDefinition.getRevocationRegistryDefinition() == null) {
			throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_NO_COMPARE_REV_REG_DEF_ID);
		}

		return revocationRegistryDefinition;
	}

	@Override
	public RevocationRegistryDefinitionInfo getRevRegInfoById(String revRegDefId) throws IWException, ZkpException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<String> encRevRegInfos = data.getRevRegInfos();
		RevocationRegistryDefinitionInfo revocationRegistryDefinition = new RevocationRegistryDefinitionInfo();
		int encodingType = data.getHead().getEncoding().getPrivacy();

		if (encRevRegInfos != null) {
			for (int i = 0; i < encRevRegInfos.size(); i++) {

				String encData = encRevRegInfos.get(i);

				// json str -> 역직렬화 속도 이슈로 revRegId만 사용하기 위해 임시 모델 (특히 key 연산속도 느림)
				RevocationRegistryDefineInfo revocationRegistryDefineInfo;

				if (ZkpSetting.getInstance().isWalletCrypto()) {
					byte[] valueByte = decrypt(encData, encodingType);
					revocationRegistryDefineInfo = new Gson().fromJson(new String(valueByte), RevocationRegistryDefineInfo.class);
				} else {
					revocationRegistryDefineInfo = new Gson().fromJson(encData, RevocationRegistryDefineInfo.class);
				}
				// id로 검출 시 역직렬화 진행
				if (revocationRegistryDefineInfo != null && revRegDefId.equals(revocationRegistryDefineInfo.getRevocationRegistryDefinition().getRevocationRegistryId())) {
					if (ZkpSetting.getInstance().isWalletCrypto()) {
						byte[] valueByte = decrypt(encData, encodingType);
						revocationRegistryDefinition = new Gson().fromJson(new String(valueByte), RevocationRegistryDefinitionInfo.class);
					} else {
						revocationRegistryDefinition = new Gson().fromJson(encData, RevocationRegistryDefinitionInfo.class);
					}
					break;
				}
			}
		} else {
			throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_NO_COMPARE_REV_REG_DEF_ID);
		}

		if (revocationRegistryDefinition.getRevocationRegistryDefinition() == null) {
			throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_NO_COMPARE_REV_REG_DEF_ID);
		}

		return revocationRegistryDefinition;
	}

	@Override
	public boolean addMasterSecrets(String masterSecretJson) throws IWException, ZkpException {

		if (!isUnLock())
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);

		MasterSecret new_vc = new MasterSecret();
		new_vc.fromJson(masterSecretJson);
		IWKeyStoreData data = iwF.getData();
		ArrayList<String> masterSecretsStore = data.getMasterSecrets();

		if (masterSecretsStore == null) {
			masterSecretsStore = new ArrayList<String>();
		}

		int encodingType = data.getHead().getEncoding().getPrivacy();

		for (int i = 0; i < masterSecretsStore.size(); i++) {

			String encData = masterSecretsStore.get(i);
			MasterSecret masterSecret = new MasterSecret();

			if (ZkpSetting.getInstance().isWalletCrypto()) {
				byte[] valueByte = decrypt(encData, encodingType);
				masterSecret.fromJson(new String(valueByte));
			} else {
				masterSecret.fromJson(encData);
			}

			if (masterSecret.getMasterSecretId().equals(new_vc.getMasterSecretId())) {
				throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_DUPLICATED, "duplicated key master secret Id");
			}
		}

		String jsonString = ZkpGsonWrapper.getGson().toJson(new_vc);
		// encrypt new VC
		String encrypted_New_VC = encrypt(jsonString.getBytes(), encodingType);
		// add new VC
		if (ZkpSetting.getInstance().isWalletCrypto()) {
			masterSecretsStore.add(encrypted_New_VC);
		} else {
			masterSecretsStore.add(jsonString);
		}

		data.setMasterSecrets(masterSecretsStore);
		iwF.write(data);
		return true;
	}

	@Override
	public void removeMasterSecrets() throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<String> infos = data.getMasterSecrets();

		if (infos == null || infos.size() == 0) {
			return;
		}

		infos.clear();
		data.setMasterSecrets(infos);
		iwF.write(data);
	}



	@Override
	public boolean isExistMasterSecret(String masterSecretId) throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<String> encMasterSecrets = data.getMasterSecrets();
		int encodingType = data.getHead().getEncoding().getPrivacy();

		if (encMasterSecrets != null) {

			for (int i = 0; i < encMasterSecrets.size(); i++) {

				String encData = encMasterSecrets.get(i);
				MasterSecret info;

				if (ZkpSetting.getInstance().isWalletCrypto()) {
					byte[] valueByte = decrypt(encData, encodingType);
					info = new Gson().fromJson(new String(valueByte), MasterSecret.class);
				} else {
					info = new Gson().fromJson(encData, MasterSecret.class);
				}

				if (masterSecretId.equals(info.getMasterSecretId())) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public ArrayList<MasterSecret> getMasterSecrets() throws IWException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<String> encMasterSecrets = data.getMasterSecrets();
		ArrayList<MasterSecret> masterSecretList = new ArrayList<MasterSecret>();
		int encodingType = data.getHead().getEncoding().getPrivacy();

		if (encMasterSecrets != null) {

			for (int i = 0; i < encMasterSecrets.size(); i++) {

				String encData = encMasterSecrets.get(i);
				MasterSecret info;

				if (ZkpSetting.getInstance().isWalletCrypto()) {
					byte[] valueByte = decrypt(encData, encodingType);
					info = new Gson().fromJson(new String(valueByte), MasterSecret.class);
				} else {
					info = new Gson().fromJson(encData, MasterSecret.class);
				}

				masterSecretList.add(info);
			}
		}

		return masterSecretList;
	}

	@Override
	public MasterSecret getMasterSecret(String masterSecretId) throws IWException, ZkpException {

		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<String> encMasterSecrets = data.getMasterSecrets();
		MasterSecret masterSecret = new MasterSecret();
		int encodingType = data.getHead().getEncoding().getPrivacy();

		if (encMasterSecrets != null) {
			for (int i = 0; i < encMasterSecrets.size(); i++) {

				String encData = encMasterSecrets.get(i);
				MasterSecret info;

				if (ZkpSetting.getInstance().isWalletCrypto()) {
					byte[] valueByte = decrypt(encData, encodingType);
					info = new Gson().fromJson(new String(valueByte), MasterSecret.class);
				} else {
					info = new Gson().fromJson(encData, MasterSecret.class);
				}

				if (masterSecretId.equals(info.getMasterSecretId())) {
					masterSecret = info;
				}
			}
		} else {
			throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_SELECT_MASTER_SECRET_FROM_WALLET_FAIL);
		}

		return masterSecret;
	}


	// dlchoi
	// remove
	@Override
	public void removeZkpCredential(CredentialInfo credentialInfo) throws IWException, ZkpException {
		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		ArrayList<CredentialInfo> zkpVcList = (ArrayList<CredentialInfo>) this.getZkpCredentials();

		if (zkpVcList == null || zkpVcList.size() == 0) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_EMPTY_VC);
		}

		int deleteIndex = -1;
		String credentialJson = credentialInfo.toJson();

		for (int index = 0; index < zkpVcList.size(); index++) {
			String targetCredential = zkpVcList.get(index).toJson();

			if (credentialJson.equals(targetCredential)) {
				deleteIndex = index;
				break;
			}
		}

		if (deleteIndex == -1) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_INVALID_VC);
		}

		ArrayList<String> encCredentialList = data.getEncZkpCredentials();
		encCredentialList.remove(deleteIndex);

		data.setEncZkpCredential(encCredentialList);

		iwF.write(data);
	}


	@Override
	public void removeAllZkpCredential() throws IWException{
		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		if (data == null) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILE_LOAD_FAIL);
		}

		ArrayList<String> zkpCredentials = data.getEncZkpCredentials();

		if (zkpCredentials != null && zkpCredentials.size() > 0) {

			data.setEncZkpCredential(null);
			iwF.write(data);
		}
	}

	@Override
	public boolean addFabricUserPrivateKey(String newPrivateKey) throws IWException {
		if(!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		if(this.isExistFabricUserPrivateKey()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_FILE_WRITE_FAIL.getCode(), "exist fabric user private key");
		}

		IWKeyStoreData data = iwF.getData();
		int encodingType = data.getHead().getEncoding().getPrivacy();

		// encrypt new fabric user private key
		String encrypted_New_Key = encrypt(newPrivateKey.getBytes(), encodingType);

		// add new fabric user private key
		String privateKey = null;
		if (ZkpSetting.getInstance().isWalletCrypto()) {
			privateKey = encrypted_New_Key;
		} else {
			privateKey = newPrivateKey;
		}

		data.setFabricUserPrivateKey(privateKey);
		iwF.write(data);

		return true;
	}

	@Override
	public String getFabricUserPrivateKey() throws IWException {
		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		String key = data.getFabricUserPrivateKey();
		int encodingType = data.getHead().getEncoding().getPrivacy();

		String privateKey = null;
		if(key != null) {
			String encData = key;

			if (ZkpSetting.getInstance().isWalletCrypto()) {
				byte[] valueByte = decrypt(encData, encodingType);
				privateKey = new String(valueByte);
			} else {
				privateKey = encData;
			}
		}

		return privateKey;
	}

	@Override
	public void removeFabricUserPrivateKey() throws IWException {
		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		String privateKey = this.getFabricUserPrivateKey();

		if (privateKey != null) {
			data.setFabricUserPrivateKey(null);
			iwF.write(data);
		}
	}

	@Override
	public boolean isExistFabricUserPrivateKey() throws IWException {
		if (!isUnLock()) {
			throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
		}

		IWKeyStoreData data = iwF.getData();
		String privateKey = data.getFabricUserPrivateKey();

		return (privateKey != null);
	}
}

