package org.omnione.did.wallet.key;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.crypto.GDPCryptoHelperClient;
import org.omnione.did.wallet.data.did.DIDs;
import org.omnione.did.wallet.data.iw.VerifiableClaim;
import org.omnione.did.wallet.data.iw.v2.VerifiableCredential;
import org.omnione.did.wallet.eoscommander.crypto.digest.Sha256;
import org.omnione.did.wallet.eoscommander.crypto.ec.EosPrivateKey;
import org.omnione.did.wallet.eoscommander.crypto.ec.EosPublicKey;
import org.omnione.did.wallet.eoscommander.crypto.util.Base58;
import org.omnione.did.wallet.eoscommander.crypto.util.HexUtils;
import org.omnione.did.wallet.eoscommander.util.StringUtils;
import org.omnione.did.wallet.exception.IWErrorCode;
import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.key.IWRecoveryManager.BACKUP_OPTION;
import org.omnione.did.wallet.key.IWRecoveryManager.BackUpCallback;
import org.omnione.did.wallet.key.IWRecoveryManager.ENCRYPTION_TYPE;
import org.omnione.did.wallet.key.IWRecoveryManager.RestoreCallback;
import org.omnione.did.wallet.key.data.AESType;
import org.omnione.did.wallet.key.data.IWHeadElement;
import org.omnione.did.wallet.key.data.IWKdf;
import org.omnione.did.wallet.key.data.IWKey;
import org.omnione.did.wallet.key.data.IWKeyElement;
import org.omnione.did.wallet.key.data.IWRecoveryHeaderData;
import org.omnione.did.wallet.key.store.IWDIDFile;
//import org.omnione.did.wallet.util.OmniOneLicenseChcker;
import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.data.Credential;
import org.omnione.did.wallet.zkp.data.MasterSecret;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.data.dto.CredentialInfo;




public class IWRecoveryManagerImpl {

	private enum RECOVERY_MANAGER_VERSION {
		LEGACY(0),
		LATEST(1);//DID_FILE_NAME_BACKUP + CREDENTIAL

		public final int value;

		RECOVERY_MANAGER_VERSION(int num) {
			this.value = num;
		}

		public int getValue() {
		    return value;
		}

	}

	private class DIDsForBackUp extends DIDs {
		@SerializedName("fileName")
	    @Expose
	    private String fileName;

		public String getFileName() {
	        return fileName;
	    }

	    public void setFileName(String fileName) {
	        this.fileName = fileName;
	    }

	    public void fromJson(String val) {
	    	super.fromJson(val);

	    	GsonWrapper gson = new GsonWrapper();
	    	DIDsForBackUp data = gson.fromJson(val, DIDsForBackUp.class);

	    	if(!StringUtils.isEmpty(data.getFileName())) {
	    		fileName = data.getFileName();
	    	}
	    }
	}


//	private final static int COMPRESSED 					= 0b10000000;
	private final static int TAG_SIZE 						= 1;
	private final static int TOTAL_BYTE_SIZE 				= 4;
	private final static int LENGTH_SIZE 					= 4;
	private final static int CHECKSUM_SIZE					= 4;
	private final static int DID_DOCUMENT_FILE_NAME_LENGTH	= 12;
	private final static int SALT_SIZE 						= 32;



	private final static char BLOCK_TYPE_CLAIM				= 'c';
	private final static char BLOCK_TYPE_DID_DOCUMENT		= 'D';
	private final static char BLOCK_TYPE_EXTRA				= 'E';
	private final static char BLOCK_TYPE_HEADER				= 'H';
	private final static char BLOCK_TYPE_KEY				= 'k';
	private final static char BLOCK_TYPE_CREDENTIAL			= 'C';
	private final static char BLOCK_TYPE_ZKP_MASTERSECRET	= 'M';
	private final static char BLOCK_TYPE_ZKP_CREDENTIAL		= 'Z';

	private final static String DID_EXTENSION				= "did";

//	@NonNull
	private IWKeyManager keyManager;

	private IWKdf kdf;
	private int encryptionType;

	private List<VerifiableClaim> restoreClaims;
	private List<IWKey> restoreKeys;
	private HashMap<String, String> restoreDidDocs;

	private List<VerifiableCredential> restoreCredentials;
	private List<MasterSecret> restoreMasterSecrets;
	private List<CredentialInfo> restoreZkpCredentials;


	public IWRecoveryManagerImpl(IWKeyManager keyManager){
		init(keyManager);
	}

	private void init(IWKeyManager keyManager){
		this.keyManager = keyManager;
	}





	/**
	 * Back Up the stored data with selected options parameters
	 *
	 * @param range			: Back Up Range
	 * @param type			: Encryption Type
	 * @param key			: Encryption Key
	 * @param didPaths		: Array of Did Document Path to Back Up
	 * @param extraString	: Extra String to be backed up
	 * @param callback		: Success or failure callback
	 * @return
	 * @throws IWException
	 */




