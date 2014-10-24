package jp.co.worksap.timachine.dynamo.migrations;

import jp.co.worksap.dynamo.DynamoUtil;
import jp.co.worksap.timachine.dynamo.DynamoMigration;
import jp.co.worksap.timachine.model.Revocable;

@Revocable
public class M1 extends DynamoMigration {

    @Override
    public void up() {
        dynamo().createTable("user", DynamoUtil.createThroughput(1L, 1L), DynamoUtil.createAttrList("number", "N"), DynamoUtil.createKeyElementList("number", "HASH"));
        System.out.println("M1 Dynamo up");
    }


    @Override
    public void down() {
        dynamo().deleteTable("user");
    }
}
