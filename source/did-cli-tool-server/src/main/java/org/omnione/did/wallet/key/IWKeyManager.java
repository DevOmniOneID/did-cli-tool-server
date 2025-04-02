package org.omnione.did.wallet.key;

import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.key.data.AESType;
import org.omnione.did.wallet.key.extend.IWEosKeyManagerAdapter;

public class IWKeyManager extends IWEosKeyManagerAdapter {
	/**
	 * (Default) KeyStore 생성자 : Password 인증
	 * 
	 * @param walletfile_pathWithName
	 * @param pwd
	 * @throws Exception
	 */
	public IWKeyManager(String walletfile_pathWithName, char[] pwd) throws IWException {
		super(walletfile_pathWithName, pwd);
	}
	
	public IWKeyManager(String walletfile_pathWithName, char[] pwd, AESType aesType) throws IWException {
		super(walletfile_pathWithName, pwd, aesType);
	}

	// for android
	public IWKeyManager(String walletfile_pathWithName) throws IWException {
		super(walletfile_pathWithName);
	}
}
