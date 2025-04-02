package org.omnione.did.wallet.sample;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

import org.spongycastle.util.encoders.Hex;

import org.omnione.did.wallet.crypto.GDPCryptoHelperClient;
import org.omnione.did.wallet.data.did.DIDAssertionType;
import org.omnione.did.wallet.data.did.DIDs;
import org.omnione.did.wallet.data.did.PublicKey;
import org.omnione.did.wallet.data.iw.Privacy;
import org.omnione.did.wallet.data.iw.Unprotected;
import org.omnione.did.wallet.data.iw.VerifiableClaim;
import org.omnione.did.wallet.eoscommander.crypto.digest.Sha256;
import org.omnione.did.wallet.eoscommander.crypto.digest.Sha512;
import org.omnione.did.wallet.eoscommander.crypto.ec.EosPrivateKey;
import org.omnione.did.wallet.eoscommander.crypto.ec.EosPublicKey;
import org.omnione.did.wallet.eoscommander.crypto.util.HexUtils;
import org.omnione.did.wallet.exception.IWErrorCode;
import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.key.IWAppResult;
import org.omnione.did.wallet.key.IWDIDManager;
import org.omnione.did.wallet.key.IWKeyManager;
import org.omnione.did.wallet.key.IWDIDManagerInterface.Callback;
import org.omnione.did.wallet.key.IWKeyManagerInterface.OnUnLockListener;
import org.omnione.did.wallet.key.data.AESType;
import org.omnione.did.wallet.key.data.IWKey;
import org.omnione.did.wallet.key.data.IWKey.ALGORITHM_TYPE;
import org.omnione.did.wallet.util.GDPBase64;
import org.omnione.did.wallet.util.GDPLogger;
import org.omnione.did.wallet.util.http.HttpClient;
import org.omnione.did.wallet.util.http.HttpException;

public class Sample {
	
	private static String didFileName = "./i.wallet.dids";
	private static String keyFileName = "./i.wallet";
	private static String password = "1234";
	
	private IWDIDManager didManager;
	private IWKeyManager keyManager;
	
	private String issueUrl = "http://192.168.0.85:8103/issue.server/iw/issuer/issueNotLogin?userId=tykim";
	private String spUrl = "http://192.168.0.85:8103/issue.server/iw/sp/verify"; 
	
	public static void main(String[] args) throws Exception {
		
		IWDIDManager didManager = new IWDIDManager(didFileName);
		
		didManager.saveToFile("{\"authentication\":[{\"id\":\"did:omn:dwilcmrnskvm#mykey\",\"publicKeyBase58\":\"EOS61bF5FbrirwST2YFqobNW68RWxYRLvnPW3ZzDmxvYRZEckokp5\"}],\"id\":\"did:omn:dwilcmrnskvm\",\"publicKey\":[{\"id\":\"did:omn:dwilcmrnskvm#mykey\",\"publicKeyBase58\":\"EOS61bF5FbrirwST2YFqobNW68RWxYRLvnPW3ZzDmxvYRZEckokp5\"},{\"id\":\"did:omn:dwilcmrnskvm#key1\",\"publicKeyBase58\":\"EOS6MRyAjQq8ud7hVNYcfnVPJqcVpscN5So8BhtHuGYqET5GDW5CV\"},{\"id\":\"did:omn:dwilcmrnskvm#key2\",\"publicKeyBase58\":\"EOS5LxopPcmFK6U867doLF76EGaZMKSmjkGGgVYTDTaaGAXvfQgDk\"}],\"updated\":\"2019-12-18T07:37:26.000\"}");
		
		
  
 
	}
	
	Sample(IWDIDManager didManager, IWKeyManager keyManager) {
		this.didManager = didManager;
		this.keyManager = keyManager;
	}
	
	private void test1_AfterUnLock() {
		try {			
			GDPLogger.print("");
			GDPLogger.print("테스트", "DID Document Create");
			didmanager_create_didDoc("key-1", "key-1", "key-2");
			didmanager_read_didDoc();
			
	        GDPLogger.print("");
	        GDPLogger.print("테스트", "DID Document Update");
	        didmanager_update_didDoc("key-1", "key-3");
	        didmanager_read_didDoc();
	    	
	        GDPLogger.print("");
	        GDPLogger.print("테스트", "DID Document Delete");
	        didmanager_delete_didDoc("key-1", "key-3");
	        didmanager_read_didDoc();
	    	
	        GDPLogger.print("");
	        GDPLogger.print("테스트", "DID Sign, Verify");
	        didmanager_sign_verify("key-1", "this is source for did auth signature.");
	    	
	        GDPLogger.print("");
	        GDPLogger.print("테스트", "DID Assertion");
	        didmanager_create_didAssertion("key-1", "this is source for did assertion signature.");
	    	
	        GDPLogger.print("");
	        GDPLogger.print("테스트", "Verifiable Claim 발급");
	        keymanager_send_VC();
	    	
	        GDPLogger.print("");
	        GDPLogger.print("테스트", "Verifiable Claim Copy 제출");
	        keymanager_send_VCCopy();
	    	
		} catch (IWException e) {
    		e.printStackTrace();
    	}
	}
	
