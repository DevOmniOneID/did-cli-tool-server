package org.omnione.did.wallet.zkp.revoc.data.dto;

import com.google.gson.annotations.SerializedName;

public class ProveUnrevealedAttribute {
//    @SerializedName("referent_key")
    @SerializedName("referentKey")
    private String referentKey;

    public ProveUnrevealedAttribute(Builder builder) {

        this.referentKey = builder.referentKey;
    }

    public String getReferentKey() {
        return referentKey;
    }

    public static class Builder {
        private String referentKey;

        public Builder() {}

        public Builder setReferentKey(String referentKey) {
            this.referentKey = referentKey;
            return this;
        }

        public ProveUnrevealedAttribute build() {
            return new ProveUnrevealedAttribute(this);
        }


    }
}
