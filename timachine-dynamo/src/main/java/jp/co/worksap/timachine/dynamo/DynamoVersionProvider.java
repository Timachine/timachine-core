package jp.co.worksap.timachine.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import jp.co.worksap.dynamo.Dynamo;
import jp.co.worksap.dynamo.DynamoOnline;
import jp.co.worksap.dynamo.DynamoUtil;
import jp.co.worksap.timachine.spi.VersionProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by liuyang on 14-11-7.
 */
public class DynamoVersionProvider implements VersionProvider {
    private final Dynamo dynamo = new DynamoOnline();

    private final List<DatabaseVersion> versions;
    private final String currentVersion;
    private final int nextSeq;

    public DynamoVersionProvider() {
        if (!dynamo.existTable("DatabaseVersion")) {
            versions = null;
            currentVersion = null;
            nextSeq = 1;
        } else {
            versions = new ArrayList<>(dynamo.getMapper().scan(DatabaseVersion.class, new DynamoDBScanExpression()));
            Collections.sort(versions);
            if (versions.isEmpty()) {
                currentVersion = null;
                nextSeq = 1;
            } else {
                DatabaseVersion newest = versions.get(versions.size() - 1);
                currentVersion = newest.getTargetVersion();
                nextSeq = newest.getExecutionSeq() + 1;
            }
        }
    }

    private void createVersionTable() {
        ProvisionedThroughput throughput = new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L);

        List<AttributeDefinition> attrList = DynamoUtil.createAttrList("executionSeq", "N");
        List<KeySchemaElement> elements = DynamoUtil.createKeyElementList("executionSeq", "HASH");

        dynamo.createTable("DatabaseVersion", throughput, attrList, elements);
    }

    @Override
    public String currentVersion() {
        return currentVersion;
    }

    @Override
    public void updateVersion(String newVersion) {
        if (!dynamo.existTable("DatabaseVersion")) {
            createVersionTable();
        }
        DatabaseVersion newDatabaseVersion = new DatabaseVersion();
        newDatabaseVersion.setTargetVersion(newVersion);
        newDatabaseVersion.setExecutionSeq(nextSeq);
        dynamo.save(newDatabaseVersion);
    }
}
