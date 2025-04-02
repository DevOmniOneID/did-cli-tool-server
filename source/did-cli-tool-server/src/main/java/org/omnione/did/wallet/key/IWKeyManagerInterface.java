package org.omnione.did.wallet.key;
import java.util.ArrayList;
import java.util.List;

import org.omnione.did.wallet.data.did.PublicKey;
import org.omnione.did.wallet.data.iw.Extension;
import org.omnione.did.wallet.data.iw.Privacy;
import org.omnione.did.wallet.data.iw.Unprotected;
import org.omnione.did.wallet.data.iw.VerifiableClaim;
import org.omnione.did.wallet.data.iw.profile.AbstractProfile;
import org.omnione.did.wallet.data.iw.v2.VerifiableCredential;
import org.omnione.did.wallet.exception.IWErrorCode;
import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.key.data.AESType;
import org.omnione.did.wallet.key.data.IWHeadElement;
import org.omnione.did.wallet.key.data.IWKey;
import org.omnione.did.wallet.zkp.data.Credential;
import org.omnione.did.wallet.zkp.data.MasterSecret;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.data.dto.CredentialDefinitionInfo;
import org.omnione.did.wallet.zkp.revoc.data.dto.CredentialInfo;
import org.omnione.did.wallet.zkp.revoc.data.dto.RevocationRegistryDefinitionInfo;

import java.util.ArrayList;
import java.util.List;


public interface IWKeyManagerInterface {

	public static interface OnUnLockListener {
		void onSuccess();
		void onFail(int errCode);
		void onCancel();
	}

	public interface SuccessCallBack {
		public void success();
		public void failure(IWErrorCode errorCode);
	}

	public IWHeadElement getHeader();

	public boolean lock();

	public boolean isPasswordSet();

	public void unLock(byte [] pwdOrAlias, OnUnLockListener listener) throws IWException;

	public void unLock(char [] pwdOrAlias, OnUnLockListener listener) throws IWException;

	public void changePassword(byte[] oldPassword, byte[] newPassword, boolean autoLock, SuccessCallBack callBack);

	public void changePassword(char[] oldPassword, char[] newPassword, boolean autoLock, SuccessCallBack callBack) throws IWException;

	public boolean isUnLock();

	public void addKey(IWKey key) throws IWException;

	public void generateRandomKey(String keyId, int algType) throws IWException;

	public void removeKey(String keyId) throws IWException;

	public void removeAllKeys() throws IWException;

	public byte[] getSign(String keyId, byte[] source) throws IWException;

	public void verifySign(String keyId, byte[] source, byte[] sign) throws IWException;

	public void verifySignWithPublicKey(String publicKey, byte[] source, byte[] sign) throws IWException;

	public boolean verifyProfile(String didsJSON, AbstractProfile profile) throws IWException;

	public String getPublicKey(String keyId) throws IWException;

	public boolean isExistKey(String keyId) throws IWException;

	public String getAlgoType(String keyId) throws IWException;

	public byte[] getAllKeyElementsEncrypted() throws IWException;

	public List<String> getKeyIdList() throws IWException;

	public String readKeyFile() throws IWException;

	public String readKeyFileHeader() throws IWException;

	public String makeRequestVerifiableClaim(String did, Privacy privacy);

	public String makeRequestVerifiableClaimCopy(String verifiableClaimJSON, Privacy privacy, String spName, String expires) throws IWException;

	public String makeProof(String verifiableClaimJSON, String signKeyId, byte[] nonce) throws IWException;

	public String makeDelegateProof(String verifiableClaimCopyJSON, String did, String signKeyId, byte[] nonce) throws IWException;

	public boolean addClaim(String claimJSON) throws IWException;

	public List<VerifiableClaim> getClaims() throws IWException;

	public List<VerifiableClaim> getClaims(String filter) throws IWException;

	public List<VerifiableClaim> getClaimsInSPProfile(String serviceProviderProfile) throws IWException;

	public void removeClaim(int index) throws IWException;

	public void removeClaim(VerifiableClaim claim) throws IWException;

	public void removeClaims(String filter) throws IWException;

	public void removeAllClaims() throws IWException;

	public byte[] getECIESEncryptBytes(String keyId, byte[] nonce, String publicKey, byte[] source, AESType aesType) throws IWException;

	public byte[] getECIESDecryptBytes(String keyId, byte[] nonce, String publicKey, byte[] source, AESType aesType) throws IWException;


	public byte[] getECIESEncryptBytes(byte[] nonce, String publicKey, String privateKey, byte[] source, AESType aesType) throws IWException;

