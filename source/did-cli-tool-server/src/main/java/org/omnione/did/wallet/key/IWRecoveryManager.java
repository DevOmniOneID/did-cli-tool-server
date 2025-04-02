package org.omnione.did.wallet.key;

import org.omnione.did.wallet.exception.IWErrorCode;
import org.omnione.did.wallet.exception.IWException;

public class IWRecoveryManager extends IWRecoveryManagerImpl {

	public enum BACKUP_OPTION {
		TOTAL(0),
		DID_DOCUMENT(1<<0),
		CLAIM(1<<1), 
		KEY(1<<2),
		CREDENTIAL(1<<3),
		MASTERSECRET(1<<4),
		ZKPCREDENTIAL(1<<5);
		
		public final int value;
		
		BACKUP_OPTION(int num) {
			this.value = num;
		}
		
		public int getValue() {
		    return value;
		}
		
	}
	

	public enum ENCRYPTION_TYPE {
		AES_128(0), AES_256(1);
		
		private int value;
		private ENCRYPTION_TYPE(int value) {
		   this.value = value;
		}
		  
		public int getValue() {
		   return value;
		}

		public static ENCRYPTION_TYPE fromValue(int value) {
            for (ENCRYPTION_TYPE type : values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            return null;
        }
		  
	}
	
	public interface BackUpCallback {
		public void success(byte[] backedUpData);
		public void failure(IWErrorCode errorCode);
	}
	
	public interface RestoreCallback {
		
		public void success(String extraString);
		public void failure(IWErrorCode errorCode);
		
	}
	
	public IWRecoveryManager(IWKeyManager keyManager) {
		super(keyManager);
	}
    
	
	@Override
	public void backUpStoredData(int range, ENCRYPTION_TYPE type, String key, String[] didPaths, String extraString,
			BackUpCallback callback) throws IWException {
		
		super.backUpStoredData(range, type, key, didPaths, extraString, callback);
	}

	@Override
	public void restoreBackedUpByte(byte[] backedUpByte, String key, String didPath, RestoreCallback callback) throws IWException {
		
		super.restoreBackedUpByte(backedUpByte, key, didPath, callback);
		
	}


}
