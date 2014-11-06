package jp.co.worksap.timachine.dynamo.migrations;

import jp.co.worksap.dynamo.DynamoUtil;
import jp.co.worksap.timachine.dynamo.DynamoMigration;
import jp.co.worksap.timachine.model.Down;
import jp.co.worksap.timachine.model.Migration;
import jp.co.worksap.timachine.model.Up;

@Migration
public class M20141107173530 extends DynamoMigration {

    @Up
    public void up() {
        dynamo().createTable("ctmapp", DynamoUtil.createThroughput(1L, 1L),
                DynamoUtil.createAttrList("appId", "S", "accountId", "S"),
                DynamoUtil.createKeyElementList("appId", "HASH", "accountId", "RANGE"));
        System.out.println("M2 Dynamo up");
    }

    @Down
    public void down() {
        dynamo().deleteTable("ctmapp");
    }
}
