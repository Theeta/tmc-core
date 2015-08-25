package fi.helsinki.cs.tmc.core.cache;

import fi.helsinki.cs.tmc.core.domain.LocalExercise;

import java.nio.file.Path;

public interface ExerciseChecksumCache extends KeyValueCache<Path, LocalExercise> {
}
