package org.omnione.did.wallet.zkp.revoc.data;

import com.google.gson.annotations.JsonAdapter;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpSerializer;

import java.util.Vector;

public class NonRevocProofTauList {
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 t1;
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 t2;
    @JsonAdapter(ZkpSerializer.class)
    private Pair t3;
    @JsonAdapter(ZkpSerializer.class)
    private Pair t4;
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 t5;
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 t6;
    @JsonAdapter(ZkpSerializer.class)
    private Pair t7;
    @JsonAdapter(ZkpSerializer.class)
    private Pair t8;

    public NonRevocProofTauList(PointG1 t1, PointG1 t2, Pair t3, Pair t4, PointG1 t5, PointG1 t6, Pair t7, Pair t8) {

        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
        this.t6 = t6;
        this.t7 = t7;
        this.t8 = t8;

//        System.out.println("====================================================================================================");
//        System.out.println("NonRevocProofTauList build");
//        System.out.println("====================================================================================================");
//        AMCLUtils.toHashString(t1, "t1");
//        AMCLUtils.toHashString(t2, "t2");
//        AMCLUtils.toHashString(this.t3.getPair(), "t3");
//        AMCLUtils.toHashString(this.t4.getPair(), "t4");
//        AMCLUtils.toHashString(t5, "t5");
//        AMCLUtils.toHashString(t6, "t6");
//        AMCLUtils.toHashString(this.t7.getPair(), "t7");
//        AMCLUtils.toHashString(this.t8.getPair(), "t8");
    }

    public Vector<byte[]> asSlice() throws ZkpException {

        Vector<byte[]> vec = new Vector<byte[]>();
        vec.add(getT1().toBytes());
        vec.add(getT2().toBytes());
        vec.add(getT3().toBytes());
        vec.add(getT4().toBytes());
        vec.add(getT5().toBytes());
        vec.add(getT6().toBytes());
        vec.add(getT7().toBytes());
        vec.add(getT8().toBytes());
        return vec;
    }

    public PointG1 getT1() {
        return t1;
    }

    public PointG1 getT2() {
        return t2;
    }

    public Pair getT3() {
        return t3;
    }

    public Pair getT4() {
        return t4;
    }

    public PointG1 getT5() {
        return t5;
    }

    public PointG1 getT6() {
        return t6;
    }

    public Pair getT7() {
        return t7;
    }

    public Pair getT8() {
        return t8;
    }


}
