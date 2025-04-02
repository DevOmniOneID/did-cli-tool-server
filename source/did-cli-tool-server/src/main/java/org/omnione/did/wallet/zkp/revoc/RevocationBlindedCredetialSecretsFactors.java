package org.omnione.did.wallet.zkp.revoc;

import com.google.gson.annotations.JsonAdapter;
import org.omnione.did.wallet.zkp.revoc.data.GroupOrderElement;
import org.omnione.did.wallet.zkp.revoc.data.PointG1;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpSerializer;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.PAIR;

public class RevocationBlindedCredetialSecretsFactors {
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 ur;
    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement vrPrime;

    public RevocationBlindedCredetialSecretsFactors(CredentialRevocationPublicKey rPubKey) {

        this.vrPrime = new GroupOrderElement();
        this.ur = new PointG1();
        this.ur.setPoint(PAIR.G1mul(rPubKey.getH2().getPoint(), vrPrime.getBn()));
    }

    public PointG1 getUr() {
        return ur;
    }

    public void setUr(PointG1 ur) {
        this.ur = ur;
    }

    public GroupOrderElement getVrPrime() {
        return vrPrime;
    }

    public void setVrPrime(GroupOrderElement vrPrime) {
        this.vrPrime = vrPrime;
    }
}
