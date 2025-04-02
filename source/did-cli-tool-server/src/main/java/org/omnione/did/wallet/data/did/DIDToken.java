
package org.omnione.did.wallet.data.did;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class DIDToken extends IWObject {

	@SerializedName("toDid")
    @Expose
    private String toDid;
	
	@SerializedName("quantity")
    @Expose
    private String quantity;
	
	@SerializedName("txId")
    @Expose
    private String txId;
	
	@SerializedName("memo")
    @Expose
    private String memo;

    public DIDToken() {
    	
    }
    
    public DIDToken(String didsJson) {
    	fromJson(didsJson);
    }

    
    
    public String getToDid() {
		return toDid;
	}

	public void setToDid(String toDid) {
		this.toDid = toDid;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}
	
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Override
    public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
    	DIDToken data = gson.fromJson(val, DIDToken.class);
    	toDid = data.getToDid();
    	quantity = data.getQuantity();
    	txId = data.getTxId();
    	memo = data.getMemo();
    }
}
