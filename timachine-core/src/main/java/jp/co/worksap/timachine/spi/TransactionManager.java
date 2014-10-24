package jp.co.worksap.timachine.spi;

/**
 * Created by liuyang on 14-10-8.
 */
public interface TransactionManager {
    void begin();
    void commit();
    void rollback();
	void close();
}
