package org.omnione.did.wallet.zkp.enums;

public enum CredentialType {
    CL(1);

    private int value;

    CredentialType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
