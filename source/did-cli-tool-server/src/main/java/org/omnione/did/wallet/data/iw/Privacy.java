
package org.omnione.did.wallet.data.iw;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class Privacy extends IWObject {

	@SerializedName("unprotected")
	@Expose
	private List<Unprotected> unprotected = new ArrayList<Unprotected>();

	@SerializedName("protected")
	@Expose
	private String _protected;

	public List<Unprotected> getUnprotected() {
		
		if(unprotected == null){
			return null;
		}
		List<Unprotected> lUnprotected = new ArrayList<Unprotected>();
		lUnprotected.addAll(unprotected);
    	
		return lUnprotected;
	}

	public void setUnprotected(List<Unprotected> unprotected) {
		
		if(unprotected != null){
			List<Unprotected> new_unprotected = new ArrayList<Unprotected>();
			new_unprotected.addAll(unprotected);
			this.unprotected = new_unprotected;
		}
		else{
			this.unprotected = null;
		}
		
	}

	public String getProtected() {
		return _protected;
	}

	public void setProtected(String _protected) {
		this._protected = _protected;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Privacy.class.getName()).append('@')
				.append(Integer.toHexString(System.identityHashCode(this))).append('[');
		sb.append("unprotected");
		sb.append('=');
		sb.append(((this.unprotected == null) ? "<null>" : this.unprotected));
		sb.append(',');
		sb.append("_protected");
		sb.append('=');
		sb.append(((this._protected == null) ? "<null>" : this._protected));
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
		result = ((result * 31)
				+ ((this._protected == null) ? 0 : this._protected.hashCode()));
		result = ((result * 31)
				+ ((this.unprotected == null) ? 0 : this.unprotected.hashCode()));
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Privacy) == false) {
			return false;
		}
		Privacy rhs = ((Privacy) other);
		return (((this._protected == rhs._protected)
				|| ((this._protected != null) && this._protected.equals(rhs._protected)))
				&& ((this.getUnprotected() == rhs.getUnprotected()) || ((this.getUnprotected() != null)
						&& this.getUnprotected().equals(rhs.getUnprotected()))));
	}

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		Privacy obj = gson.fromJson(val, Privacy.class);
		unprotected = obj.unprotected;
		_protected = obj._protected;
	}	

}
