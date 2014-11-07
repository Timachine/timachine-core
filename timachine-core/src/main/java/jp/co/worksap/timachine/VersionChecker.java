package jp.co.worksap.timachine;


import jp.co.worksap.timachine.model.VersionDifference;
import jp.co.worksap.timachine.spi.VersionProvider;

import java.util.List;

/**
 * Created by liuyang on 14-10-8.
 */
public class VersionChecker {

    private final VersionProvider versionProvider;
    private final Migrations migrations;

    public VersionChecker(VersionProvider versionProvider, Migrations migrations) {
        this.versionProvider = versionProvider;
        this.migrations = migrations;
    }

    public VersionDifference versionDifference(String from, String to) {
        VersionDifference versionDifference = new VersionDifference();
        List<String> allVersions = migrations.getVersions();
        if (from == null) {
            from = versionProvider.currentVersion();
        }
        if (to == null) {
            to = allVersions.get(allVersions.size() - 1);
        }
        versionDifference.setTargetVersion(to);
        if (from != null && !contains(from)) {
            throw new IllegalArgumentException("Can not find 'from' version " + from);
        }
        if (!contains(to)) {
            throw new IllegalArgumentException("Can not find 'to' version " + to);
        }
        int fromIndex;
        if (from == null) {
            fromIndex = -1;
        } else {
            fromIndex = allVersions.indexOf(from);
        }
        int toIndex = allVersions.indexOf(to);
        if (fromIndex < toIndex) {
            versionDifference.setVersions(allVersions.subList(fromIndex + 1, toIndex + 1));
        } else {
            versionDifference.setBehind(true);
            versionDifference.setVersions(allVersions.subList(toIndex + 1, fromIndex + 1));
        }
        return versionDifference;
    }

    public VersionDifference diffUtil(String to) {
        return versionDifference(null, to);
    }

    public VersionDifference diffUtilNow() {
        return versionDifference(null, null);
    }

    public boolean contains(String version) {
        return migrations.getVersions().contains(version);
    }
}
