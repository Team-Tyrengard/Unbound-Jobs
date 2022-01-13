package com.tyrengard.unbound.jobs.quests.internal;

import com.tyrengard.unbound.jobs.tasks.JobQuestTask;
import dev.morphia.annotations.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public final class JobQuestData {
    private static final int
            DEFAULT_DAILY_QUEST_SLOTS = 2, DEFAULT_WEEKLY_QUEST_SLOTS = 1,
            MAX_DAILY_QUEST_SLOTS = 4, MAX_WEEKLY_QUEST_SLOTS = 2;

    private final ArrayList<JobQuestInstance> dailyQuests;
    private final ArrayList<JobQuestInstance> weeklyQuests;

    public JobQuestData() {
        this(new ArrayList<>(Collections.nCopies(DEFAULT_DAILY_QUEST_SLOTS, null)),
                new ArrayList<>(Collections.nCopies(DEFAULT_WEEKLY_QUEST_SLOTS, null)));
    }

    public JobQuestData(ArrayList<JobQuestInstance> dailyQuests, ArrayList<JobQuestInstance> weeklyQuests) {
        this.dailyQuests = dailyQuests;
        this.weeklyQuests = weeklyQuests;
    }

    public JobQuestInstance getDailyQuest(int slot) {
        return dailyQuests.size() > slot ? dailyQuests.get(slot) : null;
    }

    public JobQuestInstance getWeeklyQuest(int slot) {
        return weeklyQuests.size() > slot ? weeklyQuests.get(slot) : null;
    }

    public List<String> getQuestIds(JobQuestType jobQuestType) {
        return (jobQuestType == JobQuestType.DAILY ? dailyQuests : weeklyQuests).stream()
                .filter(Objects::nonNull)
                .map(JobQuestInstance::getQuestId)
                .collect(Collectors.toList());
    }

    public @Nullable JobQuestInstance getInstance(JobQuest jobQuest) {
        return (jobQuest.getType() == JobQuestType.DAILY ? dailyQuests : weeklyQuests).stream()
                .filter(Objects::nonNull)
                .filter(jqi -> jqi.getQuestId().equals(jobQuest.getId())).findFirst().orElse(null);
    }

    public List<JobQuestInstance> getAllInstances() {
        return Stream.concat(dailyQuests.stream(), weeklyQuests.stream()).collect(Collectors.toList());
    }

    public void setDailyQuest(@Nullable JobQuest jobQuest, int slot) {
        if (jobQuest == null)
            dailyQuests.set(slot, null);
        else
            dailyQuests.set(slot, new JobQuestInstance(jobQuest.getId(), jobQuest.getTasks().stream()
                .map(JobQuestTask::getId).collect(Collectors.toSet())));
    }

    public void setWeeklyQuest(@Nullable JobQuest jobQuest, int slot) {
        if (jobQuest == null)
            weeklyQuests.set(slot, null);
        else
            weeklyQuests.set(slot, new JobQuestInstance(jobQuest.getId(), jobQuest.getTasks().stream()
                .map(JobQuestTask::getId).collect(Collectors.toSet())));
    }

    public int getDailyQuestSlots() {
        return dailyQuests.size();
    }

    public void unlockNextDailyQuestSlot() {
        if (dailyQuests.size() < MAX_DAILY_QUEST_SLOTS) {
            dailyQuests.add(null);
        }
    }

    public int getWeeklyQuestSlots() {
        return weeklyQuests.size();
    }

    public void unlockNextWeeklyQuestSlot() {
        if (weeklyQuests.size() < MAX_WEEKLY_QUEST_SLOTS) {
            weeklyQuests.add(null);
        }
    }
}
