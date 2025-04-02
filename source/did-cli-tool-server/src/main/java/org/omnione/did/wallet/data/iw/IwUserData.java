
package org.omnione.did.wallet.data.iw;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class IwUserData extends IWObject {

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

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		IwUserData obj = gson.fromJson(val, IwUserData.class);
		issuer = obj.issuer;
		assertion = obj.assertion;
		claim = obj.claim;
	}	

}
