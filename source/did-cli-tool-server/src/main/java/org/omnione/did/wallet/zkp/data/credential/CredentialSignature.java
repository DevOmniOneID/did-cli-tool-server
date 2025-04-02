package org.omnione.did.wallet.zkp.data.credential;

import com.google.gson.annotations.SerializedName;

public class CredentialSignature {

//    @SerializedName("p_credential")
    @SerializedName("pCredential")
    private PrimaryCredentialSignature primaryCredential;
//    @SerializedName("r_credential")
    @SerializedName("rCredential")
    private NonRevocationCredentialSignature nonRevocationCredential;


    public PrimaryCredentialSignature getPrimaryCredential() {
        return primaryCredential;
    }
    public void setPrimaryCredential(PrimaryCredentialSignature primaryCredential) {
        this.primaryCredential = primaryCredential;
    }

    public NonRevocationCredentialSignature getNonRevocationCredential() {
        return nonRevocationCredential;
    }
    public void setNonRevocationCredential(NonRevocationCredentialSignature nonRevocationCredential) {
        this.nonRevocationCredential = nonRevocationCredential;
    }
}
