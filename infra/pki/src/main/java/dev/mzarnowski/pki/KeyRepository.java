package dev.mzarnowski.pki;

import java.security.PublicKey;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

interface KeyRepository {

    UUID add(PublicKey key);
    boolean contains(UUID id);

    Set<Pending> pending();

    record Pending(UUID id, Hash fingerprint, ZonedDateTime since) {}
}