	private void test2_AfterUnLock() {
		try {
			// Verifiable Claim 사본 제출 테스트
			GDPLogger.print("");
	        GDPLogger.print("테스트", "Verifiable Claim Copy 제출");
	        keymanager_send_VCCopy();
	        didmanager_read_didDoc();
		
		} catch (IWException e) {
    		e.printStackTrace();
    	}
	}
	
	
	/*
	 * DID Document 등록 테스트
	 */
	private void didmanager_create_didDoc(String signKeyId, String addKeyId1, String addKeyId2) throws IWException {  

		// did        
        String did = didManager.genDID();
        
		// 서명용 keyId & 등록할 keyIds
        List<String> createKeyIds = new ArrayList<String>();
        createKeyIds.add(addKeyId1);
        createKeyIds.add(addKeyId2);
        
		// create DIDs & create Keys
//		didManager.create(did, createKeyIds, signKeyId, keyManager);
		didManager.makeRequestDIDsCreate(did, createKeyIds, keyManager);
	}
		
	
	/*
	 * DID Document 수정 테스트 (key 추가)
	 */
	private void didmanager_update_didDoc(String signKeyId, String addKeyId) throws IWException{  
    	
		// add DIDs & add Keys			
//		didManager.update(addKeyId, signKeyId, keyManager);
		didManager.makeRequestDIDsAdd(addKeyId, keyManager);
	}
	
	
	/*
	 * DID Document 수정 테스트 (key 삭제)
	 */
	private void didmanager_delete_didDoc(String signKeyId, String deleteKeyId) throws IWException {
        		
		// delete DIDs & delete Keys
		didManager.makeRequestDIDsDelete(deleteKeyId);
	}
		
