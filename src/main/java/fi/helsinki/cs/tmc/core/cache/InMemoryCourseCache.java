package fi.helsinki.cs.tmc.core.cache;

import fi.helsinki.cs.tmc.core.domain.Course;

public class InMemoryCourseCache extends InMemoryKeyValueCache<Integer, Course> implements CourseCache {

}
