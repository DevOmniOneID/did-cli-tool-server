package org.omnione.did.wallet.zkp.revoc.verifier;


import org.omnione.did.wallet.zkp.data.credential.NonRevocationCredentialSignature;

import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.CredentialRevocationPublicKey;
import org.omnione.did.wallet.zkp.revoc.RevocationPublicKey;
import org.omnione.did.wallet.zkp.revoc.RevocationRegistry;
import org.omnione.did.wallet.zkp.revoc.data.GroupOrderElement;
import org.omnione.did.wallet.zkp.revoc.data.Pair;
import org.omnione.did.wallet.zkp.revoc.data.PointG1;
import org.omnione.did.wallet.zkp.revoc.data.PointG2;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpLogger;
import org.omnione.did.wallet.zkp.revoc.utils.Witness;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.*;
import org.omnione.did.wallet.zkp.util.BigIntegerUtil;

import java.math.BigInteger;

public class NonRevocationCredentialVerifier {

    public static boolean verify(NonRevocationCredentialSignature r_cred,
                                 GroupOrderElement vrPrime,
                                 CredentialRevocationPublicKey credRevPubKey,
                                 RevocationPublicKey revKeyPub,
                                 RevocationRegistry revReg,
                                 Witness witness) throws ZkpException {

        BigInteger r_cnxt_m2 = BigIntegerUtil.fromBytes(r_cred.getM2().toBytes());
        // update c2 with vrPrime
        r_cred.getVrPrimePrime().setBn(vrPrime.add_mod(r_cred.getVrPrimePrime().getBn()));

        Pair z_calc = new Pair(r_cred.getWitnessSignature().getGI(), revReg.getAccum());

        Pair result = new Pair(credRevPubKey.getG(), witness.getOmega());

        result.getPair().conj();
        z_calc.getPair().mul(result.getPair());
        z_calc.getPair().reduce();

        if (!z_calc.getPair().equals(revKeyPub.getZ().getPair())) {
            ZkpLogger.info("Issuer is sending incorrect data, NonRevocation Credential [z_calc, z] not match");
//            throw new ZkpException(ErrorCode.OMNI_ERROR_ZKP_PROVER_CHECK_WITNESS_SIGNATURE_FAIL, "Issuer is sending incorrect data, NonRevocation Credential [z_calc, z] not match");
            return false;
        }

        PointG1 pk = new PointG1();
        pk.getPoint().copy(credRevPubKey.getPk().getPoint());
        pk.getPoint().add(r_cred.getGI().getPoint());
        Pair pair_gg_calc = new Pair(pk, r_cred.getWitnessSignature().getSigmaI());
        Pair pair_gg = new Pair(credRevPubKey.getG(), credRevPubKey.getGDash());

        if (!pair_gg_calc.getPair().equals(pair_gg.getPair())) {
            ZkpLogger.info("Issuer is sending incorrect data, NonRevocation Credential [gg_calc, gg] not match");
//            throw new ZkpException(ErrorCode.OMNI_ERROR_ZKP_PROVER_CHECK_WITNESS_SIGNATURE_FAIL, "Issuer is sending incorrect data, NonRevocation Credential [gg_calc, gg] not match");
            return false;
        }

        BIG m2 = GroupOrderElement.from_bytes(BigIntegerUtil.asUnsignedByteArray(r_cnxt_m2));

        PointG2 hCap = new PointG2();
        PointG2 y = new PointG2();
        hCap.getPoint().copy(credRevPubKey.getHCap().getPoint());
        y.getPoint().copy(credRevPubKey.getY().getPoint());
        y.getPoint().add(PAIR.G2mul(hCap.getPoint(), r_cred.getC().getBn()));

        Pair pair_h1 = new Pair(r_cred.getSigma(), y);

        PointG1 h0 = new PointG1();
        h0.getPoint().copy(credRevPubKey.getH0().getPoint());
        h0.getPoint().add(PAIR.G1mul(credRevPubKey.getH1().getPoint(), m2));
        h0.getPoint().add(PAIR.G1mul(credRevPubKey.getH2().getPoint(), r_cred.getVrPrimePrime().getBn()));
        h0.getPoint().add(r_cred.getGI().getPoint());
        Pair pair_h2 = new Pair(h0, credRevPubKey.getHCap());

        if (!pair_h1.getPair().equals(pair_h2.getPair())) {
            ZkpLogger.info("Issuer is sending incorrect data, NonRevocation Credential [h1, h2] not match");
//            throw new ZkpException(ErrorCode.OMNI_ERROR_ZKP_PROVER_CHECK_WITNESS_SIGNATURE_FAIL, "Issuer is sending incorrect data, NonRevocation Credential [h1, h2] not match (ur, vrPrime)");
            return false;
        }

        return true;
    }

}
