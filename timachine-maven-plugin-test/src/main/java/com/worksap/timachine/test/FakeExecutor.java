package com.worksap.timachine.test;

import com.worksap.timachine.Executor;
import com.worksap.timachine.spi.TransactionManager;
import com.worksap.timachine.spi.VersionProvider;

import java.util.List;

/**
 * Created by liuyang on 3/6/15.
 */
public class FakeExecutor extends Executor{
    public FakeExecutor() {
        super(new TransactionManager() {
            @Override
            public void begin() {

            }

            @Override
            public void commit() {

            }

            @Override
            public void rollback() {

            }

            @Override
            public void close() {

            }
        }, new VersionProvider() {
            @Override
            public List<String> executedVersions() {
                return null;
            }

            @Override
            public void updateVersion(List<String> executedVersions) {

            }
        });
    }
}
