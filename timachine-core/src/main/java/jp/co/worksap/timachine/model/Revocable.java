package jp.co.worksap.timachine.model;

import java.lang.annotation.*;

/**
 * <p>Indicate a migration is revocable, which means the "down" method is implemented and valid.</p>
 * <p>When migrate up, warnings will show if any migration between the "from" and "to" version is annotated with revocable.</p>
 * <p>When migrate down, process will not start if any migration between the "from" and "to" version is annotated with revocable.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Revocable {
}
