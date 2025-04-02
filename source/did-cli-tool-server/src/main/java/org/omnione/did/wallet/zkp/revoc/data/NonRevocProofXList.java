package org.omnione.did.wallet.zkp.revoc.data;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpSerializer;

import java.util.Vector;

public class NonRevocProofXList {
    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement rho;
    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement r;
//    @SerializedName("r_prime")
    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement rPrime;
    @JsonAdapter(ZkpSerializer.class)
//    @SerializedName("r_prime_prime")
    private GroupOrderElement rPrimePrime;
    @JsonAdapter(ZkpSerializer.class)
//    @SerializedName("r_prime_prime_prime")
    private GroupOrderElement rPrimePrimePrime;
    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement o;
    @JsonAdapter(ZkpSerializer.class)
//    @SerializedName("o_prime")
    private GroupOrderElement oPrime;
    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement m;
    @JsonAdapter(ZkpSerializer.class)
//    @SerializedName("m_prime")
    private GroupOrderElement mPrime;
    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement t;
    @JsonAdapter(ZkpSerializer.class)
//    @SerializedName("t_prime")
    private GroupOrderElement tPrime;
    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement m2;
    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement s;
    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement c;

    public NonRevocProofXList(GroupOrderElement rho,
                              GroupOrderElement r,
                              GroupOrderElement rPrime,
                              GroupOrderElement rPrimePrime,
                              GroupOrderElement rPrimePrimePrime,
                              GroupOrderElement o,
                              GroupOrderElement oPrime,
                              GroupOrderElement m,
                              GroupOrderElement mPrime,
                              GroupOrderElement t,
                              GroupOrderElement tPrime,
                              GroupOrderElement m2,
                              GroupOrderElement vrPrimePrime,
                              GroupOrderElement c) {

//        System.out.println("====================================================================================================");
//        System.out.println("NonRevocProofXList build");
//        System.out.println("====================================================================================================");
//        AMCLUtils.toHashString(rho, "rho");
//        AMCLUtils.toHashString(r, "r");
//        AMCLUtils.toHashString(rPrime, "rPrime");
//        AMCLUtils.toHashString(rPrimePrime, "rPrimePrime");
//        AMCLUtils.toHashString(rPrimePrimePrime, "rPrimePrimePrime");
//        AMCLUtils.toHashString(o, "o");
//        AMCLUtils.toHashString(oPrime, "oPrime");
//        AMCLUtils.toHashString(m, "m");
//        AMCLUtils.toHashString(mPrime, "mPrime");
//        AMCLUtils.toHashString(t, "t");
//        AMCLUtils.toHashString(tPrime, "tPrime");
//        AMCLUtils.toHashString(m2, "m2");
//        AMCLUtils.toHashString(vrPrimePrime, "s");
//        AMCLUtils.toHashString(c, "c");
        this.rho = rho;
        this.r = r;
        this.rPrime = rPrime;
        this.rPrimePrime = rPrimePrime;
        this.rPrimePrimePrime = rPrimePrimePrime;
        this.o = o;
        this.oPrime = oPrime;
        this.m = m;
        this.mPrime = mPrime;
        this.t = t;
        this.tPrime = tPrime;
        this.m2 = m2;
        this.s = vrPrimePrime;
        this.c = c;

    }

    public Vector<GroupOrderElement> asList() {

        Vector<GroupOrderElement> vec = new Vector<GroupOrderElement>();
        vec.add(getRho());
        vec.add(getO());
        vec.add(getC());
        vec.add(getoPrime());
        vec.add(getM());
        vec.add(getmPrime());
        vec.add(getT());
        vec.add(gettPrime());
        vec.add(getM2());
        vec.add(getS());
        vec.add(getR());
        vec.add(getrPrime());
        vec.add(getrPrimePrime());
        vec.add(getrPrimePrimePrime());
        return vec;
    }

    static public NonRevocProofXList fromList(Vector<GroupOrderElement> seq) {

        return new NonRevocProofXList(
                seq.get(0),
                seq.get(10),
                seq.get(11),
                seq.get(12),
                seq.get(13),
                seq.get(1),
                seq.get(3),
                seq.get(4),
                seq.get(5),
                seq.get(6),
                seq.get(7),
                seq.get(8),
                seq.get(9),
                seq.get(2));
    }

    public GroupOrderElement getRho() {
        return rho;
    }

    public void setRho(GroupOrderElement rho) {
        this.rho = rho;
    }

    public GroupOrderElement getR() {
        return r;
    }

    public void setR(GroupOrderElement r) {
        this.r = r;
    }

    public GroupOrderElement getrPrime() {
        return rPrime;
    }

    public void setrPrime(GroupOrderElement rPrime) {
        this.rPrime = rPrime;
    }

    public GroupOrderElement getrPrimePrime() {
        return rPrimePrime;
    }

    public void setrPrimePrime(GroupOrderElement rPrimePrime) {
        this.rPrimePrime = rPrimePrime;
    }

    public GroupOrderElement getrPrimePrimePrime() {
        return rPrimePrimePrime;
    }

    public void setrPrimePrimePrime(GroupOrderElement rPrimePrimePrime) {
        this.rPrimePrimePrime = rPrimePrimePrime;
    }

    public GroupOrderElement getO() {
        return o;
    }

    public void setO(GroupOrderElement o) {
        this.o = o;
    }

    public GroupOrderElement getoPrime() {
        return oPrime;
    }

    public void setoPrime(GroupOrderElement oPrime) {
        this.oPrime = oPrime;
    }

    public GroupOrderElement getM() {
        return m;
    }

    public void setM(GroupOrderElement m) {
        this.m = m;
    }

    public GroupOrderElement getmPrime() {
        return mPrime;
    }

    public void setmPrime(GroupOrderElement mPrime) {
        this.mPrime = mPrime;
    }

    public GroupOrderElement getT() {
        return t;
    }

    public void setT(GroupOrderElement t) {
        this.t = t;
    }

    public GroupOrderElement gettPrime() {
        return tPrime;
    }

    public void settPrime(GroupOrderElement tPrime) {
        this.tPrime = tPrime;
    }

    public GroupOrderElement getM2() {
        return m2;
    }

    public void setM2(GroupOrderElement m2) {
        this.m2 = m2;
    }

    public GroupOrderElement getC() {
        return c;
    }

    public void setC(GroupOrderElement c) {
        this.c = c;
    }

    public GroupOrderElement getS() {
        return s;
    }

    public void setS(GroupOrderElement s) {
        this.s = s;
    }
}
