package com.tyrengard.unbound.jobs.workers;

import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.JobData;
import com.tyrengard.unbound.jobs.JobManager;
import com.tyrengard.unbound.jobs.quests.internal.JobQuest;
import com.tyrengard.unbound.jobs.quests.internal.JobQuestData;
import com.tyrengard.unbound.jobs.workers.enums.BossBarExpIndicatorSetting;
import dev.morphia.annotations.AlsoLoad;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
public final class Worker {
    @Id
    private final UUID id;
    @AlsoLoad("jobs")
    private HashMap<String, JobData> jobData;
    private HashMap<String, JobQuestData> jobQuestData;

    private int dailyQuestSlots, weeklyQuestSlots;
    private LocalDate lastQuestRefreshDate;

    private BossBarExpIndicatorSetting bossBarExpIndicatorSetting;

    public Worker(UUID id, int dailyQuestSlots, int weeklyQuestSlots) {
        this(id, new HashMap<>(), new HashMap<>(),
                dailyQuestSlots, weeklyQuestSlots, BossBarExpIndicatorSetting.DEFAULT);
    }

    public Worker(UUID id, HashMap<String, JobData> jobData,
                  HashMap<String, JobQuestData> jobQuestData,
                  int dailyQuestSlots, int weeklyQuestSlots,
                  BossBarExpIndicatorSetting bossBarExpIndicatorSetting) {
        this.id = id;
        this.jobData = jobData;
        this.jobQuestData = jobQuestData;
        this.dailyQuestSlots = dailyQuestSlots;
        this.weeklyQuestSlots = weeklyQuestSlots;
        this.bossBarExpIndicatorSetting = bossBarExpIndicatorSetting;
    }

    public UUID getId() {
        return id;
    }

    private HashMap<String, JobData> getJobDataMap() {
        if (jobData == null)
            jobData = new HashMap<>();
        return jobData;
    }

    public List<Job> getJobs() {
        return getJobDataMap().keySet().stream().map(JobManager::getJob).filter(Objects::nonNull).toList();
    }

    public JobData getJobData(Job j) {
        return getJobDataMap().get(j.getId());
    }

    public void setJobData(Job j, JobData jd) {
        getJobDataMap().put(j.getId(), jd);
    }

    public boolean hasJob(Job j) {
        return getJobDataMap().containsKey(j.getId());
    }

    public void removeJob(Job j) {
        getJobDataMap().remove(j.getId());
    }

    private HashMap<String, JobQuestData> getJobQuestData() {
        if (jobQuestData == null)
            jobQuestData = new HashMap<>();
        return jobQuestData;
    }

    public JobQuestData getJobQuestData(Job j) {
        return getJobQuestData().computeIfAbsent(j.getId(), jobId -> new JobQuestData());
    }

    public void setDailyJobQuest(JobQuest jobQuest, int slot) {
        String jobId = jobQuest.getJob().getId();
        if (getJobQuestData().containsKey(jobId))
            getJobQuestData().computeIfAbsent(jobId, unused -> new JobQuestData()).setDailyQuest(jobQuest, slot);
    }

    public void setWeeklyJobQuest(JobQuest jobQuest, int slot) {
        String jobId = jobQuest.getJob().getId();
        if (getJobQuestData().containsKey(jobId))
            getJobQuestData().computeIfAbsent(jobId, id -> new JobQuestData()).setWeeklyQuest(jobQuest, slot);
    }

    public LocalDate getLastQuestRefreshDate() {
        return lastQuestRefreshDate;
    }

    public void setLastQuestRefreshDate(LocalDate lastQuestRefreshDate) {
        this.lastQuestRefreshDate = lastQuestRefreshDate;
    }

    public int getDailyQuestSlots() {
        return dailyQuestSlots;
    }

    public void setDailyQuestSlots(int dailyQuestSlots) {
        this.dailyQuestSlots = dailyQuestSlots;
    }

    public int getWeeklyQuestSlots() {
        return weeklyQuestSlots;
    }

    public void setWeeklyQuestSlots(int weeklyQuestSlots) {
        this.weeklyQuestSlots = weeklyQuestSlots;
    }

    public BossBarExpIndicatorSetting getBossBarExpIndicatorSetting() {
        return bossBarExpIndicatorSetting == null ? BossBarExpIndicatorSetting.DEFAULT : bossBarExpIndicatorSetting;
    }

    public void setBossBarExpIndicatorSetting(BossBarExpIndicatorSetting bossBarExpIndicatorSetting) {
        this.bossBarExpIndicatorSetting = bossBarExpIndicatorSetting;
    }
}
