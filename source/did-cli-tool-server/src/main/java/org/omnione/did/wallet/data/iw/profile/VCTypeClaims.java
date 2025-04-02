package org.omnione.did.wallet.data.iw.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class VCTypeClaims extends IWObject {

	@SerializedName("code")
	@Expose
	private String code;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("type")
	@Expose
	private String type;

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		VCTypeClaims obj = gson.fromJson(val, VCTypeClaims.class);
		code = obj.code;
		name = obj.name;
		type = obj.type;
	}
}
