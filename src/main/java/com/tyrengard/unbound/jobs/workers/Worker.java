package com.tyrengard.unbound.jobs.workers;

import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.JobData;
import com.tyrengard.unbound.jobs.JobManager;
import com.tyrengard.unbound.jobs.quests.JobQuest;
import com.tyrengard.unbound.jobs.quests.internal.JobQuestData;
import com.tyrengard.unbound.jobs.workers.enums.BossBarExpIndicatorSetting;
import com.tyrengard.unbound.jobs.workers.enums.ProfileVisibility;
import dev.morphia.annotations.AlsoLoad;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private LocalDate lastQuestRefreshDate;

    // region Settings
    private ProfileVisibility profileVisibility;
    private BossBarExpIndicatorSetting bossBarExpIndicatorSetting;
    // endregion

    public Worker(UUID id, ProfileVisibility profileVisibility) {
        this(id, new HashMap<>(), new HashMap<>(), profileVisibility, BossBarExpIndicatorSetting.DEFAULT);
    }

    public Worker(UUID id, HashMap<String, JobData> jobData,
                  HashMap<String, JobQuestData> jobQuestData,
                  ProfileVisibility profileVisibility,
                  BossBarExpIndicatorSetting bossBarExpIndicatorSetting) {
        this.id = id;
        this.jobData = jobData;
        this.jobQuestData = jobQuestData;
        this.profileVisibility = profileVisibility;
        this.bossBarExpIndicatorSetting = bossBarExpIndicatorSetting;
    }

    public UUID getId() {
        return id;
    }

    public String getPlayerName() { return Bukkit.getOfflinePlayer(id).getName(); }

    public @NotNull List<Job> getJobs() {
        return getJobDataMap().keySet().stream().map(JobManager::getJob).filter(Objects::nonNull).toList();
    }

    public @Nullable JobData getJobData(@NotNull Job j) {
        return getJobDataMap().get(j.getId());
    }

    public void setJobData(@NotNull Job j, @NotNull JobData jd) {
        getJobDataMap().put(j.getId(), jd);
    }

    public boolean hasJob(@NotNull Job j) {
        return getJobDataMap().containsKey(j.getId());
    }

    public void removeJob(@NotNull Job j) {
        getJobDataMap().remove(j.getId());
    }

    public @NotNull JobQuestData getJobQuestData(Job j) {
        return getJobQuestData().computeIfAbsent(j.getId(), jobId -> new JobQuestData());
    }

    public void setDailyJobQuest(@NotNull Job job, int slot, @Nullable JobQuest jobQuest) {
        String jobId = job.getId();
        JobQuestData jobQuestData = getJobQuestData().get(jobId);
        if (jobQuestData != null)
            jobQuestData.setDailyQuest(jobQuest, slot);
    }

    public void setWeeklyJobQuest(@NotNull Job job, int slot, @Nullable JobQuest jobQuest) {
        String jobId = job.getId();
        JobQuestData jobQuestData = getJobQuestData().get(jobId);
        if (jobQuestData != null)
            jobQuestData.setWeeklyQuest(jobQuest, slot);
    }

    public @Nullable LocalDate getLastQuestRefreshDate() {
        return lastQuestRefreshDate;
    }

    public void setLastQuestRefreshDate(LocalDate lastQuestRefreshDate) {
        this.lastQuestRefreshDate = lastQuestRefreshDate;
    }

    public @NotNull BossBarExpIndicatorSetting getBossBarExpIndicatorSetting() {
        return bossBarExpIndicatorSetting == null ? BossBarExpIndicatorSetting.DEFAULT : bossBarExpIndicatorSetting;
    }

    public void setBossBarExpIndicatorSetting(BossBarExpIndicatorSetting bossBarExpIndicatorSetting) {
        this.bossBarExpIndicatorSetting = bossBarExpIndicatorSetting;
    }

    public ProfileVisibility getProfileVisibility() {
        return profileVisibility;
    }

    public void setProfileVisibility(ProfileVisibility profileVisibility) {
        this.profileVisibility = profileVisibility;
    }

    private @NotNull HashMap<String, JobData> getJobDataMap() {
        if (jobData == null)
            jobData = new HashMap<>();
        return jobData;
    }

    private @NotNull HashMap<String, JobQuestData> getJobQuestData() {
        if (jobQuestData == null)
            jobQuestData = new HashMap<>();
        return jobQuestData;
    }
}
