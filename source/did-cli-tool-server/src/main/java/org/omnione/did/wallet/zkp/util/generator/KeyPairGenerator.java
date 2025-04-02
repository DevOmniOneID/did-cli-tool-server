package org.omnione.did.wallet.zkp.util.generator;

import org.omnione.did.wallet.zkp.ZkpConstants;
import org.omnione.did.wallet.zkp.data.CredentialPrimaryKeyPair;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPrivateKey;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.data.keypair.PublicKeyMetadata;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.*;
import org.omnione.did.wallet.zkp.revoc.data.GroupOrderElement;
import org.omnione.did.wallet.zkp.revoc.data.PointG1;
import org.omnione.did.wallet.zkp.revoc.data.PointG2;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpLogger;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.PAIR;
import org.omnione.did.wallet.zkp.util.BigIntegerUtil;
import org.omnione.did.wallet.zkp.util.gson.ZkpGsonWrapper;

import java.math.BigInteger;
import java.util.*;

public class KeyPairGenerator {

    public static CredentialPrimaryKeyPair generateKeyPair(List<String> attrNames, String masterSecret) throws ZkpException {

        //TODO: attrNames null check 필요
        if (attrNames == null || attrNames.size() <= 0) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "attributes null in generateKeyPair");
        }

        if (!attrNames.contains(masterSecret)) {
            attrNames.add(masterSecret);
        }

        try {
            BigIntegerUtil generator = new BigIntegerUtil();
            /**
             * Let l be the number of attributes we work with. Let P be a description of the attribute set (types, number, length).
             * Every credential is bind to a pseudonym, which is derived from the master secret m1.
             * Steps to set up the issuer
             * 1. Generate random 1024-bit primes p′, q′ such that p ← 2p′ + 1 and q ← 2q′ + 1 are primes too. Finally
             * compute n ← pq.
             * 2. Generate a random quadratic residue1 S modulo n;
             * 3. Select random xZ,xR1,...,xRl ∈ [2;p′q′ − 1] and compute Z ← SxZ (mod n),Ri ← SxRi (mod n) for 1 ≤ i ≤ l.
             * The issuer’s public key is pkI = (n,S,Z,R1,R2,...,Rl,P) and the private key is skI = (p,q).
             * */
            //TODO: 재작성이 필요한 부분 [Safe Prime]
            // probablePrime : 지정된 length로 random 값 소수를 리턴
            BigInteger p_safe;
            BigInteger p;
            BigInteger q_safe;
            BigInteger q;
            try {
                p_safe = BigInteger.probablePrime(ZkpConstants.LARGE_PRIME + 1, generator.getRandom());
                p = p_safe.shiftRight(1);
                q_safe = BigInteger.probablePrime(ZkpConstants.LARGE_PRIME + 1, generator.getRandom());
                q = q_safe.shiftRight(1);
            } catch (Exception e) {
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_GENERATE_PR_PRIVATE_KEY_FAIL);
            }

            BigInteger n;
            BigInteger s;
            BigInteger z;
            LinkedHashMap<String, BigInteger> r;
            BigInteger rctxt;

            BigInteger xz;
            LinkedHashMap<String, BigInteger> xr;

            try {
                n = p_safe.multiply(q_safe);
                // 네이밍 = (Q_safe), Random R
                s = generator.generateQR(n);
                // 네이밍 = 곱하기 연산 X
                xz = generator.generateX(p, q);
                z = s.modPow(xz, n);

                // let rctxt = s.mod_exp(&gen_x(&p, &q)?, &n, Some(&mut ctx))?;
                rctxt = s.modPow(generator.generateX(p, q), n);

                r = new LinkedHashMap<String, BigInteger>();
                xr = new LinkedHashMap<String, BigInteger>();

                for (String attrName : attrNames) {
                    BigInteger xr_value = generator.generateX(p, q);
                    xr.put(attrName, xr_value);
                    r.put(attrName, s.modPow(xr_value, n));
                }
            } catch (Exception e) {
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_GENERATE_PR_PUBLIC_KEY_FAIL);
            }

            CredentialPrimaryPublicKey publicKey = new CredentialPrimaryPublicKey(n, s, z, r, rctxt);
            CredentialPrimaryPrivateKey privateKey = new CredentialPrimaryPrivateKey(p, q);
            PublicKeyMetadata publicKeyMetadata = new PublicKeyMetadata(xz, xr);

            CredentialPrimaryKeyPair credentialPrimaryKeyPair = new CredentialPrimaryKeyPair(publicKey, privateKey, publicKeyMetadata);
            ZkpLogger.info("====================================================================================================");
            ZkpLogger.info("CredentialPrimaryKeyPair: " + ZkpGsonWrapper.getGsonPrettyPrinting().toJson(credentialPrimaryKeyPair));
            ZkpLogger.info("====================================================================================================");

            return credentialPrimaryKeyPair;
        } catch(Exception e) {
            // BigInteger 연산 에러 처리
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_GENERATE_CRED_PRIMARY_KEY_FAIL, "generate credential primary key");
        }
    }

    public static CredentialRevocationKeyPair revocKeyPair() throws ZkpException {

        try {
            PointG1 h = new PointG1();
            PointG1 h0 = new PointG1();
            PointG1 h1 = new PointG1();
            PointG1 h2 = new PointG1();
            PointG1 htilde = new PointG1();
            PointG1 g = new PointG1();
            PointG2 u = new PointG2();
            PointG2 h_cap = new PointG2();

            GroupOrderElement x = new GroupOrderElement();
            GroupOrderElement sk = new GroupOrderElement();
            PointG2 g_dash = new PointG2();

            PointG1 pk = new PointG1();
            pk.setPoint(PAIR.G1mul(g.getPoint(), sk.getBn()));

            PointG2 y = new PointG2();
            y.setPoint(PAIR.G2mul(h_cap.getPoint(), x.getBn()));

            CredentialRevocationPublicKey publicKey =
                    new CredentialRevocationPublicKey(g, g_dash, h, h0, h1, h2, htilde, h_cap, u, pk, y);

            CredentialRevocationPrivateKey privateKey = new CredentialRevocationPrivateKey(x, sk);

            CredentialRevocationKeyPair keyPair = new CredentialRevocationKeyPair(privateKey, publicKey);
            ZkpLogger.info("====================================================================================================");
            ZkpLogger.info("CredentialRevocationKeyPair: " + ZkpGsonWrapper.getGsonPrettyPrinting().toJson(keyPair));
            ZkpLogger.info("====================================================================================================");

            return keyPair;
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_GENERATE_REVOCATION_KEY_FAIL, "generate credential revocation primary key");
        }
    }
}
