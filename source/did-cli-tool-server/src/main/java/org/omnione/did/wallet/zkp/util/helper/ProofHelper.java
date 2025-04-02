package org.omnione.did.wallet.zkp.util.helper;

import org.omnione.did.wallet.zkp.revoc.CredentialRevocationPublicKey;
import org.omnione.did.wallet.zkp.revoc.RevocationRegistry;
import org.omnione.did.wallet.zkp.revoc.data.*;
import org.omnione.did.wallet.zkp.revoc.utils.AMCLUtils;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.BIG;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.PAIR;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.ROM;

public class ProofHelper {

    public static NonRevocProofTauList createTauListValues(CredentialRevocationPublicKey rPubKey,
                                                           RevocationRegistry revReg,
                                                           NonRevocProofXList params,
                                                           NonRevocProofCList proofC) {
// t1
        PointG1 t1 = new PointG1();
        t1.getPoint().copy(rPubKey.getH().getPoint());
        t1.setPoint(PAIR.G1mul(t1.getPoint(), params.getRho().getBn()));
        t1.getPoint().add(PAIR.G1mul(rPubKey.getHtilde().getPoint(), params.getO().getBn()));

//        AMCLUtils.toHashString(t1, "t1");

//t2
        PointG1 h = new PointG1();
        h.getPoint().copy(rPubKey.getH().getPoint());
        GroupOrderElement m = new GroupOrderElement();
        m.getBn().copy(params.getM().getBn());
        PointG1 htilde = new PointG1();
        htilde.getPoint().copy(rPubKey.getHtilde().getPoint());
        GroupOrderElement t = new GroupOrderElement();
        t.getBn().copy(params.getT().getBn());
        PointG1 t2 = new PointG1();
        t2.getPoint().copy(proofC.getE().getPoint());
        t2.setPoint(PAIR.G1mul(t2.getPoint(), params.getC().getBn()));
        t2.getPoint().add(PAIR.G1mul(h.getPoint(), BIG.modneg(m.getBn(), new BIG(ROM.CURVE_Order))));
        t2.getPoint().add(PAIR.G1mul(htilde.getPoint(), BIG.modneg(t.getBn(), new BIG(ROM.CURVE_Order))));
        if (t2.getPoint().is_infinity()) {
            t2 = new PointG1();
            t2.getPoint().inf();
//            t2.getP();
        }

//        AMCLUtils.toHashString(t2, "t2");
// t3
        Pair t3 = new Pair(proofC.getA(), rPubKey.getHCap());
        t3.getPair().copy(PAIR.GTpow(t3.getPair(), params.getC().getBn()));
        Pair t3_pair = new Pair(rPubKey.getHtilde(), rPubKey.getHCap());
        t3.getPair().mul(PAIR.GTpow(t3_pair.getPair(), params.getR().getBn()));
        t3.getPair().reduce();
        Pair t3_subPair = new Pair(rPubKey.getHtilde(), rPubKey.getY());
        t3_subPair.getPair().copy(PAIR.GTpow(t3_subPair.getPair(), params.getRho().getBn()));
        Pair t3_subPair1 = new Pair(rPubKey.getHtilde(), rPubKey.getHCap());
        t3_subPair.getPair().mul(PAIR.GTpow(t3_subPair1.getPair(), params.getM().getBn()));
        t3_subPair.getPair().reduce();
        Pair t3_usbPair2 = new Pair(rPubKey.getH1(), rPubKey.getHCap());
        t3_subPair.getPair().mul(PAIR.GTpow(t3_usbPair2.getPair(), params.getM2().getBn()));
        t3_subPair.getPair().reduce();
        Pair t3_subPair3 = new Pair(rPubKey.getH2(), rPubKey.getHCap());
        t3_subPair.getPair().mul(PAIR.GTpow(t3_subPair3.getPair(), params.getS().getBn()));
        t3_subPair.getPair().reduce();
        t3_subPair.getPair().conj();
        t3.getPair().mul(t3_subPair.getPair());
        t3.getPair().reduce();

//        AMCLUtils.toHashString(t3.getPair(), "t3");
// t4
        Pair t4 = new Pair(rPubKey.getHtilde(), revReg.getAccum());
        t4.getPair().copy(PAIR.GTpow(t4.getPair(), params.getR().getBn()));
        PointG1 t4_g = new PointG1();
        t4_g.getPoint().copy(rPubKey.getG().getPoint());
        t4_g.getPoint().neg();
        Pair t4_pair = new Pair(t4_g, rPubKey.getHCap());
        t4.getPair().mul(PAIR.GTpow(t4_pair.getPair(), params.getrPrime().getBn()));
        t4.getPair().reduce();

//        AMCLUtils.toHashString(t4.getPair(), "t4");
// t5
        PointG1 t5 = new PointG1();
        t5.getPoint().copy(rPubKey.getG().getPoint());
        t5.setPoint(PAIR.G1mul(t5.getPoint(), params.getR().getBn()));
        t5.getPoint().add(PAIR.G1mul(rPubKey.getHtilde().getPoint(), params.getoPrime().getBn()));

//        AMCLUtils.toHashString(t5, "t5");
// t6
        PointG1 t6 = new PointG1();
        t6.getPoint().copy(proofC.getD().getPoint());
        t6.setPoint(PAIR.G1mul(t6.getPoint(), params.getrPrimePrime().getBn()));
        t6.getPoint().add(PAIR.G1mul(rPubKey.getG().getPoint(), BIG.modneg(params.getmPrime().getBn(), new BIG(ROM.CURVE_Order))));
        t6.getPoint().add(PAIR.G1mul(rPubKey.getHtilde().getPoint(), BIG.modneg(params.gettPrime().getBn(), new BIG(ROM.CURVE_Order))));
        if (t6.getPoint().is_infinity()) {
            t6 = new PointG1();
            t6.getPoint().inf();
//            t6.getP();
        }

//        AMCLUtils.toHashString(t6, "t6");
// t7
        PointG1 pk = new PointG1();
        pk.getPoint().copy(rPubKey.getPk().getPoint());
        pk.getPoint().add(proofC.getG().getPoint());
        Pair t7 = new Pair(pk, rPubKey.getHCap());
        t7.getPair().copy(PAIR.GTpow(t7.getPair(), params.getrPrimePrime().getBn()));
        Pair t7_pair1 = new Pair(rPubKey.getHtilde(), rPubKey.getHCap());
        t7_pair1.getPair().copy((PAIR.GTpow(t7_pair1.getPair(), BIG.modneg(params.getmPrime().getBn(), new BIG(ROM.CURVE_Order)))));
        t7.getPair().mul(t7_pair1.getPair());
        t7.getPair().reduce();
        Pair t7_pair2 = new Pair(rPubKey.getHtilde(), proofC.getS());
        t7_pair2.getPair().copy(PAIR.GTpow(t7_pair2.getPair(), params.getR().getBn()));
        t7.getPair().mul(t7_pair2.getPair());
        t7.getPair().reduce();
//        AMCLUtils.toHashString(t7.getPair(), "t7");
// t8
        Pair t8 = new Pair(rPubKey.getHtilde(), rPubKey.getU());
        t8.getPair().copy(PAIR.GTpow(t8.getPair(), params.getR().getBn()));
        PointG1 t8_g = new PointG1();
        t8_g.getPoint().copy(rPubKey.getG().getPoint());
        t8_g.getPoint().neg();
        Pair t8_pair = new Pair(t8_g, rPubKey.getHCap());
        t8_pair.getPair().copy(PAIR.GTpow(t8_pair.getPair(), params.getrPrimePrimePrime().getBn()));
        t8.getPair().mul(t8_pair.getPair());
        t8.getPair().reduce();

//        AMCLUtils.toHashString(t8.getPair(), "t8");
        return new NonRevocProofTauList(t1, t2, t3, t4, t5, t6, t7, t8);
    }
}
