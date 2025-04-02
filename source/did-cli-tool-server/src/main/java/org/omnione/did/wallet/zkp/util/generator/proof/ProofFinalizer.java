package org.omnione.did.wallet.zkp.util.generator.proof;

import org.omnione.did.wallet.zkp.ZkpConstants;
import org.omnione.did.wallet.zkp.data.*;
import org.omnione.did.wallet.zkp.data.proof.AggregatedProof;
import org.omnione.did.wallet.zkp.data.proof.CredentialValues;
import org.omnione.did.wallet.zkp.data.proof.RequestedProof;
import org.omnione.did.wallet.zkp.data.schema.CredentialSchema;
import org.omnione.did.wallet.zkp.data.schema.NonCredentialSchema;
import org.omnione.did.wallet.zkp.data.subproof.InitProof;
import org.omnione.did.wallet.zkp.data.subproof.PrimaryEqualProof;
import org.omnione.did.wallet.zkp.data.subproof.PrimaryPredicateInequalityProof;
import org.omnione.did.wallet.zkp.data.subproof.primaryproof.PrimaryEqualInitProof;
import org.omnione.did.wallet.zkp.data.subproof.primaryproof.PrimaryInitProof;
import org.omnione.did.wallet.zkp.data.subproof.primaryproof.PrimaryPredicateInequalityInitProof;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.NonRevocInitProof;
import org.omnione.did.wallet.zkp.revoc.data.GroupOrderElement;
import org.omnione.did.wallet.zkp.revoc.data.NonRevocProofXList;
import org.omnione.did.wallet.zkp.revoc.data.dto.Identifiers;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpLogger;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpSetting;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.BIG;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.ROM;
import org.omnione.did.wallet.zkp.util.BigIntegerUtil;
import org.omnione.did.wallet.zkp.util.bulider.ChallengeBuilder;

import java.math.BigInteger;
import java.util.*;

public class ProofFinalizer {

    public static Proof finalize(Vector<InitProof> initProofs, Vector<byte[]> cList, Vector<byte[]> tauList, BigInteger nonce, RequestedProof requestedProof, List<Identifiers> identifiers) throws ZkpException {

        if (cList == null || tauList == null || nonce == null) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "proof finalize is fail detected null field ");
        }

        BigInteger challenge = new ChallengeBuilder()
                .add(tauList)
                .add(cList)
                .add(nonce)
                .buildWithHashing();

