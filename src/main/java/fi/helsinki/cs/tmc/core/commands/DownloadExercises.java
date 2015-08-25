package fi.helsinki.cs.tmc.core.commands;

import fi.helsinki.cs.tmc.core.cache.Cache;
import fi.helsinki.cs.tmc.core.cache.CourseCache;
import fi.helsinki.cs.tmc.core.cache.ExerciseChecksumCache;
import fi.helsinki.cs.tmc.core.communication.ExerciseDownloader;
import fi.helsinki.cs.tmc.core.communication.TmcApi;
import fi.helsinki.cs.tmc.core.communication.UrlCommunicator;
import fi.helsinki.cs.tmc.core.configuration.TmcSettings;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.LocalExercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.exceptions.TmcCoreException;

import com.google.common.base.Optional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Command} for downloading exercises.
 */
public class DownloadExercises extends Command<List<Exercise>> {

    private ExerciseDownloader exerciseDownloader;
    private ExerciseChecksumCache exerciseChecksumCache;
    private TmcApi tmcApi;
    private List<Exercise> exercises;
    private int courseId;
    private Path downloadsRoot;
    private CourseCache courseCache;
    private Cache.QueryStrategy queryStrategy;

    /**
     *  Constructs a new downloaded exercises command for downloading {@code exercises} into TMC
     *  main directory.
     *
     * @param settings      Provides login credentials and download location.
     * @param exercises     List of exercises to download.
     * @param observer      This observer is notified of command's progress. May be {@code null}.
     * @param exerciseChecksumCache         A cache for storing the downloads.
     */
    public DownloadExercises(
            TmcSettings settings,
            List<Exercise> exercises,
            ProgressObserver observer,
            ExerciseChecksumCache exerciseChecksumCache,
            CourseCache courseCache,
            Cache.QueryStrategy queryStrategy)
            throws TmcCoreException {
        super(settings, observer);

        this.tmcApi = new TmcApi(settings);
        this.exerciseDownloader = new ExerciseDownloader(new UrlCommunicator(settings), tmcApi);
        this.exercises = exercises;
        this.downloadsRoot = Paths.get(settings.getTmcMainDirectory());
        this.courseCache = courseCache;
        this.queryStrategy = queryStrategy;

        Optional<Course> currentCourse = settings.getCurrentCourse();
        if (currentCourse.isPresent()) {
            this.courseId = currentCourse.get().getId();
        } else {
            throw new TmcCoreException("Unable to determine course, cannot download");
        }

        this.exerciseChecksumCache = exerciseChecksumCache;
    }

    /**
     * Constructs a new downloaded exercises command for downloading exercises of the course
     * identified by {@code courseId} into {@code downloadsRoot}.
     *
     * @param settings      Provides login credentials and download location.
     * @param downloadsRoot          Target path for downloads.
     * @param courseId      Identifies which course's exercises should be downloaded.
     * @param observer      This observer is notified of command's progress. May be {@code null}.
     * @param exerciseChecksumCache         A cache for storing the downloads.
     */
    public DownloadExercises(
            TmcSettings settings,
            Path downloadsRoot,
            int courseId,
            ProgressObserver observer,
            ExerciseChecksumCache exerciseChecksumCache,
            CourseCache courseCache,
            Cache.QueryStrategy queryStrategy) {
        super(settings, observer);

        this.downloadsRoot = downloadsRoot;
        this.courseId = courseId;
        this.tmcApi = new TmcApi(settings);
        this.exerciseDownloader = new ExerciseDownloader(new UrlCommunicator(settings), tmcApi);
        this.exerciseChecksumCache = exerciseChecksumCache;
        this.tmcApi = new TmcApi(settings);
        this.courseCache = courseCache;
        this.queryStrategy = queryStrategy;
    }

