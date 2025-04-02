package org.omnione.did.wallet.zkp.revoc.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

import org.omnione.did.wallet.zkp.revoc.data.GroupOrderElement;
import org.omnione.did.wallet.zkp.revoc.data.PointG1;
import org.omnione.did.wallet.zkp.revoc.data.PointG2;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpLogger;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.*;
import org.omnione.did.wallet.zkp.revoc.utils.acml.HASH256;
import org.omnione.did.wallet.zkp.revoc.utils.acml.RAND;

public class AMCLUtils {
    private static final BIG gx = new BIG(ROM.CURVE_Gx);
    private static final BIG gy = new BIG(ROM.CURVE_Gy);
    static final ECP genG1 = new ECP(gx, gy);
    private static final BIG pxa = new BIG(ROM.CURVE_Pxa);
    private static final BIG pxb = new BIG(ROM.CURVE_Pxb);
    private static final FP2 px = new FP2(pxa, pxb);
    private static final BIG pya = new BIG(ROM.CURVE_Pya);
    private static final BIG pyb = new BIG(ROM.CURVE_Pyb);
    private static final FP2 py = new FP2(pya, pyb);
    static final ECP2 genG2 = new ECP2(px, py);
    static final FP12 genGT = PAIR.fexp(PAIR.ate(genG2, genG1));
    static final BIG GROUP_ORDER = new BIG(ROM.CURVE_Order);
    static final int FIELD_BYTES = BIG.BIGBITS;

    private AMCLUtils() {
        // private constructor as there shouldn't be instances of this utility class
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(final byte b: a) {
            sb.append(String.format("%02x ", b & 0xff));
        }
        return sb.toString();
    }

    public static String byteArrayToInteger(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(final byte b: a) {
            sb.append(String.format("%d ", b));
        }
        return sb.toString();
    }

    public static byte[] reverse(byte[] array) {

        if (null == array) {
            return null;
        }

        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
        return array;
    }

    public static String byteToHex(byte a) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%02x ", a&0xff));
        return sb.toString();
    }

    // Byte order : Little
    public static byte[] transform_i32_to_array_of_i8(int x) {
        // djpark0402 20210512
        // Java for 6
        int idx = 0;
        byte[] result = new byte[4];  // for java 6
        for (int i = 3; i >= 0 ; i--) {
            result[idx] = (byte)(x >> (i * 8));
//            System.out.println("i :"+i+", result :"+result[i]);
            idx++;
        }

        return result;

        // djpark0402 20210512
        // Java for 8
//        Vector<Byte> result = new Vector<Byte>();
//        for (int i = 3; i >= 0 ; i--) {
//            result.add(((byte)(x >> (i * 8))));
//        }
////        System.out.println("result :"+result);
//        byte[] bytes = new byte[result.size()];
//        for (int i = 0; i < result.size(); i++) {
//            bytes[i] = result.get(i);
////            System.out.println("--> "+AMCLUtils.byteToHex(bytes[i]));
//        }
//        return bytes;
    }

    /**
     * Returns a random number generator, amcl.RAND,
     * initialized with a fresh seed.
     *
     * @return a random number generator
     */
    public static RAND getRand() {
        // construct a secure seed
        int entropy_bytes = 128;

        SecureRandom random = new SecureRandom();
//        byte[] seed = new byte[entropy_bytes];
        byte[] seed = random.generateSeed(entropy_bytes);
//        System.out.println(AMCLUtils.byteArrayToHex(seed));
        // create a new amcl.RAND and initialize it with the generated seed
        RAND rng = new RAND();
        rng.clean();
        rng.seed(entropy_bytes, seed);

        return rng;
    }
    /**
     * @return a random BIG in 0, ..., GROUP_ORDER-1
     */
    public static BIG randModOrder(RAND rng) {
        BIG q = new BIG(ROM.CURVE_Order);
        // Takes random element in this Zq.
        return BIG.randomnum(q, rng);
    }

    /**
     * hashModOrder hashes bytes to an amcl.BIG
     * in 0, ..., GROUP_ORDER
     *
     * @param data the data to be hashed
     * @return a BIG in 0, ..., GROUP_ORDER-1 that is the hash of the data
     */
    public static BIG hashModOrder(byte[] data) {
        HASH256 hash = new HASH256();
        for (byte b : data) {
            hash.process(b);
        }

        byte[] hasheddata = hash.hash();

        BIG ret = BIG.fromBytes(hasheddata);
        ret.mod(AMCLUtils.GROUP_ORDER);

        return ret;
    }

    /**
     * bigToBytes turns a BIG into a byte array
     *
     * @param big the BIG to turn into bytes
     * @return a byte array representation of the BIG
     */
    public static byte[] bigToBytes(BIG big) {
        byte[] ret = new byte[AMCLUtils.FIELD_BYTES];
        big.toBytes(ret);
        return ret;
    }

    /**
     * ecpToBytes turns an ECP into a byte array
     *
     * @param e the ECP to turn into bytes
     * @return a byte array representation of the ECP
     */
    static byte[] ecpToBytes(ECP e) {
        byte[] ret = new byte[2 * FIELD_BYTES + 1];
        e.toBytes(ret, false);
        return ret;
    }

    /**
     * ecpToBytes turns an ECP2 into a byte array
     *
     * @param e the ECP2 to turn into bytes
     * @return a byte array representation of the ECP2
     */
    static byte[] ecpToBytes(ECP2 e) {
        byte[] ret = new byte[4 * FIELD_BYTES];
        e.toBytes(ret);
        return ret;
    }

    /**
     * append appends a byte array to an existing byte array
     *
     * @param data     the data to which we want to append
     * @param toAppend the data to be appended
     * @return a new byte[] of data + toAppend
     */
    static byte[] append(byte[] data, byte[] toAppend) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(data);
            stream.write(toAppend);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }

    /**
     * append appends a boolean array to an existing byte array
     * @param data     the data to which we want to append
     * @param toAppend the data to be appended
     * @return a new byte[] of data + toAppend
     */
    static byte[] append(byte[] data, boolean[] toAppend) {
        byte[] toAppendBytes = new byte[toAppend.length];
        for (int i = 0; i < toAppend.length; i++) {
            toAppendBytes[i] = toAppend[i] ? (byte) 1 : (byte) 0;
        }
        return append(data, toAppendBytes);
    }

