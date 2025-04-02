package org.omnione.did.wallet.zkp.revoc.data.dto;

import com.google.gson.annotations.SerializedName;

public class ProveRevealedAttribute {
//    @SerializedName("attr_name")
    @SerializedName("attrName")
    private String attributeName;

//    @SerializedName("referent_key")
    @SerializedName("referentKey")
    private String referentKey;

    public ProveRevealedAttribute(Builder builder) {
        attributeName = builder.attributeName;
        referentKey = builder.referentKey;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getReferentKey() {
        return referentKey;
    }

    public static class Builder {
        private String attributeName;
        private String referentKey;

        public Builder setAttributeName(String attributeName) {
            this.attributeName = attributeName;
            return this;
        }

        public Builder setReferentKey(String referentKey) {
            this.referentKey = referentKey;
            return this;
        }

        public ProveRevealedAttribute build() {
            return new ProveRevealedAttribute(this);
        }
    }
}
