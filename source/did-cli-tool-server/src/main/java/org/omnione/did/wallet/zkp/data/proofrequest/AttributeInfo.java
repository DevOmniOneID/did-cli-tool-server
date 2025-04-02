package org.omnione.did.wallet.zkp.data.proofrequest;

import org.omnione.did.wallet.zkp.revoc.utils.NonRevocedInterval;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttributeInfo {

    private String name;

    // schema_id, issuer_did, cred_def_id,
    private List<Map<String, String>> restrictions = new ArrayList<Map<String, String>>();

    private NonRevocedInterval nonRevoked;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<Map<String, String>> getRestrictions() {
        return restrictions;
    }
    public void setRestrictions(List<Map<String, String>> restrictions) {
        this.restrictions = restrictions;
    }

    //util
    public void addRestriction(Map<String, String> restriction) {
        this.restrictions.add(restriction);
    }

    public NonRevocedInterval getNonRevoked() {
        return nonRevoked;
    }

    public void setNonRevoked(NonRevocedInterval nonRevoked) {
        this.nonRevoked = nonRevoked;
    }
}
