package org.omnione.did.wallet.key;

import java.util.List;

import org.omnione.did.wallet.data.did.DIDAssertionType;
import org.omnione.did.wallet.data.did.DIDToken;
import org.omnione.did.wallet.data.did.DIDs;
import org.omnione.did.wallet.exception.IWException;

public interface IWDIDManagerInterface {

	public interface Callback {
		void processServerResponse(String result) throws Exception;
	}

	public String genDID() throws IWException;

	public String genDID(String prefix) throws IWException;

	public String genDidWithMethodName(String methodName) throws IWException;

	public byte[] getSign(String signKeyId, byte[] source, IWKeyManagerInterface keyManager) throws IWException;

	public void verifySign(String signKeyId, byte[] source, byte[] sign, IWKeyManagerInterface keyManager) throws IWException;

	public String makeRequestDIDsCreate(String did, List<String> createKeyIds, IWKeyManagerInterface keyManager) throws IWException;

	public String makeRequestDIDsAdd(String addKeyId, IWKeyManagerInterface keyManager) throws IWException;

	public String makeRequestDIDsDelete(String deleteKeyId) throws IWException;

	public String makeRequestDIDsModify(String didsJSON, String keyId, IWKeyManagerInterface keyManager) throws IWException;

	public boolean isExistKeyId(String didsJSON, String keyId) throws IWException;

	public String makeProof(String didsJSON, String signKeyId, byte[] nonce, IWKeyManagerInterface keyManager) throws IWException;

	public String makeDIDAssertion(DIDAssertionType type, String signKeyId, byte[] nonce, String value, IWKeyManagerInterface keyManager) throws IWException;

	public String makeDIDTokenAssertion(DIDAssertionType type, String signKeyId, DIDToken didToken, byte[] nonce, IWKeyManagerInterface keyManager) throws IWException;

	public String makeRequestDIDsRead() throws IWException;

	public DIDs verifyWithLocal(String didAuth, DIDs didDoc) throws IWException;

	public boolean isExistDID();

	public void sendToServer(String serverUrl, String dids, Callback callback);

	public boolean saveToFile(String didsJSON) throws IWException;

	public void deleteDIDFile();

}
