package dev.mzarnowski.pki;

import java.security.PublicKey;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

final class InMemory implements KeyRepository {
    private final Map<UUID, Request> pending = new HashMap<>();
    private final Map<UUID, PublicKey> keys = new HashMap<>();
    private final PublicKey authority;

    public InMemory(PublicKey authority) {
        this.authority = authority;
    }

    @Override
    public UUID add(PublicKey key) {
        var id = UUID.randomUUID();
        var request = new Request(key, Hash.sha3Of(key.getEncoded()), ZonedDateTime.now());
        pending.put(id, request);
        return id;
    }

    @Override
    public boolean contains(UUID id) {
        return keys.containsKey(id);
    }

    @Override
    public Set<Pending> pending() {
        return pending.entrySet().stream()
                .map(e -> new Pending(e.getKey(), e.getValue().fingerprint(), e.getValue().since()))
                .collect(Collectors.toSet());
    }

    @Override
    public void approve(UUID id, Signature signature) {
        var pending = this.pending.get(id);
        if (pending == null) return;

        var key = pending.key();

        if (signature.matches(key.getEncoded(), authority)) {
            keys.put(id, pending.key());
            this.pending.remove(id);
        }
    }

}
