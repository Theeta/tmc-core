package fi.helsinki.cs.tmc.core.commands;

import fi.helsinki.cs.tmc.core.cache.Cache;
import fi.helsinki.cs.tmc.core.cache.CourseCache;
import fi.helsinki.cs.tmc.core.communication.TmcApi;
import fi.helsinki.cs.tmc.core.configuration.TmcSettings;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.exceptions.TmcCoreException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A {@link Command} for retrieving the course list from the server.
 */
public class ListCourses extends Command<List<Course>> {

    private final TmcApi tmcApi;
    private final CourseCache cache;
    private final Cache.QueryStrategy cacheFetchStrategy;

    /**
     * Constructs a new list courses command with {@code settings}.
     */
    public ListCourses(TmcSettings settings, CourseCache cache, Cache.QueryStrategy cacheFetchStrategy) {
        this(settings, cache, cacheFetchStrategy, new TmcApi(settings));
    }

    /**
     * Constructs a new list courses command with {@code settings} that uses {@code tmcApi} to
     * communicate with the server.
     */
    public ListCourses(TmcSettings settings, CourseCache cache, Cache.QueryStrategy cacheFetchStrategy, TmcApi tmcApi) {
        super(settings);
        this.cache = cache;
        this.cacheFetchStrategy = cacheFetchStrategy;

        this.tmcApi = tmcApi;
    }

    /**
     * Entry point for launching this command.
     */
    @Override
    public List<Course> call() throws TmcCoreException {
        if (!settings.userDataExists()) {
            throw new TmcCoreException("User must be authorized first");
        }

        if (cacheFetchStrategy == Cache.QueryStrategy.PREFER_LOCAL) {
            return getFromLocalCache();
        } else {
            return getFromServer();
        }
    }

    private List<Course> getFromServer() throws TmcCoreException {
        try {
            return tmcApi.getCourses();
        } catch (IOException ex) {
            throw new TmcCoreException("Failed to fetch courses from server", ex);
        }
    }

    private List<Course> getFromLocalCache() throws TmcCoreException {
        Collection<Course> courses;
        try {
            courses = cache.values();
        } catch (IOException e) {
            throw new TmcCoreException("Failed to fetch courses from local cache", e);
        }
        if (courses == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(courses);
    }
}
