package com.farmmart.util;

import java.security.SecureRandom;

// replaces: server/utils/generatedOtp.js
public class OtpUtil {

    private static final SecureRandom random = new SecureRandom();

    // replaces: Math.floor(Math.random() * 900000) + 100000
    // Uses SecureRandom instead of Math.random - much more secure
    public static String generate() {
        int otp = 100000 + random.nextInt(900000); // gives 6-digit number
        return String.valueOf(otp);
    }
}
