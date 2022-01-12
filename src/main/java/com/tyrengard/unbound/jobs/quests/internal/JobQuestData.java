package com.tyrengard.unbound.jobs.quests.internal;

import com.tyrengard.unbound.jobs.tasks.JobQuestTask;
import dev.morphia.annotations.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public final class JobQuestData {
    private final ArrayList<JobQuestInstance> dailyQuests;
    private final ArrayList<JobQuestInstance> weeklyQuests;

    public JobQuestData() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public JobQuestData(ArrayList<JobQuestInstance> weeklyQuests, ArrayList<JobQuestInstance> dailyQuests) {
        this.weeklyQuests = weeklyQuests;
        this.dailyQuests = dailyQuests;
    }

    public JobQuestInstance getDailyQuest(int slot) {
        return dailyQuests.size() > slot ? dailyQuests.get(slot) : null;
    }

    public JobQuestInstance getWeeklyQuest(int slot) {
        return weeklyQuests.size() > slot ? weeklyQuests.get(slot) : null;
    }

    public List<String> getQuestIds(JobQuestType jobQuestType) {
        return (jobQuestType == JobQuestType.DAILY ? dailyQuests : weeklyQuests).stream()
                .map(JobQuestInstance::getQuestId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public JobQuestInstance getInstance(JobQuest jobQuest) {
        return (jobQuest.getType() == JobQuestType.DAILY ? dailyQuests : weeklyQuests).stream()
                .filter(jqi -> jqi.getQuestId().equals(jobQuest.getId())).findFirst().orElse(null);
    }

    public List<JobQuestInstance> getAllInstances() {
        return Stream.concat(dailyQuests.stream(), weeklyQuests.stream()).collect(Collectors.toList());
    }

//    public Integer getQuestProgress(JobQuest jobQuest) {
//        String jobQuestId = jobQuest.getId();
//        return Stream.concat(dailyQuests.stream(), weeklyQuests.stream())
//                .filter(jqi -> jqi.getQuestId().equals(jobQuestId))
//                .map(jqi -> jqi.getProgress())
//                .findFirst().orElse(null);
//    }
//
//    public void setQuestProgress(JobQuest jobQuest, int amount) {
//        String jobQuestId = jobQuest.getId();
//        Stream.concat(dailyQuests.stream(), weeklyQuests.stream())
//                .filter(jqi -> jqi.getQuestId().equals(jobQuestId))
//                .findFirst()
//                .ifPresent(jqi -> jqi.setAmount(amount));
//    }

    public void setDailyQuest(JobQuest jobQuest, int slot) {
        for (int c = 0; c <= slot - dailyQuests.size(); c++)
            dailyQuests.add(null);
        dailyQuests.set(slot, new JobQuestInstance(jobQuest.getId(), jobQuest.getTasks().stream()
                .map(JobQuestTask::getId).collect(Collectors.toSet())));
    }

    public void setWeeklyQuest(JobQuest jobQuest, int slot) {
        for (int c = 0; c <= slot - weeklyQuests.size(); c++)
            weeklyQuests.add(null);
        weeklyQuests.set(slot, new JobQuestInstance(jobQuest.getId(), jobQuest.getTasks().stream()
                .map(JobQuestTask::getId).collect(Collectors.toSet())));
    }
}
