package tests;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class BaseTest {

    private static Logger logger = Logger.getLogger(BaseTest.class.getName());
    private static AtomicLong finishedTestsCount = new AtomicLong();

    @Rule
    public TestRule watcher = new TestWatcher() {

        private long started;

        @Override
        protected void starting(Description description) {
            logger.info(String.format("Starting test %s", description.getMethodName()));
            started = System.currentTimeMillis();
        }

        @Override
        protected void finished(Description description) {
            long finished = System.currentTimeMillis();
            logger.info(
                    String.format(
                            "Finished test %s took %s ms",
                            description.getMethodName(),
                            finished - started));
            logger.info(
                    String.format(
                            "Finished tests count: %s",
                            finishedTestsCount.incrementAndGet()));
        }
    };

    @BeforeClass
    public static void setClass() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        Config.initialize();
    }
}
