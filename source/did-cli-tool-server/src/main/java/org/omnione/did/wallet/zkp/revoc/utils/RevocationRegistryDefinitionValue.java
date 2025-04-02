package org.omnione.did.wallet.zkp.revoc.utils;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.revoc.enums.IssuanceType;

public class RevocationRegistryDefinitionValue {

    @SerializedName("issuance_type")
    private IssuanceType issuanceType;
    private int maxCredNum;
    private RevocationRegistryDefinitionValuePublicKeys publicKeys;
    private String tailsHash;
    private String tailsLocation;

    public RevocationRegistryDefinitionValue(int max_cred_num,
                                             IssuanceType issuanceByDefault,
                                             RevocationRegistryDefinitionValuePublicKeys rev_keys_pub,
                                             String path,
                                             String hash) {
        this.maxCredNum = max_cred_num;
        this.issuanceType = issuanceByDefault;
        this.publicKeys = rev_keys_pub;
        this.tailsHash = hash;
        this.tailsLocation = path;
    }

    public IssuanceType getIssuanceType() {
        return issuanceType;
    }

    public void setIssuanceType(IssuanceType issuanceType) {
        this.issuanceType = issuanceType;
    }

    public int getMaxCredNum() {
        return maxCredNum;
    }

    public void setMaxCredNum(int maxCredNum) {
        this.maxCredNum = maxCredNum;
    }

    public RevocationRegistryDefinitionValuePublicKeys getPublicKeys() {
        return publicKeys;
    }

    public void setPublicKeys(RevocationRegistryDefinitionValuePublicKeys publicKeys) {
        this.publicKeys = publicKeys;
    }

    public String getTailsHash() {
        return tailsHash;
    }

    public void setTailsHash(String tailsHash) {
        this.tailsHash = tailsHash;
    }

    public String getTailsLocation() {
        return tailsLocation;
    }

    public void setTailsLocation(String tailsLocation) {
        this.tailsLocation = tailsLocation;
    }
}
