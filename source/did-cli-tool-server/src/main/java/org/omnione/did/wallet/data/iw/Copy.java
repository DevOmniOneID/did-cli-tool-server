
package org.omnione.did.wallet.data.iw;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class Copy extends IWObject {

	@SerializedName("issued")
	@Expose
	private String issued;

	@SerializedName("expires")
	@Expose
	private String expires;

	@SerializedName("authorizedServiceProvider")
	@Expose
	private String authorizedServiceProvider;

	public Date getIssuedDate() {
		return VerifiableClaim.stringToDate(issued);
	}

	public void setIssued(Date issued) {
		this.issued = VerifiableClaim.dateToString(issued);
	}

	public void setIssued(String issued) {
		this.issued = issued;
	}

	public Date getExpiresDate() {
		return VerifiableClaim.stringToDate(expires);
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = VerifiableClaim.dateToString(expires);
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public String getAuthorizedServiceProvider() {
		return authorizedServiceProvider;
	}

	public void setAuthorizedServiceProvider(String authorizedServiceProvider) {
		this.authorizedServiceProvider = authorizedServiceProvider;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Copy.class.getName()).append('@')
				.append(Integer.toHexString(System.identityHashCode(this))).append('[');
		sb.append("issued");
		sb.append('=');
		sb.append(((this.issued == null) ? "<null>" : this.issued));
		sb.append(',');
		sb.append("expires");
		sb.append('=');
		sb.append(((this.expires == null) ? "<null>" : this.expires));
		sb.append(',');
		sb.append("authorizedServiceProvider");
		sb.append('=');
		sb.append(((this.authorizedServiceProvider == null) ? "<null>"
				: this.authorizedServiceProvider));
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
		result = ((result * 31) + ((this.issued == null) ? 0 : this.issued.hashCode()));
		result = ((result * 31) + ((this.authorizedServiceProvider == null) ? 0
				: this.authorizedServiceProvider.hashCode()));
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Copy) == false) {
			return false;
		}
		Copy rhs = ((Copy) other);
		return ((((this.expires == rhs.expires)
				|| ((this.expires != null) && this.expires.equals(rhs.expires)))
				&& ((this.issued == rhs.issued)
						|| ((this.issued != null) && this.issued.equals(rhs.issued))))
				&& ((this.authorizedServiceProvider == rhs.authorizedServiceProvider)
						|| ((this.authorizedServiceProvider != null)
								&& this.authorizedServiceProvider
										.equals(rhs.authorizedServiceProvider))));
	}

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		Copy obj = gson.fromJson(val, Copy.class);
		issued = obj.issued;
		expires = obj.expires;
		authorizedServiceProvider = obj.authorizedServiceProvider;
	}	
	
}
