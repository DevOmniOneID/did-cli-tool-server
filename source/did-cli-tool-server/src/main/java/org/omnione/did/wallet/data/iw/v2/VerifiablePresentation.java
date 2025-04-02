package org.omnione.did.wallet.data.iw.v2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.data.did.AddSignData;
import org.omnione.did.wallet.data.did.Proof;
import org.omnione.did.wallet.data.iw.Assertion;
import org.omnione.did.wallet.data.iw.Issuer;
import org.omnione.did.wallet.data.iw.VerifiableClaim;
import org.omnione.did.wallet.eoscommander.util.StringUtils;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class VerifiablePresentation extends IWObject {
	
	/**
	 *
	 * (Required)
	 *
	 */
	@SerializedName("@context")
	@Expose
	private List<String> context;

	
	/**
	 *
	 * (Required)
	 *
	 */
	
	@SerializedName("id")
	@Expose
	private String id;
	

	/**
	 *
	 * (Required)
	 *
	 */
	@SerializedName("verifiableCredential")
	@Expose
	private ArrayList<VerifiableCredential> verifiableCredential;


	@SerializedName("expirationDate")
	@Expose
	private String expirationDate;
	
	/**
	 *
	 * (Required)
	 *
	 */
	@SerializedName("proof")
	@Expose
	private Proof proof;
	

	
	/**
	 *
	 * (Required)
	 *
	 */
	@SerializedName("type")
	@Expose
	private List<String> type;
	
	
	@SerializedName("addSignData")
	@Expose
	private AddSignData addSignData;
	
	
	@SerializedName("addSignDataList")
	@Expose
	private List<AddSignData> addSignDataList;
	
	
	public List<String> getContext() {
		return context;
	}

	public void setContext(List<String> context) {
		this.context = context;
	}
	
	public void setContext() {
		ArrayList<String> contextArrayList = new ArrayList<String>();
		contextArrayList.add("https://www.w3.org/2018/credentials/v1");	
		this.context = contextArrayList;
	}
	
	public String getId() {
		return id;
	}

	
	public String getExpirationDate() {
		return expirationDate;
	}
	
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public Date getExpirationDateObject() {
		return VerifiableClaim.stringToDate(this.expirationDate);
	}

	
	public List<String> getType() {
		return type;
	}

	public void setType(List<String> type) {
		this.type = type;
	}
	
	public void setType() {
		ArrayList<String> typeList = new ArrayList<String>();
		typeList.add("VerifiablePresentation");
		this.type = typeList;
	}
	


	
	public ArrayList<VerifiableCredential> getVerifiableCredential() {
		return verifiableCredential;
	}

	public void setVerifiableCredential(ArrayList<VerifiableCredential> verifiableCredential) {
		this.verifiableCredential = verifiableCredential;
	}
	
	
	public void setId() {
		if(StringUtils.isEmpty(this.id)) {
			this.id = UUID.randomUUID().toString();
		}
	}

	public Proof getProof() {
		return proof;
	}

	public void setProof(Proof proof) {
		this.proof = proof;
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
		VerifiablePresentation obj = gson.fromJson(val, VerifiablePresentation.class);
		context = obj.context;
		id = obj.id;
		expirationDate = obj.expirationDate;
		type = obj.type;
		verifiableCredential = obj.verifiableCredential;
		proof = obj.proof;
		addSignData = obj.addSignData;
		addSignDataList = obj.addSignDataList;

	}	


	
	
}