	public void backUpStoredData(int range, ENCRYPTION_TYPE type, String key, String[] didPaths, String extraString, BackUpCallback callback) throws IWException{

//		OmniOneLicenseChcker.check();

		clearRestorationDummies();

		if(key == null || key.length() == 0) {
			callback.failure(IWErrorCode.ERR_CODE_RECOVERY_MANAGER_INVALID_PASSWORD);
			return;
		}

		if(!keyManager.isUnLock()) {

			callback.failure(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
			return;
		}

		encryptionType = type.getValue();

		GDPCryptoHelperClient cryptoHelper = new GDPCryptoHelperClient();
		byte[] salt = null;
		try {
			salt = cryptoHelper.generateSecureRandom(SALT_SIZE);
		} catch (IWException e1) {
			callback.failure(IWErrorCode.ERR_CODE_CRYPTOHELPER_GENSRANDOM_FAIL);
			return;
		}
		String saltString = HexUtils.toHex(salt);




		byte [] keyByte = null;
		try {
			keyByte = key.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
			return;
		}

		Sha256 hash = Sha256.from(keyByte);
		byte[] hashedByte = hash.getBytes();

		int iterations = 2048;

		if(kdf != null) {
			kdf.clean();
		}
		else {
			kdf = new IWKdf();
		}

		kdf.deriveKeyWithSeed(hashedByte, saltString, iterations);

		IWRecoveryHeaderData header = new IWRecoveryHeaderData();
		header.setEncryptionType(encryptionType);
		header.setSalt(saltString);
		header.setIterations(iterations);
		header.setVersion(RECOVERY_MANAGER_VERSION.LATEST.getValue());


		List<byte []> backUpArrayList = new ArrayList<byte[]>();

		if(range == BACKUP_OPTION.TOTAL.getValue()) {
			range = ~range;
		}

		if((range & BACKUP_OPTION.DID_DOCUMENT.getValue()) == BACKUP_OPTION.DID_DOCUMENT.getValue()) {
			if(didPaths == null || didPaths.length == 0) {

				callback.failure(IWErrorCode.ERR_CODE_RECOVERY_MANAGER_EMPTY_DID_PATH);
				return;
			}

			JsonArray jsonArray = new JsonArray();

			for (String path :didPaths) {
				IWDIDFile didFile = new IWDIDFile(path);
				if(!didFile.isExist()) {
					callback.failure(IWErrorCode.ERR_CODE_DIDMANAGER_FILE_NOT_EXIST);
					return;
				}

				DIDs did = didFile.getData();
				if(did == null) {
					callback.failure(IWErrorCode.ERR_CODE_DIDMANAGER_FILE_NOT_EXIST);
					return;
				}

				String fileName = null;
		        int i = path.lastIndexOf('/');

		        if (i > 0 &&  i < path.length() - 1) {
		        	fileName = path.substring(i+1);
		        }

		        if(fileName != null && fileName.length() > 4) {
		        	DIDsForBackUp didsWithFileName = new DIDsForBackUp();
					didsWithFileName.fromJson(did.toJson());
		        	didsWithFileName.setFileName(fileName);
		        	jsonArray.add(didsWithFileName.toJson());
		        }
		        else {
		        	jsonArray.add(did.toJson());
		        }


			}

			byte[] bodyByte = null;
			try {
				bodyByte = jsonArrayConvertToByteBlock(BLOCK_TYPE_DID_DOCUMENT, jsonArray);
			} catch (IWException e) {
				callback.failure(IWErrorCode.ERR_CODE_CRYPTOHELPER_ENCRYPT);
				return;

			} catch (UnsupportedEncodingException e) {
				callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
				return;
			}
			backUpArrayList.add(bodyByte);

		}

		if((range & BACKUP_OPTION.CLAIM.getValue()) == BACKUP_OPTION.CLAIM.getValue()) {
			List<VerifiableClaim> claims = null;
			try {
				claims = keyManager.getClaims();
			} catch (IWException e) {
				IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
				callback.failure(errorCode);
				return;
			}
			if(claims != null && claims.size() > 0 ) {

				JsonArray jsonArray = new JsonArray();
				for(VerifiableClaim claim : claims) {

					jsonArray.add(claim.toJson());
				}

				byte[] bodyByte = null;
				try {
					bodyByte = jsonArrayConvertToByteBlock(BLOCK_TYPE_CLAIM, jsonArray);
				} catch (IWException e) {

//					e.printStackTrace();
					IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
					callback.failure(errorCode);
					return;

				} catch (UnsupportedEncodingException e) {

//					e.printStackTrace();
					callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
					return;
				}
				backUpArrayList.add(bodyByte);

			}
		}

		if((range & BACKUP_OPTION.KEY.getValue()) == BACKUP_OPTION.KEY.getValue()) {

			byte[] encrypted = null;
			try {
				encrypted = keyManager.getAllKeyElementsEncrypted();
			} catch (IWException e) {

//				e.printStackTrace();

				IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
				callback.failure(errorCode);
				return;
			}

			if(encrypted != null) {
				IWHeadElement kmHeader = keyManager.getHeader();
				if(kmHeader != null && kmHeader.getProxyKey() != null && kmHeader.getProxyKey().length() > 0) {
					String proxyKey = kmHeader.getProxyKey();

					byte[] keyData = null;
					try {
						keyData = proxyKey.getBytes("UTF-8");
					} catch (UnsupportedEncodingException e) {

//						e.printStackTrace();
						callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
						return;
					}

					byte[] decrypted = null;
					try {
						decrypted = GDPCryptoHelperClient.aesDecrypt(encrypted, keyData, AESType.AES128);
					} catch (IWException e) {

//						e.printStackTrace();
						IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
						callback.failure(errorCode);
						return;
					}

					JsonArray jsonArray = byteToJsonArray(decrypted);



					byte[] bodyByte = null;
					try {
						bodyByte = jsonArrayConvertToByteBlock(BLOCK_TYPE_KEY, jsonArray);
					} catch (IWException e) {
						IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
						callback.failure(errorCode);
						return;

					} catch (UnsupportedEncodingException e) {

						callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
						return;
					}
					backUpArrayList.add(bodyByte);

				}

			}

		}

		if(extraString != null && extraString.length() > 0) {
			byte [] extraSource = null;
			try {
				extraSource = extraString.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
				return;
			}

			byte[] encrypted = null;
			try {
				encrypted = encryptSource(extraSource);
			} catch (IWException e) {
				IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
				callback.failure(errorCode);
				return;
			}

			byte[] extraByte = makeByteBlock(BLOCK_TYPE_EXTRA, encrypted);
			backUpArrayList.add(extraByte);

		}

		if((range & BACKUP_OPTION.CREDENTIAL.getValue()) == BACKUP_OPTION.CREDENTIAL.getValue()) {
			List<VerifiableCredential> credentials = null;
			try {
				credentials = keyManager.getCredentials();
			} catch (IWException e) {
				IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
				callback.failure(errorCode);
				return;
			}
			if(credentials != null && credentials.size() > 0 ) {

				JsonArray jsonArray = new JsonArray();
				for(VerifiableCredential credential : credentials) {

					jsonArray.add(credential.toJson());
				}

				byte[] bodyByte = null;
				try {
					bodyByte = jsonArrayConvertToByteBlock(BLOCK_TYPE_CREDENTIAL, jsonArray);
				} catch (IWException e) {

//					e.printStackTrace();
					IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
					callback.failure(errorCode);
					return;

				} catch (UnsupportedEncodingException e) {

//					e.printStackTrace();
					callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
					return;
				}
				backUpArrayList.add(bodyByte);

			}
		}

		// dlchoi - masterSecret 추가
		if((range & BACKUP_OPTION.MASTERSECRET.getValue()) == BACKUP_OPTION.MASTERSECRET.getValue()) {
			ArrayList<MasterSecret> masterSecrets = null;
			try {
				masterSecrets = keyManager.getMasterSecrets();
			} catch (IWException e) {
				IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
				callback.failure(errorCode);
				return;
			}
			if(masterSecrets != null && masterSecrets.size() > 0 ) {

				JsonArray jsonArray = new JsonArray();
				for(MasterSecret ms : masterSecrets) {

					jsonArray.add(ms.toJson());
				}

				byte[] bodyByte = null;
				try {
					bodyByte = jsonArrayConvertToByteBlock(BLOCK_TYPE_ZKP_MASTERSECRET, jsonArray);
				} catch (IWException e) {

//					e.printStackTrace();
					IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
					callback.failure(errorCode);
					return;

				} catch (UnsupportedEncodingException e) {

//					e.printStackTrace();
					callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
					return;
				}
				backUpArrayList.add(bodyByte);

			}
		}

		// dlchoi - zkpCredential 추가
		if((range & BACKUP_OPTION.ZKPCREDENTIAL.getValue()) == BACKUP_OPTION.ZKPCREDENTIAL.getValue()) {
			ArrayList<CredentialInfo> zkpCredentials = null;
			try {
				zkpCredentials = keyManager.getZkpCredentials();
			} catch (IWException e) {
				IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
				callback.failure(errorCode);
				return;
			} catch (ZkpException e) {
				e.printStackTrace();
			}
			if(zkpCredentials != null && zkpCredentials.size() > 0 ) {

				JsonArray jsonArray = new JsonArray();
				for(CredentialInfo credentialInfo : zkpCredentials) {

					jsonArray.add(credentialInfo.toJson());
				}

				byte[] bodyByte = null;
				try {
					bodyByte = jsonArrayConvertToByteBlock(BLOCK_TYPE_ZKP_CREDENTIAL, jsonArray);
				} catch (IWException e) {

//					e.printStackTrace();
					IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
					callback.failure(errorCode);
					return;

				} catch (UnsupportedEncodingException e) {

//					e.printStackTrace();
					callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
					return;
				}
				backUpArrayList.add(bodyByte);

			}
		}


		kdf.clean();

		if(backUpArrayList.size() == 0) {

			callback.failure(IWErrorCode.ERR_CODE_RECOVERY_MANAGER_EMPTY_CONTENT);
			return;

		}

		String headerJson = header.toJson();

		byte[] headerBodyByte = null;
		try {
			headerBodyByte = headerJson.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
			return;
		}

		byte[] headerByte = makeByteBlock(BLOCK_TYPE_HEADER, headerBodyByte);

		int bufferSize = headerByte.length;
		for(byte[] blockByte : backUpArrayList) {
			bufferSize += blockByte.length;
		}

		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		buffer.put(headerByte);

		for(byte[] blockByte : backUpArrayList) {
			buffer.put(blockByte);
		}


		byte[] totalBodyByte = buffer.array();
		byte[] totalSizeByte = ByteBuffer.allocate(4).putInt(totalBodyByte.length).array();
		byte[] totalCheckSum = getCheckSum(totalBodyByte);

		ByteBuffer backUpBuffer = ByteBuffer.allocate(TOTAL_BYTE_SIZE + totalBodyByte.length + CHECKSUM_SIZE);
		backUpBuffer.put(totalSizeByte);
		backUpBuffer.put(totalBodyByte);
		backUpBuffer.put(totalCheckSum);

		byte[] backUpByte = backUpBuffer.array();

		byte[] encrypted = null;
		try {
			encrypted = GDPCryptoHelperClient.aesEncrypt(backUpByte, keyByte, AESType.AES128);
		} catch (IWException e) {
			IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
			callback.failure(errorCode);
			return;
		}


		callback.success(encrypted);

	}



	/**
	 * Restore the backed up data
	 *
	 * @param backedUpByte	: Backed Up Data
	 * @param key			: Decryption Key
	 * @param didPath		: Did Document Path To Be Stored
	 * @param callback		: Success or failure callback
	 * @return
	 * @throws IWException
	 */

	public void restoreBackedUpByte(byte[] backedUpByte, String key, String didPath, RestoreCallback callback) throws IWException{
//		OmniOneLicenseChcker.check();

		clearRestorationDummies();

		if(key == null || key.length() == 0) {
			callback.failure(IWErrorCode.ERR_CODE_RECOVERY_MANAGER_INVALID_PASSWORD);
			return;
		}

		if(!keyManager.isUnLock()) {
			callback.failure(IWErrorCode.ERR_CODE_KEYMANAGER_LOCKED);
			return;
		}

		byte [] keyByte = null;
		try {
			keyByte = key.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
			return;
		}

		StringBuilder didTargetPath = new StringBuilder();

		didTargetPath.append(didPath);

		String lastWord = didPath.substring(didPath.length()-1, didPath.length());
		if(lastWord.equals("/") == false) {
			didTargetPath.append("/");
		}


		byte[] decryptedTotalByte = null;
		try {
			decryptedTotalByte = GDPCryptoHelperClient.aesDecrypt(backedUpByte, keyByte, AESType.AES128);
		} catch (IWException e) {
			IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
			callback.failure(errorCode);
			return;
		}


		if(decryptedTotalByte == null || decryptedTotalByte.length == 0) {
			callback.failure(IWErrorCode.ERR_CODE_CRYPTOHELPER_DECRYPT);
			return;
		}

		if(decryptedTotalByte.length <= (TOTAL_BYTE_SIZE + CHECKSUM_SIZE)) {
			callback.failure(IWErrorCode.ERR_CODE_RECOVERY_MANAGER_CONTENT_IS_CORRUPTED);
			return;
		}

		byte[] totalSizeByte = Arrays.copyOfRange(decryptedTotalByte, 0, TOTAL_BYTE_SIZE);

		int totalSize = byteToInt(totalSizeByte, TOTAL_BYTE_SIZE);

		if((TOTAL_BYTE_SIZE + totalSize + CHECKSUM_SIZE) != decryptedTotalByte.length) {

			callback.failure(IWErrorCode.ERR_CODE_RECOVERY_MANAGER_CONTENT_IS_CORRUPTED);
			return;
		}

		byte[] totalBodyByte = Arrays.copyOfRange(decryptedTotalByte, TOTAL_BYTE_SIZE, TOTAL_BYTE_SIZE + totalSize);

		byte[] totalCheckSum = Arrays.copyOfRange(decryptedTotalByte, TOTAL_BYTE_SIZE + totalSize, TOTAL_BYTE_SIZE + totalSize + CHECKSUM_SIZE);

		byte[] tempTotalCheckSum = getCheckSum(totalBodyByte);

		if(java.util.Arrays.equals(totalCheckSum, tempTotalCheckSum) == false) {
			callback.failure(IWErrorCode.ERR_CODE_RECOVERY_MANAGER_CHECKSUM_NOT_MATCH);
			return;
		}

		if(kdf != null) {
			kdf.clean();
		}
		else {
			kdf = new IWKdf();
		}

		int index = 0;

		String extraString = null;

		while(index < totalSize) {

			if(totalSize <= (index + TAG_SIZE)) {

				String path = didTargetPath.toString();
				try {
					failureRestore(path);
				} catch (IWException e1) {
					clearRestorationDummies();

					IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e1.getErrorCode());
					callback.failure(errorCode);

					return;
				}

				callback.failure(IWErrorCode.ERR_CODE_RECOVERY_MANAGER_CONTENT_IS_CORRUPTED);
				return;
			}

			byte[] tagByte = Arrays.copyOfRange(totalBodyByte, index, index + TAG_SIZE);
			char tag = getTagChar(tagByte[0]);

			index += TAG_SIZE;

			if(totalSize <= (index + LENGTH_SIZE)) {

				String path = didTargetPath.toString();
				try {
					failureRestore(path);
				} catch (IWException e1) {
					clearRestorationDummies();

					IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e1.getErrorCode());
					callback.failure(errorCode);

					return;
				}

				callback.failure(IWErrorCode.ERR_CODE_RECOVERY_MANAGER_CONTENT_IS_CORRUPTED);
				return;
			}

			byte[] lengthByte = Arrays.copyOfRange(totalBodyByte, index, index + LENGTH_SIZE);
			int size = byteToInt(lengthByte, LENGTH_SIZE);

			index += LENGTH_SIZE;

			if(totalSize <= (index + size)) {

				String path = didTargetPath.toString();
				try {
					failureRestore(path);
				} catch (IWException e1) {
					clearRestorationDummies();

					IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e1.getErrorCode());
					callback.failure(errorCode);

					return;
				}

				callback.failure(IWErrorCode.ERR_CODE_RECOVERY_MANAGER_CONTENT_IS_CORRUPTED);
				return;
			}

