package org.omnione.did.wallet.data.did;

public enum DIDAssertionType {
	DEFAULT("default"),TOKEN_TRANS("tokenTrans"),VAULT("vault"),PAYLOAD("payload");

	private String type;

	DIDAssertionType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static DIDAssertionType fromString(String text) {
		for (DIDAssertionType b : DIDAssertionType.values()) {
			if (b.type.equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}
}
