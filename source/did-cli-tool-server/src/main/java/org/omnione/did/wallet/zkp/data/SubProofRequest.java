package org.omnione.did.wallet.zkp.data;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.enums.PredicateType;

import java.util.HashSet;
import java.util.TreeSet;

public class SubProofRequest {

//    @SerializedName("revealed_attrs")
    private TreeSet<String> revealedAttrs;

//    @SerializedName("predicates")
    private HashSet<Predicate> predicates;

    public SubProofRequest() {
        this.revealedAttrs = new TreeSet<String>();
        this.predicates = new HashSet<Predicate>();
    }

    public HashSet<Predicate> getPredicates() {
        return predicates;
    }

    public TreeSet<String> getRevealedAttrs() {
        return revealedAttrs;
    }

    public SubProofRequest addRevealedAttribute(String attr) {
        this.revealedAttrs.add(attr);
        return this;
    }

    @Deprecated
    public SubProofRequest addPredicate(String attrName, String pType, int value) {
        //TODO: valueOf에 대한 예외처리가 필요하다
        PredicateType type = PredicateType.valueOf(pType);
        predicates.add(new Predicate(attrName, type, value));
        return this;
    }
    public SubProofRequest addPredicate(String attrName, PredicateType pType, int value) {
        predicates.add(new Predicate(attrName, pType, value));
        return this;
    }

    public String toJson() {
        return GsonWrapper.getGsonPrettyPrinting().toJson(this);
    }
}
