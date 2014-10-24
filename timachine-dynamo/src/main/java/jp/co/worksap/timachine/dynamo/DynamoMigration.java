package jp.co.worksap.timachine.dynamo;

import jp.co.worksap.dynamo.Dynamo;
import jp.co.worksap.timachine.spi.Migration;

/**
 * @author david
 * @version 0.1
 * @since 10-13-2014
 */
public abstract class DynamoMigration implements Migration {

    protected Dynamo dynamo() {
        return DynamoConnectionManager.dynamo;
    }

}
