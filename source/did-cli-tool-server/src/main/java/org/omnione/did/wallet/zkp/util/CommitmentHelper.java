package org.omnione.did.wallet.zkp.util;

import java.math.BigInteger;

public class CommitmentHelper {

    /**
     * Pedersen Commitment Scheme
     *
     */
    public static BigInteger commitment(BigInteger z, BigInteger z_exp, BigInteger s, BigInteger s_exp, BigInteger modular) {
        return z.modPow(z_exp, modular).multiply(s.modPow(s_exp, modular)).mod(modular);
    }
}
