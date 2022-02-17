package dev.mzarnowski.pki;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;

public record Signature(String algorithm, byte[] value) {
    static Signature sign(byte[] content, PrivateKey key) {
        try {
            var algorithm = switch (key.getAlgorithm()) {
                case "RSA" -> "SHA1withRSA";
                default -> throw new IllegalStateException("Unsupported key algorithm: " + key.getAlgorithm());
            };

            var signature = java.security.Signature.getInstance(algorithm);
            signature.initSign(key);
            signature.update(content);

            var value = signature.sign();
            return new Signature(algorithm, value);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    boolean matches(byte[] content, PublicKey signatory) {
        try {
            var signature = java.security.Signature.getInstance(algorithm);
            signature.initVerify(signatory);
            signature.update(content);

            return signature.verify(value);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
