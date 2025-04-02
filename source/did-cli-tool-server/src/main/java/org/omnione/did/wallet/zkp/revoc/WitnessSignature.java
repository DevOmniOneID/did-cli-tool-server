package org.omnione.did.wallet.zkp.revoc;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.revoc.data.PointG1;
import org.omnione.did.wallet.zkp.revoc.data.PointG2;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpSerializer;

public class WitnessSignature {

//    @SerializedName("sigma_i")
    @JsonAdapter(ZkpSerializer.class)
    private PointG2 sigmaI;

//    @SerializedName("u_i")
    @JsonAdapter(ZkpSerializer.class)
    private PointG2 uI;

//    @SerializedName("g_i")
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 gI;

    public WitnessSignature(PointG2 sigma_i, PointG2 u_i, PointG1 g_i) {
        this.sigmaI = sigma_i;
        this.uI = u_i;
        this.gI = g_i;
    }

    public PointG2 getSigmaI() {
        return sigmaI;
    }

    public void setSigmaI(PointG2 sigma_i) {
        this.sigmaI = sigma_i;
    }

    public PointG2 getUI() {
        return uI;
    }

    public void setUI(PointG2 u_i) {
        this.uI = u_i;
    }

    public PointG1 getGI() {
        return gI;
    }

    public void setGI(PointG1 g_i) {
        this.gI = g_i;
    }
}
