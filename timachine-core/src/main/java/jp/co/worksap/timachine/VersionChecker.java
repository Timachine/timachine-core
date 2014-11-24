package jp.co.worksap.timachine;


import jp.co.worksap.timachine.model.VersionDifference;
import jp.co.worksap.timachine.spi.VersionProvider;

import java.util.List;

/**
 * Created by liuyang on 14-10-8.
 */
public class VersionChecker {

    public static final String INIT_VERSION = "INIT";
    private final VersionProvider versionProvider;
    private final Migrations migrations;

    public VersionChecker(VersionProvider versionProvider, Migrations migrations) {
        this.versionProvider = versionProvider;
        this.migrations = migrations;
    }

    public VersionDifference versionDifference(String to) {
        VersionDifference versionDifference = new VersionDifference();
        List<String> allVersions = migrations.getVersions();

        if (to == null) {
            to = allVersions.get(allVersions.size() - 1);
        }
        versionDifference.setTargetVersion(to);

        if (!contains(to) && !to.equals(INIT_VERSION)) {
            throw new IllegalArgumentException("Can not find 'to' version " + to);
        }
        int fromIndex, toIndex;
        String currentVersion = versionProvider.currentVersion();
        if (currentVersion == null) {
            fromIndex = -1;
        } else {
            fromIndex = allVersions.indexOf(currentVersion);
        }
        if (to.equals(INIT_VERSION)) {
            toIndex = -1;
        } else {
            toIndex = allVersions.indexOf(to);
        }
        if (fromIndex < toIndex) {
            versionDifference.setVersions(allVersions.subList(fromIndex + 1, toIndex + 1));
        } else {
            versionDifference.setBehind(true);
            versionDifference.setVersions(allVersions.subList(toIndex + 1, fromIndex + 1));
        }
        return versionDifference;
    }

    public boolean contains(String version) {
        return migrations.getVersions().contains(version);
    }
}
