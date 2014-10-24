package jp.co.worksap.timachine.dynamo;

import jp.co.worksap.timachine.Executor;
import jp.co.worksap.timachine.spi.VersionProvider;

/**
 * Created by liuyang on 14-10-24.
 */
public class DynamoExecutor extends Executor {
    public DynamoExecutor(VersionProvider versionProvider) {
        super(new TransactionManagerDynamo(), versionProvider);
    }
}
