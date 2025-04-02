package org.omnione.did.wallet.zkp.util.generator;

import org.omnione.did.wallet.eoscommander.crypto.digest.Sha256;
import org.omnione.did.wallet.zkp.ZkpConstants;
import org.omnione.did.wallet.zkp.data.CredentialPrimaryKeyPair;
import org.omnione.did.wallet.zkp.data.attribute.AttributeValue;
import org.omnione.did.wallet.zkp.data.credential.CredentialValues;
import org.omnione.did.wallet.zkp.data.credential.NonRevocationCredentialSignature;
import org.omnione.did.wallet.zkp.data.credential.PrimaryCredentialSignature;
import org.omnione.did.wallet.zkp.data.credentialrequest.BlindedCredentialSecrets;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPrivateKey;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.CredentialRevocationKeyPair;
import org.omnione.did.wallet.zkp.revoc.CredentialRevocationPublicKey;
import org.omnione.did.wallet.zkp.revoc.RevocationPrivateKey;
import org.omnione.did.wallet.zkp.revoc.WitnessSignature;
import org.omnione.did.wallet.zkp.revoc.data.GroupOrderElement;
import org.omnione.did.wallet.zkp.revoc.data.PointG1;
import org.omnione.did.wallet.zkp.revoc.data.PointG2;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpLogger;
import org.omnione.did.wallet.zkp.revoc.utils.AMCLUtils;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.BIG;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.PAIR;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.ROM;
import org.omnione.did.wallet.zkp.util.BigIntegerUtil;

import java.math.BigInteger;
import java.util.Map;

public class CredentialSignatureGenerator {

    public static PrimaryCredentialSignature generateCredential(CredentialPrimaryKeyPair credentialKeyPair,
                                                                CredentialValues credentialValues,
                                                                BlindedCredentialSecrets blindedSecret) {

        BigIntegerUtil generator = new BigIntegerUtil();

        final BigInteger u = blindedSecret.getU();
        final BigInteger s = credentialKeyPair.getPublicKey().getS();
        final BigInteger z = credentialKeyPair.getPublicKey().getZ();
        final BigInteger n = credentialKeyPair.getPublicKey().getN();
        final Map<String, BigInteger> r = credentialKeyPair.getPublicKey().getR();

        BigInteger e = generator.createRandomPrime(ZkpConstants.LARGE_E_START_VALUE, ZkpConstants.LARGE_E_END_RANGE, ZkpConstants.LARGE_E_MAX_BITS);
        //BigInteger e = new BigInteger("259344723055062059907025491480697571938277889515152306249728583105665800713306759149981690559193987143012367913206299323899696942213235956742929854367029634042700217682195917414249");
        BigInteger e_inverse = e.modInverse(credentialKeyPair.getPrivateKey().getP().multiply(credentialKeyPair.getPrivateKey().getQ()));

        BigInteger v_prime_prime = generator.createRandomBigInteger(ZkpConstants.LARGE_VPRIME_VPRIME - 1).setBit(ZkpConstants.LARGE_VPRIME_VPRIME - 1);
        //BigInteger v_prime_prime = new BigInteger("9651403114158385403134744473308057805159178958731139971952836285505845103824490399244970400885006058547284269832562929696896050284512779487609964125616380255564856912657489193996947421737028723056007287197504832725476558626203901161001022024277598890181048885702876429229216539561686577558289043928093922943211007611799949845252886064877615893509990907067996947252613152286414759225939348644387765687804380168781858642855855017054972108788258722027566010690208521689318817177111068582468595414103829143504970959053214388536695541679561598938964084401759934577228011321161685391735911110179852506745589054294017416994348453554000408151326431241545147557605055142726848029164954815982374228852181382408076107925270174834457348541715949744912611753841997968083361738408987565299771108486552817068514429392549836114325225043");
        //BigInteger US_vprime_vprime = u.multiply(s.modPow(v_prime_prime, n)).mod(n);
        BigInteger US_vprime_vprime = u.multiply(s.modPow(v_prime_prime, n));

        for (String key : credentialValues.getValues().keySet()) {
            AttributeValue attributeValue = credentialValues.getValues().get(key);
            US_vprime_vprime = US_vprime_vprime.multiply(r.get(key).modPow(attributeValue.getEncoded(), n)).mod(n);
        }

        BigInteger q = z.multiply(US_vprime_vprime.modInverse(n)).mod(n);
        BigInteger a = q.modPow(e_inverse, n);

        PrimaryCredentialSignature pCredSign = new PrimaryCredentialSignature(a, e, v_prime_prime);
        pCredSign.setQ(q);
        return pCredSign;
    }

