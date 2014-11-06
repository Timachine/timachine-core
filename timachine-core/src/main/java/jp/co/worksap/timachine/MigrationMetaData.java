package jp.co.worksap.timachine;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * Created by liuyang on 14-11-6.
 */
@Data
public class MigrationMetaData {

    private String version;
    private Class<?> clazz;
    private Method up;
    private Method down;


    public boolean isRevocable() {
        return down != null;
    }
}
