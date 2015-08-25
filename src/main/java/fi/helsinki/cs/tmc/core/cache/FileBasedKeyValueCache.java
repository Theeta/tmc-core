package fi.helsinki.cs.tmc.core.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class FileBasedKeyValueCache<K, V> extends FileBasedCache<Map<K, V>> implements KeyValueCache<K, V> {

    public FileBasedKeyValueCache(Path cacheFile) throws FileNotFoundException {
        super(cacheFile);
    }

    @Override
    public void put(K key, V value) throws IOException {
        Map<K, V> cache = read();
        cache.put(key, value);
        write(cache);
    }

    @Override
    public void clearValue(K key) throws IOException {
        Map<K, V> cache = read();
        cache.put(key, null);
        write(cache);
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


}
