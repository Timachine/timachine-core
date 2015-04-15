package com.worksap.timachine.spi;

import java.util.List;

/**
 * Created by liuyang on 14-10-8.
 */
public interface VersionProvider {
    List<String> executedVersions();

    void updateVersion(List<String> executedVersions);
}
