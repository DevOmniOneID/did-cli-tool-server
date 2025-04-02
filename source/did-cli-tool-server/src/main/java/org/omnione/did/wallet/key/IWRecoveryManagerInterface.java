package org.omnione.did.wallet.key;

import org.omnione.did.wallet.key.IWRecoveryManager.BackUpCallback;
import org.omnione.did.wallet.key.IWRecoveryManager.ENCRYPTION_TYPE;
import org.omnione.did.wallet.key.IWRecoveryManager.RestoreCallback;

public interface IWRecoveryManagerInterface {

	
	
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
	 */
	
	public void backUpStoredData(int range, ENCRYPTION_TYPE type, String key, String[] didPaths, String extraString, BackUpCallback callback);
	
	/**
	 * Restore the backed up data
	 * 
	 * @param backedUpByte	: Backed Up Data
	 * @param key			: Decryption Key
	 * @param didPath		: Did Document Path To Be Stored 
	 * @param callback		: Success or failure callback
	 * @return
	 */
	
	public void restoreBackedUpByte(byte[] backedUpByte, String key, String didPath, RestoreCallback callback);
}
