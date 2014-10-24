package jp.co.worksap.timachine;

import jp.co.worksap.timachine.spi.Migration;
import lombok.Getter;

import java.util.*;

/**
 * Created by liuyang on 14-10-10.
 */
public class Migrations {

    @Getter
    private final List<String> versions;

    private final Map<String, Class<? extends Migration>> migrationsMap;

    public Migrations(List<Class<? extends Migration>> migrationClasses) {
        //Reflections.log = null;
        versions = new ArrayList<>();
        migrationsMap = new HashMap<>();
        for (Class<? extends Migration> migration : migrationClasses) {
            versions.add(migration.getSimpleName());
            migrationsMap.put(migration.getSimpleName(), migration);
        }
        Collections.sort(versions);
    }

    public Class<? extends Migration> migration(String name) {
        return migrationsMap.get(name);
    }
}
