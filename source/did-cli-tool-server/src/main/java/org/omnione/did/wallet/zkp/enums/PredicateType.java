package org.omnione.did.wallet.zkp.enums;

import com.google.gson.annotations.SerializedName;

public enum PredicateType {

    @SerializedName("GE")
    GE(">="),
    @SerializedName("LE")
    LE("<="),
    @SerializedName("GT")
    GT(">"),
    @SerializedName("LT")
    LT("<"),
    @SerializedName("EQ")
    EQ("==");

    private String expression;

    PredicateType(String expression) {
        this.expression = expression;
    }

    public String getValue() {
        return expression;
    }
}
