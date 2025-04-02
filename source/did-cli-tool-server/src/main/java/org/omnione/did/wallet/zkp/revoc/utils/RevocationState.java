package org.omnione.did.wallet.zkp.revoc.utils;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.revoc.RevocationRegistry;

// witness *
public class RevocationState {

    private Witness witness;
    @SerializedName("rev_reg")

    private RevocationRegistry revReg;

    private String timestamp;

    @SerializedName("rev_state_id")
    private String revStateId;
    @SerializedName("rev_reg_id")
    private String revRegId;

    public RevocationState(Witness witness, RevocationRegistry revReg, String timestamp) {

        this.witness = witness;
        this.revReg = revReg;
        this.timestamp = timestamp;
//        System.out.println("====================================================================================================");
//        System.out.println("RevocationState build");
//        System.out.println("====================================================================================================");
//        AMCLUtils.toHashString(witness.getOmega(), "omega");
//        AMCLUtils.toHashString(revReg.getAccum(), "accum");
    }

    public RevocationState(Witness witness, RevocationRegistry revReg, String revStateId, String regRegDefId, String timestamp) {

        this.witness = witness;
        this.revReg = revReg;
        this.timestamp = timestamp;
        this.revStateId = revStateId;
        this.revRegId = regRegDefId;
//        System.out.println("====================================================================================================");
//        System.out.println("RevocationState build");
//        System.out.println("====================================================================================================");
//        AMCLUtils.toHashString(witness.getOmega(), "omega");
//        AMCLUtils.toHashString(revReg.getAccum(), "accum");
    }

    public Witness getWitness() {
        return witness;
    }

    public void setWitness(Witness witness) {
        this.witness = witness;
    }

    public RevocationRegistry getRevReg() {
        return revReg;
    }

    public void setRevReg(RevocationRegistry revReg) {
        this.revReg = revReg;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timetamp) {
        this.timestamp = timetamp;
    }

    public String getRevStateId() {
        return revStateId;
    }

    public void setRevStateId(String revStateId) {
        this.revStateId = revStateId;
    }

    public String getRevRegId() {
        return revRegId;
    }

    public void setRevRegId(String revRegId) {
        this.revRegId = revRegId;
    }
}
