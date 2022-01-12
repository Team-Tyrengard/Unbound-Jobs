package com.tyrengard.unbound.jobs.quests;

import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.quests.impl.CommandQuestReward;
import com.tyrengard.unbound.jobs.quests.impl.JobExpQuestReward;
import com.tyrengard.unbound.jobs.quests.impl.PayQuestReward;

import java.util.Arrays;

public interface JobQuestRewardType {
    String getId();
    JobQuestReward getJobQuestReward(Job source, String rewardLine);

    enum Default implements JobQuestRewardType {
        EXP,
        PAY,
        COMMAND,
        ;

        @Override
        public String getId() {
            return name().toLowerCase();
        }

        @Override
        public JobQuestReward getJobQuestReward(Job source, String str) {
            String[] arr = str.split(" ");
            return switch (this) {
                case EXP -> new JobExpQuestReward(source, Integer.parseInt(arr[1]));
                case PAY -> new PayQuestReward(Double.parseDouble(arr[1]));
                case COMMAND -> new CommandQuestReward(String.join(" ", Arrays.copyOfRange(arr, 1, arr.length)));
            };
        }
    }
}