	private void didmanager_read_didDoc() throws IWException {
		// read DIDs
		GDPLogger.print(didFileName, didManager.makeRequestDIDsRead());
		
		// read Keys
		GDPLogger.print(keyFileName, keyManager.getKeyIdList().toString());
	}
	
	
	/*
	 * DID Sign 서명 테스트 
	 */
	private void didmanager_sign_verify(String signKeyId, String src) throws IWException {
		byte[] src_bytes = src.getBytes();
		byte[] sign_bytes = didManager.getSign(signKeyId, src_bytes, keyManager);
		GDPLogger.print("sign", Hex.toHexString(sign_bytes));
		
		// sign_bytes[2] = 'a'; fail test
		try {
			didManager.verifySign(signKeyId, src_bytes, sign_bytes, keyManager);
			GDPLogger.print("verify", "DID Sign 검증 성공");
		} catch (IWException e) {
			GDPLogger.print("verify", "DID Sign 검증 실패 (" + e.getErrorCode() + ")");
		}		
	}
	
	
	/*
	 * DID Auth 인증 테스트 
	 */
	private void didmanager_create_didAssertion(String signKeyId, String src) throws IWException {		
		byte[] src_bytes = src.getBytes();		
		String didAuth_bytes = didManager.makeDIDAssertion(DIDAssertionType.DEFAULT, signKeyId, src_bytes, null, keyManager);
		GDPLogger.print("didAssertion", didAuth_bytes);
	}

	
	/*
	 * Verifiable Claim 발급 테스트
	 */
	private void keymanager_send_VC() {
		try {
			// user data
			GDPCryptoHelperClient cryptoHelper = new GDPCryptoHelperClient();
			byte[] nonce = cryptoHelper.generateNonce();		
			Privacy privacy = getSamplePrivacy();
			
			// check if dids is existed
	        if (!didManager.isExistDID()) {
	            GDPLogger.print("error", "등록된 DID가 없음");
	            return;
	        }
	        
	        // did
	        DIDs dids = new DIDs();
	        dids.fromJson(didManager.makeRequestDIDsRead());
	        String did = dids.getId();
	        
			// sign keyid
			String signKeyId = keyManager.getKeyIdList().get(0);
			
			// verifiable claim
			String claim = keyManager.makeRequestVerifiableClaim(did, privacy);
			String claim_proof = keyManager.makeProof(claim, signKeyId, nonce);
			        		
			// request http
	    	sendToServer(issueUrl, claim_proof, new Callback() {
				@Override
				public void processServerResponse(String result) throws Exception {
					IWAppResult data = new IWAppResult();
					data.fromJson(result);
					String claim = null;
				    if(data.isResult() == true) {
				    	claim = new String(GDPBase64.decodeUrl(data.getClaimBase64()));		
				    	
				    	// save dids
				    	boolean isSave = keyManager.addClaim(claim);
				    	if(isSave) {
				    		GDPLogger.print("info", "Claim 저장 성공");
				    	} else {
				    		GDPLogger.print("error", "Claim 저장 실패");
				    	}
				    } else {
				    	throw new Exception("Claim 등록 실패");
				    }
				}    		
	    	});    
		} catch (IWException e) {
			e.printStackTrace();
		}
	}
	
	
	/*
	 * Verifiable Claim 사본 제출 테스트
	 */
	private void keymanager_send_VCCopy() {
		try {
			// user data
			GDPCryptoHelperClient cryptoHelper = new GDPCryptoHelperClient();
			byte[] nonce = cryptoHelper.generateNonce();		
			Privacy privacy = getSamplePrivacyCopy(getSamplePrivacy());
			
			// check if dids is existed
	        if (!didManager.isExistDID()) {
	            GDPLogger.print("error", "등록된 DID가 없음");
	            return;
	        }
	        
	        // did
	        DIDs dids = new DIDs();
	        dids.fromJson(didManager.makeRequestDIDsRead());
	        String did = dids.getId();
	        
			// sign keyid
			String signKeyId = keyManager.getKeyIdList().get(0);
			
			// verifiable claim
			String claim = keyManager.makeRequestVerifiableClaimCopy(did, privacy, null, null); // 임의로 에러만 없앰, 나중에 변경 필요
			String claim_proof = keyManager.makeProof(claim, signKeyId, nonce);
			        		
			// request http
	    	sendToServer(spUrl, claim_proof, new Callback() {
				@Override
				public void processServerResponse(String result) throws Exception {
					IWAppResult data = new IWAppResult();
					data.fromJson(result);
				    if(data.isResult() == true) {
				    	GDPLogger.print("info", "Claim Copy 검증 성공");
				    } else {
				    	throw new Exception("Claim Copy 검증 실패");
				    }
				}    		
	    	});      
		} catch (IWException e) {
			e.printStackTrace();
		}
	}	
	
	
	private static String getAutoNextKeyId(IWDIDManager didManager) {
		
		String keyId = null;
		try {
			
			DIDs dids = new DIDs();
	    	dids.fromJson(didManager.makeRequestDIDsRead());
			
	    	List<PublicKey> pubs = dids.getPublicKey();
	    	
	    	keyId = pubs.get(0).getId();
	    	int keyIdPos = keyId.indexOf("key-");
	    	
	    	int keyIdNum = Integer.MIN_VALUE;    	
	    	for(int i=0; i<pubs.size(); i++) {    	
	    		int tmpNum = Integer.valueOf(pubs.get(i).getId().substring(keyIdPos+4, keyId.length()));    		
	    		keyIdNum = keyIdNum < tmpNum ? tmpNum : keyIdNum;
	    	}
	    	keyId = "key-" + (keyIdNum+1);
	    	
		} catch (IWException e) {
			e.printStackTrace();
		}
    	return keyId;
    }
	
	private static String getLastKeyId(IWDIDManager didManager) {
		
		String keyId = null;
		try {
			
			DIDs dids = new DIDs();
	    	dids.fromJson(didManager.makeRequestDIDsRead());
			
	    	List<PublicKey> pubs = dids.getPublicKey();
	    	
	    	keyId = pubs.get(0).getId();
	    	int keyIdPos = keyId.indexOf("key-");
	    	
	    	int keyIdNum = Integer.MIN_VALUE;    	
	    	for(int i=0; i<pubs.size(); i++) {    	
	    		int tmpNum = Integer.valueOf(pubs.get(i).getId().substring(keyIdPos+4, keyId.length()));    		
	    		keyIdNum = keyIdNum < tmpNum ? tmpNum : keyIdNum;
	    	}
	    	keyId = "key-" + keyIdNum;
	    	
		} catch (IWException e) {
			e.printStackTrace();
		}
		
    	return keyId;
    }
		
    
    public static void printKeyIdList(IWKeyManager keyManager) throws IWException {
    	System.out.println("key list :");
    	for(String keyId : keyManager.getKeyIdList()) {
        	System.out.println(keyId);
        }
    }
    
