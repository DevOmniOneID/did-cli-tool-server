package org.omnione.did.wallet.data.iw.profile;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.data.iw.Assertion;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class Filter extends IWObject {

	@SerializedName("allowIssuerList")
	@Expose
	private List<String> allowIssuerList;

	@SerializedName("requiredPrivacyList")
	@Expose
	private List<String> requiredPrivacyList;

	@SerializedName("requiredAssertionList")
	@Expose
	private List<Assertion> requiredAssertionList;

	private String spName;
	public String getSpName() {
		return spName;
	}
	public void setSpName(String strSpName) {
		this.spName = strSpName;
	}

	public List<String> getAllowIssuerList() {

		if (allowIssuerList == null) {
			return null;
		}

		List<String> lAllowIssuerList = new ArrayList<String>();
		lAllowIssuerList.addAll(allowIssuerList);

		return lAllowIssuerList;
	}

	public void setAllowIssuerList(List<String> allowIssuerList) {

		if (allowIssuerList != null) {
			List<String> new_allowIssuerList = new ArrayList<String>();
			new_allowIssuerList.addAll(allowIssuerList);
			this.allowIssuerList = new_allowIssuerList;
		} else {
			this.allowIssuerList = null;
		}

	}

	public List<String> getRequiredPrivacyList() {

		if (requiredPrivacyList == null) {
			return null;
		}

		List<String> lRequiredPrivacyList = new ArrayList<String>();
		lRequiredPrivacyList.addAll(requiredPrivacyList);

		return lRequiredPrivacyList;
	}

	public void setRequiredPrivacyList(List<String> requiredPrivacyList) {

		if (requiredPrivacyList != null) {
			List<String> new_requiredPrivacyList = new ArrayList<String>();
			new_requiredPrivacyList.addAll(requiredPrivacyList);
			this.requiredPrivacyList = new_requiredPrivacyList;
		} else {
			this.requiredPrivacyList = null;
		}

	}

	public List<Assertion> getRequiredAssertionList() {

		if (requiredAssertionList == null) {
			return null;
		}

		List<Assertion> lRequiredAssertionList = new ArrayList<Assertion>();
		lRequiredAssertionList.addAll(requiredAssertionList);

		return lRequiredAssertionList;
	}

	public void setRequiredAssertionList(List<Assertion> requiredAssertionList) {

		if (requiredAssertionList != null) {
			List<Assertion> new_requiredAssertionList = new ArrayList<Assertion>();
			new_requiredAssertionList.addAll(requiredAssertionList);
			this.requiredAssertionList = new_requiredAssertionList;
		} else {
			this.requiredAssertionList = null;
		}

	}

	@Override
	public void fromJson(String val) {
		GsonWrapper gson = new GsonWrapper();
		Filter obj = gson.fromJson(val, Filter.class);
		allowIssuerList = obj.allowIssuerList;
		requiredPrivacyList = obj.requiredPrivacyList;
		requiredAssertionList = obj.requiredAssertionList;
	}
}
