package org.omnione.did.wallet.zkp.revoc.data;

import com.google.gson.annotations.JsonAdapter;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpSerializer;

import java.util.Vector;

public class NonRevocProofCList {
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 e;
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 d;
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 a;
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 g;
    @JsonAdapter(ZkpSerializer.class)
    private PointG2 w;
    @JsonAdapter(ZkpSerializer.class)
    private PointG2 s;
    @JsonAdapter(ZkpSerializer.class)
    private PointG2 u;

    public NonRevocProofCList(PointG1 e, PointG1 d, PointG1 a, PointG1 g, PointG2 w, PointG2 s, PointG2 u) {

//        System.out.println("====================================================================================================");
//        System.out.println("NonRevocProofCList build");
//        System.out.println("====================================================================================================");
//        AMCLUtils.toHashString(e, "e");
//        AMCLUtils.toHashString(d, "d");
//        AMCLUtils.toHashString(a, "a");
//        AMCLUtils.toHashString(g, "g");
//        AMCLUtils.toHashString(w, "w");
//        AMCLUtils.toHashString(s, "s");
//        AMCLUtils.toHashString(u, "u");
        this.e = e;
        this.d = d;
        this.a = a;
        this.g = g;
        this.w = w;
        this.s = s;
        this.u = u;
    }

    public Vector<byte[]> asList() throws ZkpException {

        Vector<byte[]> vec = new Vector<byte[]>();
        vec.add(e.toBytes());
        vec.add(d.toBytes());
        vec.add(a.toBytes());
        vec.add(g.toBytes());
        vec.add(w.toBytes());
        vec.add(s.toBytes());
        vec.add(u.toBytes());
        return vec;
    }

    public PointG1 getE() {
        return e;
    }

    public void setE(PointG1 e) {
        this.e = e;
    }

    public PointG1 getD() {
        return d;
    }

    public void setD(PointG1 d) {
        this.d = d;
    }

    public PointG1 getA() {
        return a;
    }

    public void setA(PointG1 a) {
        this.a = a;
    }

    public PointG1 getG() {
        return g;
    }

    public void setG(PointG1 g) {
        this.g = g;
    }

    public PointG2 getW() {
        return w;
    }

    public void setW(PointG2 w) {
        this.w = w;
    }

    public PointG2 getS() {
        return s;
    }

    public void setS(PointG2 s) {
        this.s = s;
    }

    public PointG2 getU() {
        return u;
    }

    public void setU(PointG2 u) {
        this.u = u;
    }
}
