
package org.omnione.did.wallet.data.iw;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class Assertion extends IWObject {

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
	@SerializedName("name")
	@Expose
	private String name;

	/**
	 *
	 * (Required)
	 *
	 */
	@SerializedName("type")
	@Expose
	private String type;

	/**
	 *
	 * (Required)
	 *
	 */
	@SerializedName("level")
	@Expose
	private Integer level;
	
	
	
	@SerializedName("version")
	@Expose
	private Integer version;	
	

	/**
	 *
	 * (Required)
	 *
	 */
	

	/**
	 *
	 * (Required)
	 *
	 */
	@SerializedName("code")
	@Expose
	private String code;

	

	/**
	 *
	 * (Required)
	 *
	 */
	@SerializedName("desc")
	@Expose
	private String desc;

	
	
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

	/**
	 *
	 * (Required)
	 *
	 */
	public String getCode() {
		return code;
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 *
	 * (Required)
	 *
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
	/**
	 *
	 * (Required)
	 *
	 */
	public String getType() {
		return type;
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public Integer getLevel() {
		return level;
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}
	
	

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Assertion.class.getName()).append('@')
				.append(Integer.toHexString(System.identityHashCode(this))).append('[');
		sb.append("id");
		sb.append('=');
		sb.append(((this.id == null) ? "<null>" : this.id));
		sb.append(',');
		sb.append("name");
		sb.append('=');
		sb.append(((this.name == null) ? "<null>" : this.name));
		sb.append(',');
		sb.append("type");
		sb.append('=');
		sb.append(((this.type == null) ? "<null>" : this.type));
		sb.append(',');
		sb.append("level");
		sb.append('=');
		sb.append(((this.level == null) ? "<null>" : this.level));
		sb.append(',');
		sb.append("code");
		sb.append('=');
		sb.append(((this.code == null) ? "<null>" : this.code));
		sb.append(',');
		sb.append("desc");
		sb.append('=');
		sb.append(((this.desc == null) ? "<null>" : this.desc));
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
		result = ((result * 31) + ((this.type == null) ? 0 : this.type.hashCode()));
		result = ((result * 31) + ((this.level == null) ? 0 : this.level.hashCode()));
		result = ((result * 31) + ((this.code == null) ? 0 : this.code.hashCode()));
		result = ((result * 31) + ((this.desc == null) ? 0 : this.desc.hashCode()));

		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Assertion) == false) {
			return false;
		}
		Assertion rhs = ((Assertion) other);
		return (((((this.name == rhs.name)
				|| ((this.name != null) && this.name.equals(rhs.name)))
				&& ((this.id == rhs.id) || ((this.id != null) && this.id.equals(rhs.id))))
				&& ((this.type == rhs.type)
						|| ((this.type != null) && this.type.equals(rhs.type))))
				&& ((this.level == rhs.level)
						|| ((this.level != null) && this.level.equals(rhs.level)))
				&& ((this.code == rhs.code)
						|| ((this.code != null) && this.code.equals(rhs.code)))
				&& ((this.desc == rhs.desc)
						|| ((this.desc != null) && this.desc.equals(rhs.desc)))
				);
	}
	
	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		Assertion obj = gson.fromJson(val, Assertion.class);
		id = obj.id;
		name = obj.name;
		type = obj.type;
		level = obj.level;
		version = obj.version;
		code = obj.code;
		desc = obj.desc;
	}	

}
