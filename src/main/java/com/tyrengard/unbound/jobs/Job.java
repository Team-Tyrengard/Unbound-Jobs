package com.tyrengard.unbound.jobs;

import com.tyrengard.aureycore.foundation.common.utils.StringUtils;
import com.tyrengard.unbound.jobs.actions.Action;
import com.tyrengard.unbound.jobs.quests.internal.JobQuest;
import com.tyrengard.unbound.jobs.quests.internal.JobQuestType;
import com.tyrengard.unbound.jobs.tasks.JobTask;
import com.tyrengard.unbound.jobs.tasks.TaskSource;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Job implements TaskSource {
    private final String id;
    private final String name;
    private final Material icon;
    private final String shortDescription;
    private final List<String> fullDescription;
    private final HashSet<JobTask> tasks;
    private final HashMap<String, JobQuest> dailyQuests;
    private final HashMap<String, JobQuest> weeklyQuests;

    public Job(YamlConfiguration jobConfig) throws InvalidConfigurationException, NumberFormatException {
        //region Info section
        ConfigurationSection infoSection = jobConfig.getConfigurationSection("info");
        if (infoSection == null)
            throw new InvalidConfigurationException("Job config file is missing config: info");
        String name = infoSection.getString("name");
        if (name == null)
            throw new InvalidConfigurationException("Job name cannot be null");
        this.name = StringUtils.stripNonAlphabeticCharacters(name);
        this.id = StringUtils.toKebabCase(name);

        String iconString = infoSection.getString("icon");
        if (iconString == null)
            throw configException("is missing property: info.icon");
        Material mat = Material.matchMaterial(iconString);
        if (mat == null)
            throw configException("has invalid property: info.icon => " + iconString);
        icon = mat;

        String shortDescriptionString = infoSection.getString("short-description");
        if (shortDescriptionString == null)
            throw configException("is missing property: info.short-description");
        shortDescription = shortDescriptionString;

        List<String> fullDescriptionList = infoSection.getStringList("full-description");
        if (fullDescriptionList.isEmpty())
            fullDescription = Collections.singletonList(shortDescription);
        else
            fullDescription = fullDescriptionList;

        // TODO: parse pay-cap and xp-decay
        //endregion
        //region Quests section
        dailyQuests = new HashMap<>();
        weeklyQuests = new HashMap<>();
        ConfigurationSection questsSection = jobConfig.getConfigurationSection("quests");
        if (questsSection != null) {
            Set<String> questIds = questsSection.getKeys(false);
            for (String questId : questIds) {
                ConfigurationSection questConfigSection = questsSection.getConfigurationSection(questId);
                if (questConfigSection == null)
                    throw configException("has invalid quest config: " + questId);
                JobQuest jobQuest = new JobQuest(this, questId, questConfigSection);
                switch (jobQuest.getType()) {
                    case DAILY -> dailyQuests.put(questId, jobQuest);
                    case WEEKLY -> weeklyQuests.put(questId, jobQuest);
                }
            }
        }
        //endregion
        // region Tasks
        tasks = new HashSet<>();
        for (String line : jobConfig.getStringList("tasks")) {
            String taskTypeId = line.split(" ")[0];
            Action action = TaskManager.getAction(taskTypeId);
            if (action == null)
                throw configException("has missing task type: " + taskTypeId);
            JobTask task = action.getJobTask(this, line);
            if (task == null)
                throw configException("has invalid short-form task: " + line);
            tasks.add(task);
        }
        for (Map<?, ?> configSectionMap : jobConfig.getMapList("tasks")) {
            String taskTypeId = (String) configSectionMap.get("task");
            Action action = TaskManager.getAction(taskTypeId);
            if (action == null)
                throw configException("has missing task type: " + taskTypeId);
            JobTask task = action.getJobTask(this, configSectionMap);
            if (task == null)
                throw configException("has invalid expanded task: " + configSectionMap);
            tasks.add(task);
        }
        // endregion
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public List<String> getFullDescription() {
        return fullDescription;
    }

    public HashSet<JobTask> getJobTasks() {
        return tasks;
    }

    public List<JobQuest> getJobQuests(JobQuestType jobQuestType) {
        return switch (jobQuestType) {
            case DAILY -> new ArrayList<>(dailyQuests.values());
            case WEEKLY -> new ArrayList<>(weeklyQuests.values());
        };
    }

    public List<JobQuest> getAllJobQuests() {
        return Stream.concat(dailyQuests.values().stream(), weeklyQuests.values().stream()).collect(Collectors.toList());
    }

    public JobQuest getJobQuest(String questId) {
        JobQuest jobQuest = dailyQuests.get(questId);
        if (jobQuest == null)
            jobQuest = weeklyQuests.get(questId);
        return jobQuest;
    }

    private InvalidConfigurationException configException(String errorMessage) {
        return new InvalidConfigurationException("\"" + name + "\" job config file " + errorMessage);
    }
}
