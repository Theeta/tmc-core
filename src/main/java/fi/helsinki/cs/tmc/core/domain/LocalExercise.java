package fi.helsinki.cs.tmc.core.domain;

public class LocalExercise {

    private final int courseId;
    private final String exerciseName;
    private final String checksum;

    public LocalExercise(int courseId, String exerciseName, String checksum) {
        this.courseId = courseId;
        this.exerciseName = exerciseName;
        this.checksum = checksum;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public String getChecksum() {
        return checksum;
    }
}
