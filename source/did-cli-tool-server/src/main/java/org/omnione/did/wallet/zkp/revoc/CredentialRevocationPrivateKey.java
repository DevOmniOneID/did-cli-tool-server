package org.omnione.did.wallet.zkp.revoc;

import com.google.gson.annotations.JsonAdapter;
import org.omnione.did.wallet.zkp.revoc.data.GroupOrderElement;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpSerializer;

public class CredentialRevocationPrivateKey {
    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement x;
    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement sk;

    public CredentialRevocationPrivateKey(GroupOrderElement x, GroupOrderElement sk) {

        this.x = x;
        this.sk = sk;

//        System.out.println("\n====================================================================================================");
//        System.out.println("CredentialRevocationPrivateKey build");
//        System.out.println("====================================================================================================");
//        AMCLUtils.toHashString(x, "x");
//        AMCLUtils.toHashString(sk, "sk");
    }

    public GroupOrderElement getX() {
        return x;
    }

    public void setX(GroupOrderElement x) {
        this.x = x;
    }

    public GroupOrderElement getSk() {
        return sk;
    }

    public void setSk(GroupOrderElement sk) {
        this.sk = sk;
    }

    public String toString() {
        return this.x.getBn().toString();
    }

}
