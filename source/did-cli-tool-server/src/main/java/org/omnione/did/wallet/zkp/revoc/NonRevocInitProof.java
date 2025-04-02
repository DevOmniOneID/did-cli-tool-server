package org.omnione.did.wallet.zkp.revoc;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.data.NonRevocProofCList;
import org.omnione.did.wallet.zkp.revoc.data.NonRevocProofTauList;
import org.omnione.did.wallet.zkp.revoc.data.NonRevocProofXList;

import java.util.Vector;

public class NonRevocInitProof {

//    @SerializedName("c_list_params")
    private NonRevocProofXList cListParams;
//    @SerializedName("tau_list_params")
    private NonRevocProofXList tauListParams;
//    @SerializedName("c_list")
    private NonRevocProofCList cList;
//    @SerializedName("tau_list")
    private NonRevocProofTauList tauList;

    public NonRevocInitProof(NonRevocProofXList cListParams, NonRevocProofXList tauListParams,
                             NonRevocProofCList cList, NonRevocProofTauList tauList) {
        this.cListParams = cListParams;
        this.tauListParams = tauListParams;
        this.cList = cList;
        this.tauList = tauList;
    }

    public Vector<byte[]> asCList() throws ZkpException {
        return cList.asList();
    }

    public Vector<byte[]> asTauList() throws ZkpException {
        return this.tauList.asSlice();
    }

    public NonRevocProofXList getcListParams() {
        return cListParams;
    }

    public void setcListParams(NonRevocProofXList cListParams) {
        this.cListParams = cListParams;
    }

    public NonRevocProofXList getTauListParams() {
        return tauListParams;
    }

    public void setTauListParams(NonRevocProofXList tauListParams) {
        this.tauListParams = tauListParams;
    }

    public NonRevocProofCList getcList() {
        return this.cList;
    }

    public void setcList(NonRevocProofCList cList) {
        this.cList = cList;
    }

    public NonRevocProofTauList getTauList() {
        return this.tauList;
    }

    public void setTauList(NonRevocProofTauList tauList) {
        this.tauList = tauList;
    }
}
