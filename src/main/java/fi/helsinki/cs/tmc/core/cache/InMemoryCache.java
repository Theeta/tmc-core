package fi.helsinki.cs.tmc.core.cache;

import java.io.IOException;

public class InMemoryCache<T> implements Cache<T> {

    private T cache;

    public InMemoryCache(T initial) {
        this.cache = initial;
    }

    @Override
    public void write(T data) throws IOException {
        this.cache = data;
    }

    @Override
    public T read() throws IOException {
        return this.cache;
    }

    @Override
    public void clear() throws IOException {
        this.cache = null;
    }
}
