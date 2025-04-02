package org.omnione.did.wallet.zkp.revoc.data.dto;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.data.Predicate;
import org.omnione.did.wallet.zkp.enums.PredicateType;

public class ProvePredicate extends Predicate {

//    @SerializedName("referent_key")
    @SerializedName("referentKey")
    private String referentKey;

    public ProvePredicate(Builder builder) {
        super.setPType(builder.pType);
        super.setPValue(builder.pValue);
        super.setAttrName(builder.attrName);
        this.referentKey = builder.referentKey;
    }

    public String getReferentKey() {
        return referentKey;
    }

    public static class Builder {
        private String referentKey;
        private PredicateType pType;
        private int pValue;
        private String attrName;

        public Builder() {}

        public Builder setReferentKey(String referentKey) {
            this.referentKey = referentKey;
            return this;
        }

        public Builder setPType(PredicateType pType) {
            this.pType = pType;
            return this;
        }

        public Builder setPValue(int pValue) {
            this.pValue = pValue;
            return this;
        }

        public Builder setAttributeName(String attrName) {
            this.attrName = attrName;
            return this;
        }

        public ProvePredicate build() {
            return new ProvePredicate(this);
        }


    }
}
