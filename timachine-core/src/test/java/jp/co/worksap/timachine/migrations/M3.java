package jp.co.worksap.timachine.migrations;

import jp.co.worksap.timachine.model.Revocable;
import jp.co.worksap.timachine.spi.Migration;

/**
 * Created by liuyang on 14-10-8.
 */
@Revocable
public class M3 implements Migration {
    @Override
    public void up() {
        System.out.println("up 3");
    }

    @Override
    public void down() {
        System.out.println("down 3");

    }
}