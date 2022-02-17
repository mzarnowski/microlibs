package dev.mzarnowski.pki;

import java.security.PublicKey;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

interface KeyRepository {

    UUID add(PublicKey key);
    boolean contains(UUID id);

    Set<Pending> pending();
    void approve(UUID id, Signature signature);

    record Request(PublicKey key, Hash fingerprint, ZonedDateTime since) {}
    record Pending(UUID id, Hash fingerprint, ZonedDateTime since) {}
}


