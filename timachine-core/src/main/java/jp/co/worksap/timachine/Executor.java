package jp.co.worksap.timachine;

import jp.co.worksap.timachine.model.Options;
import jp.co.worksap.timachine.model.VersionDifference;
import jp.co.worksap.timachine.spi.TransactionManager;
import jp.co.worksap.timachine.spi.VersionProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyang on 14-10-8.
 */
public class Executor {
    private TransactionManager transactionManager;
    private VersionProvider versionProvider;

    public Executor(TransactionManager transactionManager, VersionProvider versionProvider) {
        this.transactionManager = transactionManager;
        this.versionProvider = versionProvider;
    }

    public void execute(Options options, List<Class<?>> migrationClasses) throws Exception {
        Migrations migrations = new Migrations(migrationClasses);
        VersionChecker versionChecker = new VersionChecker(versionProvider, migrations);
        VersionDifference versionDifference = versionChecker.versionDifference(options.getFromVersion(), options.getToVersion());
        if (versionDifference.getVersions().isEmpty()) {
            System.out.println("Nothing to migrate");
            return;
        }
        migrate(versionDifference, migrations);
    }

    private void migrate(VersionDifference versionDifference, Migrations migrations) throws Exception {
        List<MigrationMetaData> targetMigrations = new ArrayList<>();

        boolean revocable = true;
        for (String version : versionDifference.getVersions()) {
            MigrationMetaData metaData = migrations.migration(version);
            targetMigrations.add(metaData);
            if (!metaData.isRevocable()) {
                revocable = false;
            }
        }
        if (!versionDifference.isBehind() && !revocable) {
            System.out.println("This operation is not revocable since there is irrevocable migration!!!");
        }
        if (versionDifference.isBehind() && !revocable) {
            throw new IllegalArgumentException("Can not migrate backwards since there is irrevocable migration.");
        }
        apply(versionDifference, targetMigrations);
    }

    private void apply(VersionDifference versionDifference, List<MigrationMetaData> migrations) throws Exception {
        transactionManager.begin();
        try {
            if (!versionDifference.isBehind()) {
                for (MigrationMetaData metaData : migrations) {
                    Object obj = metaData.getClazz().newInstance();
                    metaData.getUp().invoke(obj);
                }
            } else {
                for (int i = migrations.size() - 1; i >= 0; i--) {
                    MigrationMetaData metaData = migrations.get(i);
                    Object obj = metaData.getClazz().newInstance();
                    metaData.getDown().invoke(obj);
                }
            }
            transactionManager.commit();
        } catch (Exception e) {
            transactionManager.rollback();
            throw e;
        } finally {
            transactionManager.close();
        }
    }
}
