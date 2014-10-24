package jp.co.worksap.timachine.dynamo;

import jp.co.worksap.dynamo.DynamoLocalInstance;
import jp.co.worksap.timachine.Executor;
import jp.co.worksap.timachine.dynamo.migrations.M1;
import jp.co.worksap.timachine.dynamo.migrations.M2;
import jp.co.worksap.timachine.model.Options;
import jp.co.worksap.timachine.spi.Migration;
import jp.co.worksap.timachine.spi.VersionProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author david
 * @version 0.1
 * @since 10-13-2014
 */
public class DynamoTest {
    private static final List<Class<? extends Migration>> migrations;


    static {
        migrations = new ArrayList<>();
        migrations.add(M1.class);
        migrations.add(M2.class);
    }

    @BeforeClass
    public static void initLocal() {
        DynamoLocalInstance.start();

        opt = new Options();
        opt.setToVersion("M2");
    }

    @AfterClass
    public static void closeLocal() {
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