    public static PrimaryCredentialSignature generateCredential(String proverId,
                                                                int revIdx,
                                                                CredentialPrimaryKeyPair credentialKeyPair,
                                                                CredentialValues credentialValues,
                                                                BlindedCredentialSecrets blindedSecret) throws ZkpException {

        /**
         * _sign_primary_credential()
         * */
        CredentialPrimaryPublicKey primaryPublicKey = credentialKeyPair.getPublicKey();
        CredentialPrimaryPrivateKey primaryPrivateKey = credentialKeyPair.getPrivateKey();

        final BigInteger u = blindedSecret.getU();
        final BigInteger s = primaryPublicKey.getS();
        final BigInteger z = primaryPublicKey.getZ();
        final BigInteger n = primaryPublicKey.getN();
        final Map<String, BigInteger> r = primaryPublicKey.getR();

        BigIntegerUtil generator = new BigIntegerUtil();
        // generate random v''
        BigInteger v_prime_prime = generator.createRandomBigInteger(ZkpConstants.LARGE_VPRIME_VPRIME - 1).setBit(ZkpConstants.LARGE_VPRIME_VPRIME - 1);
        // most significant bit eqaul 1 and random prime e
        BigInteger e = generator.createRandomPrime(ZkpConstants.LARGE_E_START_VALUE, ZkpConstants.LARGE_E_END_RANGE, ZkpConstants.LARGE_E_MAX_BITS);

        BigInteger US_vPrime_Prime = u.multiply(s.modPow(v_prime_prime, n)).mod(n);

        //I3. generate revocation context m2
        BigInteger credentialContext = getCredentialContext(proverId, String.valueOf(revIdx));

        // djpark 2021.04.30 primary 미구현된 부분 추가
        US_vPrime_Prime = US_vPrime_Prime.multiply(primaryPublicKey.getRctxt().modPow(credentialContext, n)).mod(n);

        for (String key : credentialValues.getValues().keySet()) {
            AttributeValue attributeValue = credentialValues.getValues().get(key);
            if (r.get(key) == null || attributeValue == null) {
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_GENERATE_PRIMARY_CREDENTIAL_FAIL, "{"+key+"} attribute is not exist in primaryPublicKey r");
            }

            US_vPrime_Prime = US_vPrime_Prime.multiply(r.get(key).modPow(attributeValue.getEncoded(), n)).mod(n);
        }

        BigInteger q = z.multiply(US_vPrime_Prime.modInverse(n)).mod(n);

        BigInteger e_inverse = e.modInverse(primaryPrivateKey.getP().multiply(primaryPrivateKey.getQ()));

        BigInteger a = q.modPow(e_inverse, n);

        PrimaryCredentialSignature pCredSign = new PrimaryCredentialSignature(a, e, v_prime_prime);
        pCredSign.setQ(q);
        pCredSign.setM2(credentialContext);

        return pCredSign;
    }
