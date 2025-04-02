package org.omnione.did.wallet.zkp.util.generator.proof;

import org.omnione.did.wallet.zkp.ZkpConstants;
import org.omnione.did.wallet.zkp.data.CredentialValue;
import org.omnione.did.wallet.zkp.data.Predicate;
import org.omnione.did.wallet.zkp.data.SubProofRequest;
import org.omnione.did.wallet.zkp.data.credential.PrimaryCredentialSignature;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.data.proof.CredentialValues;
import org.omnione.did.wallet.zkp.data.schema.CredentialSchema;
import org.omnione.did.wallet.zkp.data.schema.NonCredentialSchema;
import org.omnione.did.wallet.zkp.data.subproof.primaryproof.PrimaryEqualInitProof;
import org.omnione.did.wallet.zkp.data.subproof.primaryproof.PrimaryInitProof;
import org.omnione.did.wallet.zkp.data.subproof.primaryproof.PrimaryPredicateInequalityInitProof;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.util.BigIntegerUtil;
import org.omnione.did.wallet.zkp.util.CommitmentHelper;

import java.math.BigInteger;
import java.util.*;

public class ProofInitiator {

    public static PrimaryInitProof initPrimaryProof(CredentialPrimaryPublicKey publicKey,
                                                    CredentialSchema credSchema,
                                                    NonCredentialSchema nonCredSchema,
                                                    SubProofRequest subProofReq,
                                                    CredentialValues credValues,
                                                    PrimaryCredentialSignature credSign,
                                                    BigInteger m2_tilde,
                                                    Map<String, BigInteger> commonAttributes) throws ZkpException {

        try {
            PrimaryEqualInitProof eqProof = initEqProof(publicKey, credSchema, nonCredSchema, credSign, subProofReq, m2_tilde, commonAttributes);

            Vector<PrimaryPredicateInequalityInitProof> neProofs = generateInitNeProofs(publicKey, eqProof.getM_tilde(), credValues, subProofReq);

            return new PrimaryInitProof(eqProof, neProofs);
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_INITIALIZE_PRIMARY_PROOF_FAIL);
        }
    }

    //Wrapper Method
    private static <T extends PrimaryPredicateInequalityInitProof> Vector<T> generateInitNeProofs(CredentialPrimaryPublicKey publicKey,
                                                                                                  Map<String, BigInteger> m_tilde,
                                                                                                  CredentialValues credValues,
                                                                                                  SubProofRequest subProofReq) throws ZkpException {
        Vector<T> neProofs = new Vector();
        for (Predicate predicate : subProofReq.getPredicates()) {
            neProofs.add((T)initNeProof(publicKey, m_tilde, credValues, predicate));
        }
        return neProofs;
    }

    private static PrimaryPredicateInequalityInitProof initNeProof(CredentialPrimaryPublicKey publicKey,
                                                                   Map<String, BigInteger> m_tilde,
                                                                   CredentialValues credValues,
                                                                   Predicate predicate) throws ZkpException {

        try {
            BigIntegerUtil generator = new BigIntegerUtil();

            // Load Z, S from issuer’s public key.
            final BigInteger z = publicKey.getZ();
            final BigInteger s = publicKey.getS();

            final BigInteger n = publicKey.getN();

            CredentialValue credValue = credValues.get(predicate);

            if (credValue == null) {
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "Value by key " + predicate.getAttrName() + " not found in cred_values");
            }

            int delta = predicate.getDelta(credValue);

            //TODO: 0은 왜 정상인지 재고려
            if (delta < 0) {
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_UNKNOWN, "Predicate is not satisfied");
            }

            // ∆ ← mj − zj 라고 하고 ∆ = (u1)^2 + (u2)^2 + (u3)^2 + (u4)^2가 되도록 u1, u2, u3, u4를 찾는다.

            int[] uList = BigIntegerUtil.four_squares(delta);

            Map<String, BigInteger> u = new HashMap<String, BigInteger>();
            Map<String, BigInteger> r = new HashMap<String, BigInteger>();
            Map<String, BigInteger> t = new HashMap<String, BigInteger>();

            Vector<BigInteger> cList = new Vector<BigInteger>();

            for (int i = 0; i < uList.length; i++) {
                int value = uList[i];
                String index = String.valueOf(i);
                // Generate random 2128-bit numbers r1, r2, r3, r4, r∆, compute
                BigInteger cur_r = generator.createRandomBigInteger(ZkpConstants.LARGE_VPRIME);

                BigInteger cur_t = CommitmentHelper.commitment(z, BigInteger.valueOf(value), s, cur_r, n);

                u.put(index, BigInteger.valueOf(value));
                r.put(index, cur_r);
                t.put(index, cur_t);
                // add these values to cList
                cList.add(cur_t);
            }

            BigInteger rDelta = generator.createRandomBigInteger(ZkpConstants.LARGE_VPRIME);
            // T∆ ← Z∆ Sr∆
            BigInteger tDelta = CommitmentHelper.commitment(z, BigInteger.valueOf(delta), s, rDelta, n);

            r.put(ZkpConstants.DELTA, rDelta);
            t.put(ZkpConstants.DELTA, tDelta);
            // add these values to cList
            cList.add(tDelta);

            //////////////////////////////////////////////////////////////////////////////////////////////////////

            Map<String, BigInteger> u_tilde = new HashMap<String, BigInteger>();
            Map<String, BigInteger> r_tilde = new HashMap<String, BigInteger>();


            for (int i = 0; i < ZkpConstants.ITERATION; i++) {
                // generate random 592-bit numbers u1,u2,u3,u4
                u_tilde.put(Integer.toString(i), generator.createRandomBigInteger(ZkpConstants.LARGE_UTILDE));
                // generate random 672-bit numbers r1 , r2 , r3 , r4 , r∆ compute
                r_tilde.put(Integer.toString(i), generator.createRandomBigInteger(ZkpConstants.LARGE_RTILDE));
            }
            r_tilde.put(ZkpConstants.DELTA, generator.createRandomBigInteger(ZkpConstants.LARGE_RTILDE));


            // Generate random 2787-bit number α and compute
            BigInteger alpha_tilde = generator.createRandomBigInteger(ZkpConstants.LARGE_ALPHATILDE);
            BigInteger mj = m_tilde.get(predicate.getAttrName());

            if (mj == null) {
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "Value by key " + predicate.getAttrName() + " not found in eqProof.mTilde");
            }
            // add this values to T in the order T1, T2, T3, T4, T∆.
            Vector<BigInteger> tList = BigIntegerUtil.calc_tne(publicKey, u_tilde, r_tilde, mj, alpha_tilde, t, predicate.isLess());

            if (tList == null) {
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_CALCULATE_TNE_FAIL);
            }

            return new PrimaryPredicateInequalityInitProof(cList, tList, u, u_tilde, r, r_tilde, alpha_tilde, predicate, t);

        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_INITIALIZE_PRIMARY_NE_PROOF_FAIL);
        }
    }

    private static PrimaryEqualInitProof initEqProof(CredentialPrimaryPublicKey publicKey,
                                                     CredentialSchema credSchema,
                                                     NonCredentialSchema nonCredSchema,
                                                     PrimaryCredentialSignature credSign,
                                                     SubProofRequest subProofReq,
                                                     BigInteger m2_tilde,
                                                     Map<String, BigInteger> commonAttributes) throws ZkpException
    {
        try {
            BigIntegerUtil generator = new BigIntegerUtil();

            // 6.2.4 ProveCL
            // Choose random 2128-bit r;
            BigInteger r = generator.createRandomBigInteger(ZkpConstants.LARGE_VPRIME);

            BigInteger e_tilde = generator.createRandomBigInteger(ZkpConstants.LARGE_ETILDE);
            BigInteger v_tilde = generator.createRandomBigInteger(ZkpConstants.LARGE_VTILDE);

            // unrevealedAttrs 연산
            Set<String> unrevealedAttrs = new HashSet<String>();

            for (String value : nonCredSchema.getNonCredSchema()) {
                unrevealedAttrs.add(value);
            }

            for (String value : credSchema.getAttrNames()) {
                if (!subProofReq.getRevealedAttrs().contains(value)) {
                    unrevealedAttrs.add(value);
                }
            }

            System.out.println(unrevealedAttrs);

            Map<String, BigInteger> m_tilde = new HashMap<String, BigInteger>();
            m_tilde.putAll(commonAttributes);

            // 공개되지 않은 각 속성에 대해 i ∈ A는 임의의 592비트 숫자 m1을 생성합니다.
            // generate m_tilde <- by unrevealedAttrs
            for (String key : unrevealedAttrs) {
//            ZkpLogger.debug("unrevealedAttrs ..... "+key);
                if (key.equals(ZkpConstants.MASTER_SECRET_KEY)) {
                    continue;
                }
                m_tilde.put(key, generator.createRandomBigInteger(ZkpConstants.LARGE_MVECT));
            }

            BigInteger s = publicKey.getS();
            BigInteger n = publicKey.getN();

            BigInteger a = credSign.getA();
            BigInteger e = credSign.getE();
            BigInteger v = credSign.getV();

            // a' = A*S^r (mod n) 인데, 성능 향상을 위한 {(S^r mod n)*(A mod n)} mod n 작업 처리
            // A′ ← A * S^r (mod n)
            BigInteger a_prime = s.modPow(r, n).multiply(a.mod(n)).mod(n);

            //  e′ ← e - 2^596.
            BigInteger e_prime = e.subtract(ZkpConstants.LARGE_E_START_VALUE);
            // v′ ← v−e * r in integers.
            BigInteger v_prime = v.subtract(e.multiply(r));

            BigInteger t = BigIntegerUtil.calc_teq(publicKey, a_prime, e_tilde, v_tilde, m_tilde, m2_tilde, unrevealedAttrs);
            if (t == null) {
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_CALCULATE_TEQ_FAIL);
            }

            PrimaryEqualInitProof primaryEqualInitProof = new PrimaryEqualInitProof(a_prime,
                    t,
                    e_tilde,
                    e_prime,
                    v_tilde,
                    v_prime,
                    m_tilde,
                    m2_tilde,
                    credSign.getM2());
            return primaryEqualInitProof;
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_INITIALIZE_PRIMARY_EQ_PROOF_FAIL);
        }
    }
