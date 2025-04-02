
package org.omnione.did.wallet.data.iw;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.data.did.Proof;
import org.omnione.did.wallet.eoscommander.util.StringUtils;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class VerifiableClaim extends IWObject {
	
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
	@SerializedName("assertion")
	@Expose
	private Assertion assertion;

	/**
	 *
	 * (Required)
	 *
	 */
	@SerializedName("claim")
	@Expose
	private Claim claim;

	@SerializedName("copy")
	@Expose
	private Copy copy;

	@SerializedName("signature")
	@Expose
	private String signature;

	@SerializedName("proof")
	@Expose
	private Proof proof;

	

	@SerializedName("issuerProof")
	@Expose
	private Proof issuerProof;
	
	@SerializedName("delegateProof")
	@Expose
	private Proof delegateProof;

	
	
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
	public Claim getClaim() {
		return claim;
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public Copy getCopy() {
		return copy;
	}

	public void setCopy(Copy copy) {
		this.copy = copy;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Proof getProof() {
		return proof;
	}

	public void setProof(Proof proof) {
		this.proof = proof;
	}
	
	//추가 
	public Proof getIssuerProof() {
		return issuerProof;
	}

	public void setIssuerProof(Proof proof) {
		this.issuerProof = proof;
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

	public Proof getDelegateProof() {
		return delegateProof;
	}

	public void setDelegateProof(Proof delegateProof) {
		this.delegateProof = delegateProof;
	}

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		VerifiableClaim obj = gson.fromJson(val, VerifiableClaim.class);
		issuer = obj.issuer;
		assertion = obj.assertion;
		claim = obj.claim;
		copy = obj.copy;
		signature = obj.signature;
		proof = obj.proof;
		issuerProof = obj.issuerProof;
		delegateProof = obj.delegateProof;
		id = obj.id;
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
