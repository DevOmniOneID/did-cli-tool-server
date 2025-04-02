package org.omnione.did.wallet.zkp.data;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.revoc.data.NonRevocProofCList;
import org.omnione.did.wallet.zkp.revoc.data.NonRevocProofXList;

public class NonRevocProof {
//    @SerializedName("x_list")
    private NonRevocProofXList xList;

//    @SerializedName("c_list")
    private NonRevocProofCList cList;

    public NonRevocProof(NonRevocProofXList x_list, NonRevocProofCList c_list) {
        this.xList = x_list;
        this.cList = c_list;
    }

    public NonRevocProofCList getCList() {
        return cList;
    }

    public NonRevocProofXList getXList() {
        return xList;
    }
}
