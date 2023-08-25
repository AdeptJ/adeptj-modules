package com.adeptj.modules.commons.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static com.adeptj.modules.commons.utils.Constants.SHA_256;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Utility methods for Java byte arrays.
 *
 * @author Rakesh Kumar, AdeptJ
 */
public class Bytes {

    public static byte[] toByteArray(char[] chars) {
        ByteBuffer buffer = UTF_8.encode(CharBuffer.wrap(chars));
        byte[] bytes = new byte[buffer.limit()];
        System.arraycopy(buffer.array(), 0, bytes, 0, buffer.limit());
        return bytes;
    }

    public static byte[] sha256DigestAsBase64(String text) {
        return Base64.getEncoder().encode(sha256Digest(text.getBytes(UTF_8)));
    }

    public static byte[] sha256Digest(byte[] bytes) {
        try {
            return MessageDigest.getInstance(SHA_256).digest(bytes);
        } catch (final NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