//    {
//
//        BigIntegerUtil generator = new BigIntegerUtil();
//
//        // 6.2.4 ProveCL
//        BigInteger r = new BigInteger("38312680185599014090679322288025721687417593680764981709595390126717769899946360945328334525501178676253208442908968493232629571175813198072559447499616926652707756010369652235308819831198547994919364958360740896032461684514904213411175527964307521634625633274916242141662778303224963097834230525387688354197901748234707987294055362627561115575899314168034139530100340504639664891799252693076238026316393366563046262422159643325220174173739314224252115831192505873556199183248831450682384556640585478862611572717936255973665909200087695323230746140195721396330018989215282312404447735747807645563386711287525548403518494451962478428035990140");//generator.createRandomBigInteger(ZkpConstants.LARGE_VPRIME);
//        BigInteger e_tilde = new BigInteger("131789027254831403604055852886368409911318231249477095427927423016779884894425369505878642448688097810625730282507712566237395769192880102");//generator.createRandomBigInteger(ZkpConstants.LARGE_ETILDE);
//        BigInteger v_tilde = new BigInteger("1297949122853949753162594995579844633156565018511615732508928270936517842342997881421247681892053496069995768310690201804835034781613684452663367693628848543181343915039178288115892860762563125498338496458581035347674853112031697791790800513698437371337337647443826062660210054069750375534968942479123102042458467059044034789625097054701286284699225615827362161436611121259031710008822620281491807651382584634651378652104971595784249593138787488228507195479801699507412742852524994785876805434782351817919325598967711787914351095284516260074570949771595609374498211401823716271945485392711028276818062630101753267192528182455386248116048487511751107540021403083359407460040239569243200001897278446979300881049877966888501012098574799918716552053917166887438873268170865414077155940997503839796321761157946299432252369209783941266694967450844501954112061844751315811091356695108743136692903847420647249493079135869605031075");//generator.createRandomBigInteger(ZkpConstants.LARGE_VTILDE);
//
//        // unrevealedAttrs 연산
//        Set<String> unrevealedAttrs = new HashSet<String>();
//
//        for (String value : nonCredSchema.getNonCredSchema()) {
//            unrevealedAttrs.add(value);
//        }
//
//        for (String value : credSchema.getAttrNames()) {
//            if (!subProofReq.getRevealedAttrs().contains(value)) {
//                unrevealedAttrs.add(value);
//            }
//        }
//
//        Map<String, BigInteger> m_tilde = new HashMap<String, BigInteger>();
//        m_tilde.put("master_secret", new BigInteger("12372426671457080963073249005695221689363456107428361290913020776393917847314795473768166350291662655307769528637087085413614623458889091052537296590409579846700895635176241158420"));
//        m_tilde.put("salary", new BigInteger("12372286032705421794458539041418618914242089656541280063341646861251301432209949716887517935385609950668703012095205561081222152449481049730452829106058128643814948763882268675983"));
//        m_tilde.put("department", new BigInteger("12213941025059775217734664774598544853725926643428349009929070404704233925275527756971906187959248492191180870337742954455990975289959024082365293954980868424158180369969816165977"));
//        m_tilde.put("experience", new BigInteger("8769493257346299165917235070936603292686416432212433994014990106440530330885302112345886709267293439206198351629751542283400727689534617775369914895481138327654930119065501456361"));
//        // generate m_tilde <- by unrevealedAttrs
////        for (String key : unrevealedAttrs) {
////            System.out.println("unrevealedAttrs ..... "+key);
////            m_tilde.put(key, generator.createRandomBigInteger(ZkpConstants.LARGE_MVECT));
////        }
//
//
//        BigInteger s = new BigInteger("8598297463318199149134078974868571445027914312929069782472229561400718024869148304437881427080128642826883847950284325837078883331359817411549004651153233122161918650890037584202659637015177228689355114099315196911793822743331222907981759146412808362666394042164454717542039895239762285008416501677108970686937982865227867190846782776881463053445097691913684328777695420052504374059525560659522136844911113858891005294881603328746099042939813801993965305236023873008868314292694257483147772992784599392580082568086579020285700919411276180971330565086516062009964015438640577521429153481088206222437741191734463046367");//publicKey.getS();
//        BigInteger n = new BigInteger("103477756773321781470602257130908061956660706558155350276615355125616944893893399942371300476477092120665892538459090422888810389912049602706677015315473017668719291705022051801018494246942192717057622622701467522977131003454808661976774131147969160725556874260283295857410780109363793502417995193735421884293925039172911383215695498387519734812050292066716860348836891955429960972496243942112398415015378349600957954439097213347838158612230366951436571842465002480719644614655795523364697528773657182019745647476849735908482402696878678150641974927612651414434345300668928722290458191368152628699929705576090602520749");//publicKey.getN();
//        BigInteger a = new BigInteger("20068681533666208282737265379641426953648053168141435752607580759094680522100951710309416402401021883212582065962057613180384497453274782995152167169410144947640169172890501921949146935163460846595427034446933463292782442638050331830993591001432600196122397398858350489722866949129115636216991715380952087579024243088225738398961869190748305874205361628257201215715120503920521954272410850594809545418831409025000056740080753473566573590658472341787524278390578252862100826863184405145098791564312361063969074133947557520013028637459664055062669802655232219882727733958095791228242879936145946354117411627968005807294");//credSign.getA();
//        BigInteger e = new BigInteger("259344723055062059907025491480697571938277889515152306249728583105665800713306759149981690559193987143012367913206299323899696942213235956742929749500884840429067637059496135772629");//credSign.getE();
//        BigInteger v = new BigInteger("56657686980636350993827947603749220377184612042076226458914486234238385185457325951462782900289446881793944613476985655450080309503572472898085808356591331201637322052526318986895773164437376637311482199959527668598636250221934381084701381639189455336832881014992213495925257659474921837429802611393005510705661254090739937116847237202395245828001350738771148723836042857080969223776935589850723353336826128424118614170605930030907879228318915889753667872738616377006023705384424511702959884157531051750786660688142073854261198438735042007159420226963964738220628742289863827883612334961483156624145945169208038418527331076701430529132870942");//credSign.getV();
//
//        // a' = A*S^r (mod n) 인데, 성능 향상을 위한 {(S^r mod n)*(A mod n)} mod n 작업 처리
//        BigInteger a_prime = s.modPow(r, n).multiply(a.mod(n)).mod(n);
//        System.out.println("a_prime ..... "+a_prime.toString());
//        BigInteger e_prime = e.subtract(ZkpConstants.LARGE_E_START_VALUE);
//        System.out.println("e_prime ..... "+e_prime.toString());
//        BigInteger v_prime = v.subtract(e.multiply(r));
//        System.out.println("v_prime ..... "+v_prime.toString());
////        System.out.println("m2_tilde ..... "+m2_tilde.toString());
//
//        publicKey.setRctxt(new BigInteger("102753481930617300564869246948502159387262029920338424711270967038523803552516960401188942035387844694323514884793722651362255267285612282295974947661392710148379526387907918574948364252357044101409420279401100230548279025475487245088360998476349471612694246185275958946304931645453896662054333561043179829075918868632867104160726888212759533509272364537581349863032651038992619468967227190545368909111201733351825410737430967702361913788171235387974444553417204250448022239381794354277589272203293867843387611566085009741333087138808931253004367357041636311118649008834382045219352381890872301875853135299265628825681"));
//        publicKey.setN(n);
//        publicKey.setS(s);
//
//
//        LinkedHashMap<String, BigInteger> rf = new LinkedHashMap<String, BigInteger>();
//        rf.put("salary",new BigInteger("57811146993288565608543709728252168975241331942691339954791175085711803914530160621523153023561697404017771394020773312058902713908892472121650109001727433078508529209856822884945402273072206034810372451710788406361546899963562686972099766781742732560643581287283760746645743331754177168838476121521572157544385937923623548416683152216972311418877733938550680149216241785101406288606349521435784091198091560380611037182877055904919095384070063870702555346060079848862927845109624931393896954824286244886240601717912770398773415722178047306159801237640971817889334280266834442850876941119988673307459484912950208629192"));
//        rf.put("name",new BigInteger("68749909968025460245648999861692099865179115418398170027667886124178193175621463256136691914414519628553283280437453946524790141145505050715999238643026180445770822519553127566392130199447195679473782323490791329002653241640784615902898485811468802901154933713954974535617666914876275645974117588666805093326019335835789695706585790382607351724808719583154963864785526674981584363642498836413324960809868506582237184426072671200949898778663031096907587991464047560635410085413180786988542851354265496569328137174300226422153320374565974421822050980268466293927053991129672183973696226689722677796781037388291270528637"));
//        rf.put("department",new BigInteger("11997982805134108318065426314823506143999607973502925081922275092316242051631761206958296494390344547309470048452624051707309106568942283841907841768966759777184635314540804550574853957251094596315563651646454732176954634724224489645684370113593987586447455755088559367341397994747671196387917052744768799543803895980879004619949799738813830164521659192982691580322262578879050980101471665254910512700321049830103991829692467236529014499306856088825331924375318877571932156357366155330488685569357606314651248053328452098000330888912710582515266772424183785320581071233007204859928405223976315382008607642954529093371"));
//        rf.put("experience",new BigInteger("73361233693323167186510990759221825095470670239647792880781318817133594270740012912378818083789161727533790844769605914148205901911227641233521191417874234218689150209752080601515776378709445437141657713100711196736626136336010963680169714713695761353735493300917934221461759910512080625970534598423851389462707508368674417618572744209117428363348890700219717351770703678531221867875253094602279414636545593678981744651378666692892454345148792955863496352346402048470925688597400327083984234001150469122594559518389094141130801110698535876224507186395921672189379506068604995040831643342532991043879063599822059150635"));
//        rf.put("master_secret",new BigInteger("5225335863209357271490224615871090635997731395568054334653439038747136131237890121288657152052667985951005996402858992836468832359313064281937297548420763600986455345178889458709912413075008838216244366949791680606730710159397115426851900014254571682699693670109756996639589998351501252268256095551329362478420784763306275118390787233173542519886219380738648674665079828991681837748970457448111874185531727625015879895131900938653594770825853481465208531861198584148801940220864585910752147522033916363634930612506167458917542347474398228093575433278194263094963521379682688088644456769647903504944694966351064295466"));
//        publicKey.setR(rf);
//
//        BigInteger t = BigIntegerUtil.calc_teq(publicKey, a_prime, e_tilde, v_tilde, m_tilde, new BigInteger("10095008831755370568921444978898660143420196689847622466264813368958828339478"), unrevealedAttrs);
//        System.out.println("t ..... "+t.toString());
//
//        PrimaryEqualInitProof primaryEqualInitProof = new PrimaryEqualInitProof(a_prime,
//                t,
//                e_tilde,
//                e_prime,
//                v_tilde,
//                v_prime,
//                m_tilde,
//                m2_tilde,
//                credSign.getM2());
//        return primaryEqualInitProof;
//    }

}
