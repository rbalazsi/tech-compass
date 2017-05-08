package com.robertbalazsi.techcompass.twofactorauth.account;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;

/**
 * Service for configuring and using 2-factor authentication.
 */
@Service
public class TwoFactorService {
    public static final String ISSUER = "2FA Demo";
    public static final int DEFAULT_TOTP_INTERVAL_SECS = 30;
    private static final String UTF_8 = "UTF-8";

    private SecureRandom random = new SecureRandom();

    public String generateBase32SecretKey() {
        return new BigInteger(130, random).toString(32);
    }

    public String getNextTOTP(String secret) {
        String normalizedSecret = secret.toUpperCase();
        Base32 base32 = new Base32();
        byte[] decodedBytes = base32.decode(normalizedSecret);
        long time = (System.currentTimeMillis() / 1000) / DEFAULT_TOTP_INTERVAL_SECS;
        return TOTP.generateTOTP(Hex.encodeHexString(decodedBytes), Long.toHexString(time), "6");
    }

    public void generateQRCodePNG(String secret, String account, String path, int width, int height) {
        String barCodeLink = getGoogleAuthenticatorBarLink(secret, account);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(barCodeLink, BarcodeFormat.QR_CODE, width, height);
            try (FileOutputStream out = new FileOutputStream(path)) {
                MatrixToImageWriter.writeToStream(bitMatrix, "png", out);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String getGoogleAuthenticatorBarLink(String secret, String account) {
        String normalizedSecret = secret.toUpperCase();

        try {
            return ("otpauth://totp/" + URLEncoder.encode(ISSUER + ":" + account, UTF_8)
                    + "?secret=" + URLEncoder.encode(normalizedSecret, UTF_8)
                    + "&issuer=" + URLEncoder.encode(ISSUER, UTF_8)).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
