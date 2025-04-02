package org.omnione.did.wallet.crypto;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * 인터페이스 : Server 와 Client 가 함께 사용할 수 있는 서명 및 검증에 대한 interface를 정의
 * <pre>History:</b>
 *		Eliot, 2018.09.13 최초작성
 * </pre>
 *
 * @author Eliot
 * @version 1.0
 * @see None
 */

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;

import org.omnione.did.wallet.exception.IWErrorCode;
import org.omnione.did.wallet.exception.IWException;

public abstract class GDPCryptoHelperInterface {
		
	static {
		Security.removeProvider(GDPCryptoConst.Provider_SC);
		Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
	}
	
	// r1 sign, k1 sign
	public byte[] sign(String signAlg, byte[] signSource, PrivateKey privateKey) throws IWException {
		Signature signature = null;
		
		try {
			signature = Signature.getInstance(signAlg, GDPCryptoConst.Provider_SC);
		} catch (NoSuchAlgorithmException e) {
			// 해당 알고리즘  없음
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_SIGN, e);		
		} catch (NoSuchProviderException e) {
			// 해당 프로바이더 없음
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_SIGN, e);		
		}
	
		try {
			signature.initSign(privateKey);
		}
		catch (InvalidKeyException e) {
			// private키가 잘못됨
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_SIGN, e);	
		}

		byte[] signData = null;
		try {
			signature.update(signSource);
			signData = signature.sign();
		}
		catch (SignatureException e) {
			// 서명 실패
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_SIGN, e);			
		}

		return signData;
	}
	
	public boolean verify(String signAlg, byte[] sign, PublicKey publicKey, byte[] signSource) throws IWException {
		Signature signature = null;

		try {
			signature = Signature.getInstance(signAlg, GDPCryptoConst.Provider_SC);
		}
		catch (NoSuchAlgorithmException e) {
			// 해당 알고리즘  없음
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_VERIFY, e);		
		} catch (NoSuchProviderException e) {
			// 해당 프로바이더 없음
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_VERIFY, e);		
		}

		try {
			signature.initVerify(publicKey);
		}
		catch (InvalidKeyException e) {
			// public키가 잘못됨
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_VERIFY, e);		
		}

		try {
			signature.update(signSource);
		}
		catch (SignatureException e) {
			// 검증 실패
			throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_VERIFY, e);	
		}

		boolean ret = false;
		if (sign != null) {
			try {
				ret = signature.verify(sign);
			}
			catch (SignatureException e) {
				// 검증 실패
				ret = false;
				throw new IWException(IWErrorCode.ERR_CODE_CRYPTOHELPER_VERIFY, e);	
			}
		}

		return ret;
	}
	
	public boolean verify(String signAlg, byte[] sign, byte[] publicKey, byte[] signSource) throws IWException {
		return false;
	}
	
	abstract public KeyPair generateKeyPair();
}
