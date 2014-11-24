package jp.co.worksap.timachine.model;


/**
 * Created by liuyang on 14-11-6.
 */
public enum MigrationType {
    MAIN("M"), TEST("T");

    MigrationType(String prefix) {
        this.prefix = prefix;
    }

    private String prefix;

    public String getPrefix() {
        return prefix;
    }
}
