package com.zylo.resumecraft.service;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface KeyValueStore {
    String get(String key);

    void set(String key, String value, long timeout, TimeUnit unit);

    void delete(String key);

    void delete(Collection<String> keys);

    boolean hasKey(String key);

    Set<String> keys(String pattern);

    Long increment(String key);

    void expire(String key, long timeout, TimeUnit unit);
}

