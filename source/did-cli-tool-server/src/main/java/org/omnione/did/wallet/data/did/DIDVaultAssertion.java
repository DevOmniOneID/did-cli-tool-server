package org.omnione.did.wallet.data.did;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class DIDVaultAssertion extends DIDDefaultAssertion {

	@SerializedName("vault")
	@Expose
	private Vault vault;

	public DIDVaultAssertion() {

	}

	public DIDVaultAssertion(String didsJson) {
		fromJson(didsJson);
	}

	public Vault getVault() {
		return vault;
	}

	public void setVault(Vault vault) {
		this.vault = vault;
	}

	@Override
	public void fromJson(String val) {
		super.fromJson(val);

		GsonWrapper gson = new GsonWrapper();
		DIDVaultAssertion data = gson.fromJson(val, DIDVaultAssertion.class);
		vault = data.getVault();

	}
}