	public byte[] getECIESDecryptBytes(byte[] nonce, String publicKey, String privateKey, byte[] source, AESType aesType) throws IWException;

	public byte[] getECIESEncryptBytes(byte[] nonce, String publicKey, String privateKey, byte[] source, AESType aesType, int curveParam) throws IWException;

	public byte[] getECIESDecryptBytes(byte[] nonce, String publicKey, String privateKey, byte[] source, AESType aesType, int curveParam) throws IWException;

	public abstract void deleteWalletFile();

	public abstract void resetWalletFile() throws IWException;

	public abstract String makeRequestVerifiableCredential(String did, List<Unprotected> privacyList, String keyId, byte[] nonce) throws IWException;

	public abstract String makeRequestVerifiableCredential(String did, List<Unprotected> privacyList, String keyId, byte[] nonce, Extension extension) throws IWException;

	public abstract boolean addCredential(String credentialJSON) throws IWException;

	public abstract List<VerifiableCredential> getCredentials() throws IWException;

	public abstract List<VerifiableCredential> getCredentials(String filter) throws IWException;

	public abstract void removeCredential(VerifiableCredential credential) throws IWException;
	public abstract void removeCredentials(String filter) throws IWException;


	public abstract void removeAllCredentials() throws IWException;

	public abstract String makeRequestVerifiablePresentation(String expirationDate, ArrayList<VerifiableCredential> credentials, String did, String keyId,
															 byte[] nonce) throws IWException;

	public abstract String makeRequestVerifiablePresentation(String expirationDate, ArrayList<VerifiableCredential> credentials, String did, String keyId,
															 byte[] nonce, String data) throws IWException;


	public String makeRequestVerifiablePresentation(String expirationDate, ArrayList<VerifiableCredential> credentials, String did, String keyId,
													byte[] nonce, List<String> data) throws IWException;

	public abstract void setSlot(int slot);


	/////////////////////////////////////////////////////////////////////////////////////////
	// ZKP Extra Data
	/////////////////////////////////////////////////////////////////////////////////////////
	boolean addZkpCredential(String credentialId, Credential credential) throws IWException, ZkpException;

	ArrayList<CredentialInfo> getZkpCredentials() throws IWException, ZkpException;

	void removeCredential(String credentialId, Credential credential) throws IWException, ZkpException;

	boolean addCredDefInfos(String credDefInfos) throws IWException, ZkpException;

	void removeCredDefInfos() throws IWException, ZkpException;

	boolean isExistCredDefInfo(String credDefId) throws IWException, ZkpException;

	CredentialDefinitionInfo getCredDefInfo(String credDefId) throws IWException, ZkpException;

	ArrayList<CredentialDefinitionInfo> getCredDefInfos() throws IWException, ZkpException;

	boolean addRevRegInfos(String revRegInfoJson) throws IWException, ZkpException;

	boolean isExistRevRegInfo(String revRegId) throws IWException, ZkpException;

	ArrayList<RevocationRegistryDefinitionInfo> getRevRegInfos() throws IWException, ZkpException;

	RevocationRegistryDefinitionInfo getRevRegInfo(String revRegDefId) throws IWException, ZkpException;

	RevocationRegistryDefinitionInfo getRevRegInfoByCredDefId(String credDefId) throws IWException, ZkpException;

	RevocationRegistryDefinitionInfo getRevRegInfoById(String revRegDefId) throws IWException, ZkpException;

	void removeRevRegInfos() throws IWException, ZkpException;

	void removeRevRegInfo(String revRegDefId) throws IWException, ZkpException;

	boolean addMasterSecrets(String masterSecret) throws IWException, ZkpException;

	void removeMasterSecrets() throws IWException, ZkpException;

	boolean isExistMasterSecret(String masterSecretId) throws IWException, ZkpException;

	ArrayList<MasterSecret> getMasterSecrets() throws IWException, ZkpException;

	MasterSecret getMasterSecret(String masterSecretId) throws IWException, ZkpException;

	void removeZkpCredential(CredentialInfo credentialInfo) throws IWException, ZkpException;

	void removeAllZkpCredential() throws IWException;

	public byte[] rsaDecrypt(String keyId, byte[] encryptedData , AESType aesType) throws IWException;
	public byte[] rsaEncrypt(String keyId, byte[] plainData, AESType aesType) throws IWException;
	public byte[] rsaEncrypt(PublicKey pubKey, byte[] bytes, AESType aes256) throws IWException;

	boolean addFabricUserPrivateKey(String privateKey) throws IWException;

	String getFabricUserPrivateKey() throws IWException;

	void removeFabricUserPrivateKey() throws IWException;

	boolean isExistFabricUserPrivateKey() throws IWException;

}

