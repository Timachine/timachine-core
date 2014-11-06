package jp.co.worksap.timachine.dynamo.migrations;

import jp.co.worksap.dynamo.DynamoUtil;
import jp.co.worksap.timachine.dynamo.DynamoMigration;
import jp.co.worksap.timachine.model.Down;
import jp.co.worksap.timachine.model.Migration;
import jp.co.worksap.timachine.model.Up;

@Migration
public class M20141106173530 extends DynamoMigration {

    @Up
    public void up() {
        dynamo().createTable("user", DynamoUtil.createThroughput(1L, 1L), DynamoUtil.createAttrList("number", "N"), DynamoUtil.createKeyElementList("number", "HASH"));
        System.out.println("M1 Dynamo up");
    }


    @Down
    public void down() {
        dynamo().deleteTable("user");
    }
}
