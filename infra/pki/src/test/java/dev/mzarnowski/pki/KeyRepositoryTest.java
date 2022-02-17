package dev.mzarnowski.pki;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

final class KeyRepositoryTest {
    private final KeyPair KEYS = generateKeyPair();
    private final Hash PUBLIC_KEY_FINGERPRINT = Hash.sha3Of(KEYS.getPublic().getEncoded());

    @Test
    public void added_keys_are_pending_approval() {
        KeyRepository repository = new InMemory();

        var id = repository.add(KEYS.getPublic());

        var pending = repository.pending().stream().collect(Collectors.toMap(it -> it.id(), it -> it.fingerprint()));
        Assertions.assertEquals(PUBLIC_KEY_FINGERPRINT, pending.get(id));
        Assertions.assertFalse(repository.contains(id));
    }

    private KeyPair generateKeyPair() {
        try {
            var generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
