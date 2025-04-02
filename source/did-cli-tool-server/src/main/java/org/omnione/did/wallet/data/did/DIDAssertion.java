
package org.omnione.did.wallet.data.did;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class DIDAssertion extends IWObject {

	@SerializedName("type")
    @Expose
    private String type;
	
	
	@SerializedName("addSignData")
	@Expose
	private AddSignData addSignData;
	
	@SerializedName("addSignDataList")
	@Expose
	private List<AddSignData> addSignDataList;
	
	
    public DIDAssertion() {
    	
    }
    
    public DIDAssertion(String didsJson) {
    	fromJson(didsJson);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
	public AddSignData getAddSignData() {
		return addSignData;
	}

	public void setAddSignData(AddSignData addSignData) {
		this.addSignData = addSignData;
	}
	
	public List<AddSignData> getAddSignDataList() {
		return addSignDataList;
	}

	public void setAddSignDataList(List<AddSignData> addSignDataList) {
		this.addSignDataList = addSignDataList;
	}
	
    @Override
    public void fromJson(String val) {
    	GsonWrapper gson = new GsonWrapper();
    	DIDAssertion data = gson.fromJson(val, DIDAssertion.class);
    	type = data.getType();
    	addSignData = data.getAddSignData();
    	addSignDataList = data.addSignDataList;
    }
    
    
    public static DIDDefaultAssertion parsingDidAuth(String didAuth) {
    	DIDAssertion assertion = new DIDAssertion(didAuth);
		DIDAssertionType didAssertionType = DIDAssertionType.fromString(assertion.getType());

		DIDDefaultAssertion defaultAssertion = null;
		
		switch (didAssertionType) {
		case DEFAULT:
			defaultAssertion = new DIDDefaultAssertion(didAuth);
			break;
		case TOKEN_TRANS:
			defaultAssertion = new DIDTokenAssertion(didAuth);
			break;	
		default:
			break;
		}
		
		return defaultAssertion;
    }
}
