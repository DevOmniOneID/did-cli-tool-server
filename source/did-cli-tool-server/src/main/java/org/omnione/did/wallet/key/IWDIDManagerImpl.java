package org.omnione.did.wallet.key;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongycastle.util.encoders.Hex;

import org.omnione.did.wallet.crypto.GDPCryptoHelperClient;
import org.omnione.did.wallet.data.did.Authentication;
import org.omnione.did.wallet.data.did.Vault;
import org.omnione.did.wallet.data.did.DIDAssertion;
import org.omnione.did.wallet.data.did.DIDAssertionType;
import org.omnione.did.wallet.data.did.Payload;
import org.omnione.did.wallet.data.did.DIDVaultAssertion;
import org.omnione.did.wallet.data.did.DIDDefaultAssertion;
import org.omnione.did.wallet.data.did.DIDPayloadAssertion;
import org.omnione.did.wallet.data.did.DIDToken;
import org.omnione.did.wallet.data.did.DIDTokenAssertion;
import org.omnione.did.wallet.data.did.DIDs;
import org.omnione.did.wallet.data.did.Proof;
import org.omnione.did.wallet.data.did.PublicKey;
import org.omnione.did.wallet.data.did.StorageList;
import org.omnione.did.wallet.data.iw.VerifiableClaim;
import org.omnione.did.wallet.eoscommander.crypto.digest.Sha256;
import org.omnione.did.wallet.eoscommander.crypto.ec.EcDsa;
import org.omnione.did.wallet.eoscommander.crypto.ec.EcSignature;
import org.omnione.did.wallet.eoscommander.crypto.ec.EosPublicKey;
import org.omnione.did.wallet.eoscommander.crypto.util.Base58;
import org.omnione.did.wallet.exception.IWErrorCode;
import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.key.data.IWKey;
import org.omnione.did.wallet.key.data.IWKeyElement;
import org.omnione.did.wallet.key.data.IWKeyStoreData;
import org.omnione.did.wallet.key.data.IWKey.ALGORITHM_TYPE;
import org.omnione.did.wallet.key.store.IWDIDFile;
import org.omnione.did.wallet.util.GDPBase58;
import org.omnione.did.wallet.util.GDPBase64;
import org.omnione.did.wallet.util.GDPLogger;
import org.omnione.did.wallet.util.http.HttpClient;
import org.omnione.did.wallet.util.http.HttpException;

public abstract class IWDIDManagerImpl extends IWManager implements IWDIDManagerInterface {
	private IWDIDFile iwD;

	private final static String ALGORITHM_VERIFICATION		= "VerificationKey2018";

	/**
	 * IWDIDManager Constructor
	 * @param pathWithName
	 */
	public IWDIDManagerImpl(String pathWithName) throws IWException {
		iwD = new IWDIDFile(pathWithName);

	}

	/**
	 * DID Document 파일 존재 여부
	 * @return
	 */
	@Override
	public boolean isExistDID() {
		return iwD.isExist();
	}

	/**
	 * DID 생성
	 * @return
	 */
	@Override
	public String genDID() throws IWException {

		/*
		 * DID Naming
		 *
		 * "did:iwt:base58(random)"
		 */
		GDPCryptoHelperClient cryptoHelper = new GDPCryptoHelperClient();
		return "did:omn:" + GDPBase58.encode(cryptoHelper.generateNonce());
	}

	/**
	 * DID 생성
	 * @param prefix
	 * @return
	 */
	@Override
	public String genDID(String prefix) throws IWException {

		/*
		 * DID Naming
		 *
		 * "did:iwt:prefix:base58(random)"
		 */



		if (!prefix.matches("^[0-9a-zA-Z]+$"))
			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_INVALID_PREFIX);


		GDPCryptoHelperClient cryptoHelper = new GDPCryptoHelperClient();
		String address = GDPBase58.encode(cryptoHelper.generateNonce());