//        System.out.println("proof challenge: "+challenge);

        Vector<SubProof> proofs = new Vector<SubProof>();

        //PrimaryProof 생성
        for (InitProof initProof : initProofs) {

            NonRevocProof nonRevocProof = ZkpSetting.getInstance().isSupportedRevocation() ? finalizeNonRevocationProof(initProof.getNonRevocInitProof(), challenge) : null;
            PrimaryProof primaryProof = finalizePrimaryProof(initProof, challenge);
            //TODO: SubProof 구조의 개선필요
            proofs.add(new SubProof(primaryProof, nonRevocProof));
        }
        // Then (c, {PrC }, {Prp}, C) is the full proof sent to the Verifier.
        return new Proof(proofs, new AggregatedProof(challenge, cList), requestedProof, identifiers);
    }

    private static NonRevocProof finalizeNonRevocationProof(NonRevocInitProof initProof, BigInteger challenge) throws ZkpException {

        BIG ch_num_z = GroupOrderElement.from_bytes(BigIntegerUtil.asUnsignedByteArray(challenge));

        ZkpLogger.debug("finalizeNonRevocationProof ch_num_z: "+ch_num_z);

        Vector<GroupOrderElement> x_list = new Vector<GroupOrderElement>();
        Iterator tauParams = initProof.getTauListParams().asList().iterator();
        Iterator cParams = initProof.getcListParams().asList().iterator();

        while (tauParams.hasNext() && cParams.hasNext()) {

            GroupOrderElement x = (GroupOrderElement)tauParams.next();
            GroupOrderElement y = (GroupOrderElement)cParams.next();

            GroupOrderElement res = new GroupOrderElement();

            BIG mulmod = BIG.modmul(ch_num_z, y.getBn(), new BIG(ROM.CURVE_Order));
            BIG modneg = BIG.modneg(mulmod, new BIG(ROM.CURVE_Order));
            BIG addmod = x.add_mod(modneg);

            res.getBn().copy(addmod);
            x_list.add(res);
        }

        return new NonRevocProof(NonRevocProofXList.fromList(x_list), initProof.getcList());
    }

    private static PrimaryProof finalizePrimaryProof(InitProof initProof, BigInteger challenge) throws ZkpException {
        return finalizePrimaryProof(initProof.getPrimaryInitProof(),
                                    challenge,
                                    initProof.getCredentialSchema(),
                                    initProof.getNonCredentialSchema(),
                                    initProof.getCredentialValues(),
                                    initProof.getSubProofRequest());
    }
    private static PrimaryProof finalizePrimaryProof(PrimaryInitProof initProof,
                                                     BigInteger challenge,
                                                     CredentialSchema credSchema,
                                                     NonCredentialSchema nonCredSchema,
                                                     CredentialValues credValues,
                                                     SubProofRequest subProofReq) throws ZkpException {
        try {

            PrimaryEqualProof equalProof = finalizeEqProof(initProof.getEqProof(),
                                                            challenge,
                                                            credSchema,
                                                            nonCredSchema,
                                                            credValues,
                                                            subProofReq);

            Vector<PrimaryPredicateInequalityProof> neProofs = generateNeProofs(initProof, equalProof, challenge);

            return new PrimaryProof(equalProof, neProofs);

        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_FINALIZE_PRIMARY_PROOF_FAIL);
        }
    }

    //Wrapper Method
    private static Vector<PrimaryPredicateInequalityProof> generateNeProofs(PrimaryInitProof initProof, PrimaryEqualProof equalProof, BigInteger challenge) throws ZkpException {
        Vector<PrimaryPredicateInequalityProof> neProofs = new Vector<PrimaryPredicateInequalityProof>();

        for (PrimaryPredicateInequalityInitProof neInitProof : initProof.getNeProofs()) {
            neProofs.add(finalizeNeProof(challenge, neInitProof, equalProof));
        }
        return neProofs;
    }

    private static PrimaryPredicateInequalityProof finalizeNeProof(BigInteger challenge,
                                                                   PrimaryPredicateInequalityInitProof neInitProof,
                                                                   PrimaryEqualProof eqProof) throws ZkpException {

        if (challenge == null) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "finalizeNeProof challenge is null in finalizeNeProof");
        } else if (neInitProof == null) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "finalizeNeProof neInitProof is null in finalizeNeProof");
        } else if (eqProof == null) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "finalizeNeProof eqProof is null in finalizeNeProof");
        }

        try {
            Map<String, BigInteger> u = new HashMap<String, BigInteger>();
            Map<String, BigInteger> r = new HashMap<String, BigInteger>();
            BigInteger ur = BigInteger.ZERO;

            final Map<String, BigInteger> u_tilde = neInitProof.getUTilde();
            final Map<String, BigInteger> r_tilde = neInitProof.getRTilde();
            final Map<String, BigInteger> uInit = neInitProof.getU();
            final Map<String, BigInteger> rInit = neInitProof.getR();
            final Map<String, BigInteger> tInit = neInitProof.getT();
            final BigInteger alpha_tilde = neInitProof.getAlphaTilde();
            final Predicate predicate = neInitProof.getPredicate();

            //TODO: for 조건문 ZkpConstants.ITERATION으로 변경 고려
            for (String key : neInitProof.getUTilde().keySet()) {
                BigInteger cur_u = uInit.get(key);
                BigInteger cur_r = rInit.get(key);
                // For 1 ≤ i ≤ 4 compute ui ← ui + cu.
                u.put(key, challenge.multiply(cur_u).add(u_tilde.get(key)));
                // For1 ≤ i ≤ 4 computer ri ← ri + cr.
                r.put(key, challenge.multiply(cur_r).add(r_tilde.get(key)));

                ur = cur_u.multiply(cur_r).add(ur);
            }

            //TODO: indy에서의 구현이 이상해서 일단 변경해둠
            BigInteger r_delta = rInit.get(ZkpConstants.DELTA);
            BigInteger r_delta_tilde = r_tilde.get(ZkpConstants.DELTA);

            r.put(ZkpConstants.DELTA, challenge.multiply(r_delta).add(r_delta_tilde));

            // Compute r∆ ← r∆ + c * r∆.
            // Compute α ← α + c(r∆ − u1*r1 − u2*r2 − u3*r3 − u4*r4).
            BigInteger alpha = r_delta.subtract(ur).multiply(challenge).add(alpha_tilde);

            //The values Prp = ({ui},{ri},r∆,α,mj) are the sub-proof for predicate p
            PrimaryPredicateInequalityProof neProof = new PrimaryPredicateInequalityProof(u, r, tInit, eqProof.getM().get(predicate.getAttrName()), alpha, predicate);
            return neProof;

        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_FINALIZE_PRIMARY_NE_PROOF_FAIL);
        }
    }
