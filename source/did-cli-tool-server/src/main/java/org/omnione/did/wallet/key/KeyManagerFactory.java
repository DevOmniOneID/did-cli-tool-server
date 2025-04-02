package org.omnione.did.wallet.key;

import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.key.data.AESType;

import org.omnione.did.wallet.key.store.IWKeyFile;
import org.omnione.did.wallet.util.GDPLogger;

public class KeyManagerFactory {

	public enum KeyManagerType {
		DEFAULT, NCIPHER_HSM_TYPE, LUNA_HSM_TYPE;
	}


	private static final String NFAST_SERVER_PORT_KEY = "NFAST_SERVER_PORT";
	private static final String NFAST_SERVER_PRIVPORT_KEY = "NFAST_SERVER_PRIVPORT";
	private static final String LUNA_SERVER_SLOT_KEY = "LUNA_SERVER_PORT";
	private static final String PASSWD = "LUNA_SERVER_PASSWD";

	public static void setKeyOneTimeLoad(boolean load) {
		//tykim: 서버쪽에서 사용할 경우 키 로드를 매번 하지 않도록함
		IWKeyFile.ONETIME_LOAD = load;
	}


	public static void setHsmPortOption(String NFAST_SERVER_PORT, String NFAST_SERVER_PRIVPORT ) {
		if(NFAST_SERVER_PORT != null) {
			GDPLogger.debug("NFAST_SERVER_PORT: " + NFAST_SERVER_PORT);
			System.setProperty(NFAST_SERVER_PORT_KEY, NFAST_SERVER_PORT);
		}

		if(NFAST_SERVER_PRIVPORT != null) {
			GDPLogger.debug("NFAST_SERVER_PRIVPORT: " + NFAST_SERVER_PRIVPORT);
			System.setProperty(NFAST_SERVER_PRIVPORT_KEY, NFAST_SERVER_PRIVPORT);
		}
	}

	public static void setHsmSlotOption(String LUNA_SERVER_SLOT, String LUNA_SERVER_PASSWD ) {
		if(LUNA_SERVER_SLOT != null) {
			GDPLogger.debug("LUNA_SERVER_SLOT: " + LUNA_SERVER_SLOT);
			System.setProperty(LUNA_SERVER_SLOT_KEY, LUNA_SERVER_SLOT);
		}

		if(LUNA_SERVER_PASSWD != null) {
			GDPLogger.debug("LUNA_SERVER_PWD: " + LUNA_SERVER_PASSWD);
			System.setProperty(PASSWD, LUNA_SERVER_PASSWD);
		}
	}


	public static IWKeyManagerInterface getKeyManager(KeyManagerType type, String keyFile_pathWithName, char [] pwd ) throws IWException {

		IWKeyManagerInterface manager = null;

		switch (type) {
			case DEFAULT:
				manager = new IWKeyManager(keyFile_pathWithName, pwd);
				break;
			default:
				break;
		}

		return manager;
	}

	public static IWKeyManagerInterface getKeyManager(KeyManagerType type, String keyFile_pathWithName) throws IWException {

		IWKeyManagerInterface manager = null;

		switch (type) {
			case DEFAULT:
				manager = new IWKeyManager(keyFile_pathWithName);
				break;
			case NCIPHER_HSM_TYPE:
				manager = new IWKeyManager(keyFile_pathWithName);
				break;
			case LUNA_HSM_TYPE:
				manager = new IWKeyManager(keyFile_pathWithName);
				break;
			default:
				break;
		}

		return manager;
	}


	public static IWKeyManagerInterface getKeyManager(KeyManagerType type, String keyFile_pathWithName, char[] pwd,
													  AESType aesType) throws IWException {

		if (aesType == AESType.AES256) {
			return getKeyManager(type, keyFile_pathWithName, pwd);
		}

		IWKeyManagerInterface manager = null;
		manager = new IWKeyManager(keyFile_pathWithName, pwd, AESType.AES128);

		return manager;
	}

}
