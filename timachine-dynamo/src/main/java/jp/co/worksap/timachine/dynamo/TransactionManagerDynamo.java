package jp.co.worksap.timachine.dynamo;

import jp.co.worksap.timachine.spi.TransactionManager;

/**
 * @author david
 * @version 0.1
 * @since 10-13-2014
 */
public class TransactionManagerDynamo implements TransactionManager {

    //TODO use DI in the future
    public TransactionManagerDynamo() {
    }

    @Override
    public void begin() {
    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {
        System.out.println("please rollback by yourself!");
    }

    @Override
    public void close() {
        DynamoConnectionManager.close();
    }
}
