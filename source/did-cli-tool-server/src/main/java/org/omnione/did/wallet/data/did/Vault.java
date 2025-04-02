package org.omnione.did.wallet.data.did;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class Vault  extends IWObject {

	@SerializedName("uuid")
	@Expose
	private String uuid;
	
	@SerializedName("storageList")
	@Expose
	private List<StorageList> storageList;
	
	
	@SerializedName("splitCount")
	@Expose
	private int splitCount;
	
	@SerializedName("threshold")
	@Expose
	private int threshold;
	
	@SerializedName("nonce")
	@Expose
	private String nonce;
	
	@SerializedName("backupCenterUrl")
	@Expose
	private String backupCenterUrl;
	
	

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	
	public List<StorageList> getStorageList() {
		return this.storageList;
	}

	public void setStorageList(List<StorageList> storageList) {
		this.storageList = storageList;
	}
	
	
	public int getSplitCount() {
		return this.splitCount;
	}

	public void setSplitCount(int splitCount) {
		this.splitCount = splitCount;
	}

	
	public int getThreshold() {
		return this.threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	

	public String getNonce() {
		return this.nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
	
	public String getBackupCenterUrl() {
		return this.backupCenterUrl;
	}

	public void setBackupCenterURl(String backupCenterUrl) {
		this.backupCenterUrl = backupCenterUrl;
	}
	

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		Vault obj = gson.fromJson(val, Vault.class);
		uuid = obj.getUuid();
		storageList = obj.getStorageList();
		splitCount = obj.getSplitCount();
		threshold = obj.getThreshold();
		nonce = obj.getNonce();
		backupCenterUrl = obj.getBackupCenterUrl();
	}	
}
