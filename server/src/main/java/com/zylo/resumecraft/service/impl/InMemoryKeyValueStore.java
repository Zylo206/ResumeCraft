package com.zylo.resumecraft.service.impl;

import com.zylo.resumecraft.service.KeyValueStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "app.local.enabled", havingValue = "true")
public class InMemoryKeyValueStore implements KeyValueStore {
    private final ConcurrentHashMap<String, Entry> entries = new ConcurrentHashMap<>();

    @Override
    public String get(String key) {
        Entry entry = entries.get(key);
        if (entry == null || entry.isExpired()) {
            entries.remove(key);
            return null;
        }
        return entry.value();
    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit unit) {
        entries.put(key, new Entry(value, expiresAt(timeout, unit)));
    }

    @Override
    public void delete(String key) {
        entries.remove(key);
    }

    @Override
    public void delete(Collection<String> keys) {
        keys.forEach(entries::remove);
    }

    @Override
    public boolean hasKey(String key) {
        return get(key) != null;
    }

    @Override
    public Set<String> keys(String pattern) {
        Pattern regex = Pattern.compile(pattern.replace("*", ".*"));
        return entries.keySet().stream()
                .filter(key -> get(key) != null)
                .filter(key -> regex.matcher(key).matches())
                .collect(Collectors.toSet());
    }

    @Override
    public Long increment(String key) {
        return Long.parseLong(entries.compute(key, (ignored, existing) -> {
            if (existing == null || existing.isExpired()) {
                return new Entry("1", 0);
            }
            long next = Long.parseLong(existing.value()) + 1;
            return new Entry(Long.toString(next), existing.expiresAt());
        }).value());
    }

    @Override
    public void expire(String key, long timeout, TimeUnit unit) {
        entries.computeIfPresent(key, (ignored, existing) -> new Entry(existing.value(), expiresAt(timeout, unit)));
    }

    private long expiresAt(long timeout, TimeUnit unit) {
        if (timeout <= 0) {
            return 0;
        }
        return System.currentTimeMillis() + unit.toMillis(timeout);
    }

    private record Entry(String value, long expiresAt) {
        boolean isExpired() {
            return expiresAt > 0 && System.currentTimeMillis() >= expiresAt;
        }
    }
}