//    {
//        /**
//         * _sign_primary_credential()
//         * */
//        CredentialPrimaryPublicKey primaryPublicKey = credentialKeyPair.getPublicKey();
//        CredentialPrimaryPrivateKey primaryPrivateKey = credentialKeyPair.getPrivateKey();
//
//        final BigInteger privateP = new BigInteger("153859866867530319192942566915137623701072590815368407575612000459823863736531155065200056882660545540833406494112283043380675979719594301618563574282911800363709367606229957710301732580877582651738986868178025425558875878621413765350584890747340274485348395019468819579341377680445816706016277143537672718283");
//        final BigInteger privateQ = new BigInteger("168136367982193929534007366443133573270725380753888322414124773147750087582354228759122342681720044695737053497857946274897138041659645470196987254005564204886409457346388010838899586080709596364338457587870536460780460796028982826685483979998681175279623379026863618343553089110850041194004008303949258448173");
//
//        BigInteger rctxt = new BigInteger("64690257904530278963283296829756869475228052351480316676792473064836051154221422260891975293310223384031228040909084087285367761030867537320606060392555746853709955716755905801316139988894361841700315638204880789098872460058664239256415249471886968814055791272759905652362068890440142301156991054366306364245072212841292065176595692367681145434312202249481835156769642379532736383717055943256557076712868232888100035134192211677323251170239330400353196902694908091270636685004479147566509736892207376851890692962258451020337523102931156195241294481866610070645305146841611592746911587063491643713166077149896683939835");//primaryPublicKey.getRctxt();
//        final BigInteger u = new BigInteger("18209428112261920245073035008998030102052013840175413486251322631149584280959042614347944469227254740405440672591306409719010950717350470107488420619110670947639319329652708970174018872090948767655246851229068910900147904119440425417726816816459821334768671760846730229265713972029087580304513458679102243313425396808508981536089194667753815584245784277475097887573851043326726642642977838041800454158504863377706669550566633141048544233389718326859444778296739065322700986831413411807376747123672972140272582819866144768317088644774330051262618137082969206696630081536923806311474112846324589034847036515328736148388");//blindedSecret.getU();
//        final BigInteger s = new BigInteger("64711451941301081143202948725106598123207232339629835804034388844007261611965236925288963067660412871528458938640517393091041678019058936134467584374041747382547075919093566624647915858767058907964872131289145926909767179191501546877118881105886553307449879750152677756345984659076212778926093887889529861941228835119862751347736736137842114928790430086510118019049247164142583836982061421126055100324121729073586922703170640091338092501321614085069343375603884682238426826681655467161413603198708186341885440880736702176825122270202103129037925727017773388496907115713184382611942891928419640088138940495022534350267");// primaryPublicKey.getS();
//        final BigInteger z = new BigInteger("85985772818624612891952906210610755057486916628143739229015366480952729901465355904588744915814792616324982274977175714669255775560820631454625402210547327631099445839553846817023260724549671036326323755655023977607156356453461668244747130997217130828303312349184712150657942400442307809316571466190993298544083050475661947437623184079233230490286563412672598153367732763788113976504697093761053663475874259114657886703172507302932245959188771605630548489173093348919831945413260985421730346392791587410870289329572536679259425263942698667450575179753028439379885866359729815962242110954838425937918734408715145260368");//primaryPublicKey.getZ();
//        final BigInteger n = new BigInteger("103477756773321781470602257130908061956660706558155350276615355125616944893893399942371300476477092120665892538459090422888810389912049602706677015315473017668719291705022051801018494246942192717057622622701467522977131003454808661976774131147969160725556874260283295857410780109363793502417995193735421884293925039172911383215695498387519734812050292066716860348836891955429960972496243942112398415015378349600957954439097213347838158612230366951436571842465002480719644614655795523364697528773657182019745647476849735908482402696878678150641974927612651414434345300668928722290458191368152628699929705576090602520749");//primaryPublicKey.getN();
//
//        String sJson = "{\"License\":\"94038286668105406669401878918212501220366655483691721625619926111334483628490453782566752939069338922834642542102191236847944797079123908476558676790378611825553554744869504964545189490222558365553859836138515445788721008419693832323159132172053262779422768247860193484035249282230669364211213643745808107305009445966786162338274493892413337477621894741471456301421355825249672132921764273373004406087325556186189102765263447020715152613823425406879795164985573939815409150098518108823836270565426159891024335569035381207802857430905021099046940891964496433255468688310213740601194707507174885154663955446792385636305\",\"Name\":\"6043244117283422439413737424643218778208178672367108558833245158507724413016463523941540277599260180856311936098542050757686862739730154721064758508815108323638277999086324074327800349386359464981776755486493961708498400038284853654575376835257229584969327270924467648735931726594336098686156033372680535518785691211753691975590289693663786459198664700626267178291546782330955530067456189085700702663970315489560376450130795159221736938499147083573452322184330100939790986182383749081160469206640039106320881482985916168732768982901660061479011944001034867187681450549827270201727920440915997431114905633851315939018\",\"Address\":\"101937560053746696367744337687871294126003622684274322333962337530671108967474004896548210170035198829131370299756109124406383096710326106409082527374425725448767865210118427512151346804942067963644655797580924165578311317551799761697582670240378808130874645368670877103315667505459595757863925910341910835046374244834646669843464183963756821102326643645954703217022531017620331749083932927026271056687854877667491866598585721190204910966295790353809557463896463767794854165383553602959217282349051256591246453409060794549153125508857869052469176431611131882124304215764211909485359678107881252939176596252539269131015\",\"Age\":\"32659440286236742306502806314169871986183542104066464169683226331688651973177846045944837310947569365728145641818344094049128913813585291680865816396729385196793024701271964718444410772446803036593158286108360998005733437443193816941276062396332974371060738705876368291008943889199633616050272002692274061337830284952027909832034133934786838213643548537615906684666738671258744651419145050790796216177898834005345582649104987248631575594221417782376224031704864022033788795858506512675286444590196561984785590839901566875008052018980013224709789988026312915739911128747571197222729976665428974357491169610150933357643\",\"Grade\":\"4626142435021917481520519224765898926362475427857661781275942111901718589123340229297184882699741464443603875452379254501262311114268475629202867133689446889968617186904914732056636677599983099686657107537741976798949684934248905822033725448225925390286220077276563215618442958337542860965851062851775344691491511642524147133724331148359243739036459931618100933712572288887952187350350635293463023844138110477674567754780499029420479106818157670701264684790054845511194090198254799375251045295680840224643341674539440792238115873152636882768660547845735022135332908784215083859236300609934120221827855143104819684809\",\"master_secret\":\"61512278788706106466233220603365325432892859377037688359528040142978442744872157141585274475187663770713029168653279003330367840274739025443057558999879064662458628662565159893644821188241684088841600231140204427827208227380879466953323252493458756469006491456105626514637451217371172842095440542128892996614332935180351653780712705396436326678515066881164717065291978349042520254512740453486456507368093551486995462899542686163331893593436970312111159645653421331939736334452652048782548472231745614703932924926985156350641517011309397618157637361465824777937913867999467649105208441795244637025349961202595967361778\"}";
//
//        Type listType = new TypeToken<Map<String, BigInteger>>(){}.getType();
//        final Map<String, BigInteger> r = new Gson().fromJson(sJson, listType);//primaryPublicKey.getR();
//
//        BigInteger v_prime_prime = new BigInteger("32906504603338855272351900095414221307698749776541067814033210513531738181921411367507736055385522746519208728925929640861080229358247731293484597089143484437986845339134903534923743598324140850305660994706072264653211547756701238869292194422715284719304547215637090400721108126986612408393709778157836511391584429857214304172772021314142827443821029800161986815834210455075476582455412101609297286807464057800225414252463564031071990096650356638926304195440380071786846462663510736173454655681179168824688666646162184270353016670714140525865354464245178698315413135024573919431736493868135032875718830280939755811000524529839290066517678745");//generator.createRandomBigInteger(ZkpConstants.LARGE_VPRIME_VPRIME - 1).setBit(ZkpConstants.LARGE_VPRIME_VPRIME - 1);
//        BigInteger e = new BigInteger("259344723055062059907025491480697571938277889515152306249728583105665800713306759149981690559193987143012367913206299323899696942213235956742929710267774274621252609888574196432051");//generator.createRandomPrime(ZkpConstants.LARGE_E_START_VALUE, ZkpConstants.LARGE_E_END_RANGE, ZkpConstants.LARGE_E_MAX_BITS);
//
//        BigInteger rx = u.multiply(s.modPow(v_prime_prime, n)).mod(n);
//        System.out.println("rx 1: "+rx.toString());
//        // TODO REVOCATION 처리
//        BigInteger credentialContext = new BigInteger("341140011640752572633147052081171498956367443383470374401078787842697977955");//getCredentialContext(proverId, String.valueOf(revIdx));
//
//        /**
//         *
//         * rx = rx.mod_mul(p_pub_key.rctxt.mod_exp(cred_context, &p_pub_key.n), p_pub_key.n);
//         * */
//
//        // djpark
//        // mod_mul                                 // mod_exp
//        rx = rx.multiply(rctxt.modPow(credentialContext, n)).mod(n);
////        rx = rx.multiply(rctxt.modPow(credentialContext, n).mod(n));
//
//        System.out.println("rx 2: "+rx.toString());
//
//
//        Map<String, AttributeValue> attr = new HashMap<String, AttributeValue>();
//        AttributeValue atv1 = new AttributeValue();
//        atv1.setEncoded(new BigInteger("85727376323360356851739301479828700379633240616599214078873810281261493953192"));
//        atv1.setRaw("한글");
//        attr.put("License", atv1);
//
//        AttributeValue atv2 = new AttributeValue();
//        atv2.setEncoded(new BigInteger("82383310780985980348044268224467898559867473925114223095877705815616342974138"));
//        atv2.setRaw("english");
//        attr.put("Name", atv2);
//
//        AttributeValue atv3 = new AttributeValue();
//        atv3.setEncoded(new BigInteger("2"));
//        atv3.setRaw("2");
//        attr.put("Address", atv3);
//
//        AttributeValue atv4 = new AttributeValue();
//        atv4.setEncoded(new BigInteger("3"));
//        atv4.setRaw("3");
//        attr.put("Age", atv4);
//
//        AttributeValue atv5 = new AttributeValue();
//        atv5.setEncoded(new BigInteger("4"));
//        atv5.setRaw("4");
//        attr.put("Grade", atv5);
//
//
//        for (String key : attr.keySet()) {
//            System.out.println("for key: "+key);
//            AttributeValue attributeValue = attr.get(key);
//
//            System.out.println("for r.get(key): "+r.get(key));
//            System.out.println("for attributeValue.getEncoded(): "+attributeValue.getEncoded());
//            rx = rx.multiply(r.get(key).modPow(attributeValue.getEncoded(), n)).mod(n);
//        }
//
//        System.out.println("rx 3: "+rx.toString());
//
//        BigInteger q = z.multiply(rx.modInverse(n)).mod(n);
//        System.out.println("q: "+q.toString());
//        BigInteger e_inverse = e.modInverse(privateP.multiply(privateQ));
//        System.out.println("e_inverse: "+e_inverse.toString());
//
//
//        BigInteger a = q.modPow(e_inverse, n);
//        System.out.println("a : "+a.toString());
//
//        PrimaryCredentialSignature pCredSign = new PrimaryCredentialSignature(a, e, v_prime_prime);
//        pCredSign.setQ(q);
//        pCredSign.setM2(credentialContext);
//
//        return pCredSign;
//    }

    private static BigInteger getCredentialContext(String proverId, String idIndex) throws ZkpException {

        try {
            byte[] attrProverId = Sha256.from(proverId.getBytes()).getBytes();
            byte[] attrRevIdx = Sha256.from(idIndex.getBytes()).getBytes();

            attrProverId = AMCLUtils.reverse(attrProverId);
            attrRevIdx = AMCLUtils.reverse(attrRevIdx);
//        ArrayUtil.reverse(attrProverId);
//        ArrayUtil.reverse(attrRevIdx);

            byte[] result = new byte[attrProverId.length + attrRevIdx.length];
            System.arraycopy(attrProverId, 0, result, 0, attrProverId.length);
            System.arraycopy(attrRevIdx, 0, result, attrProverId.length, attrRevIdx.length);

            ZkpLogger.debug("credentialContext: " + BigIntegerUtil.getHash(result));

            return BigIntegerUtil.getHash(result);
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_GENERATE_CREDENTIAL_CONTEXT_FAIL);
        }
    }


    // Revocation 서명
    public static NonRevocationCredentialSignature generateRevocationCredentialSignature(String proverId,
                                                                                         int revIdx,
                                                                                         CredentialRevocationKeyPair credentialRevocationKeyPair,
                                                                                         RevocationPrivateKey revocationPrivateKey,
                                                                                         BlindedCredentialSecrets blindedSecrets) throws ZkpException {

        GroupOrderElement vr_prime_prime = new GroupOrderElement();
        GroupOrderElement c = new GroupOrderElement();
        GroupOrderElement m2 = new GroupOrderElement();

        // generate revocation context m2
        BigInteger credentialContext = getCredentialContext(proverId, String.valueOf(revIdx));
        m2.setBn(GroupOrderElement.from_bytes(BigIntegerUtil.asUnsignedByteArray(credentialContext)));

        // g_i
        PointG1 g_i = new PointG1();
        byte[] i_bytes = AMCLUtils.transform_i32_to_array_of_i8(revIdx);
        BIG pow = GroupOrderElement.from_bytes(i_bytes);
        pow = revocationPrivateKey.getGamma().getBn().powmod(pow, new BIG(ROM.CURVE_Order));
        g_i.getPoint().copy(credentialRevocationKeyPair.getPublicKey().getG().getPoint());
        g_i.setPoint(PAIR.G1mul(g_i.getPoint(), pow));

        CredentialRevocationPublicKey rPubKey = credentialRevocationKeyPair.getPublicKey();
        PointG1 sigma = new PointG1();
        sigma.getPoint().copy(rPubKey.getH0().getPoint());

        sigma.getPoint().add(PAIR.G1mul(rPubKey.getH1().getPoint(), m2.getBn()));

        GroupOrderElement x = new GroupOrderElement();
        x.getBn().copy(credentialRevocationKeyPair.getPrivateKey().getX().getBn());

        sigma.getPoint().add(blindedSecrets.getUr().getPoint());
        sigma.getPoint().add(g_i.getPoint());
        sigma.getPoint().add(PAIR.G1mul(rPubKey.getH2().getPoint(), vr_prime_prime.getBn()));

        BIG sum = x.add_mod(c.getBn());
        sum.invmodp(new BIG(ROM.CURVE_Order));
        sigma.setPoint(PAIR.G1mul(sigma.getPoint(), sum));

        PointG2 sigma_i = new PointG2();
        BIG bytes = GroupOrderElement.from_bytes(AMCLUtils.transform_i32_to_array_of_i8(revIdx));
        GroupOrderElement gamma = revocationPrivateKey.getGamma();
        GroupOrderElement sk = new GroupOrderElement();
        sk.getBn().copy(credentialRevocationKeyPair.getPrivateKey().getSk().getBn());

        PointG2 g_dash = credentialRevocationKeyPair.getPublicKey().getGDash();
        BIG result = sk.add_mod(gamma.getBn().powmod(bytes, new BIG(ROM.CURVE_Order)));
        result.invmodp(new BIG(ROM.CURVE_Order));
        sigma_i.setPoint(PAIR.G2mul(g_dash.getPoint(), result));

        // u_i
        PointG2 u = credentialRevocationKeyPair.getPublicKey().getU();
        pow = gamma.getBn().powmod(GroupOrderElement.from_bytes(AMCLUtils.transform_i32_to_array_of_i8(revIdx)), new BIG(ROM.CURVE_Order));
        PointG2 u_i = new PointG2();
        u_i.setPoint(PAIR.G2mul(u.getPoint(), pow));

        WitnessSignature witnessSignature = new WitnessSignature(sigma_i, u_i, g_i);
        NonRevocationCredentialSignature nonRevCredSign = new NonRevocationCredentialSignature(sigma, c, vr_prime_prime, witnessSignature, g_i, revIdx, m2);

        return nonRevCredSign;
    }
}

