package jp.co.worksap.timachine.dynamo.migrations;

import jp.co.worksap.dynamo.DynamoUtil;
import jp.co.worksap.timachine.dynamo.DynamoMigration;
import jp.co.worksap.timachine.model.Revocable;

@Revocable
public class M2 extends DynamoMigration {

    @Override
    public void up() {
        dynamo().createTable("ctmapp", DynamoUtil.createThroughput(1L, 1L),
                DynamoUtil.createAttrList("appId", "S", "accountId", "S"),
                DynamoUtil.createKeyElementList("appId", "HASH", "accountId", "RANGE"));
        System.out.println("M2 Dynamo up");
    }

    @Override
    public void down() {
        dynamo().deleteTable("ctmapp");
    }
}
