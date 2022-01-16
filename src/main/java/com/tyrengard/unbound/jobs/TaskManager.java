package com.tyrengard.unbound.jobs;

import com.tyrengard.aureycore.foundation.AManager;
import com.tyrengard.aureycore.foundation.Configured;
import com.tyrengard.aureycore.foundation.common.utils.InventoryUtils;
import com.tyrengard.unbound.jobs.actions.Action;
import com.tyrengard.unbound.jobs.actions.BlockBased;
import com.tyrengard.unbound.jobs.actions.EntityBased;
import com.tyrengard.unbound.jobs.actions.ItemStackBased;
import com.tyrengard.unbound.jobs.events.PlayerPerformJobQuestTaskEvent;
import com.tyrengard.unbound.jobs.events.PlayerPerformJobTaskEvent;
import com.tyrengard.unbound.jobs.events.PlayerPerformActionEvent;
import com.tyrengard.unbound.jobs.exceptions.UnboundJobsException;
import com.tyrengard.unbound.jobs.quests.internal.JobQuestInstance;
import com.tyrengard.unbound.jobs.tasks.*;
import com.tyrengard.unbound.jobs.workers.Worker;
import com.tyrengard.unbound.jobs.workers.WorkerManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class TaskManager extends AManager<UnboundJobs> implements Listener, Configured {
    private static NamespacedKey UJ_PAID_KEY;
    private final HashMap<String, Action> actions;

    // region Config values
    private List<String> worldsDisabled;
    // endregion

    private static TaskManager instance;
    TaskManager(UnboundJobs plugin) {
        super(plugin);
        instance = this;

        UJ_PAID_KEY = new NamespacedKey(plugin, "paid");
        actions = new HashMap<>();
    }

    // region Manager overrides
    @Override
    protected void startup() {
        for (Action action : Action.Default.values()) {
            try {
                registerAction(action);
            } catch (UnboundJobsException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void cleanup() {

    }
    // endregion
    // region Configured overrides
    @Override
    public void loadSettingsFromConfig(FileConfiguration config) throws InvalidConfigurationException {
        ConfigurationSection generalSection = config.getConfigurationSection("general");
        if (generalSection == null)
            throw new InvalidConfigurationException("Plugin config has missing section: " + "general");

        // region Worlds allowed
        if (!generalSection.contains("worlds-disabled"))
            throw new InvalidConfigurationException("Plugin config has missing section: " + "general.worlds-disabled");
        worldsDisabled = generalSection.getStringList("worlds-disabled");
        // endregion
    }
    // endregion

    public static void registerAction(Action action) throws UnboundJobsException {
        String actionId = action.getId();
        if (instance.actions.containsKey(actionId))
            throw new UnboundJobsException("The action id \"" + actionId + "\" already exists.");
        else
            instance.actions.put(actionId, action);
    }

    public static Action getAction(String actionId) {
        return instance.actions.get(actionId);
    }

    // region Event listeners
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onTaskPerform(PlayerPerformActionEvent e) {
        if (worldsDisabled.contains(e.getWorld().getName()))
        logDebug("TaskManager.onTaskPerform: Handling PlayerPerformActionEvent");
        Player p = e.getPlayer();

        Worker worker = WorkerManager.obtainWorker(p.getUniqueId());

        List<Job> jobs = worker.getJobs();
        List<JobTask> jobTasks = jobs.stream()
                .flatMap(job -> job.getJobTasks().stream())
                .filter(jobTask -> jobTask.getAction() == e.getAction())
                .collect(Collectors.toList());
        List<JobQuestTask> jobQuestTasks = new ArrayList<>();
        for (Job job : jobs) {
            jobQuestTasks.addAll(worker.getJobQuestData(job).getAllInstances().stream()
                    .filter(Objects::nonNull)
                    .filter(JobQuestInstance::isActive)
                    .map(JobQuestInstance::getQuestId)
                    .map(job::getJobQuest)
                    .filter(Objects::nonNull)
                    .flatMap(jobQuest -> jobQuest.getTasks().stream())
                    .collect(Collectors.toList()));
        }
        Block block = e.getBlock();
        ItemStack itemStack = e.getItemStack();
        Entity entity = e.getEntity();

        JobTask performedJobTask = null;
        List<JobQuestTask> performedJobQuestTasks = null;
        if (block != null) {
            performedJobTask = jobTasks.stream().filter(t -> t instanceof BlockBased bb && bb.acceptsBlock(block)).findFirst().orElse(null);
            performedJobQuestTasks = jobQuestTasks.stream().filter(t -> t instanceof BlockBased bb && bb.acceptsBlock(block)).collect(Collectors.toList());
        } else if (itemStack != null) {
            performedJobTask = jobTasks.stream().filter(t -> t instanceof ItemStackBased isb && isb.acceptsItemStack(itemStack)).findFirst().orElse(null);
            performedJobQuestTasks = jobQuestTasks.stream().filter(t -> t instanceof ItemStackBased isb && isb.acceptsItemStack(itemStack)).collect(Collectors.toList());
        } else if (entity != null) {
            performedJobTask = jobTasks.stream().filter(t -> t instanceof EntityBased eb && eb.acceptsEntity(entity)).findFirst().orElse(null);
            performedJobQuestTasks = jobQuestTasks.stream().filter(t -> t instanceof EntityBased eb && eb.acceptsEntity(entity)).collect(Collectors.toList());
        }

        if (performedJobTask != null) {
            logDebug("TaskManager.onTaskPerform: Found a performed job task");
            JobData jobData = worker.getJobData(performedJobTask.getSource());
            if (jobData != null)
                Bukkit.getPluginManager().callEvent(new PlayerPerformJobTaskEvent(p, jobData.level(), performedJobTask));
        }
        if (performedJobQuestTasks != null) {
            logDebug("TaskManager.onTaskPerform: Found a performed job quest task");
            for (JobQuestTask t : performedJobQuestTasks)
                Bukkit.getPluginManager().callEvent(new PlayerPerformJobQuestTaskEvent(p, t));
        }
    }
    //endregion
}
