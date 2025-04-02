
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
import org.omnione.did.wallet.data.did.Proof;
import org.omnione.did.wallet.data.iw.Assertion;
import org.omnione.did.wallet.data.iw.Extension;
import org.omnione.did.wallet.data.iw.Issuer;
import org.omnione.did.wallet.data.iw.VerifiableClaim;
import org.omnione.did.wallet.eoscommander.util.StringUtils;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class VerifiableCredential extends IWObject {
	
	@SerializedName("id")
	@Expose
	private String id;
	

	/**
	 *
	 * (Required)
	 *
	 */
	@SerializedName("issuer")
	@Expose
	private Issuer issuer;
	

	/**
	 *
	 * (Required)
	 *
	 */
	@SerializedName("issuanceDate")
	@Expose
	private String issuanceDate;
	
	@SerializedName("expirationDate")
	@Expose
	private String expirationDate;
	
	
	
	@SerializedName("assertion")
	@Expose
	private Assertion assertion;

	/**
	 *
	 * (Required)
	 *
	 */
	@SerializedName("credentialSubject")
	@Expose
	private CredentialSubject credentialSubject;


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
	@SerializedName("@context")
	@Expose
	private List<String> context;

	/**
	 *
	 * (Required)
	 *
	 */
	@SerializedName("type")
	@Expose
	private List<String> type;
	
	
	
	@SerializedName("extension")
	@Expose
	private Extension extension;
	
	
	
	
	
	/**
	 *
	 * (Required)
	 *
	 */
	public Issuer getIssuer() {
		return issuer;
	}

	
	/**
	 *
	 * (Required)
	 *
	 */
	public void setIssuer(Issuer issuer) {
		this.issuer = issuer;
	}

	public String getIssuanceDate() {
		return issuanceDate;
	}

	
	/**
	 *
	 * (Required)
	 *
	 */
	public void setIssuanceDate(String issuanceDate) {
		this.issuanceDate = issuanceDate;
	}
	
	public void setIssuanceDate(Date issuanceDate) {
		this.issuanceDate = VerifiableClaim.dateToString(issuanceDate);
	}
	
	public Date getIssuanceDateObject() {
		return VerifiableClaim.stringToDate(this.issuanceDate);
	}

	
	public String getExpirationDate() {
		return expirationDate;
	}
	
	public Date getExpirationDateObject() {
		return VerifiableClaim.stringToDate(this.expirationDate);
	}
	
	public void setExpirationDate(Date expires) {
		this.expirationDate = VerifiableClaim.dateToString(expires);
	}
	
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	/**
	 *
	 * (Required)
	 *
	 */
	public Assertion getAssertion() {
		return assertion;
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public void setAssertion(Assertion assertion) {
		this.assertion = assertion;
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public CredentialSubject getCredentialSubject() {
		return credentialSubject;
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public void setCredentialSubject(CredentialSubject credentialSubject) {
		this.credentialSubject = credentialSubject;
	}


	public Proof getProof() {
		return proof;
	}

	public void setProof(Proof proof) {
		this.proof = proof;
	}
		
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void setId() {
		if(StringUtils.isEmpty(this.id)) {
			this.id = UUID.randomUUID().toString();
		}
	}


	public List<String> getContext() {
		return context;
	}

	public void setContext(List<String> context) {
		this.context = context;
	}

	public void setContext() {
		ArrayList<String> contextList = new ArrayList<String>();
		contextList.add("https://www.w3.org/2018/credentials/v1");
//		contextList.add("https://www.w3.org/2018/credentials/examples/v1");
        this.context = contextList;
        
	}
	public List<String> getType() {
		return type;
	}

	public void setType(List<String> type) {
		this.type = type;
	}
	
	public void setType() {
		ArrayList<String> typeList = new ArrayList<String>();
		typeList.add("VerifiableCredential");
		typeList.add("OmniOneCredential");
		this.type = typeList;
	}
	

	public void setExtension(Extension extension) {
		this.extension = extension;
	}
	
	public Extension getExtension() {
		return extension;
	}

	
	

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		VerifiableCredential obj = gson.fromJson(val, VerifiableCredential.class);
		issuer = obj.issuer;
		issuanceDate = obj.issuanceDate;
		assertion = obj.assertion;
		credentialSubject = obj.credentialSubject;
		proof = obj.proof;
		id = obj.id;
		context = obj.context;
		type = obj.type;
		expirationDate = obj.expirationDate;
		extension = obj.extension;
	}	

	private static final DateFormat DATE_FROMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	public static String dateToString(Date date) {
		return DATE_FROMAT.format(date);
	}

	public static Date stringToDate(String date) {
		try {
			return DATE_FROMAT.parse(date);
		} catch (ParseException e) {
//			e.printStackTrace();
		}
		return null;
	}
	
	
}
