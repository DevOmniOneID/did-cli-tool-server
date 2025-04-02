package org.omnione.did.wallet.zkp.revoc.enums;

public enum IssuanceType {
    ISSUANCE_BY_DEFAULT("ISSUANCE_BY_DEFAULT"),
    ISSUANCE_ON_DEMAND("ISSUANCE_ON_DEMAND");

    private String value;

    IssuanceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
