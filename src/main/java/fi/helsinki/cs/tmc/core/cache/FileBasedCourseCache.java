package fi.helsinki.cs.tmc.core.cache;

import fi.helsinki.cs.tmc.core.domain.Course;

import java.io.FileNotFoundException;
import java.nio.file.Path;

public class FileBasedCourseCache extends FileBasedKeyValueCache<Integer, Course> implements CourseCache {

    public FileBasedCourseCache(Path cacheFile) throws FileNotFoundException {
        super(cacheFile);
    }
}
