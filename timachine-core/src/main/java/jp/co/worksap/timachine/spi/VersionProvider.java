package jp.co.worksap.timachine.spi;

/**
 * Created by liuyang on 14-10-8.
 */
public interface VersionProvider {
    String currentVersion();

    void updateVersion(String newVersion);
}
