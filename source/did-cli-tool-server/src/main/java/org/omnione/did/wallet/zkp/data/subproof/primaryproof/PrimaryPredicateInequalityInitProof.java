package org.omnione.did.wallet.zkp.data.subproof.primaryproof;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.data.Predicate;

import java.math.BigInteger;
import java.util.Map;
import java.util.Vector;

public class PrimaryPredicateInequalityInitProof {

//    @SerializedName("c_list")
    private Vector<BigInteger> cList;

//    @SerializedName("tau_list")
    private Vector<BigInteger> tauList;
    private Map<String, BigInteger> u;

//    @SerializedName("u_tilde")
    private Map<String, BigInteger> uTilde;
    private Map<String, BigInteger> r;

//    @SerializedName("r_tilde")
    private Map<String, BigInteger> rTilde;

//    @SerializedName("alpha_tilde")
    private BigInteger alphaTilde;
    private Predicate predicate;
    private Map<String, BigInteger> t;

    public PrimaryPredicateInequalityInitProof(Vector<BigInteger> c_list, Vector<BigInteger> tau_list,
                                               Map<String, BigInteger> u, Map<String, BigInteger> u_tilde, Map<String, BigInteger> r,
                                               Map<String, BigInteger> r_tilde, BigInteger alpha_tilde, Predicate predicate,
                                               Map<String, BigInteger> t) {
        this.cList = c_list;
        this.tauList = tau_list;
        this.u = u;
        this.uTilde = u_tilde;
        this.r = r;
        this.rTilde = r_tilde;
        this.alphaTilde = alpha_tilde;
        this.predicate = predicate;
        this.t = t;
    }

    public Vector<BigInteger> getCommonValues(){
        return this.cList;
    }

    public Vector<BigInteger> getTValues(){
        return this.tauList;
    }

    public Map<String, BigInteger> getU() {
        return u;
    }

    public Map<String, BigInteger> getUTilde() {
        return uTilde;
    }

    public Map<String, BigInteger> getR() {
        return r;
    }

    public Map<String, BigInteger> getRTilde() {
        return rTilde;
    }

    public BigInteger getAlphaTilde() {
        return alphaTilde;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public Map<String, BigInteger> getT() {
        return t;
    }
}