			byte[] bodyByte = Arrays.copyOfRange(totalBodyByte, index, index + size);
			index += size;

			if(totalSize < (index + CHECKSUM_SIZE)) {

				String path = didTargetPath.toString();
				try {
					failureRestore(path);
				} catch (IWException e1) {
					clearRestorationDummies();

					IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e1.getErrorCode());
					callback.failure(errorCode);

					return;
				}

				callback.failure(IWErrorCode.ERR_CODE_RECOVERY_MANAGER_CONTENT_IS_CORRUPTED);
				return;
			}

			byte[] checkSumByte = Arrays.copyOfRange(totalBodyByte, index, index + CHECKSUM_SIZE);
			index += CHECKSUM_SIZE;

			byte[] checkSumSourceByte = new byte[LENGTH_SIZE + size];
			System.arraycopy(lengthByte , 0, checkSumSourceByte, 0				, LENGTH_SIZE);
			System.arraycopy(bodyByte, 0, checkSumSourceByte, LENGTH_SIZE	, size);

			byte[] tempCheckSumByte = getCheckSum(checkSumSourceByte);


			if (java.util.Arrays.equals(checkSumByte, tempCheckSumByte) == false) {

				String path = didTargetPath.toString();
				try {
					failureRestore(path);
				} catch (IWException e1) {
					clearRestorationDummies();

					IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e1.getErrorCode());
					callback.failure(errorCode);

					return;
				}

				callback.failure(IWErrorCode.ERR_CODE_RECOVERY_MANAGER_CHECKSUM_NOT_MATCH);
				return;
			}

			if( tag == BLOCK_TYPE_HEADER) {
				getHeaderFromJson(keyByte, bodyByte);
			}
			else if(tag == BLOCK_TYPE_EXTRA) {
				extraString = new String(getExtraString(bodyByte, didTargetPath, callback));
			}
			else {
				byte[] decryptedBodyByte = getDecryptedBodyByte(bodyByte, callback);
				JsonArray jsonArray = byteToJsonArray(decryptedBodyByte);

				if(jsonArray == null || jsonArray.size() == 0) {
					kdf.clean();
					callback.failure(IWErrorCode.ERR_CODE_CRYPTOHELPER_DECRYPT);
					return;
				}

				switch (tag) {
					case BLOCK_TYPE_DID_DOCUMENT:
					{
						String path = didTargetPath.toString();

						try {
							removeDidFiles(path, true);
						} catch (IWException e) {
							IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
							callback.failure(errorCode);
							return;
						}


						break;
					}
					case BLOCK_TYPE_CLAIM:
					{

						try {
							restoreClaims = keyManager.getClaims();

							keyManager.removeAllClaims();
						} catch (IWException e) {
							kdf.clean();

							IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
							callback.failure(errorCode);
							return;
						}
						break;
					}
					case BLOCK_TYPE_KEY:
					{
						byte[] encrypted = null;
						try {
							encrypted = keyManager.getAllKeyElementsEncrypted();
						} catch (IWException e) {
							IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
							callback.failure(errorCode);
							return;
						}

						if(encrypted != null) {
							IWHeadElement kmHeader = keyManager.getHeader();
							if(kmHeader != null && kmHeader.getProxyKey() != null && kmHeader.getProxyKey().length() > 0) {
								String proxyKey = kmHeader.getProxyKey();


								byte[] decrypted = getAllKeyElementsDecrypted(proxyKey, encrypted, callback);

								if(decrypted == null) return;

								JsonArray keyJsonArray = byteToJsonArray(decrypted);

								restoreKeys = new ArrayList<IWKey>();

								for(JsonElement item :keyJsonArray) {
									String stringItem = item.getAsString();

									IWKey keyItem = jsonConvertToIWKey(stringItem);

									restoreKeys.add(keyItem);
								}
							}
						}

						try {
							keyManager.removeAllKeys();
						} catch (IWException e) {
							kdf.clean();

							IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
							callback.failure(errorCode);
							return;
						}

						break;
					}
					case BLOCK_TYPE_CREDENTIAL:
					{

						try {
							restoreCredentials = keyManager.getCredentials();

							keyManager.removeAllCredentials();
						} catch (IWException e) {
							kdf.clean();

							IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
							callback.failure(errorCode);
							return;
						}
						break;
					}
					case BLOCK_TYPE_ZKP_MASTERSECRET:
					{

						try {
							restoreMasterSecrets = keyManager.getMasterSecrets();

							keyManager.removeMasterSecrets();
						} catch (IWException e) {
							kdf.clean();

							IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
							callback.failure(errorCode);
							return;
						}
						break;
					}
					case BLOCK_TYPE_ZKP_CREDENTIAL:
					{

						try {
							try {
								restoreZkpCredentials = keyManager.getZkpCredentials();
							} catch (ZkpException e) {
								e.printStackTrace();
							}

							keyManager.removeAllZkpCredential();
						} catch (IWException e) {
							kdf.clean();

							IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
							callback.failure(errorCode);
							return;
						}
						break;
					}

					default:
					{
						break;
					}
				}

				for(JsonElement item :jsonArray) {
					String stringItem = item.getAsString();


					switch (tag) {
						case BLOCK_TYPE_DID_DOCUMENT:
						{

							if(didPath == null || didPath.length() == 0) {

								kdf.clean();

								String path = didTargetPath.toString();
								try {
									failureRestore(path);
								} catch (IWException e1) {
									clearRestorationDummies();

									IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e1.getErrorCode());
									callback.failure(errorCode);

									return;
								}

								callback.failure(IWErrorCode.ERR_CODE_RECOVERY_MANAGER_EMPTY_DID_PATH);

								return;
							}

							setDidToIwFile(stringItem, didTargetPath);

							break;
						}
						case BLOCK_TYPE_CLAIM:
						{

							try {
								keyManager.addClaim(stringItem);
							} catch (IWException e) {
								kdf.clean();

								String path = didTargetPath.toString();
								try {
									failureRestore(path);
								} catch (IWException e1) {
									clearRestorationDummies();

									IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e1.getErrorCode());
									callback.failure(errorCode);

									return;
								}

								IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
								callback.failure(errorCode);
								return;
							}
							break;
						}
						case BLOCK_TYPE_KEY:
						{
					    	IWKey keyItem = jsonConvertToIWKey(stringItem);

					    	try {
								keyManager.addKey(keyItem);
							} catch (IWException e) {
								kdf.clean();

								String path = didTargetPath.toString();
								try {
									failureRestore(path);
								} catch (IWException e1) {
									clearRestorationDummies();

									IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e1.getErrorCode());
									callback.failure(errorCode);

									return;
								}

								IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
								callback.failure(errorCode);
								return;
							}

							break;
						}
						case BLOCK_TYPE_CREDENTIAL:
						{

							try {
								keyManager.addCredential(stringItem);
							} catch (IWException e) {
								kdf.clean();

								String path = didTargetPath.toString();
								try {
									failureRestore(path);
								} catch (IWException e1) {
									clearRestorationDummies();

									IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e1.getErrorCode());
									callback.failure(errorCode);

									return;
								}

								IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
								callback.failure(errorCode);
								return;
							}
							break;
						}
						case BLOCK_TYPE_ZKP_MASTERSECRET:
						{

							try {
								keyManager.addMasterSecrets(stringItem);
							} catch (IWException e) {
								kdf.clean();

								String path = didTargetPath.toString();
								try {
									failureRestore(path);
								} catch (IWException e1) {
									clearRestorationDummies();

									IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e1.getErrorCode());
									callback.failure(errorCode);

									return;
								}

								IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
								callback.failure(errorCode);
								return;
							} catch (ZkpException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;
						}

						case BLOCK_TYPE_ZKP_CREDENTIAL:
						{

							try {
								CredentialInfo credentialInfo = new CredentialInfo();
								credentialInfo.fromJson(stringItem);
								keyManager.addZkpCredential(credentialInfo.getCredentialId(), credentialInfo.getCredential());
							} catch (IWException e) {
								kdf.clean();

								String path = didTargetPath.toString();
								try {
									failureRestore(path);
								} catch (IWException e1) {
									clearRestorationDummies();

									IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e1.getErrorCode());
									callback.failure(errorCode);

									return;
								}

								IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
								callback.failure(errorCode);
								return;
							} catch (ZkpException e) {
								// TODO Auto-generated catch block

								e.printStackTrace();
							}
							break;
						}


						default:
						{
							break;
						}
					}
				}

			}

		}

		kdf.clean();

		clearRestorationDummies();

		callback.success(extraString);

	}

	private IWKey jsonConvertToIWKey(String stringItem){
		IWKeyElement keyElement = new IWKeyElement();
		keyElement.fromJson(stringItem);

		byte[] encPrivKey = Base58.decode(keyElement.getPrivateKey());
		byte[] encPubKey = Base58.decode(keyElement.getPublicKey());

		EosPrivateKey eosPriKey = new EosPrivateKey(encPrivKey);
		EosPublicKey eosPubKey = new EosPublicKey(encPubKey);
    	IWKey keyItem = new IWKey(keyElement.getKeyId(), keyElement.getAlg(), eosPubKey, eosPriKey);

    	return keyItem;
	}

	private void failureRestore(String didPath) throws IWException {

		if(restoreClaims != null) {
			keyManager.removeAllClaims();

			for(VerifiableClaim claim : restoreClaims) {
				String claimString = claim.toJson();

				keyManager.addClaim(claimString);
			}
		}

		if(restoreKeys != null) {
			keyManager.removeAllKeys();

			for(IWKey key : restoreKeys) {
				keyManager.addKey(key);
			}
		}


		if(restoreDidDocs != null) {

			removeDidFiles(didPath, false);

			for(String key : restoreDidDocs.keySet()) {
				String didDoc = restoreDidDocs.get(key);

				IWDIDManager didManager = new IWDIDManager(key);
				didManager.saveToFile(didDoc);
			}
		}

		if(restoreCredentials != null) {
			keyManager.removeAllCredentials();

			for(VerifiableCredential credential : restoreCredentials) {
				String credentialString = credential.toJson();

				keyManager.addCredential(credentialString);
			}
		}

		if(restoreMasterSecrets != null) {
			keyManager.removeMasterSecrets();;

			for(MasterSecret ms : restoreMasterSecrets) {
				String msString = ms.toJson();

				try {
					keyManager.addMasterSecrets(msString);
				} catch (IWException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ZkpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


		if(restoreZkpCredentials != null) {
			keyManager.removeAllZkpCredential();

			for(CredentialInfo credentialInfo : restoreZkpCredentials) {
				String credentialId = credentialInfo.getCredentialId();
				Credential credential = credentialInfo.getCredential();

				try {
					keyManager.addZkpCredential(credentialId, credential);
				} catch (IWException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ZkpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}



		clearRestorationDummies();

	}

	private void removeDidFiles(String didPath, boolean needToBackUp) throws IWException {

		File dir = new File(didPath);

		File[] fileList = dir.listFiles();

		if(fileList != null && fileList.length > 0) {
			for (File file : fileList) {
				if(file.isFile()) {

					String fileName = file.getName();

					String[] splittedName = fileName.split("\\.");

					if(splittedName != null && splittedName.length > 0) {

						String extension = splittedName[splittedName.length -1];

						if(extension.equalsIgnoreCase(DID_EXTENSION)) {

							if(needToBackUp) {

								String canonicalPath = null;
								try {
									canonicalPath = file.getCanonicalPath();


								} catch (IOException e) {
									// TODO Auto-generated catch block
//									e.printStackTrace();

									kdf.clean();

									throw new IWException(IWErrorCode.ERR_CODE_RECOVERY_MANAGER_FAILED_TO_GET_DID_PATH);

								}

								IWDIDManager didManager = new IWDIDManager(canonicalPath);

								if(restoreDidDocs == null) {
									restoreDidDocs = new HashMap<String, String>();
								}

								try {
									restoreDidDocs.put(canonicalPath, didManager.makeRequestDIDsRead());
								} catch (IWException e) {
									// TODO Auto-generated catch block
//									e.printStackTrace();

									kdf.clean();

									throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_FILE_NOT_EXIST);

								}

								didManager.deleteDIDFile();


							}
							else {
								file.delete();
							}


						}
					}

				}
			}
		}
	}

	private void clearRestorationDummies() {
		if(restoreClaims != null) {
			restoreClaims = null;
		}

		if(restoreKeys != null) {
			restoreKeys = null;
		}

		if(restoreDidDocs != null) {
			restoreDidDocs.clear();
			restoreDidDocs = null;
		}

		if(restoreCredentials != null) {
			restoreCredentials.clear();
			restoreCredentials = null;
		}
		if(restoreMasterSecrets != null) {
			restoreMasterSecrets.clear();
			restoreMasterSecrets = null;
		}
		if(restoreZkpCredentials != null) {
			restoreZkpCredentials.clear();
			restoreZkpCredentials = null;
		}

	}

	private byte[] makeByteBlock(char tag, byte[] encrypted) {

		byte tagByte = getTagByte(tag);
		int length = encrypted.length;

		byte[] lengthByte = ByteBuffer.allocate(4).putInt(length).array();

		byte[] bodyByte = new byte[LENGTH_SIZE + encrypted.length];
		System.arraycopy(lengthByte , 0, bodyByte, 0		, LENGTH_SIZE);
		System.arraycopy(encrypted, 0, bodyByte, LENGTH_SIZE, length);

		byte[] checkSum = getCheckSum(bodyByte);

		int bufferCapacity = TAG_SIZE + LENGTH_SIZE + length + CHECKSUM_SIZE;
		ByteBuffer buffer = ByteBuffer.allocate(bufferCapacity);
		buffer.put(tagByte);
		buffer.put(bodyByte);
		buffer.put(checkSum);


		return buffer.array();
	}

	private int byteToInt(byte[] source, int byteSize) {

		long ret=0;
        for(int i = 0; i < byteSize; i++){

            ret |= ((source[i]& 0xff)<< (8 * (byteSize - 1 - i)));
        }

        return (int)ret;
	}

	private byte getTagByte(char tag) {

		byte[] tagByte = new byte[2];
		tagByte[1] = (byte) tag;

		return tagByte[1];
	}

	private char getTagChar(byte tagByte) {
		char tag = (char) (tagByte & 0xFF);

		return tag;
	}

	private byte[] getCheckSum(byte[] source) {
		Sha256 hash = Sha256.from(source);
		byte[] hashedByte = hash.getBytes();

		byte[] checkSum = new byte[CHECKSUM_SIZE];
		System.arraycopy(hashedByte,0,checkSum,	0, CHECKSUM_SIZE);

		return checkSum;
	}

	private byte[] encryptSource(byte[] source) throws IWException {

		byte[] iv32 = kdf.getIv();
		byte[] iv16 = new byte[16];
		System.arraycopy(iv32,0,iv16,	0, 16);

		GDPCryptoHelperClient cryptoHelper = new GDPCryptoHelperClient();
		byte[] encrypted  = null;

		ENCRYPTION_TYPE type = ENCRYPTION_TYPE.fromValue(encryptionType);

		switch(type) {
			case AES_128:{
				byte[] key32 = kdf.getKey();
				byte[] key16 = new byte[16];
				System.arraycopy(key32,0,key16,	0, 16);
				encrypted  = cryptoHelper.encrypt(key16, iv16, source);
				break;
			}
			case AES_256:{
				encrypted  = cryptoHelper.encrypt(kdf.getKey(), iv16, source);
				break;
			}
			default:{
				break;
			}
		}

		return encrypted;

	}

	private byte[] decryptByte(byte[] source) throws IWException {

		byte[] iv32 = kdf.getIv();
		byte[] iv16 = new byte[16];
		System.arraycopy(iv32,0,iv16,	0, 16);

		GDPCryptoHelperClient cryptoHelper = new GDPCryptoHelperClient();
		byte[] decrypted  = null;

		ENCRYPTION_TYPE type = ENCRYPTION_TYPE.fromValue(encryptionType);

		switch(type) {
			case AES_128:{
				byte[] key32 = kdf.getKey();
				byte[] key16 = new byte[16];
				System.arraycopy(key32,0,key16,	0, 16);
				decrypted  = cryptoHelper.decrypt(key16, iv16, source);
				break;
			}
			case AES_256:{
				decrypted  = cryptoHelper.decrypt(kdf.getKey(), iv16, source);
				break;
			}
			default:{
				break;
			}
		}

		return decrypted;

	}

	private byte[] jsonArrayConvertToByteBlock(char tag, JsonArray jsonArray) throws IWException, UnsupportedEncodingException {
		byte[] jsonByte = jsonArrayToByte(jsonArray);

		byte[] encrypted  = encryptSource(jsonByte);

		return makeByteBlock(tag, encrypted);
	}



	private byte[] jsonArrayToByte(JsonArray jsonArray) throws UnsupportedEncodingException {

		String jsonString = jsonArray.toString();

		byte[] bodyByte = jsonString.getBytes("UTF-8");

		return bodyByte;
	}

	private JsonArray byteToJsonArray(byte [] jsonByte) {
		String jsonString = new String(jsonByte);

		if(jsonString == null || jsonString.length() == 0) {
			return null;
		}
		JsonParser jsonParser = new JsonParser();
		JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonString);

		return jsonArray;

	}

	private String getSaltString(BackUpCallback callback) {
		GDPCryptoHelperClient cryptoHelper = new GDPCryptoHelperClient();
		byte[] salt = null;
		try {
			salt = cryptoHelper.generateSecureRandom(SALT_SIZE);
		} catch (IWException e1) {
			callback.failure(IWErrorCode.ERR_CODE_CRYPTOHELPER_GENSRANDOM_FAIL);

		}
		String saltString = HexUtils.toHex(salt);

		return saltString;
	}


	private byte[] getKeyByte(String key, String saltString, int iterations, BackUpCallback callback) {
		byte [] keyByte = null;
		try {
			keyByte = key.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
		}

		Sha256 hash = Sha256.from(keyByte);
		byte[] hashedByte = hash.getBytes();


		setIWKdf(hashedByte, saltString, iterations);


		return keyByte;
	}



	private void setIWKdf(byte[] hashedByte, String saltString,int iterations) {
		if(kdf != null) {
			kdf.clean();
		}
		else {
			kdf = new IWKdf();
		}

		kdf.deriveKeyWithSeed(hashedByte, saltString, iterations);

	}



	private List<byte []> setBackUpArrayList(int range, String[] didPaths, String extraString, BackUpCallback callback) throws IWException {
		List<byte []> backUpArrayList = new ArrayList<byte[]>();

		if(range == BACKUP_OPTION.TOTAL.getValue()) {
			range = ~range;
		}

		if((range & BACKUP_OPTION.DID_DOCUMENT.getValue()) == BACKUP_OPTION.DID_DOCUMENT.getValue()) {
			if(didPaths == null || didPaths.length == 0) {
				callback.failure(IWErrorCode.ERR_CODE_RECOVERY_MANAGER_EMPTY_DID_PATH);

			}
			byte[] bodyByte = setDIDDocumentData(didPaths, callback);
			backUpArrayList.add(bodyByte);
		}


		if((range & BACKUP_OPTION.CLAIM.getValue()) == BACKUP_OPTION.CLAIM.getValue()) {
			List<VerifiableClaim> claims = null;
			try {
				claims = keyManager.getClaims();
			} catch (IWException e) {
				IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
				callback.failure(errorCode);
			}
			if(claims != null && claims.size() > 0 ) {
				byte[] bodyByte = setClaimData(claims, callback);

				backUpArrayList.add(bodyByte);
			}
		}

		if((range & BACKUP_OPTION.KEY.getValue()) == BACKUP_OPTION.KEY.getValue()) {
			byte[] encrypted = null;
			try {
				encrypted = keyManager.getAllKeyElementsEncrypted();
			} catch (IWException e) {
				IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
				callback.failure(errorCode);
			}

			if(encrypted != null) {
				IWHeadElement kmHeader = keyManager.getHeader();
				if(kmHeader != null && kmHeader.getProxyKey() != null && kmHeader.getProxyKey().length() > 0) {


					byte[] bodyByte = setKeyData(encrypted, kmHeader, callback);

					backUpArrayList.add(bodyByte);

				}

			}

		}

		if(extraString != null && extraString.length() > 0) {
			byte[] extraByte = setExtraByte(extraString, callback);
			backUpArrayList.add(extraByte);
		}
		return backUpArrayList;
	}


	private byte[] setDIDDocumentData(String[] didPaths, BackUpCallback callback) throws IWException {
		JsonArray jsonArray = new JsonArray();

		for (String path :didPaths) {
			IWDIDFile didFile = new IWDIDFile(path);
			if(!didFile.isExist()) {
				callback.failure(IWErrorCode.ERR_CODE_DIDMANAGER_FILE_NOT_EXIST);
			}

			DIDs did = didFile.getData();
			if(did == null) {
				callback.failure(IWErrorCode.ERR_CODE_DIDMANAGER_FILE_NOT_EXIST);
			}
			jsonArray.add(did.toJson());
		}

		byte[] bodyByte = null;
		try {
			bodyByte = jsonArrayConvertToByteBlock(BLOCK_TYPE_DID_DOCUMENT, jsonArray);
		} catch (IWException e) {
			callback.failure(IWErrorCode.ERR_CODE_CRYPTOHELPER_ENCRYPT);

		} catch (UnsupportedEncodingException e) {
			callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
		}

		return bodyByte;

	}

	private byte[] setClaimData(List<VerifiableClaim> claims, BackUpCallback callback) {
		JsonArray jsonArray = new JsonArray();
		for(VerifiableClaim claim : claims) {
			jsonArray.add(claim.toJson());
		}

		byte[] bodyByte = null;
		try {
			bodyByte = jsonArrayConvertToByteBlock(BLOCK_TYPE_CLAIM, jsonArray);
		} catch (IWException e) {
			IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
			callback.failure(errorCode);


		} catch (UnsupportedEncodingException e) {
			callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
		}

		return bodyByte;
	}


	private byte[] setKeyData(byte[] encrypted, IWHeadElement kmHeader, BackUpCallback callback) {
		String proxyKey = kmHeader.getProxyKey();

		byte[] keyData = null;
		try {
			keyData = proxyKey.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
		}

		byte[] decrypted = null;
		try {
			decrypted = GDPCryptoHelperClient.aesDecrypt(encrypted, keyData, AESType.AES128);
		} catch (IWException e) {
			IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
			callback.failure(errorCode);

		}
		JsonArray jsonArray = byteToJsonArray(decrypted);

		byte[] bodyByte = null;
		try {
			bodyByte = jsonArrayConvertToByteBlock(BLOCK_TYPE_KEY, jsonArray);
		} catch (IWException e) {
			IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
			callback.failure(errorCode);
		} catch (UnsupportedEncodingException e) {
			callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);

		}

		return bodyByte;
	}


	private byte[] setExtraByte(String extraString, BackUpCallback callback) {
		byte [] extraSource = null;
		try {
			extraSource = extraString.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
		}

		byte[] encrypted = null;
		try {
			encrypted = encryptSource(extraSource);
		} catch (IWException e) {
			IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
			callback.failure(errorCode);
		}

		byte[] extraByte = makeByteBlock(BLOCK_TYPE_EXTRA, encrypted);

		return extraByte;
	}

	private byte[] setHeaderByte(String saltString, int iterations, BackUpCallback callback) {
		IWRecoveryHeaderData header = new IWRecoveryHeaderData();
		header.setEncryptionType(encryptionType);
		header.setSalt(saltString);
		header.setIterations(iterations);
		String headerJson = header.toJson();

		byte[] headerBodyByte = null;
		try {
			headerBodyByte = headerJson.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);

		}

		byte[] headerByte = makeByteBlock(BLOCK_TYPE_HEADER, headerBodyByte);

		return headerByte;
	}

	private void getHeaderFromJson(byte[] keyByte, byte[] bodyByte) {
		Sha256 pwdHash = Sha256.from(keyByte);
		byte[] hashedPwdByte = pwdHash.getBytes();

		String headerJson = new String(bodyByte);
		IWRecoveryHeaderData header = new IWRecoveryHeaderData();
		header.fromJson(headerJson);

		encryptionType = header.getEncryptionType();

		kdf.deriveKeyWithSeed(hashedPwdByte, header.getSalt(), header.getIterations());
	}


	private byte[] getExtraString(byte[] bodyByte, StringBuilder didTargetPath, RestoreCallback callback) {

		byte[] decryptedBodyByte = null;
		try {
			decryptedBodyByte = decryptByte(bodyByte);
		} catch (IWException e) {

			kdf.clean();

			String path = didTargetPath.toString();
			try {
				failureRestore(path);
			} catch (IWException e1) {
				clearRestorationDummies();

				IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e1.getErrorCode());
				callback.failure(errorCode);

			}

			IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
			callback.failure(errorCode);
		}
		return decryptedBodyByte;
	}

	private byte[] getDecryptedBodyByte(byte[] bodyByte, RestoreCallback callback) {
		byte[] decryptedBodyByte = null;
		try {
			decryptedBodyByte = decryptByte(bodyByte);
		} catch (IWException e) {
			kdf.clean();

			IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
			callback.failure(errorCode);

		}

		if(decryptedBodyByte == null || decryptedBodyByte.length == 0) {
			kdf.clean();
			callback.failure(IWErrorCode.ERR_CODE_CRYPTOHELPER_DECRYPT);

		}

		return decryptedBodyByte;
	}

	private byte[] getAllKeyElementsDecrypted(String proxyKey, byte[] encrypted, RestoreCallback callback) {
		byte[] keyData = null;
		try {
			keyData = proxyKey.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			callback.failure(IWErrorCode.ERR_CODE_STRING_CONVERT_TO_BYTE);
		}

		byte[] decrypted = null;
		try {
			decrypted = GDPCryptoHelperClient.aesDecrypt(encrypted, keyData, AESType.AES128);
		} catch (IWException e) {
			IWErrorCode errorCode = (IWErrorCode) IWErrorCode.getEnumByCode(e.getErrorCode());
			callback.failure(errorCode);
		}

		return decrypted;
	}

	private void setDidToIwFile(String stringItem, StringBuilder didTargetPath ) throws IWException {
		DIDsForBackUp didsWithFileName = new DIDsForBackUp();
		didsWithFileName.fromJson(stringItem);

      	String fileName = null;
      	fileName = didsWithFileName.getFileName();
      	if(fileName == null || fileName.length() == 0) {
      		String did = didsWithFileName.getId();
          	String sepaDid[] = did.split("\\:");

          	fileName = sepaDid[sepaDid.length - 1];

          	if(fileName.length() > DID_DOCUMENT_FILE_NAME_LENGTH) {
          		fileName = fileName.substring(0, DID_DOCUMENT_FILE_NAME_LENGTH);
          	}
      	}

      	StringBuilder fileNamePath = new StringBuilder();

      	fileNamePath.append(didTargetPath.toString());
      	fileNamePath.append(fileName);

      	IWDIDFile iwD = new IWDIDFile(fileNamePath.toString());

      	DIDs dids = new DIDs();
      	dids.fromJson(stringItem);
      	iwD.write(dids);
	}
}
