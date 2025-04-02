package org.omnione.did.wallet.zkp.revoc.data.dto;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.data.Credential;
import org.omnione.did.wallet.zkp.util.gson.ZkpGsonWrapper;

import java.util.*;

public class CredentialInfo {

    private String credentialId;

    @SerializedName("credential")
    private Credential credential;

    public CredentialInfo() {}

    public CredentialInfo(String credentialId, Credential credential) {
        this.credentialId = credentialId;
        this.credential = credential;
    }

    public Credential getCredential() {
        return credential;
    }

    public String getCredentialId() {
        return credentialId;
    }


//    public CredentialInfo(Map<String, Credential>map) {
//        this.credentialMap = map;
//    }
//
//    public CredentialInfo(String credentialId, Credential credential) {
//
//        credentialMap.put(credentialId, credential);
//    }
//
//    public Map<String, Credential> getCredentialMap() {
//        return credentialMap;
//    }
//
//    public void addCredentialMap(String key, Credential value) {
//        credentialMap.put(key, value);
//    }
//
//    public void setCredentialMap(Map<String, Credential> credentials) {
//        this.credentialMap = credentials;
//    }

    public void fromJson(String val) {
        GsonWrapper gson = new GsonWrapper();
        CredentialInfo result = gson.fromJson(val, CredentialInfo.class);
        this.credentialId = result.credentialId;
        this.credential = result.credential;
//        this.credentialMap = result.getCredentialMap();
    }

    public String toJson() {
        GsonWrapper gson = new GsonWrapper();
        return gson.toJson(this);
    }

//    public static void main(String args[]) {
//        CredentialInfo credInfo = new CredentialInfo();
//
//        HashMap<String, Credential> credentials = new HashMap<String, Credential>();
//        credentials.put("credId_1", new Credential());
//        credentials.put("credId_2", new Credential());
//        credInfo.setCredentialMap(credentials);
//        System.out.println(ZkpGsonWrapper.getGsonPrettyPrinting().toJson(credInfo));
//    }
}