    private Privacy getSamplePrivacy() {
    	Privacy privacy = new Privacy();
    	ArrayList<Unprotected> list = new ArrayList<Unprotected>();                	
    	Unprotected unprotected = new Unprotected();
    	unprotected.setType(Unprotected.Type.BIRTHDAY.value());
    	unprotected.setValue( "2015-4-20");
    	list.add(unprotected);
    	unprotected.setType(Unprotected.Type.EMAIL.value());
    	unprotected.setValue( "swlee@raonsecure.com");
    	list.add(unprotected);
    	unprotected.setType(Unprotected.Type.ADDRESS.value());
    	unprotected.setValue( "gangnam, yucksam, raonsecure 13F");
    	list.add(unprotected);
    	unprotected.setType(Unprotected.Type.PHONE.value());
    	unprotected.setValue( "010-1234-1234");
    	list.add(unprotected);
    	unprotected.setType(Unprotected.Type.AGE_OVER_19.value());
    	unprotected.setValue( "yes");
    	list.add(unprotected);
    	privacy.setUnprotected(list);
    	return privacy;
    }
    
    private Privacy getSamplePrivacyCopy(Privacy original) {    	
    	Privacy privacy = new Privacy();
    	ArrayList<Unprotected> list = new ArrayList<Unprotected>();                	
    	for(Unprotected item : original.getUnprotected()) {
    		if(item.getType().equals(Unprotected.Type.AGE_OVER_19.value())) {
    			Unprotected unprotected = new Unprotected();
    	    	unprotected.setType(item.getType());
    	    	unprotected.setValue(item.getValue());
    	    	list.add(unprotected);
    	    	break;    	    	    			
    		}
    			
    	}
    	privacy.setUnprotected(list);
    	return privacy;
    }
    
    private void sendToServer(String serverUrl, String dids, Callback callback) {
    	HttpClient client = new HttpClient();
    	try {
			String result = client.send(serverUrl, dids);
			callback.processServerResponse(result);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void addClaimTest() throws IWException {
    	String vc = "{\"assertion\":{\"id\":\"5\",\"level\":2,\"name\":\"학생증\",\"type\":\"COMPANY_ID\"},\"claim\":{\"expires\":\"2019-12-14T11:05:20\",\"id\":\"did:iwt:LrSD9nN314EUeM8z1DW4PotgXiP\",\"issued\":\"2018-12-14T11:05:20\",\"logo\":{\"data\":\"http://10.0.0.136:8080/issue.server/tykim_logo.png\",\"type\":\"URL\"},\"name\":\"박주현\",\"privacy\":{\"unprotected\":[{\"type\" : \"birthday\",\"value\" : \"1977-5-5\"},{\"type\" : \"address\",\"value\" : \"705-145\"}]}},\"issuer\":{\"desc\":\"first issuer\",\"id\":\"did:iwt:2EYL71d5z3dJrknPT9w5iBLEFqET\",\"logo\":{\"data\":\"http://10.0.0.136:8080/issue.server/issuer_logo.jpg\",\"type\":\"URL\"},\"name\":\"issuer\",\"url\":\"http://10.0.0.136:8080/issue.server\"}}";
    	keyManager.addClaim(vc);
    }
    
    private void addKeyTest() throws IWException {
    	EosPrivateKey eosPriKey = new EosPrivateKey();
		EosPublicKey eosPubKey = eosPriKey.getPublicKey();
    	IWKey iwKey = new IWKey("key-1", ALGORITHM_TYPE.ALGORITHM_SECP256k1.getValue(), eosPubKey, eosPriKey);
    	
    	keyManager.addKey(iwKey);
    }
    
    private void eciesTest() throws IWException {
    	String nonce = "749bd437b727155b045a55d493f50b7852168e25f58de0be6b4768c8a7342a2d";
    	
//    	String publicKey = keyManager.getPublicKey("key-1");
    	
//    	System.out.println("publickey");
//    	System.out.println(publicKey);
    	
    	String publicKey = "21P14ngMecQiktoWXTTPUD7JG5uAu48D9XkMYT9uXoqBU";
    	
    	String source = "enc test";
    	
    	byte[] sourceByte1 = null;
		try {
			sourceByte1 = source.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	byte[] enc = null;
		try {
			enc = keyManager.getECIESEncryptBytes("key-1", HexUtils.toBytes(nonce), publicKey, sourceByte1 , AESType.AES256);
		} catch (IWException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("enc");
		System.out.println(HexUtils.toHex(enc));
    	
		
    	byte[] dec = null;
		try {
			dec = keyManager.getECIESDecryptBytes("key-1", HexUtils.toBytes(nonce), publicKey, HexUtils.toBytes("2e9cb1f65cee9ef6536a001765652b4b"), AESType.AES256);
		} catch (IWException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	System.out.println("dec2");

    	String decString = null;
		try {
			decString = new String(dec, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println(decString);
    	
    	
    }
}
