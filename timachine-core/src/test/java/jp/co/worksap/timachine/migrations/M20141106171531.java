package jp.co.worksap.timachine.migrations;

import jp.co.worksap.timachine.model.Down;
import jp.co.worksap.timachine.model.Migration;
import jp.co.worksap.timachine.model.Up;

/**
 * Created by liuyang on 14-10-8.
 */
@Migration
public class M20141106171531 {
    @Up
    public void up() {
        System.out.println("up 2");
    }

    @Down
    public void down() {
        System.out.println("down 2");
    }
}
