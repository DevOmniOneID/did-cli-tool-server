package org.omnione.did.wallet.zkp;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class SecurePassword {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        int n = 997; // Prime number
        String password = "Hello";
        int g;

        // Pick generator g
        g = pickG(n);

        Random random = new Random();
        int v = random.nextInt(n) + 1; // Peggy's random value
        int c = random.nextInt(n) + 1; // Victor's random value

        // Calculate x as hash of the password mod n
        int x = hashPassword(password, n);

        // Calculate y = g^x mod n
        int y = modPow(g, x, n);

        // Calculate t = g^v mod n
        int t = modPow(g, v, n);

        // Calculate r = v - c * x
        int r = v - c * x;

        // Calculate Result
        int result;
        if (r < 0) {
            result = modMultiply(modInverse(modPow(g, -r, n), n), modPow(y, c, n), n);
        } else {
            result = modMultiply(modPow(g, r, n), modPow(y, c, n), n);
        }

        // Output
        System.out.println("Password:\t" + password);
        System.out.println("\n======Agreed parameters============");
        System.out.println("P= " + n + "\t(Prime number)");
        System.out.println("G= " + g + "\t(Generator)");

        System.out.println("\n======The secret==================");
        System.out.println("x= " + x + "\t(Peggy's secret)");

        System.out.println("\n======Random values===============");
        System.out.println("c= " + c + "\t(Victor's random value)");
        System.out.println("v= " + v + "\t(Peggy's random value)");

        System.out.println("\n======Shared value===============");
        System.out.println("g^x mod P=\t" + y);
        System.out.println("r=\t\t" + r);

        System.out.println("\n=========Results===================");
        System.out.println("t=g**v % n =\t\t" + t);
        System.out.println("((g**r) * (y**c))=\t" + result);

        if (t == result) {
            System.out.println("Peggy has proven she knows password");
        } else {
            System.out.println("Peggy has not proven she knows x");
        }
    }

    private static int pickG(int p) {
        for (int x = 1; x < p; x++) {
            int rand = x;
            int exp = 1;
            int next = rand % p;

            while (next != 1) {
                next = (next * rand) % p;
                exp++;
            }

            if (exp == p - 1) {
                return rand;
            }
        }
        return 1; // Should never reach here
    }

    private static int hashPassword(String password, int n) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();

        // Convert the first 4 bytes to an integer
        int hash = 0;
        for (int i = 0; i < 4; i++) {
            hash = (hash << 8) | (digest[i] & 0xFF);
        }
        return Math.abs(hash % n);
    }

    private static int modPow(int base, int exp, int mod) {
        return BigInteger.valueOf(base).modPow(BigInteger.valueOf(exp), BigInteger.valueOf(mod)).intValue();
    }

    private static int modInverse(int a, int mod) {
        return BigInteger.valueOf(a).modInverse(BigInteger.valueOf(mod)).intValue();
    }

    private static int modMultiply(int a, int b, int mod) {
        return BigInteger.valueOf(a).multiply(BigInteger.valueOf(b)).mod(BigInteger.valueOf(mod)).intValue();
    }
}
