package jp.co.worksap.timachine.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyang on 14-10-8.
 */


@Data
public class VersionDifference {
    @Data
    @AllArgsConstructor
    public static class Step {
        private String version;
        private boolean up;
    }

    private List<Step> steps = new ArrayList<>();
    private List<String> versionsAfterExecuted;
    private String latestExecutedVersion;
    private String targetVersion;

    private boolean behind = false;

    public void addStep(String version, boolean up) {
        steps.add(new Step(version, up));
    }

}