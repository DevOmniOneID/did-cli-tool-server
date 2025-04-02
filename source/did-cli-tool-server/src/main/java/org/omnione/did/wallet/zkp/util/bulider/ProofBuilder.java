package org.omnione.did.wallet.zkp.util.bulider;

import org.omnione.did.wallet.zkp.ZkpConstants;
import org.omnione.did.wallet.zkp.data.Proof;
import org.omnione.did.wallet.zkp.data.SubProofRequest;
import org.omnione.did.wallet.zkp.data.credential.CredentialSignature;
import org.omnione.did.wallet.zkp.data.credential.NonRevocationCredentialSignature;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.data.proof.CredentialValues;
import org.omnione.did.wallet.zkp.data.proof.RequestedProof;
import org.omnione.did.wallet.zkp.data.schema.CredentialSchema;
import org.omnione.did.wallet.zkp.data.schema.NonCredentialSchema;
import org.omnione.did.wallet.zkp.data.subproof.InitProof;
import org.omnione.did.wallet.zkp.data.subproof.primaryproof.PrimaryInitProof;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.CredentialRevocationPublicKey;
import org.omnione.did.wallet.zkp.revoc.NonRevocInitProof;
import org.omnione.did.wallet.zkp.revoc.RevocationRegistry;
import org.omnione.did.wallet.zkp.revoc.data.*;
import org.omnione.did.wallet.zkp.revoc.data.dto.Identifiers;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpSetting;
import org.omnione.did.wallet.zkp.revoc.utils.Witness;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.BIG;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.PAIR;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.ROM;
import org.omnione.did.wallet.zkp.util.BigIntegerUtil;
import org.omnione.did.wallet.zkp.util.generator.proof.ProofFinalizer;
import org.omnione.did.wallet.zkp.util.generator.proof.ProofInitiator;
import org.omnione.did.wallet.zkp.util.helper.ProofHelper;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class ProofBuilder {

    private HashMap<String, BigInteger> commonAttributes = new HashMap<String, BigInteger>();
    private Vector<InitProof> initProof = new Vector<InitProof>();

    private Vector<byte[]> cList = new Vector<byte[]>();
    private Vector<byte[]> tauList = new Vector<byte[]>();

    public ProofBuilder(String commonAttrName) {
        this.addCommonAttribute(commonAttrName);
    }

    protected ProofBuilder addCommonAttribute(String attrName) {
        commonAttributes.put(attrName, new BigIntegerUtil().createRandomBigInteger(ZkpConstants.LARGE_MVECT));
        return this;
    }

    public ProofBuilder addSubProofRequest(SubProofRequest subProofRequest,
                                           CredentialSchema credSchema,
                                           NonCredentialSchema nonCredSchema,
                                           CredentialValues credValues,
                                           CredentialSignature credentialSignature,
                                           CredentialPrimaryPublicKey publicKey,
                                           CredentialRevocationPublicKey credentialRevocationPublicKey,
                                           RevocationRegistry revocationRegistry,
                                           Witness witness) throws ZkpException {

        this.checkAddSubProofRequestParams(credValues, subProofRequest, credSchema, nonCredSchema);

        BigInteger m2_tilde;
        NonRevocInitProof nonRevocIniProof = null;

        if (ZkpSetting.getInstance().isSupportedRevocation()) {
            // 폐기되지 않음을 증명
            nonRevocIniProof = initNonRevocationProof(credentialSignature.getNonRevocationCredential(),
                    revocationRegistry,
                    credentialRevocationPublicKey,
                    witness);
            m2_tilde = BigIntegerUtil.fromBytes(nonRevocIniProof.getTauListParams().getM2().toBytes());
            this.cList.addAll(nonRevocIniProof.getcList().asList());
            this.tauList.addAll(nonRevocIniProof.getTauList().asSlice());
        }
        else {
//            m2_tilde = BigIntegerUtil.fromBytes(new GroupOrderElement().toBytes());
            // djpark0402 비해지증명 소스 삭제 과정 - GroupOrderElement 클래스 미사용
            m2_tilde = new BigIntegerUtil().createRandomBigInteger(ZkpConstants.LARGE_NONCE);
        }

        PrimaryInitProof primaryInitProof = ProofInitiator.initPrimaryProof(publicKey,
                                                                    credSchema,
                                                                    nonCredSchema,
                                                                    subProofRequest,
                                                                    credValues,
                                                                    credentialSignature.getPrimaryCredential(),
                                                                    m2_tilde,
                                                                    this.commonAttributes);

        // A′ to C.
        this.cList.addAll(primaryInitProof.getCommonValue());
        // Add t to T (primary, non_revoc)
        this.tauList.addAll(primaryInitProof.getTValue());

        this.initProof.add(new InitProof(primaryInitProof, nonRevocIniProof, credValues, subProofRequest, credSchema, nonCredSchema));

        return this;
    }

    public Proof build(BigInteger nonce, RequestedProof requestedProof, List<Identifiers> identifiers) throws ZkpException {
        return ProofFinalizer.finalize(this.initProof, this.cList, this.tauList, nonce, requestedProof, identifiers);
    }

    //TODO: 메소드 미구현
    protected void checkAddSubProofRequestParams(CredentialValues credValues, SubProofRequest subProofReq, CredentialSchema credSchema, NonCredentialSchema nonCredSchema) throws ZkpException {

    }

    public static NonRevocProofXList genClistParams(NonRevocationCredentialSignature rCred) throws ZkpException {

        GroupOrderElement rho = new GroupOrderElement();
        GroupOrderElement r = new GroupOrderElement();
        GroupOrderElement rPrime = new GroupOrderElement();
        GroupOrderElement rPrimePrime = new GroupOrderElement();
        GroupOrderElement rPrimePrimePrime = new GroupOrderElement();
        GroupOrderElement o = new GroupOrderElement();
        GroupOrderElement o_prime = new GroupOrderElement();

        GroupOrderElement m = new GroupOrderElement();
        m.getBn().copy((BIG.modmul(rho.getBn(), rCred.getC().getBn() ,new BIG(ROM.CURVE_Order))));

        GroupOrderElement mPrime = new GroupOrderElement();
        mPrime.getBn().copy((BIG.modmul(r.getBn(), rPrimePrime.getBn(), new BIG(ROM.CURVE_Order))));

        GroupOrderElement t = new GroupOrderElement();
        t.getBn().copy((BIG.modmul(o.getBn(), rCred.getC().getBn(), new BIG(ROM.CURVE_Order))));

        GroupOrderElement tPrime = new GroupOrderElement();
        tPrime.getBn().copy((BIG.modmul(o_prime.getBn(), rPrimePrime.getBn(), new BIG(ROM.CURVE_Order))));

        GroupOrderElement m2 = new GroupOrderElement();
        m2.getBn().copy((GroupOrderElement.from_bytes(rCred.getM2().toBytes())));

        return new NonRevocProofXList(rho, r, rPrime, rPrimePrime, rPrimePrimePrime, o, o_prime,
                                     m, mPrime, t, tPrime,  m2, rCred.getVrPrimePrime(), rCred.getC());
    }

    public static NonRevocProofCList createCListValues(NonRevocationCredentialSignature rCred,
                                                NonRevocProofXList params,
                                                CredentialRevocationPublicKey rPubKey,
                                                Witness witness) throws ZkpException {
        try {
// e
            PointG1 h = new PointG1();
            h.getPoint().copy(rPubKey.getH().getPoint());
            h.setPoint(PAIR.G1mul(h.getPoint(), params.getRho().getBn()));
            h.getPoint().add(PAIR.G1mul(rPubKey.getHtilde().getPoint(), params.getO().getBn()));
            PointG1 e = new PointG1();
            e.getPoint().copy(h.getPoint());
// d
            PointG1 d = new PointG1();
            d.getPoint().copy(rPubKey.getG().getPoint());
            d.setPoint(PAIR.G1mul(d.getPoint(), params.getR().getBn()));
            d.getPoint().add(PAIR.G1mul(rPubKey.getHtilde().getPoint(), params.getoPrime().getBn()));
// a
            PointG1 a = new PointG1();
            a.getPoint().copy(rCred.getSigma().getPoint());
            a.getPoint().add(PAIR.G1mul(rPubKey.getHtilde().getPoint(), params.getRho().getBn()));
// g
            PointG1 g = new PointG1();
            g.getPoint().copy(rCred.getGI().getPoint());
            g.getPoint().add(PAIR.G1mul(rPubKey.getHtilde().getPoint(), params.getR().getBn()));
// w
            // 자신의 private factor에 witness를 곱한다.
            PointG2 w = new PointG2();
            w.getPoint().copy(witness.getOmega().getPoint());
            w.getPoint().add(PAIR.G2mul(rPubKey.getHCap().getPoint(), params.getrPrime().getBn()));
// s
            PointG2 s = new PointG2();
            s.getPoint().copy(rCred.getWitnessSignature().getSigmaI().getPoint());
            s.getPoint().add(PAIR.G2mul(rPubKey.getHCap().getPoint(), params.getrPrimePrime().getBn()));
// u
            PointG2 u = new PointG2();
            u.getPoint().copy(rCred.getWitnessSignature().getUI().getPoint());
            u.getPoint().add(PAIR.G2mul(rPubKey.getHCap().getPoint(), params.getrPrimePrimePrime().getBn()));
            return new NonRevocProofCList(e, d, a, g, w, s, u);
        }
        catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_INITIALIZE_REVOCATION_PROOF_FAIL, "generate clist values fail");
        }
    }

    public static NonRevocProofXList genTauListParams() {

        GroupOrderElement rho = new GroupOrderElement();
        GroupOrderElement r = new GroupOrderElement();
        GroupOrderElement rPrime = new GroupOrderElement();
        GroupOrderElement rPrimePrime = new GroupOrderElement();
        GroupOrderElement rPrimePrimePrime = new GroupOrderElement();
        GroupOrderElement o = new GroupOrderElement();
        GroupOrderElement oPrime = new GroupOrderElement();
        GroupOrderElement m = new GroupOrderElement();
        GroupOrderElement mPrime = new GroupOrderElement();
        GroupOrderElement t = new GroupOrderElement();
        GroupOrderElement tPrime = new GroupOrderElement();
        GroupOrderElement m2 = new GroupOrderElement();
        GroupOrderElement s = new GroupOrderElement();
        GroupOrderElement c = new GroupOrderElement();

        return new NonRevocProofXList(rho, r, rPrime, rPrimePrime, rPrimePrimePrime, o, oPrime,
                                        m, mPrime, t, tPrime, m2, s, c);
    }



    private NonRevocInitProof initNonRevocationProof(NonRevocationCredentialSignature nonRevocationCredentialSignature,
                                                           RevocationRegistry revocationRegistry,
                                                           CredentialRevocationPublicKey credentialRevocationPublicKey,
                                                           Witness witness) throws ZkpException {

        NonRevocProofXList cListParams = ProofBuilder.genClistParams(nonRevocationCredentialSignature);
        // 자신의 private 한 요소에 witness를 곱하여 블록체인에 등록된 누산기 값을 도출하여 같음을 증명
        NonRevocProofCList cList = ProofBuilder.createCListValues(
                nonRevocationCredentialSignature,
                cListParams,
                credentialRevocationPublicKey,
                witness);

        NonRevocProofXList tauListParams = ProofBuilder.genTauListParams();

        NonRevocProofTauList tauList = ProofHelper.createTauListValues(
                credentialRevocationPublicKey,
                revocationRegistry,
                tauListParams,
                cList);


//        AMCLUtils.toHashString(tauList.getT1(), "t1");
//        AMCLUtils.toHashString(tauList.getT2(), "t2");
//        AMCLUtils.toHashString(tauList.getT3().getPair(), "t3");
//        AMCLUtils.toHashString(tauList.getT4().getPair(), "t4");
//        AMCLUtils.toHashString(tauList.getT5(), "t5");
//        AMCLUtils.toHashString(tauList.getT6(), "t6");
//        AMCLUtils.toHashString(tauList.getT7().getPair(), "t7");
//        AMCLUtils.toHashString(tauList.getT8().getPair(), "t8");

//        byte[] b;
//
//        b = tauList.getT1().toBytes();
//        System.out.println("t1: ..... "+String.format("%02x ", b[0] & 0xff)+" "+String.format("%02x ", b[1] & 0xff)+" "+String.format("%02x ", b[2] & 0xff)+" "+String.format("%02x ", b[3] & 0xff)+" "+String.format("%02x ", b[4] & 0xff)+" "+String.format("%02x ", b[5] & 0xff));
//
//        b = tauList.getT2().toBytes();
//        System.out.println("t2: ..... "+String.format("%02x ", b[0] & 0xff)+" "+String.format("%02x ", b[1] & 0xff)+" "+String.format("%02x ", b[2] & 0xff)+" "+String.format("%02x ", b[3] & 0xff)+" "+String.format("%02x ", b[4] & 0xff)+" "+String.format("%02x ", b[5] & 0xff));
//
//        b = tauList.getT3().toBytes();
//        System.out.println("t3: ..... "+String.format("%02x ", b[0] & 0xff)+" "+String.format("%02x ", b[1] & 0xff)+" "+String.format("%02x ", b[2] & 0xff)+" "+String.format("%02x ", b[3] & 0xff)+" "+String.format("%02x ", b[4] & 0xff)+" "+String.format("%02x ", b[5] & 0xff));
//
//        b = tauList.getT4().toBytes();
//        System.out.println("t4: ..... "+String.format("%02x ", b[0] & 0xff)+" "+String.format("%02x ", b[1] & 0xff)+" "+String.format("%02x ", b[2] & 0xff)+" "+String.format("%02x ", b[3] & 0xff)+" "+String.format("%02x ", b[4] & 0xff)+" "+String.format("%02x ", b[5] & 0xff));
//
//        b = tauList.getT5().toBytes();
//        System.out.println("t5: ..... "+String.format("%02x ", b[0] & 0xff)+" "+String.format("%02x ", b[1] & 0xff)+" "+String.format("%02x ", b[2] & 0xff)+" "+String.format("%02x ", b[3] & 0xff)+" "+String.format("%02x ", b[4] & 0xff)+" "+String.format("%02x ", b[5] & 0xff));
//
//        b = tauList.getT6().toBytes();
//        System.out.println("t6: ..... "+String.format("%02x ", b[0] & 0xff)+" "+String.format("%02x ", b[1] & 0xff)+" "+String.format("%02x ", b[2] & 0xff)+" "+String.format("%02x ", b[3] & 0xff)+" "+String.format("%02x ", b[4] & 0xff)+" "+String.format("%02x ", b[5] & 0xff));
//
//        b = tauList.getT7().toBytes();
//        System.out.println("t7: ..... "+String.format("%02x ", b[0] & 0xff)+" "+String.format("%02x ", b[1] & 0xff)+" "+String.format("%02x ", b[2] & 0xff)+" "+String.format("%02x ", b[3] & 0xff)+" "+String.format("%02x ", b[4] & 0xff)+" "+String.format("%02x ", b[5] & 0xff));
//
//        b = tauList.getT8().toBytes();
//        System.out.println("t8: ..... "+String.format("%02x ", b[0] & 0xff)+" "+String.format("%02x ", b[1] & 0xff)+" "+String.format("%02x ", b[2] & 0xff)+" "+String.format("%02x ", b[3] & 0xff)+" "+String.format("%02x ", b[4] & 0xff)+" "+String.format("%02x ", b[5] & 0xff));


        return new NonRevocInitProof(cListParams, tauListParams, cList, tauList);
    }
}
