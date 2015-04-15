package com.worksap.timachine.model;

import lombok.Data;

/**
 * Created by liuyang on 14-10-8.
 */
@Data
public class Options {

    /**
     * If null, migration will end by the newest version
     */
    private String toVersion;

}
