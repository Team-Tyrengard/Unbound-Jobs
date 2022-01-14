package com.tyrengard.unbound.jobs;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.StaticVariableSet;
import com.tyrengard.aureycore.foundation.AManager;
import com.tyrengard.aureycore.foundation.Configured;
import com.tyrengard.aureycore.foundation.common.utils.StringUtils;
import com.tyrengard.unbound.jobs.enums.PaymentPeriod;
import com.tyrengard.unbound.jobs.events.JobQuestTaskPerformEvent;
import com.tyrengard.unbound.jobs.events.JobTaskPerformEvent;
import com.tyrengard.unbound.jobs.exceptions.UnboundJobsException;
import com.tyrengard.unbound.jobs.quests.JobQuestReward;
import com.tyrengard.unbound.jobs.quests.JobQuestRewardType;
import com.tyrengard.unbound.jobs.quests.internal.JobQuest;
import com.tyrengard.unbound.jobs.quests.internal.JobQuestData;
import com.tyrengard.unbound.jobs.quests.internal.JobQuestInstance;
import com.tyrengard.unbound.jobs.quests.internal.JobQuestType;
import com.tyrengard.unbound.jobs.tasks.JobQuestTask;
import com.tyrengard.unbound.jobs.tasks.JobTask;
import com.tyrengard.unbound.jobs.workers.Worker;
import com.tyrengard.unbound.jobs.workers.WorkerManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public final class JobManager extends AManager<UnboundJobs> implements Listener, Configured {
    private final TreeMap<String, Job> jobs = new TreeMap<>();
    private final HashMap<String, JobQuestRewardType> jobQuestRewardTypes;
    private final File pluginDataFolder;
    private Economy economy;

    // region Config values
    private short maxLevel;
    private List<String> disabledJobIds;
    private int maxJobsPerPlayer;
    private boolean experienceBoostsEnabled;
    private int[] experienceTable;
    private boolean incomeBoostsEnabled;
    private PaymentPeriod paymentPeriod;
    private String incomeFormula;
    // endregion

    private static JobManager instance;
    public JobManager(UnboundJobs plugin) {
        super(plugin);
        instance = this;

        pluginDataFolder = plugin.getDataFolder();
        jobQuestRewardTypes = new HashMap<>();
    }

    public static int getExperienceForNextLevel(int currentLevel) {
        return instance.experienceTable[currentLevel];
    }

    // region Manager overrides
    @Override
    protected void startup() {
        // region Setup economy
        RegisteredServiceProvider<Economy> ecoRSP = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (ecoRSP == null) {
            logWarning("No economy plugin detected!");
            economy = null;
        } else {
            economy = ecoRSP.getProvider();
        }
        // endregion
        // region Add default job quests
        for (JobQuestRewardType type : JobQuestRewardType.Default.values()) {
            try {
                registerJobQuestReward(type);
            } catch (UnboundJobsException e) {
                e.printStackTrace();
            }
        }
        // endregion
        loadJobConfigFiles();
    }

    @Override
    protected void cleanup() {

    }
    // endregion
    // region Configured overrides
    @Override
    public void loadSettingsFromConfig(FileConfiguration config) throws InvalidConfigurationException {
        ConfigurationSection jobsSection = config.getConfigurationSection("jobs");
        if (jobsSection == null)
            throw new InvalidConfigurationException("Plugin config is missing section: " + "jobs");

        // region Disabled jobs
        disabledJobIds = jobsSection.getStringList("disabled-jobs");
        // endregion
        // region Max level
        if (!jobsSection.contains("max-level"))
            throw new InvalidConfigurationException("Plugin config is missing section: " + "jobs.max-level");

        int tempMaxLevel = jobsSection.getInt("max-level");
        if (tempMaxLevel > Short.MAX_VALUE)
            throw new InvalidConfigurationException("Highest max job level supported is " + Short.MAX_VALUE);
        maxLevel = (short) tempMaxLevel;
        // endregion
        // region Jobs limit per player
        if (!jobsSection.contains("limit-per-player"))
            throw new InvalidConfigurationException("Plugin config is missing section: " + "limit-per-player");
        maxJobsPerPlayer = jobsSection.getInt("limit-per-player");
        // endregion
        // region Experience
        DoubleEvaluator eval = new DoubleEvaluator();
        ConfigurationSection experienceSection = jobsSection.getConfigurationSection("experience");
        if (experienceSection == null)
            throw new InvalidConfigurationException("Plugin config is missing section: " + "jobs.experience");
        // region Boosts
        if (!experienceSection.isBoolean("boosts-enabled"))
            throw new InvalidConfigurationException("Plugin config has invalid section: " + "jobs.experience.boosts");
        experienceBoostsEnabled = experienceSection.getBoolean("boosts-enabled");
        // endregion
        // region Formula
        String experienceFormula = experienceSection.getString("formula");
        if (experienceFormula == null)
            throw new InvalidConfigurationException("Plugin config is missing section: " + "jobs.experience.formula");
        experienceTable = new int[maxLevel];
        for (int level = 1; level <= maxLevel; level++) {
            StaticVariableSet<Double> variableSet = new StaticVariableSet<>();
            variableSet.set("t", (double) level);
            experienceTable[level - 1] = eval.evaluate(experienceFormula, variableSet).intValue();
        }
        // endregion
        // endregion
        // region Income
        ConfigurationSection incomeSection = jobsSection.getConfigurationSection("income");
        if (incomeSection == null)
            throw new InvalidConfigurationException("Plugin config has invalid section: " + "jobs.income");
        // region Boosts
        if (!incomeSection.isBoolean("boosts-enabled"))
            throw new InvalidConfigurationException("Plugin config has invalid section: " + "jobs.income.boosts");
        incomeBoostsEnabled = incomeSection.getBoolean("boosts-enabled");
        // endregion
        // region Payment period
        if (!incomeSection.isString("payment-period"))
            throw new InvalidConfigurationException("Plugin config has invalid property: " + "jobs.income.payment-period");
        try {
            paymentPeriod = PaymentPeriod.valueOf(StringUtils.toKeyCase(incomeSection.getString("payment-period")));
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Plugin config has invalid property: " + "jobs.income.payment-period");
        }
        // endregion
        // region Formula
        if (!incomeSection.isString("formula"))
            throw new InvalidConfigurationException("Plugin config has invalid property: " + "jobs.income.formula");
        incomeFormula = incomeSection.getString("formula");
        // endregion
        // endregion
    }
    // endregion

    public static void loadJobConfigFiles() {
        if (!instance.pluginDataFolder.exists())
            instance.logError("Plugin data folder not found! No jobs will be added.");

        try {
            Files.walk(instance.pluginDataFolder.toPath()).filter(Files::isRegularFile)
                    .filter(path -> !path.endsWith("config.yml") && path.toString().endsWith(".yml"))
                    .forEach(path -> {
                        try {
                            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(path.toFile());
                            instance.logDebug("Parsing job from " + path.getFileName().toString());
                            Job job = new Job(yamlConfiguration);
                            instance.jobs.put(job.getId(), job);
                            instance.logDebug("Loaded config for job \"" + job.getName() + "\": " +
                                    job.getJobTasks().size() + " tasks, " +
                                    job.getJobQuests(JobQuestType.DAILY).size() + " daily quests," +
                                    job.getJobQuests(JobQuestType.WEEKLY).size() + " weekly quests");
                        } catch (Exception e) {
                            instance.logError("Unable to add job from file " + path.getFileName().toString() +
                                    " due to the following error:");
                            instance.logError(e.getMessage());
                        }
                    });
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static Collection<Job> getJobs() { return instance.jobs.values(); }

    public static Job getJob(String jobId) {
        return instance.jobs.get(jobId);
    }

    public static short getMaxLevel() {
        return instance.maxLevel;
    }

    public static int getMaxJobsPerPlayer() {
        return instance.maxJobsPerPlayer;
    }

    public static void registerJobQuestReward(@NotNull JobQuestRewardType jobQuestRewardType) throws UnboundJobsException {
        String jobQuestRewardId = jobQuestRewardType.getId();
        if (instance.jobQuestRewardTypes.containsKey(jobQuestRewardId))
            throw new UnboundJobsException("The job quest reward id \"" + jobQuestRewardId + "\" already exists.");
        else
            instance.jobQuestRewardTypes.put(jobQuestRewardId, jobQuestRewardType);
    }

    public static @Nullable JobQuestRewardType getJobQuestRewardType(String id) {
        return instance.jobQuestRewardTypes.get(id);
    }

    // region Event handlers
    @EventHandler
    private void onJobTaskPerform(JobTaskPerformEvent e) {
        Worker w = e.getWorker();
        JobTask t = e.getTask();
        Job j = t.getSource();

        // TODO: calculate exp properly (i.e. with bonuses and multipliers)
        WorkerManager.giveJobExperience(w, j, t.getBaseExp());

        JobData jobData = w.getJobData(j);
        if (jobData != null)
            payWorker(w, jobData.level(), t.getBasePay());
    }

    @EventHandler
    private void onJobQuestTaskPerform(JobQuestTaskPerformEvent e) throws UnboundJobsException {
        Worker w = e.getWorker();
        JobQuestTask t = e.getTask();
        JobQuest jq = t.getSource();
        Job j = jq.getJob();

        JobQuestData jobQuestData = w.getJobQuestData(j);
        JobQuestInstance jobQuestInstance = jobQuestData.getInstance(jq);
        if (jobQuestInstance == null)
            throw new UnboundJobsException("Invalid job quest data for player " + w.getPlayerName());

        if (!jobQuestInstance.isActive())
            return;

        int taskId = t.getId();
        int newProgress = jobQuestInstance.getProgress(taskId) + 1, progressRequired = t.getProgressRequired();
        if (newProgress <= progressRequired) {
            jobQuestInstance.setProgress(taskId, newProgress);
            if (newProgress == progressRequired) {
                Player p = Objects.requireNonNull(Bukkit.getPlayer(w.getId()));
                for (JobQuestReward reward : jq.getRewards())
                    reward.awardToPlayer(p);
                jobQuestInstance.setActive(false);
                p.sendMessage("Quest " + ChatColor.YELLOW + jq.getTitle() +
                        ChatColor.WHITE + " completed! Rewards have been awarded.");
            }
        }
    }
    // endregion

    private void payWorker(Worker worker, short jobLevel, double basePay) {
        if (economy == null)
            return;

        DoubleEvaluator eval = new DoubleEvaluator();
        StaticVariableSet<Double> variables = new StaticVariableSet<>();
        variables.set("p", basePay);
        variables.set("j", (double) jobLevel);
        variables.set("M", (double) maxLevel);

        // TODO: calculate pay properly (i.e. with bonuses and multipliers)
        double totalPay = eval.evaluate(incomeFormula, variables);
        economy.depositPlayer(Bukkit.getOfflinePlayer(worker.getId()), totalPay);
    }
}
