package fi.helsinki.cs.tmc.core.cache;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InMemoryKeyValueCache<K, V> extends InMemoryCache<Map<K, V>> implements KeyValueCache<K, V> {

    public InMemoryKeyValueCache() {
        super(new HashMap<K, V>());
    }

    @Override
    public void put(K key, V value) throws IOException {
        read().put(key, value);
    }

    @Override
    public void clearValue(K key) throws IOException {
        read().put(key, null);
    }

    @Override
    public V get(K key) throws IOException {
        return read().get(key);
    }

    @Override
    public Set<K> keySet() throws IOException {
        return read().keySet();
    }

    @Override
    public Collection<V> values() throws IOException {
        return read().values();
    }

    @Override
    public void clear() throws IOException {
        read().clear();
    }
}
