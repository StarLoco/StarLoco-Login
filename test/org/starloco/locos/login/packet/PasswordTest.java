package org.starloco.locos.login.packet;

import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ALL")

public class PasswordTest {

    private static String referenceCrypt(String message, String type) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(type);
        md.update(message.getBytes());
        byte[] mb = md.digest();
        StringBuilder out = new StringBuilder();
        for (byte temp : mb) {
            StringBuilder s = new StringBuilder(Integer.toHexString(temp));
            while (s.length() < 2) {
                s.insert(0, "0");
            }
            s = new StringBuilder(s.substring(s.length() - 2));
            out.append(s);
        }
        return out.toString();
    }

    private static String referenceEncrypt(String password) throws NoSuchAlgorithmException {
        return referenceCrypt(referenceCrypt(password, "MD5"), "SHA-512");
    }

    @Test
    void encryptMatchesReferenceImplementation() throws NoSuchAlgorithmException {
        String[] inputs = {"test", "password", "hello", "", "admin123", "AzErTy!@#", "azerty", "123456"};
        for (String input : inputs) {
            assertEquals(
                referenceEncrypt(input),
                Password.encrypt(input),
                "encrypt() mismatch for input: \"" + input + "\""
            );
        }
    }

    @Test
    void encryptIsDeterministic() {
        String result1 = Password.encrypt("test");
        String result2 = Password.encrypt("test");
        assertEquals(result1, result2);
    }

    @Test
    void encryptDifferentInputsProduceDifferentHashes() {
        assertNotEquals(Password.encrypt("password1"), Password.encrypt("password2"));
        assertNotEquals(Password.encrypt("admin"), Password.encrypt("Admin"));
    }

    @Test
    void encryptOutputIsLowercaseHex() {
        String result = Password.encrypt("test");
        assertNotNull(result);
        assertTrue(result.matches("[0-9a-f]+"), "Output should be lowercase hex: " + result);
    }

    @Test
    void encryptOutputLengthIsSha512() {
        assertEquals(128, Password.encrypt("test").length());
        assertEquals(128, Password.encrypt("").length());
    }


    @Test
    void benchmark() throws NoSuchAlgorithmException {
        final int WARMUP      = 5_000;
        final int ITERATIONS  = 100_000;
        final String password = "MyP@ssw0rd!";

        for (int i = 0; i < WARMUP; i++) {
            referenceEncrypt(password);
            Password.encrypt(password);
        }

        // --- ancien encrypt (old) ---
        long t0 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) referenceEncrypt(password);
        long oldNs = System.nanoTime() - t0;

        // --- new encrypt (new) ---
        t0 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) Password.encrypt(password);
        long newNs = System.nanoTime() - t0;

        double oldMs  = oldNs / 1e6;
        double newMs  = newNs / 1e6;
        double ratio  = (double) oldNs / newNs;
        double oldAvg = oldNs / (double) ITERATIONS / 1000;
        double newAvg = newNs / (double) ITERATIONS / 1000;


        System.out.printf("║  Old impl : %8.1f ms  (%6.2f µs/op)    ║%n", oldMs, oldAvg);
        System.out.printf("║  New impl : %8.1f ms  (%6.2f µs/op)    ║%n", newMs, newAvg);

        assertTrue(newNs < oldNs, "Faster ?");
    }
}
