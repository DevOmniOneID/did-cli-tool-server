package org.omnione.did.wallet.zkp.revoc.generator;

import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.CredentialRevocationPublicKey;
import org.omnione.did.wallet.zkp.revoc.RevocationPrivateKey;
import org.omnione.did.wallet.zkp.revoc.RevocationPublicKey;
import org.omnione.did.wallet.zkp.revoc.data.GroupOrderElement;
import org.omnione.did.wallet.zkp.revoc.data.Pair;
import org.omnione.did.wallet.zkp.revoc.utils.AMCLUtils;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.BIG;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.PAIR;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.ROM;

public class RevocationRegistryKeys {

    private RevocationPublicKey revocPublicKey;
    private RevocationPrivateKey revocPrivateKey;

    public RevocationRegistryKeys(CredentialRevocationPublicKey credentialRevocationPublicKey, int maxCredNum) throws ZkpException {

        GroupOrderElement gamma = new GroupOrderElement();
        CredentialRevocationPublicKey credRevPubKey = credentialRevocationPublicKey;

        Pair z = new Pair(credRevPubKey.getG(), credRevPubKey.getGDash());

        byte[] i_bytes = AMCLUtils.transform_i32_to_array_of_i8(maxCredNum + 1);
        BIG pow = GroupOrderElement.from_bytes(i_bytes);
        pow = gamma.getBn().powmod(pow, new BIG(ROM.CURVE_Order));

        z.getPair().copy(PAIR.GTpow(z.getPair(), pow));

        this.revocPublicKey = new RevocationPublicKey(z);
        this.revocPrivateKey = new RevocationPrivateKey(gamma);
    }

    public RevocationPublicKey getRevocPublicKey() {
        return revocPublicKey;
    }

    public RevocationPrivateKey getRevocPrivateKey() {
        return revocPrivateKey;
    }

}