// 검증
//    private static PrimaryEqualProof finalizeEqProof(PrimaryEqualInitProof eqInitProof1,
//                                                     BigInteger challenge1,
//                                                     CredentialSchema credSchema1,
//                                                     NonCredentialSchema nonCredSchema,
//                                                     CredentialValues credValues1,
//                                                     SubProofRequest subProofReq) throws ZkpException {
//
//        Map<String, BigInteger> r = new HashMap<>();
//        r.put("last_name", new BigInteger("84120755121673281786039752302106370959086220217339740538280097708090247635190259230177372792997871569605316729257110382690561782764865112060452673855309553610866812727652475254375586752895693526462947547583392335655354926220624806232242340491067794049952637693405354055408569250861598812920466127344909531933311856874763094390026935861576537241778516589456422194386000501559803954507554626736063708735998576932610067561198578122217454586783059123575780486212508848503825921587744029113926758152965590574108065945652796070266755922972414800751504686771827833092552383081239960161549857894785851913602364190537051570331"));
//        r.put("salary", new BigInteger("21013616439953057851459836625041564209183609275353846766192145535039056152079200296409359950631376518832819483831914888800349942178370250460351109198095564110232559728904537954939021992864893153558220286929586279331039275078764655518666470559754313557667304595735661586015817924160688942318306616565135968206035241338197352959940256314688849963120411977624590287674322671920485455197993282987715735486434892001277664902076551579462417830591369506914986031280183771993307795695658137033953459655543743171438301288719634026403710888521187696574493571596181556075881842643102175092536735014852218319603197756309307465096"));
//        r.put("employee_status", new BigInteger("102574874199111672909776892628566193598699055294639940675323658450406157850072201100229114804398487187533027105602672021139770736849913787331513035086714389180967829033365545380473334935812639633841166870392231729938401521128918804877200789547796858059198361074286353139756556475242226334649247968403629223181400361505694171043105006074755503189215690983008238697339814285545196763487076036324047000294486012178564649835638633488140360467378248418153593884315462040379367862729010602621960847293258704897978351104849916066397205302672757385457880351310728203861788387724498344675213225556558089934678707622583281878590"));
//        r.put("first_name", new BigInteger("25323981277104386824971491183732563808738818819060566488328265747234963296849562595655810165070117211940073632696606119420601926141973490824513167214927334113053245183911842658908822048110831289782226077493221883190825917668847012557277737782605127695200377323795065995136117327368920620311711093167763176838428068627524415841759375345639410480129246034004225534636338662004682987180123910613170849225347765462587985062454638693787567706907091281041594644150649305566189513419611476664809354799633816720772948737766987341650668734547011826966631276524821174996603041975181650959248755746892553070005976335635096699043"));
//        r.put("experience", new BigInteger("81209143330054815232043864000450190297604663738648665730733744083850863364705291101503461697260604678420938279196644083166071575756267768751786042156004794645470531072114048072407556721380267166713392694618252163907058002832290298760305236502919410145548460699101296860367123591332941640361542364900568932027668464834649638774763133093331959774868498886081806893181616451063540713967335542651359549104605985480666155575756568103666066741006185041715220066579018740266091230468496312860661068007167446330158506673044467744137784516636211581150967389342470961308899043984886118262381856839358299590345813054745809039329"));
//        r.put("master_secret", new BigInteger("91028775489049260061913017493582940214407826938860206484576366933753609386992721821847119713878785937066977464698744724114736186246119310347034397861928990153683901570118933140268222842504181615584807634816350326877499618325323055980244583957949582638033290935220455490426732232895694012378893921352640776453467797746385723759731304291639076291364865051529451577079717189158180516460297449182757994250033513839858513363831811510066501864753523779338084772602931544400587268735204152109465182493965085846189937422413555730579737604430184640778643794725915854867939620004160112126271556961998886791416870364358048948227"));
//        BigInteger rctxt = new BigInteger("32971459084344953221422329890952022186176628534273207637630222918756390485765322309717811239465712325760697628637239485017796122144580796031330957138409611121813042569577535170140926940332232298243121970157460030243433746013127079486919393012212328126547576821046007471246865850543177351293607189666179296361770712505955180482168913305063484405052381940823136288709876793186910983977210053469985949165716596791670006089473403007730123379255237079259193129531421805799617425316987754076717608752504068458981808372640951736372678836458337327530895952569874456101766206580441754227767278379583075875451981396705752727906");
//        BigInteger a_prime = new BigInteger("101139292671716543753816951599590311920834147667365402229304760935298039450122746278746441227731880197696240573632484329817876075524020257692428375812214587287611677466250411695061299100362132098474844405184703019351884140266036511731954322620008765961263354063960991698069862901595341913008370344644262601837928255291103991232692372054319970444159379623373554408188672267475922250678197391841281672117410790662438798743494714430691001792561235365437863773149748341254672340167368675526988977043506627391311789916497395170233722389955550633959871524244873113574423748261335142315567616792544754544141758199955397501393");
//
//
//        Map<String, BigInteger> m_tilde = new HashMap<>();
//        m_tilde.put("experience", new BigInteger("3711853002376907828151878050364320887388853395423832389819864948590356647048988355828104558074047289886092328208895305722808060303810041521836134531157432966577393416548864242631"));
//        m_tilde.put("first_name", new BigInteger("514341149772954452590634181799365993988883594407763272827512175189061351551537345055205154003123304900897288999817372742056784556201490500006084472546648891514461674774755974513"));
//        m_tilde.put("last_name", new BigInteger("19787180843539818307250486423548446685870962061508924144134816350934441838729762736221334189995782281851085318241980040239714721463258141802270694889943040046611033462819148877"));
//        m_tilde.put("master_secret", new BigInteger("15381939589860089910529569956085178509808005262111744647708968504368628833509046651172982701451784701503878807040992106469611912521326166494706185603236633260954266750014134704039"));
//        m_tilde.put("salary", new BigInteger("15298375788983779803922842626916986457900051026983371911407028603316765921483307452552882357420701407203114800687616293486881928869546793628842278975686068458648202325578412319116"));
//
//        BigInteger m2_tilde = new BigInteger("10095008831755370568921444978898660143420196689847622466264813368958828339478");
//        BigInteger e_tilde = new BigInteger("158902792528180356790439675366657770198522909153754428046262237222473673367203648899865790706911082185284924319921215941390208847193164647");
//        BigInteger v_tilde = new BigInteger("280371062070570882850273111929408718840602886790587501007356419522701134474433896889260206663330504343433589220750225323955149917666087264333075968900528148464402554069237261010226771224818703079357401487137349970337714203464332181541391706399571479769714178053607554728725286284183655523453314978054874678070594247174459523018896875105677459223124660158456492081173087431313418685652131065386078989980444482099441841615299992426826264819930842434270730272207888814100773866481320932521935549454166519720827488465306384880759218044186388706977260184927415726101196397458100121987559027497359099882040739573222157461688220625799225616177328830125004306627690715283299171594156050887377991155719268169688412905311018017167890805900738824738314762780634682120368203616952645106144173213961309614021360491400712530230390479771151712158735425138904616895847115244057119311812043024550509986573682676233842634499603818653351646");
//
//        BigInteger challenge = new BigInteger("21139197356546196026588953869114719712974438125276842049321554412570497581832");
//        BigInteger e_prime = new BigInteger("187221091705885665153674525778207663");
//        BigInteger v_prime = new BigInteger("449842633084731759457297962006537682582204684405432559513829228769896178037946748509940473835919675534404719019857546931204602442379508081538374232503398200312621330554638398013763533259309654741257643163622070861787305340754656422213946510221279106049796183533761805378456055869160275271763405481882331942977136316999251484903982017130430865593313544219209200323912402757235213225403949979460465281399169480050761092732734501976030133333436611471580477903250259917999876403227148276401160253452588507938810692066586528151600507425181157674250751129392565836358276403589428091349227866750473404551509671714271075530798710148644877211514554453466553157892908155948957435246365707311442865893734198273919814449084431045882073013701180286309742588471783744713515439103531285290170255481262433916851202636264808949225848719");
//        BigInteger e = challenge.multiply(e_prime);
//        BigInteger e_hat = e_tilde.add(e);
//
//        BigInteger v = challenge.multiply(v_prime);
//        BigInteger v_hat = v_tilde.add(v);
//        BigInteger _m2 = new BigInteger("341140011640752572633147052081171498956367443383470374401078787842697977955");
//
//
//        Set<String> unrevealedAttrs = new HashSet<>();
//        for (String key : nonCredSchema.getNonCredSchema()) {
//            unrevealedAttrs.add(key);
//        }
//
//
//        Set<String> getAttrNames = new HashSet<>();
//        getAttrNames.add("employee_status");
//        getAttrNames.add("experience");
//        getAttrNames.add("first_name");
//        getAttrNames.add("last_name");
//        getAttrNames.add("salary");
//
//
//        TreeSet<String> revealedAttrs = new TreeSet<>();
//        revealedAttrs.add("employee_status");
////        private HashSet<Predicate> predicates;
//
//        for (String key : getAttrNames) {
//            for (String attr : revealedAttrs) {
//                if (!key.equalsIgnoreCase(attr)) {
//                    unrevealedAttrs.add(key);
//                }
//            }
//        }
//
//        Map<String, CredentialValue> credValues = new HashMap<>();
//        CredentialValue c1 = new CredentialValue(AttributeType.Known, new BigInteger("214313123123213213"));
//        credValues.put("employee_status", c1);
//
//        CredentialValue c2 = new CredentialValue(AttributeType.Known, new BigInteger("10"));
//        credValues.put("experience", c2);
//
//        CredentialValue c3 = new CredentialValue(AttributeType.Known, new BigInteger("1139481716457488690172217916278103335"));
//        credValues.put("first_name", c3);
//
//        CredentialValue c4 = new CredentialValue(AttributeType.Known, new BigInteger("5321642780241790123587902456789123452"));
//        credValues.put("last_name", c4);
//
//        CredentialValue c5 = new CredentialValue(AttributeType.Hidden, new BigInteger("33933041731096191720731408836349235003331618631974893709239705952432555068128"));
//        credValues.put("master_secret", c5);
//
//        CredentialValue c6 = new CredentialValue(AttributeType.Known, new BigInteger("2400"));
//        credValues.put("salary", c6);
//
//        Map<String, BigInteger> m_hat = new HashMap<>();
//        for (String key : unrevealedAttrs) {
//            BigInteger cur_m_tilde = m_tilde.get(key);
//            if (cur_m_tilde == null) {
//                throw new ZkpException(ErrorCode.NULL, "Value by key '{}' not found in init_proof.mtilde");
//            }
//            CredentialValue cur_value = credValues.get(key);
//            if (cur_value == null) {
//                throw new ZkpException(ErrorCode.NULL, "Value by key '{}' not found in attributes_values");
//            }
//
//
//            m_hat.put(key, cur_value.getValue().multiply(challenge).add(cur_m_tilde));
//        }
//
//        // 동준
////        BigInteger m2 = eqInitProof.getM2_tilde();
//        BigInteger m2 = _m2.multiply(challenge).add(m2_tilde);
//        System.out.println("이 값이 뭘까?: "+ m2);
//
//        //TODO: 위의 for문과 병합이 불가능한가? -init 을 분석해봐야 알듯
//        Map<String, BigInteger> revealedAttrsWithValue = new TreeMap<String, BigInteger>();
//        for (String key : subProofReq.getRevealedAttrs()) {
//            CredentialValue credValue = credValues.get(key);
//            //TODO: 해당 예외처리가 의미가 있는가?
//            if (credValue == null) {
//                throw new ZkpException(ErrorCode.NULL, "InvalidStructure: Encoded value not found");
//            }
//            revealedAttrsWithValue.put(key, credValue.getValue());
//        }
//
//        PrimaryEqualProof primaryEqualProof = new PrimaryEqualProof(revealedAttrsWithValue,
//                a_prime,
//                e_hat,
//                v_hat,
//                m_hat,
//                m2);
//
//        return primaryEqualProof;
//    }

    private static PrimaryEqualProof finalizeEqProof(PrimaryEqualInitProof eqInitProof,
                                                     BigInteger challenge,
                                                     CredentialSchema credSchema,
                                                     NonCredentialSchema nonCredSchema,
                                                     CredentialValues credValues,
                                                     SubProofRequest subProofReq) throws ZkpException {

        // For each credential C = (I = {mj }, A, e, v) and Issuer’s public key pkI compute
        try {
            final BigInteger a_prime = eqInitProof.getAPrime();
            final BigInteger e_tilde = eqInitProof.getETilde();
            final BigInteger v_tilde = eqInitProof.getVTilde();
            final Map<String, BigInteger> m_tilde = eqInitProof.getM_tilde();

            BigInteger e = challenge.multiply(eqInitProof.getEPrime());
            BigInteger e_hat = e_tilde.add(e);

            BigInteger v = challenge.multiply(eqInitProof.getVPrime());
            BigInteger v_hat = v_tilde.add(v);

            Set<String> unrevealedAttrs = new HashSet<String>();
            // for 연산식 문제로 set 합집합, 차집합 연산 처리
            Set<String> nonCredSchemaList = nonCredSchema.getNonCredSchema();
            List<String> credSchemaList = credSchema.getAttrNames();
            Set<String> subProofReqAttrsList = subProofReq.getRevealedAttrs();

            unrevealedAttrs.addAll(nonCredSchemaList);
            unrevealedAttrs.addAll(credSchemaList);
            unrevealedAttrs.removeAll(subProofReqAttrsList);

            Map<String, BigInteger> m_hat = new HashMap<String, BigInteger>();
            // For all j ∈ Ar compute
            for (String key : unrevealedAttrs) {

                BigInteger cur_m_tilde = m_tilde.get(key);
                if (cur_m_tilde == null) {
                    throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "Value by key '{}' not found in init_proof.mtilde in finalizeEqProof");
                }
                CredentialValue cur_value = credValues.get(key);
                if (cur_value == null) {
                    throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "Value by key '{}' not found in attributes_values in finalizeEqProof");
                }

                m_hat.put(key, cur_value.getValue().multiply(challenge).add(cur_m_tilde));
            }

//        BigInteger m2_tilde = eqInitProof.getM2Tilde();

            // djpark0402 20210521
            BigInteger m2 = eqInitProof.getM2().multiply(challenge).add(eqInitProof.getM2Tilde());

            Map<String, BigInteger> revealedAttrsWithValue = new TreeMap<String, BigInteger>();

            for (String key : subProofReq.getRevealedAttrs()) {
                CredentialValue credValue = credValues.get(key);

                if (credValue == null) {
                    throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "InvalidStructure: Encoded value not found in finalizeEqProof");
                }
                revealedAttrsWithValue.put(key, credValue.getValue());
            }
            // The values PrC = (e_hat, v_hat, m_hat, A′ ) are the sub-proof for credential C
            PrimaryEqualProof primaryEqualProof = new PrimaryEqualProof(revealedAttrsWithValue,
                    a_prime,
                    e_hat,
                    v_hat,
                    m_hat,
                    m2);

            return primaryEqualProof;

        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_FINALIZE_PRIMARY_EQ_PROOF_FAIL);
        }
    }
}
