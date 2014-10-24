package jp.co.worksap.timachine.dynamo;


import jp.co.worksap.dynamo.Dynamo;
import jp.co.worksap.dynamo.DynamoOnline;

/**
 * @author david
 * @version 0.1
 * @since 10-13-2014
 */
public class DynamoConnectionManager {

    public static Dynamo dynamo = DynamoOnline.instance();

    static void close() {
        dynamo.close();
    }

}