		if (prefix != null && prefix.length() > 0) {
			return "did:omn:" + prefix + ":" + address;
		}
		else {
			return "did:omn:" + address;
		}
	}

	@Override
	public String genDidWithMethodName(String methodName) throws IWException {

		/*
		 * DID Naming
		 *
		 * "did:methodName:base58(random)"
		 */


		if (!methodName.matches("^[0-9a-zA-Z]+$"))
			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_INVALID_METHODNAME);

		if(methodName == null || methodName.length() < 1 || methodName.length() > 20) {
			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_INVALID_METHODNAME);
		}

		GDPCryptoHelperClient cryptoHelper = new GDPCryptoHelperClient();

		return "did:" + methodName + ":" + GDPBase58.encode(cryptoHelper.generateNonce());

	}

	/**
	 * DID Key로 서명
	 */
	@Override
	public byte[] getSign(String signKeyId, byte[] source, IWKeyManagerInterface keyManager) throws IWException {
		if(isExistKeyIdInDIDs(signKeyId) == false) {
			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_KEYID_NOT_EXIST);
		}

		return this.getSignature(source, signKeyId, keyManager);
	}

	/**
	 * DID Key로 서명 검증
	 */
	@Override
	public void verifySign(String signKeyId, byte[] source, byte[] sign, IWKeyManagerInterface keyManager) throws IWException {
		if(isExistKeyIdInDIDs(signKeyId) == false) {
			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_KEYID_NOT_EXIST);
		}

		verifySignature(signKeyId, source, sign, keyManager);
	}



	/**
	 * 기존 DID Document 읽기
	 * @return
	 */
	@Override
	public String makeRequestDIDsRead() throws IWException {
		if(!isExistDID()) {
			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_FILE_NOT_EXIST);
		}

		// 기존 DID Doucment 읽기
		DIDs data = iwD.getData();
		if (data == null)
			return null;

		return data.toJson();
	}

	/**
	 * 최초 DID Document 생성
	 * @param did
	 * @param createKeyIds
	 * @return
	 * @throws IWException
	 */
	@Override
	public String makeRequestDIDsCreate(String did, List<String> createKeyIds, IWKeyManagerInterface keyManager) throws IWException {
		if(isExistDID()) {
			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_FILE_ALREADY_EXIST);
		}

		/*
		 * DID Document Structure
		 *
		 * String				id
		 * Array(String-Base58)	publicKey
		 * Array(String)		authentication
		 * Dictionary			proof
		 */

		if(createKeyIds == null || createKeyIds.size() == 0) {
			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_ADD_KEY_FAIL);
		}

		ArrayList<PublicKey> pubKeys = new ArrayList<PublicKey>();
		ArrayList<Authentication> auths = new ArrayList<Authentication>();
		for(int i=0; i<createKeyIds.size(); i++) {

			// keyId
			String keyId = createKeyIds.get(i);


			//modified by joshua
			// new keyPair (default : secp256k1)
			if(!keyManager.isExistKey(keyId)) {
				//modified by joshua
				throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_KEYID_NOT_EXIST);
			}

			// dlchoi
			String algType = keyManager.getAlgoType(keyId);

			// keyDid
			String keyDid = getKeyDId(did, keyId);



			// publicKey
			PublicKey pubKey = new PublicKey();
			pubKey.setId(keyDid);
			pubKey.setType(algType + ALGORITHM_VERIFICATION); // secp256k1
			//dlchoi
			pubKey.setPublicKeyBase58(keyManager.getPublicKey(keyId));
			pubKeys.add(pubKey);

			// authentication
			Authentication auth = new Authentication();
			auth.setId(keyDid);
			auths.add(auth);
		}

		// dids
		DIDs dids = new DIDs();
		dids.setId(did);
		dids.setPublicKey(pubKeys);
		dids.setAuthentication(auths);
		dids.setUpdated(VerifiableClaim.dateToString(new Date()));
		return dids.toJson();
	}

	/**
	 * 기존 DID Document 정보 일부를 추가
	 * @param addKeyId
	 * @return
	 * @throws IWException
	 */
	@Override
	public String makeRequestDIDsAdd(String addKeyId, IWKeyManagerInterface keyManager) throws IWException {
		if(!isExistDID()) {
			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_FILE_NOT_EXIST);
		}
		if(isExistKeyIdInDIDs(addKeyId) == true) {
			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_KEYID_ALREADY_EXIST);
		}

		/*
		 * DID Document Structure
		 *
		 * String				id
		 * Array(String-Base58)	publicKey
		 * Array(String)		authentication
		 * Dictionary			proof
		 */

		DIDs dids = iwD.getData();

		List<PublicKey> pubKeys = dids.getPublicKey();
		List<Authentication> auths = dids.getAuthentication();

		//modified by joshua
		// new keyPair (default : secp256k1)


		if(!keyManager.isExistKey(addKeyId)) {
			//modified by joshua
			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_KEYID_NOT_EXIST);
		}

		String algType = keyManager.getAlgoType(addKeyId);


		// keyDid
		String addKeyDid = getKeyDId(dids.getId(), addKeyId);

		// publicKey
		PublicKey pubKey = new PublicKey();
		pubKey.setId(addKeyDid);
		pubKey.setType(algType + ALGORITHM_VERIFICATION); // secp256k1
		pubKey.setPublicKeyBase58(keyManager.getPublicKey(addKeyId));
		pubKeys.add(pubKey);

		// authentication
		Authentication auth = new Authentication();

		auth.setId(addKeyDid);
		auths.add(auth);

		// dids
		dids.setPublicKey(pubKeys);
		dids.setAuthentication(auths);
		dids.setUpdated(VerifiableClaim.dateToString(new Date()));
		return dids.toJson();
	}

	/**
	 * 기존 DID Document 정보 일부를 삭제
	 * @param deleteKeyId
	 * @return
	 */
	@Override
	public String makeRequestDIDsDelete(String deleteKeyId) throws IWException {
		if(!isExistDID()) {
			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_FILE_NOT_EXIST);
		}

		/*
		 * DID Document Structure
		 *
		 * String				id
		 * Array(String-Base58)	publicKey
		 * Array(String)		authentication
		 * Dictionary			proof
		 */

		DIDs dids = iwD.getData();

		List<PublicKey> pubKeys = dids.getPublicKey();
		List<Authentication> auths = dids.getAuthentication();

		// keyDid
		String deleteKeyDid = getKeyDId(dids.getId(), deleteKeyId);

		// publicKey
		for(PublicKey pubKey : pubKeys) {
			if(deleteKeyDid.equals(pubKey.getId())) {
				pubKeys.remove(pubKey);
				break;
			}
		}

		// authentication
		for(Authentication auth : auths) {
			if(deleteKeyDid.equals(auth.getId())) {
				auths.remove(auth);
				break;
			}
		}

		// dids
		dids.setPublicKey(pubKeys);
		dids.setAuthentication(auths);
		dids.setUpdated(VerifiableClaim.dateToString(new Date()));
		return dids.toJson();
	}

	/**
	 * 금결원
	 * 입력받은 "didsJson" 에 입력받은 "keyID" 를 추가. DID 문서에 중복된 keyID 가 존재할 경우, Wallet 에 있는
	 * keyID 를 추가하고, Proof 가 빠진 JSON 형태의 DID 문서를 반환한다.
	 *
	 * @param didsJSON   수정할 DID 문서
	 * @param keyId      추가할 KeyID
	 * @param keyManager IWKeyManager
	 * @return Proof 가 제외된 DID 문서
	 * @throws IWException
	 */
	@Override
	public String makeRequestDIDsModify(String didsJSON, String keyId, IWKeyManagerInterface keyManager)
			throws IWException {
		// dids 유효성 체크
		DIDs dids = new DIDs(didsJSON);
		String did = dids.getId();
		List<PublicKey> pubKeyList = dids.getPublicKey();
		List<Authentication> authenticationList = dids.getAuthentication();

		// dids 유효성 체크
		if (did == null || did.length() <= 0) {
			if (pubKeyList == null || pubKeyList.size() <= 0) {
				if (authenticationList == null || authenticationList.size() <= 0) {
					throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_INVALID_DID_DOCUMENT);
				}
			}
		}

		// KeyID 유효성 체크
		if (!keyManager.isExistKey(keyId)) {
			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_KEYID_NOT_EXIST);
		}

		// keyDid
		String keyDid = getKeyDId(dids.getId(), keyId);

		// publicKey 키 삭제
		for (PublicKey pubKey : pubKeyList) {
			if (keyDid.equals(pubKey.getId())) {
				pubKeyList.remove(pubKey);
				break;
			}
		}

		// authentication 키 삭제
		for (Authentication auth : authenticationList) {
			if (keyDid.equals(auth.getId())) {
				authenticationList.remove(auth);
				break;
			}
		}

		String algType = keyManager.getAlgoType(keyId);

		// publicKey
		PublicKey pubKey = new PublicKey();
		pubKey.setId(keyDid);
		pubKey.setType(algType + ALGORITHM_VERIFICATION); // secp256k1
		pubKey.setPublicKeyBase58(keyManager.getPublicKey(keyId));
		pubKeyList.add(pubKey);

		// authentication
		Authentication auth = new Authentication();

		auth.setId(keyDid);
		authenticationList.add(auth);

		dids.setPublicKey(pubKeyList);
		dids.setAuthentication(authenticationList);
		dids.setUpdated(VerifiableClaim.dateToString(new Date())); // 생성 날짜 업데이트
		dids.setProof(null); // proof 삭제
		return dids.toJson();
	}


	/**
	 * 금결원
	 *
	 * 입력받은 DID Document JSON에 keyID가 들어있는지 확인
	 * @param didsJSON
	 * @param keyId
	 * @return true/false
	 *
	 */
	@Override
	public boolean isExistKeyId(String didsJSON, String keyId) throws IWException{
		DIDs dids = new DIDs();
		List<String> keyIdList = new ArrayList<String>();


		if (didsJSON == null || didsJSON.equals("")){
			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_INVALID_DID_DOCUMENT);

		}

		dids.fromJson(didsJSON);

		List<PublicKey> publicKeyList = dids.getPublicKey();
		for (int i = 0; i < publicKeyList.size(); i++) {
			PublicKey publicKey = publicKeyList.get(i);
			String publicKeyId = publicKey.getId();
			String keyIdInPublicKey = getKeyIdFromKeyDid(publicKeyId);
			keyIdList.add(keyIdInPublicKey);
		}

		for (int i=0; i< keyIdList.size(); i++) {
			if(keyId.equals(keyIdList.get(i))){
				return true;
			}
		}

		return false;

	}

	/**
	 * DID Document Proof 생성
	 * @param didsJSON
	 * @param signKeyId
	 * @param nonce
	 * @param keyManager
	 * @return
	 * @throws IWException
	 */
	@Override
	public String makeProof(String didsJSON, String signKeyId, byte[] nonce, IWKeyManagerInterface keyManager) throws IWException {
		/*
		 * DID Document Structure (Request)
		 *
		 * String			id
		 * PublicKey[]		publicKey
		 * Authentication[]	authentication
		 *
		 *
		 * Proof Structure
		 *
		 * String			type
		 * String			created (서명 원문에 포함)
		 * String 			creator (서명 원문에 포함)
		 * String			nonce (서명 원문에 포함)
		 * String-Base58	signatureValue
		 */
		DIDs dids_proof = new DIDs();
		dids_proof.fromJson(didsJSON);

		if(!keyManager.isExistKey(signKeyId)) {

			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_KEYID_NOT_EXIST);
		}

		// proof.creator, proof.created, proof.nonce
		Proof proof = getProof(getKeyDId(dids_proof.getId(), signKeyId), nonce);
		dids_proof.setProof(proof);

		// proof.sign
		byte[] signature = getSignature(dids_proof.toJson().getBytes(), signKeyId, keyManager);
		proof.setSignatureValue(Base58.encode(signature));


		//dlchoi
		String algType = keyManager.getAlgoType(signKeyId);

		proof.setType(algType + ALGORITHM_VERIFICATION);

		dids_proof.setProof(proof);
		return dids_proof.toJson();
	}



	/**
	 * DID Auth 생성
	 * @param signKeyId
	 * @param source
	 * @param nonce
	 * @param value
	 * @param keyManager
	 * @return
	 * @throws IWException
	 */

	// 20200720 dlchoi 수정 - type에 따라 didauth 생성하도록 수정
    /*
     * value
       type == default -> null
       type == vault -> Vault 객체 json String
     */

    @Override
    public String makeDIDAssertion(DIDAssertionType type, String signKeyId, byte[] nonce, String value, IWKeyManagerInterface keyManager) throws IWException {
    	if(!isExistDID()) {
    		throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_FILE_NOT_EXIST);
    	}
    	
    	/*
    	 * DID Assertion Structure
    	 * 
    	 * type
    	 * $$assertion //= assertionType
    	 */
    	
    	DIDAssertion assertion = null;
    	if(type.equals(DIDAssertionType.DEFAULT)) {
    		assertion = makeDIDAssertionDefault(signKeyId, nonce, keyManager);		
    	} else if(type.equals(DIDAssertionType.VAULT)) {    		
    		assertion = makeDIDAssertionVault(signKeyId, nonce, value, keyManager);	
    	} else if(type.equals(DIDAssertionType.PAYLOAD)) {
			assertion = makeDIDAssertionPayload(signKeyId, nonce, value, keyManager);
		}
		else {
			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_DID_ASSERITON_TYPE_NOT_CORRECT);
		}
		return assertion.toJson();
	}



	private DIDDefaultAssertion makeDIDAssertionDefault(String signKeyId, byte[] nonce, IWKeyManagerInterface keyManager) throws IWException {
		/*
		 * Default DID Assertion Structure
		 *
		 * String			type (서명 원문에 포함)
		 * String			id (서명 원문에 포함)
		 * Proof			proof
		 *
		 *
		 * Proof Object Structure
		 *
		 * String			type
		 * String			created (서명 원문에 포함)
		 * String 			creator (서명 원문에 포함)
		 * String			source (서명 원문에 포함)
		 * String-Base58	signatureValue
		 */
		DIDs dids = iwD.getData();
		DIDDefaultAssertion didauth = new DIDDefaultAssertion();

		// ---서명 원문
		//
		// dids.type
		didauth.setType(DIDAssertionType.DEFAULT.getType());
		// dids.id
		didauth.setId(dids.getId());
		// dids.proof.created
		// dids.proof.creator
		// dids.proof.source
		Proof proof = getProof(getKeyDId(dids.getId(), signKeyId), nonce);
		didauth.setProof(proof);

		// ---서명 값 추가
		//
		// dids.proof.signatureValue
		GDPLogger.print("sign-src", didauth.toJson());
		byte[] signature = getSign(signKeyId, didauth.toJson().getBytes(), keyManager);
		proof.setSignatureValue(Base58.encode(signature));
		// dids.proof.type
		// dlchoi
		String algType = keyManager.getAlgoType(signKeyId);
		proof.setType(algType + ALGORITHM_VERIFICATION);

		didauth.setProof(proof);
		return didauth;
	}



	private DIDVaultAssertion makeDIDAssertionVault(String signKeyId, byte[] nonce, String value, IWKeyManagerInterface keyManager) throws IWException {
		DIDs dids = iwD.getData();
		DIDVaultAssertion didauth = new DIDVaultAssertion();

		// ---서명 원문
		//
		// dids.type
		didauth.setType(DIDAssertionType.VAULT.getType());
		// dids.id
		didauth.setId(dids.getId());
		// dids.proof.created
		// dids.proof.creator
		// dids.proof.source
		Proof proof = getProof(getKeyDId(dids.getId(), signKeyId), nonce);
		didauth.setProof(proof);


		// vault 추가
		Vault vault = new Vault();
		vault.fromJson(value);

    	didauth.setVault(vault);

    	// ---서명 값 추가
    	//
    	// dids.proof.signatureValue
    	GDPLogger.print("sign-src", didauth.toJson());
    	byte[] signature = getSign(signKeyId, didauth.toJson().getBytes(), keyManager);
    	proof.setSignatureValue(Base58.encode(signature));
    	// dids.proof.type
    	// dlchoi
    	String algType = keyManager.getAlgoType(signKeyId);
    	proof.setType(algType + ALGORITHM_VERIFICATION);

    	didauth.setProof(proof);

        return didauth;
    }

    public DIDPayloadAssertion makeDIDAssertionPayload(String signKeyId, byte[] nonce, String value, IWKeyManagerInterface keyManager) throws IWException {

		/*
		 * Payload DID Assertion Structure
		 *
		 * String			type (서명 원문에 포함)
		 * String			id (서명 원문에 포함)
		 * String			payload (서명 원문에 포함)
    	 * Proof			proof
    	 * 
    	 * Proof Object Structure
    	 * 
    	 * String			type
    	 * String			created (서명 원문에 포함)
    	 * String 			creator (서명 원문에 포함)
    	 * String			source (서명 원문에 포함)
    	 * String-Base58	signatureValue 
    	 */

    	DIDs dids = iwD.getData();
    	DIDPayloadAssertion didauth = new DIDPayloadAssertion();
    	didauth.setType(DIDAssertionType.PAYLOAD.getType());    	
    	didauth.setId(dids.getId());    	    	
    	Proof proof = getProof(getKeyDId(dids.getId(), signKeyId), nonce);
    	didauth.setProof(proof);
    	
    	// payload 추가
		Payload payload = new Payload();
		payload.fromJson(value);
    	didauth.setPayload(payload);

    	GDPLogger.print("sign-src", didauth.toJson());
    	byte[] signature = getSign(signKeyId, didauth.toJson().getBytes(), keyManager);
    	proof.setSignatureValue(Base58.encode(signature));
    	// dids.proof.type
    	// dlchoi
    	String algType = keyManager.getAlgoType(signKeyId);
    	proof.setType(algType + ALGORITHM_VERIFICATION);
    	
    	didauth.setProof(proof);

		return didauth;
	}


	@Override
	public String makeDIDTokenAssertion(DIDAssertionType type, String signKeyId, DIDToken didToken, byte[] nonce, IWKeyManagerInterface keyManager) throws IWException {
		if(!isExistDID()) {
			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_FILE_NOT_EXIST);
		}

		/*
		 * DID Assertion Structure
		 *
		 * type
		 * $$assertion //= assertionType
		 */

		if(!type.equals(DIDAssertionType.TOKEN_TRANS)) {
			throw new IWException(IWErrorCode.ERR_CODE_DIDMANAGER_DID_ASSERITON_TYPE_NOT_CORRECT);

		}

		DIDTokenAssertion assertion = makeDIDTokenAssertionInner(signKeyId, didToken, nonce, keyManager);
		return assertion.toJson();
	}
	private DIDTokenAssertion makeDIDTokenAssertionInner(String signKeyId, DIDToken didToken, byte[] nonce, IWKeyManagerInterface keyManager) throws IWException {
		/*
		 * Default DID Assertion Structure
		 *
		 * String			type (서명 원문에 포함)
		 * String			id (서명 원문에 포함)
		 * Proof			proof
		 *
		 *
		 * Proof Object Structure
		 *
		 * String			type
		 * String			created (서명 원문에 포함)
		 * String 			creator (서명 원문에 포함)
		 * String			source (서명 원문에 포함)
		 * String-Base58	signatureValue
		 */
		DIDs dids = iwD.getData();
		DIDTokenAssertion didTokenAssertion = new DIDTokenAssertion();

		// ---서명 원문
		//
		// dids.type
		didTokenAssertion.setType(DIDAssertionType.TOKEN_TRANS.getType());
		didTokenAssertion.setTransToken(didToken);
		// dids.id
		didTokenAssertion.setId(dids.getId());
		// dids.proof.created
		// dids.proof.creator
		// dids.proof.source
		Proof proof = getProof(getKeyDId(dids.getId(), signKeyId), nonce);

		// dlchoi 주석
//    	didTokenAssertion.setProof(proof);

		// ---서명 값 추가
		//
		// dids.proof.signatureValue
		GDPLogger.print("sign-src", didTokenAssertion.toJson());
		byte[] signature = getSign(signKeyId, didTokenAssertion.toJson().getBytes(), keyManager);
		proof.setSignatureValue(Base58.encode(signature));
		// dids.proof.type
		// dlchoi
		String algType = keyManager.getAlgoType(signKeyId);
		proof.setType(algType + ALGORITHM_VERIFICATION);

		didTokenAssertion.setProof(proof);
		return didTokenAssertion;
	}



	/**
	 * BlockChain에 적용할 DID Document 전송
	 * @param serverUrl
	 * @param dids
	 * @param callback
	 */
	@Override
	public void sendToServer(String serverUrl, String dids, Callback callback) {
		HttpClient client = new HttpClient();
		try {
			String result = client.send(serverUrl, dids);
			if ((result != null) && (result.length() > 0)) {
				callback.processServerResponse(result);
			}
			else {
				throw new Exception("result is null");
			}
		} catch (HttpException e) {
//			e.printStackTrace();
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	/**
	 * BlockChain에 적용된 DID Document 저장
	 * @param didsJSON
	 * @return
	 */
	@Override
	public boolean saveToFile(String didsJSON) throws IWException {
		DIDs dids = new DIDs();
		dids.fromJson(didsJSON);

		// save dids
		iwD.write(dids);


		return true;
	}


	@Override
	public DIDs verifyWithLocal(String didAuth, DIDs didDoc) throws IWException {
		DIDDefaultAssertion didDefaultAssertion = DIDAssertion.parsingDidAuth(didAuth);

		String did = didDefaultAssertion.getId();



		Proof proof = didDefaultAssertion.getProof();
		Proof proofTemp = new Proof();
		proofTemp.setCreated(proof.getCreated());
		proofTemp.setCreator(proof.getCreator());
		proofTemp.setNonce(proof.getNonce());
		didDefaultAssertion.setProof(proofTemp);


		String sigSoure = didDefaultAssertion.toJson();

		Sha256 sigSourceHash = Sha256.from(sigSoure.getBytes());

		EcSignature ecSignature = new EcSignature(Base58.decode(proof.getSignatureValue()));
		EosPublicKey recoverEosPublicKey = EcDsa.recoverPubKey(sigSourceHash.getBytes(), ecSignature);
		EosPublicKey userDidEosPublicKey = null;

		List<PublicKey> userDIDPublicKeys = didDoc.getPublicKey();
		for (PublicKey publicKey : userDIDPublicKeys) {
			if (proof.getCreator().equals(publicKey.getId())) {
				userDidEosPublicKey = new EosPublicKey(Base58.decode(publicKey.getPublicKeyBase58()));
			}
		}

		if (!recoverEosPublicKey.equals(userDidEosPublicKey)) {
			throw new IWException(IWErrorCode.ERR_USER_SIGNATURE_VERIFICATION_FAILED);
		}

		return didDoc;


	}


	////////////////////////////////////////////////////////////////////////////////
	// 내부에서 사용
	////////////////////////////////////////////////////////////////////////////////


	private String getDidFromKeyDId(String keyDid) {
		int endIndex = keyDid.indexOf('#');
		return keyDid.substring(0, endIndex);
	}

	private String getKeyIdFromKeyDid(String keyDid) {

		if (keyDid == null || !keyDid.contains("#")) {
			return null;
		}
		int startIndex = keyDid.indexOf('#');
		return keyDid.substring(startIndex+1, keyDid.length());
	}


	protected String checkCreateKeyIds(String did, List<String> createKeyIds) {
		for(int i=0; i<createKeyIds.size(); i++) {
			String nextDid = getDidFromKeyDId(createKeyIds.get(i));
			if(!did.equals(nextDid)) {
				GDPLogger.print("error", "생성하려는 KeyId에 다른 did가 존재함");
				return null;
			}
		}

		return did;
	}


	/**
	 * DID Document 파일 내에 등록된 keyId 인지 여부
	 * @return
	 */
	private boolean isExistKeyIdInDIDs(String keyId) {
		DIDs data = iwD.getData();
		if(data == null) {
			return false;
		}

		for(PublicKey pubKey : data.getPublicKey()) {
			String keyDid = pubKey.getId();
			String keyId0 = getKeyIdFromKeyDid(keyDid);

			if((keyId0 != null) && (keyId.equals(keyId0))) {
				return true;
			}
		}

		for(Authentication auth : data.getAuthentication()) {
			String keyDid = auth.getId();
			String keyId0 = getKeyIdFromKeyDid(keyDid);

			if((keyId0 != null) && (keyId.equals(keyId0))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * DID Document 파일 삭제
	 * @return
	 */
	@Override
	public void deleteDIDFile() {

		iwD.delete();

	}

	/*
	 * depends on private key type
	 *
	 * 1. SECP private key of spongy castle
	 * 2. SECP private key of eos
	 */

	protected abstract byte[] getSignature(byte[] didsJsonBytes, String signKeyId, IWKeyManagerInterface keyManager) throws IWException;



	protected abstract byte[] getPublicKeyBytes(IWKey iwKey);

	protected abstract void verifySignature(String signKeyId, byte[] source, byte[] sign, IWKeyManagerInterface keyManager) throws IWException;
}
