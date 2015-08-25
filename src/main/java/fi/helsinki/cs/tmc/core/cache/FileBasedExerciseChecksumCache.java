package fi.helsinki.cs.tmc.core.cache;

import fi.helsinki.cs.tmc.core.domain.LocalExercise;

import java.io.FileNotFoundException;
import java.nio.file.Path;

public class FileBasedExerciseChecksumCache extends FileBasedKeyValueCache<Path, LocalExercise> implements ExerciseChecksumCache {

    public FileBasedExerciseChecksumCache(Path cacheFile) throws FileNotFoundException {
        super(cacheFile);
    }
}
