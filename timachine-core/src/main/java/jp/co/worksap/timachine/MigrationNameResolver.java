package jp.co.worksap.timachine;

import jp.co.worksap.timachine.model.MigrationType;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liuyang on 14-11-6.
 */
public class MigrationNameResolver {

    private static final String PATTERN = "[MT]\\d{14}([A-Z].*)?";

    public static String generateClassName(String name, MigrationType type) {
        if (name == null) {
            name = "";
        }
        if (!name.matches("([A-Z]\\w*)?")) {
            throw new RuntimeException("Name should start with up case letter, with letters and digits following.");
        }
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return type.getPrefix() + timestamp + name;
    }

    public static String validateVersionOf(String name) {
        if (name == null) {
            throw new RuntimeException("Name should not be null!");
        }
        if (!name.matches(PATTERN)) {
            throw new RuntimeException("Name should start with M or T, following 14 timestamp digits, with optional name start with up case letter");
        }
        return name.substring(0, 15);
    }
}