//    /**
//     * Returns an amcl.BN256.ECP on input of an ECP protobuf object.
//     *
//     * @param w a protobuf object representing an ECP
//     * @return a ECP created from the protobuf object
//     */
//    static ECP transformFromProto(Idemix.ECP w) {
//        byte[] valuex = w.getX().toByteArray();
//        byte[] valuey = w.getY().toByteArray();
//        return new ECP(BIG.fromBytes(valuex), BIG.fromBytes(valuey));
//    }
//
//    /**
//     * Returns an amcl.BN256.ECP2 on input of an ECP2 protobuf object.
//     *
//     * @param w a protobuf object representing an ECP2
//     * @return a ECP2 created from the protobuf object
//     */
//    static ECP2 transformFromProto(Idemix.ECP2 w) {
//        byte[] valuexa = w.getXa().toByteArray();
//        byte[] valuexb = w.getXb().toByteArray();
//        byte[] valueya = w.getYa().toByteArray();
//        byte[] valueyb = w.getYb().toByteArray();
//        FP2 valuex = new FP2(BIG.fromBytes(valuexa), BIG.fromBytes(valuexb));
//        FP2 valuey = new FP2(BIG.fromBytes(valueya), BIG.fromBytes(valueyb));
//        return new ECP2(valuex, valuey);
//    }
//
//    /**
//     * Converts an amcl.BN256.ECP2 into an ECP2 protobuf object.
//     *
//     * @param w an ECP2 to be transformed into a protobuf object
//     * @return a protobuf representation of the ECP2
//     */
//    static Idemix.ECP2 transformToProto(ECP2 w) {
//
//        byte[] valueXA = new byte[IdemixUtils.FIELD_BYTES];
//        byte[] valueXB = new byte[IdemixUtils.FIELD_BYTES];
//        byte[] valueYA = new byte[IdemixUtils.FIELD_BYTES];
//        byte[] valueYB = new byte[IdemixUtils.FIELD_BYTES];
//
//        w.getX().getA().toBytes(valueXA);
//        w.getX().getB().toBytes(valueXB);
//        w.getY().getA().toBytes(valueYA);
//        w.getY().getB().toBytes(valueYB);
//
//        return Idemix.ECP2.newBuilder()
//                .setXa(ByteString.copyFrom(valueXA))
//                .setXb(ByteString.copyFrom(valueXB))
//                .setYa(ByteString.copyFrom(valueYA))
//                .setYb(ByteString.copyFrom(valueYB))
//                .build();
//    }
//
//    /**
//     * Converts an amcl.BN256.ECP into an ECP protobuf object.
//     *
//     * @param w an ECP to be transformed into a protobuf object
//     * @return a protobuf representation of the ECP
//     */
//    static Idemix.ECP transformToProto(ECP w) {
//        byte[] valueX = new byte[AmclUtils.FIELD_BYTES];
//        byte[] valueY = new byte[AmclUtils.FIELD_BYTES];
//
//        w.getX().toBytes(valueX);
//        w.getY().toBytes(valueY);
//
//        return Idemix.ECP.newBuilder().setX(ByteString.copyFrom(valueX)).setY(ByteString.copyFrom(valueY)).build();
//    }

    /**
     * Takes input BIGs a, b, m and returns a+b modulo m
     *
     * @param a the first BIG to add
     * @param b the second BIG to add
     * @param m the modulus
     * @return Returns a+b (mod m)
     */
    static BIG modAdd(BIG a, BIG b, BIG m) {
        BIG c = a.plus(b);
        c.mod(m);
        return c;
    }

    /**
     * Modsub takes input BIGs a, b, m and returns a-b modulo m
     *
     * @param a the minuend of the modular subtraction
     * @param b the subtrahend of the modular subtraction
     * @param m the modulus
     * @return returns a-b (mod m)
     */
    static BIG modSub(BIG a, BIG b, BIG m) {
        return modAdd(a, BIG.modneg(b, m), m);
    }

    public static void toHashString(Object obj, String instanceNm) {

        if (obj.getClass().equals(PointG2.class) ||
                obj.getClass().equals(Tail.class)) {

            PointG2 g2 = (PointG2)obj;
            ZkpLogger.debug(instanceNm+": {");
            ZkpLogger.debug(g2.getPoint().getx().geta().XES + " " + g2.getPoint().getx().geta().x.toString().toUpperCase());
            ZkpLogger.debug(g2.getPoint().getx().getb().XES + " " + g2.getPoint().getx().getb().x.toString().toUpperCase());
            ZkpLogger.debug(g2.getPoint().gety().geta().XES + " " + g2.getPoint().gety().geta().x.toString().toUpperCase());
            ZkpLogger.debug(g2.getPoint().gety().getb().XES + " " + g2.getPoint().gety().getb().x.toString().toUpperCase());
            ZkpLogger.debug(g2.getPoint().getz().geta().XES + " " + g2.getPoint().getz().geta().x.toString().toUpperCase());
            ZkpLogger.debug(g2.getPoint().getz().getb().XES + " " + g2.getPoint().getz().getb().x.toString().toUpperCase()+" \n}");
        } else if (obj.getClass().equals(PointG1.class)) {

            PointG1 g1 = (PointG1)obj;
            ZkpLogger.debug(instanceNm+": {");
            ZkpLogger.debug(g1.getPoint().getx().XES+" "+g1.getPoint().getx().x.toString().toUpperCase());
            ZkpLogger.debug(g1.getPoint().gety().XES+" "+g1.getPoint().gety().x.toString().toUpperCase());
            ZkpLogger.debug(g1.getPoint().getz().XES+" "+g1.getPoint().getz().x.toString().toUpperCase()+" \n}");

        } else if (obj.getClass().equals(GroupOrderElement.class)) {

            GroupOrderElement goe = (GroupOrderElement)obj;
            ZkpLogger.debug(instanceNm+": "+goe.getBn().toString().toUpperCase());
        } else if (obj.getClass().equals(FP12.class)) {

            FP12 fp12 = (FP12)obj;
            ZkpLogger.debug(instanceNm+": {");
            ZkpLogger.debug(fp12.geta().geta().geta().XES+" "+fp12.geta().geta().geta().x.toString().toUpperCase());
            ZkpLogger.debug(fp12.geta().geta().getb().XES+" "+fp12.geta().geta().getb().x.toString().toUpperCase());
            ZkpLogger.debug(fp12.geta().getb().geta().XES+" "+fp12.geta().getb().geta().x.toString().toUpperCase());
            ZkpLogger.debug(fp12.geta().getb().getb().XES+" "+fp12.geta().getb().getb().x.toString().toUpperCase());
            ZkpLogger.debug(fp12.getb().geta().geta().XES+" "+fp12.getb().geta().geta().x.toString().toUpperCase());
            ZkpLogger.debug(fp12.getb().geta().getb().XES+" "+fp12.getb().geta().getb().x.toString().toUpperCase());
            ZkpLogger.debug(fp12.getb().getb().geta().XES+" "+fp12.getb().getb().geta().x.toString().toUpperCase());
            ZkpLogger.debug(fp12.getb().getb().getb().XES+" "+fp12.getb().getb().getb().x.toString().toUpperCase());
            ZkpLogger.debug(fp12.getc().geta().geta().XES+" "+fp12.getc().geta().geta().x.toString().toUpperCase());
            ZkpLogger.debug(fp12.getc().geta().getb().XES+" "+fp12.getc().geta().getb().x.toString().toUpperCase());
            ZkpLogger.debug(fp12.getc().getb().geta().XES+" "+fp12.getc().getb().geta().x.toString().toUpperCase());
            ZkpLogger.debug(fp12.getc().getb().getb().XES+" "+fp12.getc().getb().getb().x.toString().toUpperCase()+" \n}");

        } else {

            ZkpLogger.debug(obj.getClass().getName()+" is not defined");
        }
    }
}
