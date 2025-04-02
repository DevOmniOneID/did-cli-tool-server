package org.omnione.did.wallet.zkp.util;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

//박수정 부장님 코드
public class RandomUtil {
    private static final Object cacheLock = new Object();
    private static SecureRandom defaultSecureRandom;

    private RandomUtil() { }

    public static SecureRandom getSecureRandom() {
        synchronized (cacheLock) {
            if (null != defaultSecureRandom) {
                return defaultSecureRandom;
            }
        }

        // 랜덤 생성
        SecureRandom tmp = new SecureRandom();
        // seed 고정
//        SecureRandom tmp = null;
//        try {
//            tmp = SecureRandom.getInstance("SHA1PRNG", "SUN");
//            int entropy_bytes = 128;
//            byte[] seed = new byte[entropy_bytes];
//            tmp.setSeed(seed);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//        }

        synchronized (cacheLock) {
            if (null == defaultSecureRandom) {
                defaultSecureRandom = tmp;
            }
            return defaultSecureRandom;
        }
    }

    public static void setSecureRandom(SecureRandom secureRandom) {
        synchronized (cacheLock) {
            defaultSecureRandom = secureRandom;
        }
    }
}
