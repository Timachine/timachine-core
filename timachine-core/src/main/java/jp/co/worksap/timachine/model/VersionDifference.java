package jp.co.worksap.timachine.model;

import lombok.Data;

import java.util.List;

/**
 * Created by liuyang on 14-10-8.
 */


@Data
public class VersionDifference {
    private boolean behind = false;
    private List<String> versions;
}