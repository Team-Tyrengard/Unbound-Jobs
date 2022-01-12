package com.tyrengard.unbound.jobs.quests.impl;

import com.tyrengard.unbound.jobs.quests.JobQuestReward;
import com.tyrengard.unbound.jobs.workers.Worker;
import com.tyrengard.unbound.jobs.workers.WorkerManager;
import org.bukkit.entity.Player;

public class PayQuestReward implements JobQuestReward {
    private final double payAmount;

    public PayQuestReward(double payAmount) {
        this.payAmount = payAmount;
    }

    @Override
    public void awardToPlayer(Player p) {
        Worker worker = WorkerManager.obtainWorker(p.getUniqueId());
        WorkerManager.payWorker(worker, payAmount);
    }
}