    /**
     * Constructs a new download exercises command for downloading exercises of the course
     * identified by {@code courseId} into {@code downloadsRoot}.
     *
     * @param settings      Provides login credentials and download location.
     * @param downloadsRoot          Target path for downloads.
     * @param courseId      Identifies which course's exercises should be downloaded.
     * @param exerciseChecksumCache         A cache for storing the downloads.
     * @param observer      This observer is notified of command's progress. May be {@code null}.
     * @param downloader    Downloader to download the the exercises with.
     * @param tmcApi        TMC server connector for querying the server with.
     */
    public DownloadExercises(
            TmcSettings settings,
            Path downloadsRoot,
            int courseId,
            ExerciseChecksumCache exerciseChecksumCache,
            CourseCache courseCache,
            ProgressObserver observer,
            ExerciseDownloader downloader,
            TmcApi tmcApi,
            Cache.QueryStrategy queryStrategy) {
        super(settings, observer);

        this.exerciseDownloader = downloader;
        this.courseId = courseId;
        this.downloadsRoot = downloadsRoot;
        this.exerciseChecksumCache = exerciseChecksumCache;
        this.courseCache = courseCache;
        this.tmcApi = tmcApi;
        this.queryStrategy = queryStrategy;
    }

    /**
     * Entry point for launching this command.
     */
    @Override
    public List<Exercise> call() throws TmcCoreException {
        if (!settings.userDataExists()) {
            throw new TmcCoreException("Unable to download exercises: missing username/password");
        }

        checkInterrupt();

        Course course = getCourse();

        if (exercises == null) {
            exercises = course.getExercises();
        }

        List<Exercise> downloadedExercises = downloadExercises(course);

        return downloadedExercises;
    }


    private List<Exercise> downloadExercises(Course course) throws TmcCoreException {
        Path target = Paths.get(exerciseDownloader.createCourseFolder(downloadsRoot.toString(), course.getName()));
        List<Exercise> downloaded = new ArrayList<>();

        for (int i = 0; i < exercises.size(); i++) {
            checkInterrupt();

            Exercise exercise = exercises.get(i);
            exercise.setCourseName(course.getName());

            boolean success = exerciseDownloader.handleSingleExercise(exercise, target.toString());

            String message = "Downloading exercise " + exercise.getName() + " failed";
            if (success) {
                downloaded.add(exercise);
                cache(target, exercise);
                message = "Downloading exercise " + exercise.getName() + " was successful";
            }

            informObserver(i, exercises.size(), message);
        }

        return downloaded;
    }

    private void cache(Path courseDir, Exercise exercise) throws TmcCoreException {
        Path exercisePath = courseDir.resolve(exercise.getName());
        try {
            LocalExercise localExercise = new LocalExercise(
                    courseId, exercise.getName(), exercise.getChecksum());
            exerciseChecksumCache.put(exercisePath, localExercise);
        } catch (IOException e) {
            throw new TmcCoreException("Failed to cache downloaded exercise");
        }
    }

    private Course getCourse() throws TmcCoreException {
        if (queryStrategy == Cache.QueryStrategy.PREFER_LOCAL) {
            return getCourseFromCache();
        } else {
            return getCourseFromServer();
        }
    }

    private Course getCourseFromCache() throws TmcCoreException {
        Course currentCourse;

        try {
            currentCourse = this.courseCache.get(this.courseId);
        } catch (IOException e) {
            throw new TmcCoreException("Failed to fetch current course's details from course cache.");
        }

        if (currentCourse ==  null) {
            throw new TmcCoreException("Failed to fetch current course's details from course cache.");
        }

        return currentCourse;
    }

    private Course getCourseFromServer() throws TmcCoreException {
        Optional<Course> courseOptional;

        try {
            courseOptional = tmcApi.getCourse(this.courseId);
        } catch (IOException | URISyntaxException e) {
            throw new TmcCoreException("Failed to fetch current course's details from server.");
        }

        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            try {
                courseCache.put(course.getId(), course);
            } catch (IOException e) {
                throw new TmcCoreException("Failed to cache retrieved course", e);
            }
            return course;
        } else {
            throw new TmcCoreException("Failed to fetch current course's details from server.");
        }
    }

}
