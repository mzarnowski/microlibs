package dev.mzarnowski.pki;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

record Hash(String algorithm, String value) {
    public static Hash sha3Of(byte[] content) {
        return Hash.of("SHA3-512", content);
    }

    public static Hash of(String algorithm, byte[] content) {
        try {
            var digest = MessageDigest.getInstance(algorithm);
            var raw = digest.digest(content);
            var encoded = Base64.getUrlEncoder().encode(raw);
            return new Hash(algorithm.toUpperCase(), new String(encoded));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}