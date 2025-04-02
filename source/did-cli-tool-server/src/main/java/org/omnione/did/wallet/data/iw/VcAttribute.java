package org.omnione.did.wallet.data.iw;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class VcAttribute extends IWObject {

	@SerializedName("encType")
	@Expose
	private Boolean encType = false;

	@SerializedName("vcType")
	@Expose
	private String vcType;

	@SerializedName("orgId")
	@Expose
	private String orgId;

	@SerializedName("regCode")
	@Expose
	private String regCode;

	@SerializedName("vcBiz")
	@Expose
	private String vcBiz;

	@SerializedName("bizCode")
	@Expose
	private String bizCode;
	
	@SerializedName("expDate")
	@Expose
	private String expDate;

	public Boolean getEncType() {
		return encType;
	}

	public void setEncType(Boolean encType) {
		this.encType = encType;
	}

	public String getVcType() {
		return vcType;
	}

	public void setVcType(String vcType) {
		this.vcType = vcType;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getRegCode() {
		return regCode;
	}

	public void setRegCode(String regCode) {
		this.regCode = regCode;
	}

	public String getVcBiz() {
		return vcBiz;
	}

	public void setVcBiz(String vcBiz) {
		this.vcBiz = vcBiz;
	}

	public String getBizCode() {
		return bizCode;
	}

	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
	}
	
	public String getExpDate() {
		return expDate;
	}

	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = GsonWrapper.getGson();
		
		VcAttribute obj = gson.fromJson(val, VcAttribute.class);
		encType = obj.getEncType();
		vcType = obj.getVcType();
		orgId = obj.getOrgId();
		regCode = obj.getRegCode();
		vcBiz = obj.getVcBiz();
		bizCode = obj.getBizCode();
		expDate = obj.getExpDate();
	}

}
