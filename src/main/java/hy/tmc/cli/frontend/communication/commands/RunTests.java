package hy.tmc.cli.frontend.communication.commands;

import com.google.common.base.Optional;
import fi.helsinki.cs.tmc.langs.NoLanguagePluginFoundException;
import fi.helsinki.cs.tmc.langs.RunResult;
import fi.helsinki.cs.tmc.langs.util.TaskExecutorImpl;
import hy.tmc.core.exceptions.ProtocolException;
import hy.tmc.cli.zipping.DefaultRootDetector;
import hy.tmc.cli.zipping.ProjectRootFinder;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RunTests extends Command<RunResult> {

    public RunTests(String path) {
        this.setParameter("path", path);
    }
    
    public RunTests(){
        
    }

    /**
     * Runs tests for exercise.
     *
     * @param exercise Path object
     * @return String contaning results
     * @throws NoLanguagePluginFoundException if path doesn't contain exercise
     */
    public RunResult runTests(Path exercise) throws NoLanguagePluginFoundException {
        TaskExecutorImpl taskExecutor = new TaskExecutorImpl();
        return taskExecutor.runTests(exercise);

    }

    @Override
    public void checkData() throws ProtocolException {
        if (!this.data.containsKey("path") || this.data.get("path").isEmpty()) {
            throw new ProtocolException("File path to exercise required.");
        }
    }

    @Override
    public RunResult call() throws ProtocolException, NoLanguagePluginFoundException {
        String path = (String) this.data.get("path");
        ProjectRootFinder finder = new ProjectRootFinder(new DefaultRootDetector());
        Optional<Path> exercise = finder.getRootDirectory(Paths.get(path));
        if (!exercise.isPresent()) {
            throw new ProtocolException("Not an exercise. (null)");
        }
        return runTests(exercise.get());
    }
}