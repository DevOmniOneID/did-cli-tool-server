package org.omnione.did.wallet.data.iw.profile;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class VCType extends IWObject {
	@SerializedName("typeCode")
	@Expose
	private String typeCode;

	@SerializedName("typeName")
	@Expose
	private String typeName;

	@SerializedName("desc")
	@Expose
	private String desc;

	@SerializedName("claims")
	@Expose
	private List<VCTypeClaims> claims = new ArrayList<VCTypeClaims>();

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeDesc() {
		return desc;
	}

	public void setTypeDesc(String desc) {
		this.desc = desc;
	}

	public List<VCTypeClaims> getClaims() {
		return claims;
	}

	public void setClaims(List<VCTypeClaims> claims) {
		this.claims = claims;
	}

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		VCType obj = gson.fromJson(val, VCType.class);
		typeCode = obj.typeCode;
		typeName = obj.typeName;
		desc = obj.desc;
		claims = obj.claims;

	}
}
