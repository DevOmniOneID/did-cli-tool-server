
package org.omnione.did.wallet.data.did;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class Proof extends IWObject {

	@SerializedName("type")
	@Expose
	private String type;
	@SerializedName("created")
	@Expose
	private String created;
	@SerializedName("creator")
	@Expose
	private String creator;
	@SerializedName("nonce")
	@Expose
	private String nonce;
	@SerializedName("signatureValue")
	@Expose
	private String signatureValue;
	@SerializedName("signatureValueList")
	@Expose
	private List<String> signatureValueList;
	

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCreated() {
		return this.created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getCreator() {
		return this.creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getSignatureValue() {
		return this.signatureValue;
	}

	public void setSignatureValue(String signatureValue) {
		this.signatureValue = signatureValue;
	}
	
	public List<String> getSignatureValueList() {
		return this.signatureValueList;
	}

	public void setSignatureValueList(List<String> signatureValue) {
		this.signatureValueList = signatureValue;
	}

	
	

	public String getNonce() {
		return this.nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		Proof obj = gson.fromJson(val, Proof.class);
		type = obj.getType();
		created = obj.getCreated();
		creator = obj.getCreator();
		nonce = obj.getNonce();
		signatureValue = obj.getSignatureValue();
		signatureValueList = obj.getSignatureValueList();
	}	
}
