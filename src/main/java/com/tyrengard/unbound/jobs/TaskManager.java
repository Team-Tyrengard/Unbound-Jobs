package com.tyrengard.unbound.jobs;

import com.tyrengard.aureycore.foundation.AManager;
import com.tyrengard.aureycore.foundation.Configured;
import com.tyrengard.aureycore.foundation.common.utils.InventoryUtils;
import com.tyrengard.unbound.jobs.actions.Action;
import com.tyrengard.unbound.jobs.events.JobQuestTaskPerformEvent;
import com.tyrengard.unbound.jobs.events.JobTaskPerformEvent;
import com.tyrengard.unbound.jobs.events.TaskPerformEvent;
import com.tyrengard.unbound.jobs.exceptions.UnboundJobsException;
import com.tyrengard.unbound.jobs.quests.internal.JobQuestInstance;
import com.tyrengard.unbound.jobs.tasks.*;
import com.tyrengard.unbound.jobs.workers.Worker;
import com.tyrengard.unbound.jobs.workers.WorkerManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
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
    private List<String> worldsAllowed;
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
        if (!generalSection.contains("worlds-allowed"))
            throw new InvalidConfigurationException("Plugin config has missing section: " + "general.worlds-allowed");
        worldsAllowed = generalSection.getStringList("worlds-allowed");
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

    //region Event listeners
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();

        if (!worldsAllowed.contains(p.getWorld().getName()))
            return;

        switch (b.getType()) {
            case WHEAT, NETHER_WART, CARROTS, POTATOES, BEETROOTS, SUGAR_CANE -> {
                if (b.getBlockData() instanceof Ageable a && a.getAge() == a.getMaximumAge()) {
                    Bukkit.getPluginManager().callEvent(new TaskPerformEvent(p, Action.Default.HARVEST_BLOCK, e.getBlock()));
                }
            }
            default -> Bukkit.getPluginManager().callEvent(new TaskPerformEvent(p, Action.Default.BREAK_BLOCK, e.getBlock()));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onEntityBreed(EntityBreedEvent e) {
        if (e.getBreeder() instanceof Player p) {
            if (!worldsAllowed.contains(p.getWorld().getName()))
                return;
            Bukkit.getPluginManager().callEvent(new TaskPerformEvent(p, Action.Default.BREED_ANIMAL, e.getEntity()));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onBrew(BrewEvent e) {
        BrewerInventory bi = e.getContents();
        BrewingStand bs = bi.getHolder();
        if (bs == null) return;

        if (!worldsAllowed.contains(bs.getBlock().getWorld().getName()))
            return;

        Worker w = WorkerManager.getActiveWorker(bs);
        if (w == null) return;

        for (ItemStack i : InventoryUtils.getProductsOfBrewerInventory(bi)) {
            if (i != null && i.getItemMeta() instanceof PotionMeta potionMeta && (potionMeta).getPersistentDataContainer()
                    .get(UJ_PAID_KEY, PersistentDataType.BYTE) == null) {
                Bukkit.getPluginManager().callEvent(new TaskPerformEvent(Bukkit.getPlayer(w.getId()), Action.Default.BREW_POTION, i));
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onPlayerFish(PlayerFishEvent e) {
        if (!worldsAllowed.contains(e.getPlayer().getWorld().getName()))
            return;

        if (e.getCaught() instanceof Item caughtItem) {
            Bukkit.getPluginManager().callEvent(new TaskPerformEvent(e.getPlayer(), Action.Default.CATCH_FISH, caughtItem));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onCraftItem(CraftItemEvent e) {
        if (!worldsAllowed.contains(e.getWhoClicked().getWorld().getName()))
            return;

        Bukkit.getPluginManager().callEvent(new TaskPerformEvent((Player) e.getWhoClicked(), Action.Default.CRAFT_ITEM, e.getRecipe().getResult()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onPlayerHarvestBlock(PlayerHarvestBlockEvent e) {
        if (!worldsAllowed.contains(e.getPlayer().getWorld().getName()))
            return;

        if (e.getHarvestedBlock().getBlockData() instanceof Ageable a && a.getAge() == a.getMaximumAge())
            Bukkit.getPluginManager().callEvent(new TaskPerformEvent(e.getPlayer(), Action.Default.HARVEST_BLOCK, e.getHarvestedBlock()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onEntityDeath(EntityDeathEvent e) {
        Player p = e.getEntity().getKiller();
        if (p == null)
            return;
        if (!worldsAllowed.contains(p.getWorld().getName()))
            return;

        Bukkit.getPluginManager().callEvent(new TaskPerformEvent(p, Action.Default.KILL_MOB, e.getEntity()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onBlockPlace(BlockPlaceEvent e) {
        if (!worldsAllowed.contains(e.getPlayer().getWorld().getName()))
            return;
        Bukkit.getPluginManager().callEvent(new TaskPerformEvent(e.getPlayer(), Action.Default.PLACE_BLOCK, e.getBlock()));
    }

//    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
//    private void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
//        Player p = e.getPlayer();
//        ItemStack usedItem = p.;
//        if (usedItem == null) return;
//        switch (usedItem.getType()) {
//            case BOWL, BUCKET, SHEARS: break;
//            default: return;
//        }
//
//        Worker w = WorkerManager.getWorker(e.getPlayer().getUniqueId());
//        for (GatherFromAnimalTask t : w.getTasks(GatherFromAnimalTask.class)) {
//            if (t.getItemMaterial() == e.getItemStack().getType()) {
//                Bukkit.getPluginManager().callEvent(new TaskPerformEvent(w, t.getSource(), t));
//                break;
//            }
//        }
//    }

    @EventHandler
    private void onTaskPerform(TaskPerformEvent e) {
        Player p = e.getPlayer();

        Worker worker = WorkerManager.obtainWorker(p.getUniqueId());

        List<Job> jobs = worker.getJobs();
        List<JobTask> jobTasks = jobs.stream().flatMap(job -> job.getJobTasks().stream()).collect(Collectors.toList());
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

        if (performedJobTask != null)
            Bukkit.getPluginManager().callEvent(new JobTaskPerformEvent(worker, performedJobTask));
        if (performedJobQuestTasks != null)
            for (JobQuestTask t : performedJobQuestTasks)
                Bukkit.getPluginManager().callEvent(new JobQuestTaskPerformEvent(worker, t));
    }
    //endregion
}
