package jp.co.worksap.timachine.migrations;

import jp.co.worksap.timachine.spi.Migration;

/**
 * Created by liuyang on 14-10-8.
 */
public class M2 implements Migration {
    @Override
    public void up() {
        System.out.println("up 2");
    }

    @Override
    public void down() {
        System.out.println("down 2");

    }
}
