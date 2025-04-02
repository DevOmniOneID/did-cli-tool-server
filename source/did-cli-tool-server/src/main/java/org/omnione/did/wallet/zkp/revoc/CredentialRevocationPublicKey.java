package org.omnione.did.wallet.zkp.revoc;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.revoc.data.PointG1;
import org.omnione.did.wallet.zkp.revoc.data.PointG2;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpSerializer;

public class CredentialRevocationPublicKey {
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 g;
//    @SerializedName("g_dash")
    @JsonAdapter(ZkpSerializer.class)
    private PointG2 gDash;
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 h;
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 h0;
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 h1;
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 h2;
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 htilde;

//    @SerializedName("h_cap")
    @JsonAdapter(ZkpSerializer.class)
    private PointG2 hCap;
    @JsonAdapter(ZkpSerializer.class)
    private PointG2 u;
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 pk;
    @JsonAdapter(ZkpSerializer.class)
    private PointG2 y;

    public CredentialRevocationPublicKey(PointG1 g,
                                         PointG2 g_dash,
                                         PointG1 h,
                                         PointG1 h0,
                                         PointG1 h1,
                                         PointG1 h2,
                                         PointG1 htilde,
                                         PointG2 h_cap,
                                         PointG2 u,
                                         PointG1 pk,
                                         PointG2 y) {
        this.g = g;
        this.gDash = g_dash;
        this.h = h;
        this.h0 = h0;
        this.h1 = h1;
        this.h2 = h2;
        this.htilde = htilde;
        this.hCap = h_cap;
        this.u = u;
        this.pk = pk;
        this.y = y;

//        System.out.println("\n====================================================================================================");
//        System.out.println("CredentialRevocationPublicKey build");
//        System.out.println("====================================================================================================");
//
//        AMCLUtils.toHashString(g, "g");
//        AMCLUtils.toHashString(g_dash, "g_dash");
//        AMCLUtils.toHashString(h, "h");
//        AMCLUtils.toHashString(h0, "h0");
//        AMCLUtils.toHashString(h1, "h1");
//        AMCLUtils.toHashString(h2, "h2");
//        AMCLUtils.toHashString(htilde, "htilde");
//        AMCLUtils.toHashString(h_cap, "h_cap");
//        AMCLUtils.toHashString(u, "u");
//        AMCLUtils.toHashString(pk, "pk");
//        AMCLUtils.toHashString(y, "y");
    }

    public PointG1 getG() {
        return g;
    }

    public void setG(PointG1 g) {
        this.g = g;
    }

    public PointG2 getGDash() {
        return gDash;
    }

    public void setGDash(PointG2 g_dash) {
        this.gDash = g_dash;
    }

    public PointG1 getH() {
        return h;
    }

    public void setH(PointG1 h) {
        this.h = h;
    }

    public PointG1 getH0() {
        return h0;
    }

    public void setH0(PointG1 h0) {
        this.h0 = h0;
    }

    public PointG1 getH1() {
        return h1;
    }

    public void setH1(PointG1 h1) {
        this.h1 = h1;
    }

    public PointG1 getH2() {
        return h2;
    }

    public void setH2(PointG1 h2) {
        this.h2 = h2;
    }

    public PointG1 getHtilde() {
        return htilde;
    }

    public void setHtilde(PointG1 htilde) {
        this.htilde = htilde;
    }

    public PointG2 getHCap() {
        return hCap;
    }

    public void setHCap(PointG2 h_cap) {
        this.hCap = h_cap;
    }

    public PointG2 getU() {
        return u;
    }

    public void setU(PointG2 u) {
        this.u = u;
    }

    public PointG1 getPk() {
        return pk;
    }

    public void setPk(PointG1 pk) {
        this.pk = pk;
    }

    public PointG2 getY() {
        return y;
    }

    public void setY(PointG2 y) {
        this.y = y;
    }
}
