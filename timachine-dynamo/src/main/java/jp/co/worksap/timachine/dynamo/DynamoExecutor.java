package jp.co.worksap.timachine.dynamo;

import jp.co.worksap.timachine.Executor;

/**
 * Created by liuyang on 14-10-24.
 */
public class DynamoExecutor extends Executor {
    public DynamoExecutor() {

        super(new TransactionManagerDynamo(), new DynamoVersionProvider());
    }
}
