package org.omnione.did.wallet.zkp.revoc;

import com.google.gson.annotations.JsonAdapter;
import org.omnione.did.wallet.zkp.revoc.data.Pair;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpSerializer;

public class RevocationPublicKey {
    @JsonAdapter(ZkpSerializer.class)
    private Pair z;

    public RevocationPublicKey(Pair z) {
        this.z = z;
//        System.out.println("====================================================================================================");
//        System.out.println("RevocationKeyPublic build");
//        System.out.println("====================================================================================================");
//        AMCLUtils.toHashString(z, "z");
    }

    public Pair getZ() {
        return z;
    }

    public void setZ(Pair z) {
        this.z = z;
    }
}
