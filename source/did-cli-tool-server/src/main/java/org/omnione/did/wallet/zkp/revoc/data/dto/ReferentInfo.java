package org.omnione.did.wallet.zkp.revoc.data.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerMapSerializer;

import java.math.BigInteger;
import java.util.*;


public class ReferentInfo {

    @SerializedName("referents")
    private HashMap<String, Referent> referents = new LinkedHashMap<String, Referent>();

    @JsonAdapter(BigIntegerMapSerializer.class)
    private LinkedHashMap<String, BigInteger> r;
    public ReferentInfo() {

    }

    public ReferentInfo(String credentialId, Referent referent) {
        referents.put(credentialId, referent);
    }

    public void addReferent(String credentialId, Referent referent) {
        referents.put(credentialId, referent);
    }

    public HashMap<String, Referent> getReferents() {
        return referents;
    }

    public void setReferents(HashMap<String, Referent> referents) {
        this.referents = referents;
    }

    public static void main(String args[]) {

//        LinkedHashMap<String, ReferentAttributeValue> attr = new LinkedHashMap<String, ReferentAttributeValue>();
//        attr.put("Name", new ReferentAttributeValue("english", "attribute_referent_2", true));
//        attr.put("Address", new ReferentAttributeValue("Seoul", "attribute_referent_3", false));
//        attr.put("Age", new ReferentAttributeValue("18", "attribute_referent_1", false));
//        attr.put("Grade", new ReferentAttributeValue("4", "attribute_referent_2", false));
//
//        Referent ref = new Referent.Builder()
//                .setSchemaId("2oTF7LvWLUft1UC6qVkTCg:2:DriverLicense:1.0")
//                .setCredDefId("2oTF7LvWLUft1UC6qVkTCg:3:CL:2oTF7LvWLUft1UC6qVkTCg:2:DriverLicense:1.0:TAG1")
//                .setRevRegDefId("2oTF7LvWLUft1UC6qVkTCg:4:2oTF7LvWLUft1UC6qVkTCg:3:CL:2oTF7LvWLUft1UC6qVkTCg:2:DriverLicense:1.0:TAG1:CL_ACCUM:TAG2")
//                .setRevId("1")
//                .setAttributes(attr)
//                .build();
//
//        ReferentInfo referentInfo = new ReferentInfo("11111111-1111-1111-1111-11111111", ref);
//
//        System.out.println(ZkpGsonWrapper.getGsonPrettyPrinting().toJson(referentInfo));
    }
}


