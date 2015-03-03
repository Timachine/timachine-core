package jp.co.worksap.timachine;


import jp.co.worksap.timachine.model.VersionDifference;
import jp.co.worksap.timachine.spi.VersionProvider;

import java.util.ArrayList;
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

    private String checkToVersion(String to) {
        List<String> allVersions = migrations.getVersions();

        if (to == null) {
            to = allVersions.get(allVersions.size() - 1);
        }
        if (!contains(to) && !to.equals(INIT_VERSION)) {
            throw new IllegalArgumentException("Can not find 'to' version " + to);
        }
        return to;
    }

    private int checkToIndex(String to) {
        if (to.equals(INIT_VERSION)) {
            return -1;
        } else {
            return migrations.getVersions().indexOf(to);
        }
    }

    public VersionDifference versionDifference(String to) {
        List<String> allVersions = migrations.getVersions();
        VersionDifference versionDifference = new VersionDifference();
        to = checkToVersion(to);
        versionDifference.setTargetVersion(to);

        int latestExecutedIndex;
        List<String> currentVersions = versionProvider.executedVersions();
        if (currentVersions == null) {
            currentVersions = new ArrayList<>();
        }
        String latestExecutedVersion = null;
        if (currentVersions.isEmpty()) {
            latestExecutedIndex = -1;
        } else {
            latestExecutedVersion = currentVersions.get(currentVersions.size() - 1);
            latestExecutedIndex = allVersions.indexOf(latestExecutedVersion);
        }
        versionDifference.setLatestExecutedVersion(latestExecutedVersion);
        int toIndex = checkToIndex(to);

        versionDifference.setVersionsAfterExecuted(allVersions.subList(0, toIndex + 1));
        //fulfill missed versions before the target
        for (int i = 0; i <= toIndex; i++) {
            String version = allVersions.get(i);
            if (!currentVersions.contains(version)) {
                versionDifference.addStep(version, true);
            }
        }
        if (latestExecutedIndex > toIndex) { // down method
            for (int i = latestExecutedIndex; i > toIndex; i--) {
                String version = allVersions.get(i);
                if (currentVersions.contains(version)) {
                    versionDifference.addStep(version, false);
                }
            }
            versionDifference.setBehind(true);
        }
        return versionDifference;
    }

    public boolean contains(String version) {
        return migrations.getVersions().contains(version);
    }
}
