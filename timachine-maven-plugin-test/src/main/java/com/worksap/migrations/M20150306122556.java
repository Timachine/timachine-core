package com.worksap.migrations;

import jp.co.worksap.timachine.model.Down;
import jp.co.worksap.timachine.model.Migration;
import jp.co.worksap.timachine.model.Up;

@Migration
public class M20150306122556{
    @Up
    public void up(){
        System.out.println("up");
    }

    @Down
    public void down(){

    }
}