package jp.co.worksap.timachine.dynamo;

import jp.co.worksap.dynamo.DynamoLocalInstance;
import jp.co.worksap.timachine.Executor;
import jp.co.worksap.timachine.dynamo.migrations.M20141106173530;
import jp.co.worksap.timachine.dynamo.migrations.M20141107173530;
import jp.co.worksap.timachine.model.Options;
import jp.co.worksap.timachine.spi.VersionProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author david
 * @version 0.1
 * @since 10-13-2014
 */
public class DynamoTest {
    private static final List<Class<?>> migrations;


    static {
        migrations = new ArrayList<>();
        migrations.add(M20141106173530.class);
        migrations.add(M20141107173530.class);
    }

    @BeforeClass
    public static void initLocal() throws IOException {
        DynamoLocalInstance.start();

        opt = new Options();
        opt.setToVersion("M20141107173530");
    }

    @AfterClass
    public static void closeLocal() throws InterruptedException {
        DynamoLocalInstance.end();
    }

    public static Options opt;

    @Test
    public void testDynamoMigration() throws Exception {
        Executor exe = new Executor(new TransactionManagerDynamo(), new VersionProvider() {

            @Override
            public String currentVersion() {
                return null;
            }
        });
        exe.execute(opt, migrations);
    }

}
