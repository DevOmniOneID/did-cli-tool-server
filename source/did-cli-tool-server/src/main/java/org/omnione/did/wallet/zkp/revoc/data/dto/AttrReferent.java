package org.omnione.did.wallet.zkp.revoc.data.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AttrReferent {

    private String name;

//    @SerializedName("check_revealed")
    private boolean checkRevealed;

    @SerializedName("referent")
    private List<AttrSubReferent> attrSubReferent;

    public AttrReferent(AttrReferent.Builder builder) {
        name = builder.name;
        checkRevealed = builder.checkRevealed;
        attrSubReferent = builder.attrSubReferent;
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

    public List<AttrSubReferent> getAttrSubReferent() {
        return attrSubReferent;
    }

    public void setAttrSubReferent(List<AttrSubReferent> attrSubReferent) {
        this.attrSubReferent = attrSubReferent;
    }

    public static class Builder {

        private String name;
        private boolean checkRevealed;
        private List<AttrSubReferent> attrSubReferent;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setCheckRevealed(boolean checkRevealed) {
            this.checkRevealed = checkRevealed;
            return this;
        }

        public Builder setAttrSubReferent(List<AttrSubReferent> attrSubReferent) {
            this.attrSubReferent = attrSubReferent;
            return this;
        }
        public AttrReferent build() {
            return new AttrReferent(this);
        }

    }


}
