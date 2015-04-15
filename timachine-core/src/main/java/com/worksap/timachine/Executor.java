package com.worksap.timachine;

import com.worksap.timachine.spi.TransactionManager;
import com.worksap.timachine.spi.VersionProvider;
import com.worksap.timachine.model.MigrationMetaData;
import com.worksap.timachine.model.Options;
import com.worksap.timachine.model.VersionDifference;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if (migrations.getVersions().size() == 0) {
            LOGGER.info("Nothing to migrate, no migration found.");
            return;
        }
        VersionChecker versionChecker = new VersionChecker(versionProvider, migrations);
        VersionDifference versionDifference = versionChecker.versionDifference(options.getToVersion());
        if (versionDifference.getSteps().isEmpty()) {
            LOGGER.info("Nothing to migrate, version already up-to-date.");
            return;
        }
        migrate(versionDifference, migrations);
    }

    private void migrate(VersionDifference versionDifference, Migrations migrations) throws Exception {

        boolean revocable = true;
        boolean executable = true;
        for (VersionDifference.Step step : versionDifference.getSteps()) {
            MigrationMetaData metaData = migrations.migration(step.getVersion());
            if (!metaData.isRevocable() && step.isUp()) {
                revocable = false;
            }
            if (!metaData.isRevocable() && !step.isUp()) {
                executable = false;
            }
        }
        if (!revocable) {
            LOGGER.warn("This operation is not revocable since there is irrevocable migration!!!");
        }
        if (!executable) {
            throw new IllegalArgumentException("Can not migrate backwards since there is irrevocable migration.");
        }
        apply(versionDifference, migrations);
    }

    private void apply(VersionDifference versionDifference, Migrations migrations) throws Exception {
        transactionManager.begin();
        try {
            for (VersionDifference.Step step : versionDifference.getSteps()) {
                MigrationMetaData metaData = migrations.migration(step.getVersion());
                Object obj = metaData.getClazz().newInstance();
                if (step.isUp()) {
                    LOGGER.info("Migrating " + metaData.getClazz().getSimpleName() + " using up method " + metaData.getUp().getName());
                    metaData.getUp().invoke(obj);
                } else {
                    LOGGER.info("Migrating " + metaData.getClazz().getSimpleName() + " using down method " + metaData.getDown().getName());
                    metaData.getDown().invoke(obj);
                }
                LOGGER.info("Success!");
            }
            versionProvider.updateVersion(versionDifference.getVersionsAfterExecuted());
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
