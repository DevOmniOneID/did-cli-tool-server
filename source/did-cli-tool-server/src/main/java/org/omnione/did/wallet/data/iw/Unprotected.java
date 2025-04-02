
package org.omnione.did.wallet.data.iw;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class Unprotected extends IWObject {

	/**
	 *
	 * (Required)
	 *
	 */
	// @SerializedName("type")
	// @Expose
	// private Unprotected.Type type;

	@SerializedName("type")
	@Expose
	private String type;

	/**
	 *
	 * (Required)
	 *
	 */
	@SerializedName("value")
	@Expose
	private String value;

	// /**
	// *
	// * (Required)
	// *
	// */
	// public Unprotected.Type getType() {
	// return type;
	// }
	//
	// /**
	// *
	// * (Required)
	// *
	// */
	// public void setType(Unprotected.Type type) {
	// this.type = type;
	// }

	/**
	 *
	 * (Required)
	 *
	 */
	public String getValue() {
		return value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 *
	 * (Required)
	 *
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Unprotected.class.getName()).append('@')
				.append(Integer.toHexString(System.identityHashCode(this))).append('[');
		sb.append("type");
		sb.append('=');
		sb.append(((this.type == null) ? "<null>" : this.type));
		sb.append(',');
		sb.append("value");
		sb.append('=');
		sb.append(((this.value == null) ? "<null>" : this.value));
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
		result = ((result * 31) + ((this.type == null) ? 0 : this.type.hashCode()));
		result = ((result * 31) + ((this.value == null) ? 0 : this.value.hashCode()));
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Unprotected) == false) {
			return false;
		}
		Unprotected rhs = ((Unprotected) other);
		return (((this.type == rhs.type)
				|| ((this.type != null) && this.type.equals(rhs.type)))
				&& ((this.value == rhs.value)
						|| ((this.value != null) && this.value.equals(rhs.value))));
	}

	public Unprotected clone() throws CloneNotSupportedException {
		  Unprotected a = (Unprotected)super.clone();
		  return a;
	}
	

			
	public enum Type {

		@SerializedName("birthday")
		BIRTHDAY("birthday"), @SerializedName("email")
		EMAIL("email"), @SerializedName("address")
		ADDRESS("address"), @SerializedName("ageOver19")
		AGE_OVER_19("ageOver19"), @SerializedName("phone")
		PHONE("phone");
		private final String value;

		private final static Map<String, Unprotected.Type> CONSTANTS = new HashMap<String, Unprotected.Type>();

		static {
			for (Unprotected.Type c : values()) {
				CONSTANTS.put(c.value, c);
			}
		}

		private Type(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public String value() {
			return this.value;
		}

		public static Unprotected.Type fromValue(String value) {
			Unprotected.Type constant = CONSTANTS.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			}
			else {
				return constant;
			}
		}

	}
	
	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		Unprotected obj = gson.fromJson(val, Unprotected.class);
		type = obj.type;
		value = obj.value;
	}

}
