
package org.omnione.did.wallet.data.iw;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class Issuer extends IWObject {

	/**
	 *
	 * (Required)
	 *
	 */
	@SerializedName("id")
	@Expose
	private String id;
	
	@SerializedName("did")
	@Expose
	private String did;

	/**
	 *
	 * (Required)
	 *
	 */
	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("desc")
	@Expose
	private String desc;

	@SerializedName("url")
	@Expose
	private String url;

	@SerializedName("logo")
	@Expose
	private Logo logo;

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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Issuer.class.getName()).append('@')
				.append(Integer.toHexString(System.identityHashCode(this))).append('[');
		sb.append("id");
		sb.append('=');
		sb.append(((this.id == null) ? "<null>" : this.id));
		sb.append(',');
		sb.append("name");
		sb.append('=');
		sb.append(((this.name == null) ? "<null>" : this.name));
		sb.append(',');
		sb.append("desc");
		sb.append('=');
		sb.append(((this.desc == null) ? "<null>" : this.desc));
		sb.append(',');
		sb.append("url");
		sb.append('=');
		sb.append(((this.url == null) ? "<null>" : this.url));
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
		result = ((result * 31) + ((this.name == null) ? 0 : this.name.hashCode()));
		result = ((result * 31) + ((this.id == null) ? 0 : this.id.hashCode()));
		result = ((result * 31) + ((this.url == null) ? 0 : this.url.hashCode()));
		result = ((result * 31) + ((this.desc == null) ? 0 : this.desc.hashCode()));
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Issuer) == false) {
			return false;
		}
		Issuer rhs = ((Issuer) other);
		return (((((this.name == rhs.name)
				|| ((this.name != null) && this.name.equals(rhs.name)))
				&& ((this.id == rhs.id) || ((this.id != null) && this.id.equals(rhs.id))))
				&& ((this.url == rhs.url)
						|| ((this.url != null) && this.url.equals(rhs.url))))
				&& ((this.desc == rhs.desc)
						|| ((this.desc != null) && this.desc.equals(rhs.desc))));
	}

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		Issuer obj = gson.fromJson(val, Issuer.class);
		id = obj.id;
		name = obj.name;
		desc = obj.desc;
		url = obj.url;
		logo = obj.logo;
		did = obj.did;
	}	

}
