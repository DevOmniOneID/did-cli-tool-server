package org.omnione.did.wallet.zkp.data.subproof;

import com.google.gson.annotations.JsonAdapter;
import org.omnione.did.wallet.zkp.data.Predicate;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerMapSerializer;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.math.BigInteger;
import java.util.Map;

public class PrimaryPredicateInequalityProof {

    @JsonAdapter(BigIntegerMapSerializer.class)
    private Map<String, BigInteger> u;

    @JsonAdapter(BigIntegerMapSerializer.class)
    private Map<String,BigInteger> r;

    @JsonAdapter(BigIntegerMapSerializer.class)
    private Map<String,BigInteger> t;

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger mj;

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger alpha;

    private Predicate predicate;


    public PrimaryPredicateInequalityProof() {}
    public PrimaryPredicateInequalityProof(Map<String, BigInteger> u, Map<String, BigInteger> r, Map<String, BigInteger> t,
                                           BigInteger mj, BigInteger alpha, Predicate predicate) {
        this.u = u;
        this.r = r;
        this.mj = mj;
        this.alpha = alpha;
        this.t = t;
        this.predicate = predicate;
    }


    public Map<String, BigInteger> getU() {
        return u;
    }
    public void setU(Map<String, BigInteger> u) {
        this.u = u;
    }

    public Map<String, BigInteger> getR() {
        return r;
    }
    public void setR(Map<String, BigInteger> r) {
        this.r = r;
    }

    public BigInteger getMj() {
        return mj;
    }
    public void setMj(BigInteger mj) {
        this.mj = mj;
    }

    public BigInteger getAlpha() {
        return alpha;
    }
    public void setAlpha(BigInteger alpha) {
        this.alpha = alpha;
    }

    public Map<String, BigInteger> getT() {
        return t;
    }
    public void setT(Map<String, BigInteger> t) {
        this.t = t;
    }

    public Predicate getPredicate() {
        return predicate;
    }
    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }
}
