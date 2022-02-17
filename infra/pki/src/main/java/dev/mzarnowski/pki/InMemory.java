package dev.mzarnowski.pki;

import java.security.PublicKey;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

final class InMemory implements KeyRepository {
    private final Set<Pending> pending = new HashSet<>();

    @Override
    public UUID add(PublicKey key) {
        var id = UUID.randomUUID();
        var pending = new Pending(id, Hash.sha3Of(key.getEncoded()), ZonedDateTime.now());
        this.pending.add(pending);
        return id;
    }

    @Override
    public boolean contains(UUID id) {
        return false;
    }

    @Override
    public Set<Pending> pending() {
        return Collections.unmodifiableSet(pending);
    }
}
