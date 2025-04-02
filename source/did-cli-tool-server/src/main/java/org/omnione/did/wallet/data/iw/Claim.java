
package org.omnione.did.wallet.data.iw;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class Claim extends IWObject {


	@SerializedName("id")
	@Expose
	private String id;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("logo")
	@Expose
	private Logo logo;

	@SerializedName("issued")
	@Expose
	private String issued;

	@SerializedName("expires")
	@Expose
	private String expires;

	@SerializedName("public_key")
	@Expose
	private String publicKey;

	@SerializedName("privacy")
	@Expose
	private Privacy privacy;


	public String getId() {
		return id;
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public void setName(String name) {
		this.name = name;
	}

	public Logo getLogo() {
		return logo;
	}

	public void setLogo(Logo logo) {
		this.logo = logo;
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public String getIssued() {
		return issued;
	}

	public Date getIssuedDate() {
		return VerifiableClaim.stringToDate(issued);
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public void setIssued(Date issued) {
		this.issued = VerifiableClaim.dateToString(issued);
	}

	public void setIssued(String issued) {
		this.issued = issued;
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public String getExpires() {
		return expires;
	}

	public Date getExpiresDate() {
		return VerifiableClaim.stringToDate(expires);
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public void setExpires(Date expires) {
		this.expires = VerifiableClaim.dateToString(expires);
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public String getPublicKey() {
		return publicKey;
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public Privacy getPrivacy() {
		return privacy;
	}

	public void setPrivacy(Privacy privacy) {
		this.privacy = privacy;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Claim.class.getName()).append('@')
				.append(Integer.toHexString(System.identityHashCode(this))).append('[');
		sb.append("id");
		sb.append('=');
		sb.append(((this.id == null) ? "<null>" : this.id));
		sb.append(',');
		sb.append("name");
		sb.append('=');
		sb.append(((this.name == null) ? "<null>" : this.name));
		sb.append(',');
		sb.append("logo");
		sb.append('=');
		sb.append(((this.logo == null) ? "<null>" : this.logo));
		sb.append(',');
		sb.append("issued");
		sb.append('=');
		sb.append(((this.issued == null) ? "<null>" : this.issued));
		sb.append(',');
		sb.append("expires");
		sb.append('=');
		sb.append(((this.expires == null) ? "<null>" : this.expires));
		sb.append(',');
		sb.append("publicKey");
		sb.append('=');
		sb.append(((this.publicKey == null) ? "<null>" : this.publicKey));
		sb.append(',');
		sb.append("privacy");
		sb.append('=');
		sb.append(((this.privacy == null) ? "<null>" : this.privacy));
		sb.append(',');
		if (sb.charAt((sb.length() - 1)) == ',') {
			sb.setCharAt((sb.length() - 1), ']');
		}
		else {
			sb.append(']');
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = ((result * 31) + ((this.expires == null) ? 0 : this.expires.hashCode()));
		result = ((result * 31) + ((this.name == null) ? 0 : this.name.hashCode()));
		result = ((result * 31) + ((this.logo == null) ? 0 : this.logo.hashCode()));
		result = ((result * 31) + ((this.privacy == null) ? 0 : this.privacy.hashCode()));
		result = ((result * 31) + ((this.id == null) ? 0 : this.id.hashCode()));
		result = ((result * 31)
				+ ((this.publicKey == null) ? 0 : this.publicKey.hashCode()));
		result = ((result * 31) + ((this.issued == null) ? 0 : this.issued.hashCode()));
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Claim) == false) {
			return false;
		}
		Claim rhs = ((Claim) other);
		return ((((((((this.expires == rhs.expires)
				|| ((this.expires != null) && this.expires.equals(rhs.expires)))
				&& ((this.name == rhs.name)
						|| ((this.name != null) && this.name.equals(rhs.name))))
				&& ((this.logo == rhs.logo)
						|| ((this.logo != null) && this.logo.equals(rhs.logo))))
				&& ((this.privacy == rhs.privacy)
						|| ((this.privacy != null) && this.privacy.equals(rhs.privacy))))
				&& ((this.id == rhs.id) || ((this.id != null) && this.id.equals(rhs.id))))
				&& ((this.publicKey == rhs.publicKey) || ((this.publicKey != null)
						&& this.publicKey.equals(rhs.publicKey))))
				&& ((this.issued == rhs.issued)
						|| ((this.issued != null) && this.issued.equals(rhs.issued))));
	}

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		Claim obj = gson.fromJson(val, Claim.class);
		id = obj.id;
		name = obj.name;
		logo = obj.logo;
		issued = obj.issued;
		expires = obj.expires;
		publicKey = obj.publicKey;
		privacy = obj.privacy;
	}		
}
