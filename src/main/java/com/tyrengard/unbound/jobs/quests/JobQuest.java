package com.tyrengard.unbound.jobs.quests;

import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.JobManager;
import com.tyrengard.unbound.jobs.TaskManager;
import com.tyrengard.unbound.jobs.actions.Action;
import com.tyrengard.unbound.jobs.tasks.JobQuestTask;
import com.tyrengard.unbound.jobs.tasks.TaskSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

public class JobQuest implements TaskSource {
    private final Job job;
    private final String id;
    private final String title;
    private final JobQuestType type;
    private final int weight;
    private final short minLevel, maxLevel;
    private final JobQuestListType listType;
    private final TreeMap<Integer, JobQuestTask> tasks;
    private final HashSet<JobQuestReward> rewards;

    public JobQuest(@NotNull Job job, @NotNull String id, @NotNull ConfigurationSection questSection) throws InvalidConfigurationException {
        this.job = job;
        this.id = id;

        this.title = questSection.getString("title");
        if (title == null)
            throw configException("has missing property: title");

        String type = questSection.getString("type");
        if (type == null)
            throw configException("has missing property: type");
        try {
            this.type = JobQuestType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw configException("has invalid type: " + type);
        }

        this.weight = questSection.getInt("weight", 1);
        this.minLevel = (short) questSection.getInt("min-level", 0);
        this.maxLevel = (short) questSection.getInt("min-level", Short.MAX_VALUE);

        String listType = questSection.getString("task-list-type");
        if (listType == null)
            throw configException("has missing property: task-list-type");
        try {
            this.listType = JobQuestListType.valueOf(listType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw configException("has invalid task-list-type: " + type);
        }

        this.tasks = new TreeMap<>();
        List<String> taskLines = questSection.getStringList("tasks");
        if (taskLines.isEmpty())
            throw configException("has no tasks");
        int taskId = 1;
        for (String line : taskLines) {
            String taskTypeId = line.split(" ")[0];
            Action action = TaskManager.getAction(taskTypeId);
            if (action == null)
                throw configException("has missing task type: " + taskTypeId);
            JobQuestTask task = action.getJobQuestTask(taskId,this, line);
            if (task == null)
                throw configException("has invalid task: " + line);
            tasks.put(taskId, task);
            taskId++;
        }

        this.rewards = new HashSet<>();
        List<String> rewardLines = questSection.getStringList("rewards");
        if (rewardLines.isEmpty())
            throw configException("has no rewards");
        for (String line : rewardLines) {
            String rewardTypeId = line.split(" ")[0];
            JobQuestRewardType rewardType = JobManager.getJobQuestRewardType(rewardTypeId);
            if (rewardType == null)
                throw configException("has missing reward type: " + rewardTypeId);
            JobQuestReward reward = rewardType.getJobQuestReward(job, line);
            if (reward == null)
                throw configException("has invalid reward: " + line);
            rewards.add(reward);
        }
    }

    public @NotNull Job getJob() {
        return job;
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull String getTitle() {
        return title;
    }

    public @NotNull JobQuestType getType() {
        return type;
    }

    public int getWeight() {
        return weight;
    }

    public short getMinLevel() {
        return minLevel;
    }

    public short getMaxLevel() {
        return maxLevel;
    }

    public @NotNull JobQuestListType getListType() {
        return listType;
    }

    public @NotNull List<JobQuestTask> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public @NotNull HashSet<JobQuestReward> getRewards() {
        return new HashSet<>(rewards);
    }

    private InvalidConfigurationException configException(String errorMessage) {
        return new InvalidConfigurationException("\"" + id + "\" job quest " + errorMessage);
    }
}
