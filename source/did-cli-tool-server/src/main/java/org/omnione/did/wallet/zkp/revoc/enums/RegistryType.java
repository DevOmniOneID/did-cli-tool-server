package org.omnione.did.wallet.zkp.revoc.enums;

public enum RegistryType {
    CL_ACCUM("CL_ACCUM");

    private String value;

    RegistryType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}