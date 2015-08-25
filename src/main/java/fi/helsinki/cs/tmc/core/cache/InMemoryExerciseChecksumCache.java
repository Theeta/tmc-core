package fi.helsinki.cs.tmc.core.cache;

import fi.helsinki.cs.tmc.core.domain.LocalExercise;

import java.nio.file.Path;

public class InMemoryExerciseChecksumCache extends InMemoryKeyValueCache<Path, LocalExercise> implements ExerciseChecksumCache {
}
