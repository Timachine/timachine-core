package com.worksap.migrations;

import com.worksap.timachine.model.Down;
import com.worksap.timachine.model.Migration;
import com.worksap.timachine.model.Up;

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