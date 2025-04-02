package org.omnione.did.wallet.key;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;

import java.util.List;

public class ItemSpservice extends IWObject {
    @SerializedName("service_code")
    @Expose
    private String service_code;

    @SerializedName("service_name")
    @Expose
    private String service_name;

    @SerializedName("service_desc")
    @Expose
    private String service_desc;

    @SerializedName("sp_did")
    @Expose
    private String sp_did;

    @SerializedName("sp_did_hash")
    @Expose
    private String sp_did_hash;

    @SerializedName("req_claims")
    @Expose
    private List<String> req_claims;

    @SerializedName("allowed_vctype_codes")
    @Expose
    private List<String> allowed_vctype_codes;

    @SerializedName("allowed_issuer_dids")
    @Expose
    private List<String> allowed_issuer_dids;

    public String getService_code() {
        return service_code;
    }

    public void setService_code(String service_code) {
        this.service_code = service_code;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_desc() {
        return service_desc;
    }

    public void setService_desc(String service_desc) {
        this.service_desc = service_desc;
    }

    public String getSp_did() {
        return sp_did;
    }

    public void setSp_did(String sp_did) {
        this.sp_did = sp_did;
    }

    public String getSp_did_hash() {
        return sp_did_hash;
    }

    public void setSp_did_hash(String sp_did_hash) {
        this.sp_did_hash = sp_did_hash;
    }

    public List<String> getReq_claims() {
        return req_claims;
    }

    public void setReq_claims(List<String> req_claims) {
        this.req_claims = req_claims;
    }

    public List<String> getAllowed_vctype_codes() {
        return allowed_vctype_codes;
    }

    public void setAllowed_vctype_codes(List<String> allowed_vctype_codes) {
        this.allowed_vctype_codes = allowed_vctype_codes;
    }

    public List<String> getAllowed_issuer_dids() {
        return allowed_issuer_dids;
    }

    public void setAllowed_issuer_dids(List<String> allowed_issuer_dids) {
        this.allowed_issuer_dids = allowed_issuer_dids;
    }

    @Override
    public void fromJson(String val) {

    }
}
