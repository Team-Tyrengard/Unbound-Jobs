package com.tyrengard.unbound.jobs.quests.impl;

import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.quests.JobQuestReward;
import com.tyrengard.unbound.jobs.workers.Worker;
import com.tyrengard.unbound.jobs.workers.WorkerManager;
import org.bukkit.entity.Player;

public class JobExpQuestReward implements JobQuestReward {
    private final Job job;
    private final int expAmount;

    public JobExpQuestReward(Job job, int expAmount) {
        this.job = job;
        this.expAmount = expAmount;
    }

    @Override
    public void awardToPlayer(Player p) {
        Worker worker = WorkerManager.obtainWorker(p.getUniqueId());
        WorkerManager.giveJobExperience(worker, job, expAmount);
    }
}
