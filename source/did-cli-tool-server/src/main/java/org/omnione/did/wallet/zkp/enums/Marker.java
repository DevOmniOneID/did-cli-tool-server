package org.omnione.did.wallet.zkp.enums;

public enum Marker {
    CRED_SCHEMA(2),
    CRED_DEF(3),
    REV_REG_DEF(4);

    private int value;


    Marker(int value) {
        this.value = value;
    }

    public int getMaker() {
        return this.value;
    }
}
