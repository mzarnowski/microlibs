package dev.mzarnowski.pki;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

final class KeyRepositoryTest {
    private final KeyPair CA = generateKeyPair();
    private final KeyPair KEYS = generateKeyPair();
    private final Hash PUBLIC_KEY_FINGERPRINT = Hash.sha3Of(KEYS.getPublic().getEncoded());

    @Test
    public void added_keys_are_pending_approval() {
        KeyRepository repository = new InMemory(CA.getPublic());

        // when
        var id = repository.add(KEYS.getPublic());

        // then
        var pending = repository.pending().stream().collect(Collectors.toMap(it -> it.id(), it -> it.fingerprint()));
        Assertions.assertEquals(PUBLIC_KEY_FINGERPRINT, pending.get(id));
        Assertions.assertFalse(repository.contains(id));
    }

    @Test
    public void pending_key_can_be_approved_by_CA() {
        KeyRepository repository = new InMemory(CA.getPublic());
        var id = repository.add(KEYS.getPublic());

        // when
        var signature = Signature.sign(KEYS.getPublic().getEncoded(), CA.getPrivate());
        repository.approve(id, signature);

        // then
        Assertions.assertTrue(repository.contains(id));
        Assertions.assertTrue(repository.pending().isEmpty());
    }


    @Test
    public void key_cannot_be_approved_by_nonCA() {
        KeyRepository repository = new InMemory(CA.getPublic());
        var id = repository.add(KEYS.getPublic());

        // when
        var signature = Signature.sign(KEYS.getPublic().getEncoded(), KEYS.getPrivate());
        repository.approve(id, signature);

        // then
        Assertions.assertFalse(repository.contains(id));
        Assertions.assertTrue(repository.pending().stream().anyMatch(it -> it.id().equals(id)));
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
