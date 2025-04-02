package org.omnione.did.wallet.sample;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import org.omnione.did.wallet.util.RSAEncryptUtil;
import org.omnione.did.wallet.util.RsaKeyPair;

/**
 * RSAEncrypt
 */
public class RSAEncrypt {

    private static RSAEncryptUtil util = new RSAEncryptUtil();

    public static void main(String[] args) throws Exception {
        
        String text = "RSA encryption and decryption test";


        RsaKeyPair pair = util.generateKeyObj();
        String strPriKey = pair.getPrivateKey();
        String strPubKey = pair.getPublicKey();

        System.out.println("pri=" + strPriKey);
        System.out.println("pub=" + strPubKey);
   

         byte[] priKeyBytes = hexToByteArray(strPriKey);
         byte[] pubKeyBytes = hexToByteArray(strPubKey);
         
       
         KeyFactory kf = KeyFactory.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
         PrivateKey priKey = kf.generatePrivate(new PKCS8EncodedKeySpec(priKeyBytes));
         PublicKey pubKey = kf.generatePublic(new X509EncodedKeySpec(pubKeyBytes));

        byte[] cipher = util.encrypt(text.getBytes("UTF-8"), pubKey);
        System.out.println("cipher=" + byteArrayToHex(cipher));

        byte[] plain = util.decrypt(cipher, priKey);
        System.out.println("plain=" + new String(plain));
    }

    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() == 0)
            return null;

        byte[] ba = new byte[hex.length() / 2];

        for (int i = 0; i < ba.length; i++)
            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);

        return ba;
    }

    public static String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0)
            return null;

        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;

        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);
            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }

        return sb.toString();
    }
}