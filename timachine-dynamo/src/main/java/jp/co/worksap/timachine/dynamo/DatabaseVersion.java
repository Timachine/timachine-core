package jp.co.worksap.timachine.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

/**
 * Created by liuyang on 14-11-7.
 */
@DynamoDBTable(tableName = "DatabaseVersion")
@Data
public class DatabaseVersion implements Comparable<DatabaseVersion> {

    @DynamoDBHashKey
    private int executionSeq;
    @DynamoDBAttribute
    private String targetVersion;

    @Override
    public int compareTo(DatabaseVersion o) {
        return executionSeq - o.getExecutionSeq();
    }
}
