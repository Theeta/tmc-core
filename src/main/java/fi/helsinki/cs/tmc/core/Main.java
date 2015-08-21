package fi.helsinki.cs.tmc.core;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import fi.helsinki.cs.tmc.core.configuration.TmcSettings;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.exceptions.TmcCoreException;
import fi.helsinki.cs.tmc.langs.domain.RunResult;
import fi.helsinki.cs.tmc.langs.domain.TestResult;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import org.apache.maven.cli.MavenCli;


public class Main {
    static String path;
    
    public static void main(String[] args) throws TmcCoreException, InterruptedException, ExecutionException {

        MavenCli cc = new MavenCli();
        
        System.getProperties().setProperty("M2_HOME", "/usr/bin/mvn");
        
        TmcCore c = new TmcCore(settings("a", "b"));
        //path = "/home/ilari/rage/tmc-langs/tmc-langs-java/src/test/resources/maven_exercise";
        path = "/home/ilari/rage/nbtest/helloworld";
        ListenableFuture<RunResult> f = c.test(Paths.get(path));
        
        RunResult rr = f.get();
        
        System.out.println("whatever: " + rr);
        
        System.out.println(rr.status);
        
        for (String s : rr.logs.keySet()) {
            System.out.println(s + " maps to " + new String(rr.logs.get(s)));
        }
        
        for (TestResult tr : rr.testResults) {
            System.out.println(tr);
        }
        
        
    }
    
    static TmcSettings settings(final String a, final String b) {
        return new TmcSettings() {

            @Override
            public String getServerAddress() {
                return "https://tmc.mooc.fi/hy";
            }

            @Override
            public String getPassword() {
                return b;
            }

            @Override
            public String getUsername() {
                return a;
            }

            @Override
            public boolean userDataExists() {
                return true;
            }

            @Override
            public Optional<Course> getCurrentCourse() {
                return Optional.of(new Course("kesa2015-wepa"));
            }

            @Override
            public String apiVersion() {
                return "7";
            }

            @Override
            public String clientName() {
                return "tmc_cli";
            }

            @Override
            public String clientVersion() {
                return "1";
            }

            @Override
            public String getFormattedUserData() {
                return this.getUsername() + ":" + this.getPassword();
            }

            @Override
            public String getTmcMainDirectory() {
                return path;
            }
            
        };
    }
}
