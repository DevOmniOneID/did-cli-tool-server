package org.omnione.did.wallet.zkp.data.proofrequest;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.enums.PredicateType;
import org.omnione.did.wallet.zkp.revoc.utils.NonRevocedInterval;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PredicateInfo {
    private String name;

//    @SerializedName("p_type")
    private PredicateType pType;

//    @SerializedName("p_value")
    private int pValue;
    private List<Map<String, String>> restrictions = new ArrayList<Map<String, String>>();

    private NonRevocedInterval nonRevoked;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public PredicateType getPType() {
        return pType;
    }
    public void setPType(PredicateType pType) {
        this.pType = pType;
    }

    public int getPValue() {
        return pValue;
    }
    public void setPValue(int pValue) {
        this.pValue = pValue;
    }

    public List<Map<String, String>> getRestrictions() {
        return restrictions;
    }
    public void setRestrictions(List<Map<String, String>> restrictions) {
        this.restrictions = restrictions;
    }

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
