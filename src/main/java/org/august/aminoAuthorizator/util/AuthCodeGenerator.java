package org.august.aminoAuthorizator.util;
import java.security.SecureRandom;

public class AuthCodeGenerator {
    private static final char[] CHARACTERS_ARRAY = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    public static String generateRandomCode(int length) {
        SecureRandom random = new SecureRandom();
        char[] code = new char[length];

        for (int i = 0; i < length; i++) {
            code[i] = CHARACTERS_ARRAY[random.nextInt(CHARACTERS_ARRAY.length)];
        }

        return new String(code);
    }
}
