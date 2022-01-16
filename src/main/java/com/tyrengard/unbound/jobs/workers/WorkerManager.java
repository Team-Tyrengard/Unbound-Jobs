package com.tyrengard.unbound.jobs.workers;

import com.tyrengard.aureycore.foundation.ADataManager;
import com.tyrengard.aureycore.foundation.Configured;
import com.tyrengard.aureycore.foundation.common.events.PlayerStartBrewEvent;
import com.tyrengard.aureycore.foundation.common.random.RandomSelector;
import com.tyrengard.aureycore.foundation.common.struct.UUIDDataType;
import com.tyrengard.aureycore.foundation.common.utils.BossBarUtils;
import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.JobData;
import com.tyrengard.unbound.jobs.JobManager;
import com.tyrengard.unbound.jobs.UnboundJobs;
import com.tyrengard.unbound.jobs.events.PlayerLevelUpJobEvent;
import com.tyrengard.unbound.jobs.quests.JobQuest;
import com.tyrengard.unbound.jobs.quests.internal.JobQuestData;
import com.tyrengard.unbound.jobs.quests.JobQuestType;
import com.tyrengard.unbound.jobs.workers.enums.BossBarExpIndicatorSetting;
import com.tyrengard.unbound.jobs.workers.enums.ProfileVisibility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.TileState;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class WorkerManager extends ADataManager<UnboundJobs, Worker, UUID> implements Listener, Configured {
    private static NamespacedKey UJ_ACTIVE_WORKER_KEY;

    private final Hashtable<UUID, Worker> workers = new Hashtable<>();

    //region Config values
    private int maxJobsPerPlayer;
    private DayOfWeek weeklyQuestRollDay;
    private ProfileVisibility defaultProfileVisibility;
    //endregion

    private static WorkerManager instance;
    public WorkerManager(UnboundJobs plugin) {
        super(plugin, Worker.class);
        instance = this;

        UJ_ACTIVE_WORKER_KEY = new NamespacedKey(plugin, "active-worker");
    }

    // region Manager overrides
    @Override
    protected void startup() {

    }

    @Override
    protected void cleanup() {
        logDebug("Saving " + workers.size() + " workers...");
        for (Worker w : workers.values())
            saveObject(w);
    }
    // endregion
    // region Configured overrides
    @Override
    public void loadSettingsFromConfig(FileConfiguration config) throws InvalidConfigurationException {
        ConfigurationSection workersSection = config.getConfigurationSection("workers");
        if (workersSection == null)
            throw new InvalidConfigurationException("Plugin config has missing section: " + "workers");

        // region Jobs limit per player
        if (!workersSection.contains("job-limit-per-player"))
            throw new InvalidConfigurationException("Plugin config is missing section: " + "workers.job-limit-per-player");
        maxJobsPerPlayer = workersSection.getInt("job-limit-per-player");
        if (maxJobsPerPlayer == 0)
            throw new InvalidConfigurationException("Plugin config has invalid section: " + "workers.job-limit-per-player");
        // endregion
        // region Weekly job quest reroll day
        String weeklyQuestRollDayString = workersSection.getString("weekly-quest-reroll-day");
        if (weeklyQuestRollDayString == null)
            throw new InvalidConfigurationException("Plugin config has missing property: " + "workers.weekly-quest-reroll-day");
        try {
            weeklyQuestRollDay = DayOfWeek.valueOf(weeklyQuestRollDayString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Plugin config has invalid property: " + "workers.weekly-quest-reroll-day");
        }
        // endregion
        // region Default profile visibility
        String defaultProfileVisibilityString = workersSection.getString("default-profile-visiblity", "private");
        try {
            defaultProfileVisibility = ProfileVisibility.valueOf(defaultProfileVisibilityString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Plugin config has invalid section: " + "workers.default-profile-visiblity");
        }
        // endregion
    }
    // endregion
    // region Static methods
    public static @NotNull Worker obtainWorker(UUID id) {
        return Objects.requireNonNullElseGet(instance.workers.computeIfAbsent(id, instance::getObject),
                () -> createNewWorker(id));
    }

    public static Worker createNewWorker(UUID id) {
        Worker worker = new Worker(id, instance.defaultProfileVisibility);
        instance.workers.put(id, worker);
        instance.saveObject(worker);
        return worker;
    }

    public static void addJobToPlayer(Player p, Job j) {
        obtainWorker(p.getUniqueId()).setJobData(j, new JobData((short) 1, 0));
        p.sendMessage("Joined job " + j.getName() + ".");
    }

    public static int getMaxJobsPerPlayer() {
        return instance.maxJobsPerPlayer;
    }

    public static void rollDailyQuest(@NotNull Worker worker, @NotNull Job job, int slot) {
        if (job.hasJobQuests(JobQuestType.DAILY)) {
            worker.setDailyJobQuest(job, slot, null);

            List<String> assignedJobQuestIds = worker.getJobQuestData(job).getQuestIds(JobQuestType.DAILY);
            List<JobQuest> jobQuestPool = job.getJobQuests(JobQuestType.DAILY).stream()
                    .filter(jq -> !assignedJobQuestIds.contains(jq.getId()))
                    .collect(Collectors.toList());
            if (jobQuestPool.isEmpty())
                return;

            JobQuest jobQuest = rollJobQuest(jobQuestPool);
            worker.setDailyJobQuest(job, slot, jobQuest);
        }
    }

    public static void rollWeeklyQuest(@NotNull Worker worker, @NotNull Job job, int slot) {
        if (job.hasJobQuests(JobQuestType.WEEKLY)) {
            worker.setWeeklyJobQuest(job, slot, null);

            List<String> assignedJobQuestIds = worker.getJobQuestData(job).getQuestIds(JobQuestType.WEEKLY);
            List<JobQuest> jobQuestPool = job.getJobQuests(JobQuestType.WEEKLY).stream()
                    .filter(jq -> !assignedJobQuestIds.contains(jq.getId()))
                    .collect(Collectors.toList());
            if (jobQuestPool.isEmpty())
                return;

            JobQuest jobQuest = rollJobQuest(jobQuestPool);
            worker.setWeeklyJobQuest(job, slot, jobQuest);
        }
    }

    public static void refreshQuests(@NotNull Worker worker, @NotNull JobQuestType jobQuestType) {
        boolean daily = jobQuestType == JobQuestType.DAILY;

        for (Job job : worker.getJobs()) {
            JobQuestData jobQuestData = worker.getJobQuestData(job);
            int slots = daily ? jobQuestData.getDailyQuestSlots() : jobQuestData.getWeeklyQuestSlots();
            for (int slot = 0; slot < slots; slot++)
                if (daily)
                    rollDailyQuest(worker, job, slot);
                else
                    rollWeeklyQuest(worker, job, slot);
        }
        worker.setLastQuestRefreshDate(LocalDate.now());
    }

    public static void giveJobExperience(@NotNull Player player, @NotNull Job job, int exp) {
        Worker worker = obtainWorker(player.getUniqueId());
        JobData jobData = worker.getJobData(job);
        if (jobData == null)
            return;

        short currentLevel = jobData.level(), nextLevel = (short) (currentLevel + 1), maxLevel = JobManager.getMaxLevel();
        if (nextLevel <= maxLevel) {
            int expToLevelUp = JobManager.getExperienceForNextLevel(currentLevel),
                    currentExp = jobData.exp(), newExp = currentExp + exp;

            boolean shouldPresent = false;

            if (expToLevelUp <= newExp) { // level up
                PlayerLevelUpJobEvent playerLevelUpJobEvent = new PlayerLevelUpJobEvent(player, job, nextLevel);
                Bukkit.getPluginManager().callEvent(playerLevelUpJobEvent);
                if (!playerLevelUpJobEvent.isCancelled()) {
                    if (nextLevel == maxLevel) newExp = 0;
                    else newExp -= expToLevelUp;
                    JobData newJobData = new JobData(nextLevel, newExp);
                    worker.setJobData(job, newJobData);

                    shouldPresent = worker.getBossBarExpIndicatorSetting() != BossBarExpIndicatorSetting.HIDDEN;
                }
            } else {
                worker.setJobData(job, new JobData(currentLevel, newExp));

                double progress = (double) newExp / expToLevelUp;
                shouldPresent = switch (worker.getBossBarExpIndicatorSetting()) {
                    case HIDDEN -> false;
                    case DEFAULT -> true;
                    case LEVELING -> progress >= 0.8;
                };
            }

            if (shouldPresent)
                presentExpBar(worker, job);
        }
    }

    public static @Nullable Worker getActiveWorker(@NotNull PersistentDataHolder pdh) {
        UUID id = pdh.getPersistentDataContainer().get(UJ_ACTIVE_WORKER_KEY, UUIDDataType.instance);
        return id == null ? null : WorkerManager.obtainWorker(id);
    }

    public static void setActiveWorker(@NotNull Block block, @NotNull Worker worker) {
        if (block.getState() instanceof TileState ts) {
            ts.getPersistentDataContainer().set(UJ_ACTIVE_WORKER_KEY, UUIDDataType.instance, worker.getId());
            ts.update();
            instance.logDebug("WorkerManager.setActiveWorker: Set active worker for " + block.getType() + " at " +
                    block.getLocation() + " to " + worker.getId());
        } else {
            instance.logDebug("WorkerManager.setActiveWorker: Block at " + block.getLocation() + " has no PersistentDataContainer.");
        }
    }

    public static void removeActiveWorker(@NotNull Block block) {
        if (block.getState() instanceof TileState ts) {
            ts.getPersistentDataContainer().remove(UJ_ACTIVE_WORKER_KEY);
            ts.update();
            instance.logDebug("WorkerManager.removeActiveWorker: Removed active worker for " + ts.getBlock().getType());
        } else {
            instance.logDebug("WorkerManager.removeActiveWorker: Block at " + block.getLocation() + " has no PersistentDataContainer.");
        }
    }
    // endregion
    // region Event listeners
    @EventHandler(ignoreCancelled = true)
    private void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Worker worker = obtainWorker(p.getUniqueId());

        if (worker.getJobs().size() == 0)
            return;

        // region Refresh daily and weekly quests
        LocalDate today = LocalDate.now(), lastQuestRefreshDate = worker.getLastQuestRefreshDate();
        boolean weeklyQuestsShouldReroll = today.getDayOfWeek() == weeklyQuestRollDay;
        if (lastQuestRefreshDate == null || today.isAfter(lastQuestRefreshDate)) {
            logDebug("Rerolling quests for " + p.getName() + "...");
            refreshQuests(worker, JobQuestType.DAILY);
            if (weeklyQuestsShouldReroll)
                refreshQuests(worker, JobQuestType.WEEKLY);
            p.sendMessage(ChatColor.YELLOW + "You have new job quests! Check them in your job profile.");
        }
        // endregion
    }

    // Block ownership check for opening inventories
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void onInventoryOpen(InventoryOpenEvent e) {
        HumanEntity p = e.getPlayer();
        Inventory inv = e.getInventory();

        if (inv.getHolder() instanceof PersistentDataHolder pdh) {
            Worker activeWorker = WorkerManager.getActiveWorker(pdh);
            if (activeWorker != null && !activeWorker.getId().equals(p.getUniqueId())) {
                p.sendMessage(ChatColor.RED + "Someone else is using that.");
                e.setCancelled(true);
            }
        }
    }

//    // Block ownership establishment for furnaces and brewing stands
//    @EventHandler(priority = EventPriority.HIGHEST)
//    private void onInventoryClose(InventoryCloseEvent e) {
//        Worker worker = obtainWorker(e.getPlayer().getUniqueId());
//        Inventory inv = e.getInventory();
//
//        // TODO: make sure that other people can't "steal" ownership by closing inventory first before owner
//        if (inv instanceof BrewerInventory brewerInventory) {
//            BrewingStand bs = brewerInventory.getHolder();
//            if (bs == null) // floating inv
//                return;
//
//            if (WorkerManager.getActiveWorker(bs) != null) // already owned
//                return;
//
//            if (bs.getBrewingTime() > 0) // unowned, started by closer
//                WorkerManager.setActiveWorker(bs, worker);
//        }
//    }

    // Block ownership establishment for brewing stands
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerStartBrew(PlayerStartBrewEvent e) {
        Worker worker = obtainWorker(e.getPlayer().getUniqueId());
        BrewingStand bs = e.getBrewingStand();
        if (bs == null) // floating inv
            return;

        if (WorkerManager.getActiveWorker(bs) != null) // already owned
            return;

        WorkerManager.setActiveWorker(e.getBrewingStandBlock(), worker);
    }

    private static void presentExpBar(@NotNull Worker w, @NotNull Job j) {
        JobData jobData = w.getJobData(j);
        if (jobData == null)
            return;

        int expForNextLevel = JobManager.getExperienceForNextLevel(jobData.level());

        BossBarUtils.addBossBarForPlayer(UnboundJobs.getInstance(), "exp-bar",
                j.getName() + " Level " + jobData.level() + ", " + jobData.exp() + " / " + expForNextLevel,
                BarColor.GREEN,
                (double) jobData.exp() / (double) expForNextLevel,
                Bukkit.getPlayer(w.getId()),
                20 * 5);
    }

    private static JobQuest rollJobQuest(List<JobQuest> jobQuestPool) {
        return RandomSelector.weighted(jobQuestPool, JobQuest::getWeight).next();
    }
}
