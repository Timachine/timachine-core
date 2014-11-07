package jp.co.worksap.timachine;

import jp.co.worksap.timachine.migrations.*;
import jp.co.worksap.timachine.model.Options;
import jp.co.worksap.timachine.spi.TransactionManager;
import jp.co.worksap.timachine.spi.VersionProvider;
import lombok.Setter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by liuyang on 14-10-8.
 */
public class TimachineTest {

    private static final List<Class<?>> migrations;


    static {
        migrations = new ArrayList<>();
        migrations.add(M20141106171530.class);
        migrations.add(M20141106171531.class);
        migrations.add(M20141106171532.class);
        migrations.add(M20141106171533WithSomeName.class);
        migrations.add(M20141106171534.class);
    }

    @Rule
    public final StandardOutputStreamLog log = new StandardOutputStreamLog();

    private static final String PACKAGE = "jp.co.worksap.timachine.migrations";
    private TransactionManager transactionManager = new TransactionManager() {
        @Override
        public void begin() {
        }

        @Override
        public void commit() {
        }

        @Override
        public void rollback() {
        }

        @Override
        public void close() {
            // TODO Auto-generated method stub

        }
    };

    private static class FakeVersionProvider implements VersionProvider {

        @Setter
        private String currentVersion;

        @Override
        public String currentVersion() {
            return currentVersion;
        }

        @Override
        public void updateVersion(String newVersion) {

        }
    }

    private FakeVersionProvider versionProvider = new FakeVersionProvider();

    @Test
    public void testUp() throws Exception {
        versionProvider.setCurrentVersion("M20141106171531");
        Executor executor = new Executor(transactionManager, versionProvider);
        Options options = new Options();
        executor.execute(options, migrations);
        assertEquals("up 3\nup 4\nup 5\n", log.getLog());
    }

    @Test
    public void testUpIrrevocable() throws Exception {
        versionProvider.setCurrentVersion("M20141106171530");
        Executor executor = new Executor(transactionManager, versionProvider);
        Options options = new Options();
        executor.execute(options, migrations);
        assertEquals("This operation is not revocable since there is irrevocable migration!!!\nup 2\nup 3\nup 4\nup 5\n", log.getLog());
    }

    @Test
    public void testDown() throws Exception {
        versionProvider.setCurrentVersion("M20141106171534");
        Executor executor = new Executor(transactionManager, versionProvider);
        Options options = new Options();
        options.setToVersion("M20141106171531");
        executor.execute(options, migrations);
        assertEquals("down 5\ndown 4\ndown 3\n", log.getLog());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDownIrrevocable() throws Exception {
        versionProvider.setCurrentVersion("M20141106171534");
        Executor executor = new Executor(transactionManager, versionProvider);
        Options options = new Options();
        options.setToVersion("M20141106171530");
        executor.execute(options, migrations);
    }
}
