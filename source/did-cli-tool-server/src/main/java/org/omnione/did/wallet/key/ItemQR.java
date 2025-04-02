package org.omnione.did.wallet.key;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;

public class ItemQR extends IWObject {
    @SerializedName("sp_did")
    @Expose
    private String sp_did;

    @SerializedName("service_code")
    @Expose
    private String service_code;

    @SerializedName("encrypt_type")
    @Expose
    private int encrypt_type;

    @SerializedName("nonce")
    @Expose
    private String nonce;

    @SerializedName("callback_url")
    @Expose
    private String callback_url;

    public String getSp_did() {
        return sp_did;
    }

    public void setSp_did(String sp_did) {
        this.sp_did = sp_did;
    }

    public String getService_code() {
        return service_code;
    }

    public void setService_code(String service_code) {
        this.service_code = service_code;
    }

    public int getEncryptType() {
        return encrypt_type;
    }

    public void setEncryptType(int encryptType) {
        this.encrypt_type = encryptType;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getCallBackUrl() {
        return callback_url;
    }

    public void setCallBackUrl(String callback_url) {
        this.callback_url = callback_url;
    }

    @Override
    public void fromJson(String val) {

    }
}
