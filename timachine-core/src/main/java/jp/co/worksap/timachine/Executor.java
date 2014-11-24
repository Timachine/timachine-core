package jp.co.worksap.timachine;

import jp.co.worksap.timachine.model.MigrationMetaData;
import jp.co.worksap.timachine.model.Options;
import jp.co.worksap.timachine.model.VersionDifference;
import jp.co.worksap.timachine.spi.TransactionManager;
import jp.co.worksap.timachine.spi.VersionProvider;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyang on 14-10-8.
 */
public class Executor {
    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

    private TransactionManager transactionManager;
    @Getter
    private VersionProvider versionProvider;

    public Executor(TransactionManager transactionManager, VersionProvider versionProvider) {
        this.transactionManager = transactionManager;
        this.versionProvider = versionProvider;
    }

    public void execute(Options options, List<Class<?>> migrationClasses) throws Exception {
        Migrations migrations = new Migrations(migrationClasses);
        VersionChecker versionChecker = new VersionChecker(versionProvider, migrations);
        VersionDifference versionDifference = versionChecker.versionDifference(options.getToVersion());
        if (versionDifference.getVersions().isEmpty()) {
            LOGGER.info("Nothing to migrate, version already up-to-date.");
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
            LOGGER.warn("This operation is not revocable since there is irrevocable migration!!!");
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
                    LOGGER.info("Migrating " + metaData.getClazz().getSimpleName() + " using up method " + metaData.getUp().getName());
                    metaData.getUp().invoke(obj);
                    LOGGER.info("Success!");
                }
            } else {
                for (int i = migrations.size() - 1; i >= 0; i--) {
                    MigrationMetaData metaData = migrations.get(i);
                    Object obj = metaData.getClazz().newInstance();
                    LOGGER.info("Migrating " + metaData.getClazz().getSimpleName() + " using down method " + metaData.getDown().getName());
                    metaData.getDown().invoke(obj);
                    LOGGER.info("Success!");
                }
            }
            versionProvider.updateVersion(versionDifference.getTargetVersion());
            LOGGER.info("Version updated to: " + versionDifference.getTargetVersion());
            transactionManager.commit();
        } catch (Exception e) {
            transactionManager.rollback();
            throw e;
        } finally {
            transactionManager.close();
        }
    }
}
