package org.omnione.did.wallet.zkp.revoc.data.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PredicateReferent {
    private String name;

    @SerializedName("check_revealed")
    private boolean checkRevealed;

    @SerializedName("referent")
    private List<PredicateSubReferent> predicateSubReferent;

    public PredicateReferent(Builder builder) {
        name = builder.name;
        checkRevealed = builder.checkRevealed;
        predicateSubReferent = builder.predicateSubReferent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCheckRevealed() {
        return checkRevealed;
    }

    public void setCheckRevealed(boolean checkRevealed) {
        this.checkRevealed = checkRevealed;
    }

    public List<PredicateSubReferent> getPredicateSubReferent() {
        return predicateSubReferent;
    }

    public void setAttrSubReferent(List<PredicateSubReferent> predicateSubReferent) {
        this.predicateSubReferent = predicateSubReferent;
    }

    public static class Builder {

        private String name;
        private boolean checkRevealed;
        private List<PredicateSubReferent> predicateSubReferent;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setCheckRevealed(boolean checkRevealed) {
            this.checkRevealed = checkRevealed;
            return this;
        }

        public Builder setPredicateReferent(List<PredicateSubReferent> predicateSubReferent) {
            this.predicateSubReferent = predicateSubReferent;
            return this;
        }

        public PredicateReferent build() {
            return new PredicateReferent(this);
        }

    }
}
