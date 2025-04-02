package org.omnione.did.wallet.zkp.data.proof;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.util.gson.ZkpGsonWrapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestedProof {

//    @SerializedName("self_attested_attrs")
    private Map<String, String> selfAttestedAttrs = new LinkedHashMap<String, String>();

//    @SerializedName("predicates")
    private Map<String, Map<String, String>> predicates = new LinkedHashMap<String, Map<String, String>>();

//    @SerializedName("revealed_attrs")
    private Map<String, Map<String, String>> revealedAttrs = new LinkedHashMap<String, Map<String, String>>();

//    @SerializedName("unrevealed_attrs")
    private Map<String, Map<String, String>> unrevealedAttrs = new LinkedHashMap<String, Map<String, String>>();

    public Map<String, String> getSelfAttestedAttrs() {
        return selfAttestedAttrs;
    }

    public void addSelfAttestedAttrs(Map<String, String> selfAttestedAttrs) {
        this.selfAttestedAttrs.putAll(selfAttestedAttrs);
    }

    public Map<String, Map<String, String>> getPredicates() {
        return predicates;
    }

    public void addPredicates(Map<String, Map<String, String>> predicates) {
        this.predicates.putAll(predicates);
    }

    public Map<String, Map<String, String>> getRevealedAttrs() {
        return revealedAttrs;
    }

    public void addRevealedAttrs(Map<String, Map<String, String>> revealedAttrs) {
        this.revealedAttrs.putAll(revealedAttrs);
    }

    public Map<String, Map<String, String>> getUnrevealedAttrs() {
        return unrevealedAttrs;
    }

    public void addUnrevealedAttrs(Map<String, Map<String, String>> unrevealedAttrs) {
        this.unrevealedAttrs.putAll(unrevealedAttrs);
    }
}
