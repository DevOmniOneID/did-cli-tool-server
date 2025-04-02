package org.omnione.did.wallet.zkp.revoc;

import com.google.gson.annotations.JsonAdapter;
import org.omnione.did.wallet.zkp.revoc.data.GroupOrderElement;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpSerializer;

public class RevocationPrivateKey {
    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement gamma;

    public RevocationPrivateKey(GroupOrderElement gamma) {
        this.gamma = gamma;
//        System.out.println("====================================================================================================");
//        System.out.println("RevocationKeyPrivate build");
//        System.out.println("====================================================================================================");
//        AMCLUtils.toHashString(gamma, "gamma");
    }

    public RevocationPrivateKey(RevocationPrivateKey revocPrivateKey) {
        this.gamma = revocPrivateKey.getGamma();
    }

    public GroupOrderElement getGamma() {
        return gamma;
    }

    public void setGamma(GroupOrderElement gamma) {
        this.gamma = gamma;
    }

}
