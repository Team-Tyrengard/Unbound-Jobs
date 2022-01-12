package com.tyrengard.unbound.jobs;

import com.tyrengard.aureycore.foundation.AManager;
import com.tyrengard.unbound.jobs.events.JobQuestTaskPerformEvent;
import com.tyrengard.unbound.jobs.exceptions.UnboundJobsException;
import com.tyrengard.unbound.jobs.quests.JobQuestReward;
import com.tyrengard.unbound.jobs.quests.JobQuestRewardType;
import com.tyrengard.unbound.jobs.quests.internal.JobQuest;
import com.tyrengard.unbound.jobs.quests.internal.JobQuestData;
import com.tyrengard.unbound.jobs.quests.internal.JobQuestInstance;
import com.tyrengard.unbound.jobs.tasks.JobQuestTask;
import com.tyrengard.unbound.jobs.workers.Worker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Objects;

public class QuestManager extends AManager<UnboundJobs> implements Listener {
    private final HashMap<String, JobQuestRewardType> jobQuestRewardTypes;

    private static QuestManager instance;
    QuestManager(UnboundJobs plugin) {
        super(plugin);
        instance = this;

        jobQuestRewardTypes = new HashMap<>();
    }

    // region Manager overrides
    @Override
    protected void startup() {
        for (JobQuestRewardType type : JobQuestRewardType.Default.values()) {
            try {
                registerJobQuestReward(type);
            } catch (UnboundJobsException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void cleanup() {

    }
    // endregion

    public static void registerJobQuestReward(JobQuestRewardType jobQuestRewardType)  throws UnboundJobsException {
        String jobQuestRewardId = jobQuestRewardType.getId();
        if (instance.jobQuestRewardTypes.containsKey(jobQuestRewardId))
            throw new UnboundJobsException("The job quest reward id \"" + jobQuestRewardId + "\" already exists.");
        else
            instance.jobQuestRewardTypes.put(jobQuestRewardId, jobQuestRewardType);
    }

    public static JobQuestRewardType getJobQuestRewardType(String id) {
        return instance.jobQuestRewardTypes.get(id);
    }

    // region Event listeners
    @EventHandler
    private void onJobQuestTaskPerform(JobQuestTaskPerformEvent e) {
        Worker w = e.getWorker();
        JobQuestTask t = e.getTask();
        JobQuest jq = t.getSource();
        Job j = jq.getJob();

        JobQuestData jobQuestData = w.getJobQuestData(j);
        if (jobQuestData == null)
            return;

        JobQuestInstance jobQuestInstance = jobQuestData.getInstance(jq);
        if (!jobQuestInstance.isActive())
            return;

        int newProgress = jobQuestInstance.getProgress(t.getId()) + 1, progressRequired = t.getProgressRequired();
        if (newProgress >= progressRequired) {
            Player p = Objects.requireNonNull(Bukkit.getPlayer(w.getId()));
            for (JobQuestReward reward : jq.getRewards())
                reward.awardToPlayer(p);
            jobQuestInstance.setActive(false);
            p.sendMessage("Quest " + ChatColor.YELLOW + jq.getTitle() +
                    ChatColor.WHITE + " completed! Rewards have been awarded.");
        } else {
            jobQuestInstance.setProgress(t.getId(), newProgress);
        }
    }
    // endregion
}
