package org.omnione.did.wallet.zkp.revoc.data.dto;

import com.google.gson.annotations.SerializedName;

public class ReferentAttributeValue {

//    @SerializedName("referent_key")
    @SerializedName("referentKey")
    private String referentKey;

//    @SerializedName("is_revealed")
    @SerializedName("isRevealed")
    private Boolean isRevealed;

    public ReferentAttributeValue(String referentKey, Boolean isRevealed) {
        this.referentKey = referentKey;
        this.isRevealed = isRevealed;
    }

    public String getReferentKey() {
        return referentKey;
    }

    public void setReferentKey(String referentKey) {
        this.referentKey = referentKey;
    }

    public Boolean getRevealed() {
        return isRevealed;
    }

    public void setRevealed(Boolean revealed) {
        isRevealed = revealed;
    }
}
