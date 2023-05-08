package com.naevis.bot.util;

import java.util.Random;

public class RandomUtils {
    private static final Random random = new Random();
    private static final char[] ALPHANUMERIC_CHARS =
            "234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static String generateRoomCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            sb.append(ALPHANUMERIC_CHARS[random.nextInt(ALPHANUMERIC_CHARS.length)]);
        }
        return sb.toString();
    }
}
