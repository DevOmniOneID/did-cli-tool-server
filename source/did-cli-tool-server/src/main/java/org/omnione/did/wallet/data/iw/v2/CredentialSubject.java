package org.omnione.did.wallet.data.iw.v2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.data.iw.Logo;
import org.omnione.did.wallet.data.iw.Unprotected;
import org.omnione.did.wallet.data.iw.VerifiableClaim;
import org.omnione.did.wallet.util.json.GsonWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CredentialSubject extends IWObject {
	@SerializedName("id")
	@Expose
	private String id;

	
	@SerializedName("privacy")
	@Expose
	private List<Unprotected> privacyList = new ArrayList<Unprotected>();

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


	public List<Unprotected> getPrivacyList() {

		if(privacyList == null){
			return null;
		}
		List<Unprotected> newPrivacyList = new ArrayList<Unprotected>();
		newPrivacyList.addAll(privacyList);

		return newPrivacyList;
	}

	public void setPrivacyList(List<Unprotected> listPrivacy) {

		if(listPrivacy != null){
			List<Unprotected> newPrivacyList = new ArrayList<Unprotected>();
			newPrivacyList.addAll(listPrivacy);
			this.privacyList = newPrivacyList;
		}
		else{
			this.privacyList = null;
		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(CredentialSubject.class.getName()).append('@')
				.append(Integer.toHexString(System.identityHashCode(this))).append('[');
		sb.append("id");
		sb.append('=');
		sb.append(((this.id == null) ? "<null>" : this.id));
		sb.append(',');
		sb.append("privacy");
		sb.append('=');
		sb.append(((this.privacyList == null) ? "<null>" : this.privacyList));
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
		result = ((result * 31) + ((this.privacyList == null) ? 0 : this.privacyList.hashCode()));
		result = ((result * 31) + ((this.id == null) ? 0 : this.id.hashCode()));
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof CredentialSubject) == false) {
			return false;
		}
		CredentialSubject rhs = ((CredentialSubject) other);
		return (((((this.privacyList == rhs.privacyList)
						|| ((this.privacyList != null) && this.privacyList.equals(rhs.privacyList))))
				&& ((this.id == rhs.id) || ((this.id != null) && this.id.equals(rhs.id)))));
	}

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		CredentialSubject obj = gson.fromJson(val, CredentialSubject.class);
		id = obj.id;
		privacyList = obj.privacyList;
	}		
}
