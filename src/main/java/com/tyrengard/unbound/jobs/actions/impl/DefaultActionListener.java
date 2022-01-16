package com.tyrengard.unbound.jobs.actions.impl;

import com.tyrengard.unbound.jobs.actions.Action;
import com.tyrengard.unbound.jobs.events.PlayerPerformActionEvent;
import org.bukkit.Bukkit;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;

public class DefaultActionListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();

        switch (b.getType()) {
            case WHEAT, NETHER_WART, CARROTS, POTATOES, BEETROOTS, SUGAR_CANE -> {
                if (b.getBlockData() instanceof Ageable a && a.getAge() == a.getMaximumAge())
                    Bukkit.getPluginManager().callEvent(
                            new PlayerPerformActionEvent(p, Action.Default.HARVEST_PLANT, e.getBlock()));
            }
            default -> Bukkit.getPluginManager().callEvent(
                    new PlayerPerformActionEvent(p, Action.Default.BREAK_BLOCK, e.getBlock()));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onEntityBreed(EntityBreedEvent e) {
        if (e.getBreeder() instanceof Player p)
            Bukkit.getPluginManager().callEvent(
                    new PlayerPerformActionEvent(p, Action.Default.BREED_ANIMAL, e.getEntity()));
    }

//    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
//    private void onBrew(BrewEvent e) {
//        logDebug("TaskManager.onBrew: Handling BrewEvent");
//
//        BrewerInventory bi = e.getContents();
//        BrewingStand bs = bi.getHolder();
//        if (bs == null) return;
//
//        logDebug("TaskManager.onBrew: Checking if world is disabled");
//        if (worldsDisabled.contains(bs.getBlock().getWorld().getName()))
//            return;
//
//        logDebug("TaskManager.onBrew: Getting active worker");
//        Worker w = WorkerManager.getActiveWorker(bs);
//        if (w == null) return;
//
//        for (ItemStack i : InventoryUtils.getContentsOfPotionSlots(bi)) {
//            if (i != null && i.getItemMeta() instanceof PotionMeta potionMeta && potionMeta.getPersistentDataContainer()
//                    .get(UJ_PAID_KEY, PersistentDataType.BYTE) == null) {
//                Bukkit.getPluginManager().callEvent(
//                        new PlayerPerformActionEvent(Bukkit.getPlayer(w.getId()), Action.Default.BREW_POTION, i));
//
//                potionMeta.getPersistentDataContainer().set(UJ_PAID_KEY, PersistentDataType.BYTE, (byte) 1);
//                i.setItemMeta(potionMeta);
//            }
//        }
//
//        WorkerManager.removeActiveWorker(bs.getBlock());
//    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onPlayerFish(PlayerFishEvent e) {
        if (e.getCaught() instanceof Item caughtItem)
            Bukkit.getPluginManager().callEvent(
                    new PlayerPerformActionEvent(e.getPlayer(), Action.Default.CATCH_FISH, caughtItem));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onCraftItem(CraftItemEvent e) {
        Bukkit.getPluginManager().callEvent(
                new PlayerPerformActionEvent((Player) e.getWhoClicked(), Action.Default.CRAFT_ITEM, e.getRecipe().getResult()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onPlayerHarvestBlock(PlayerHarvestBlockEvent e) {
        if (e.getHarvestedBlock().getBlockData() instanceof Ageable a && a.getAge() == a.getMaximumAge())
            Bukkit.getPluginManager().callEvent(
                    new PlayerPerformActionEvent(e.getPlayer(), Action.Default.HARVEST_PLANT, e.getHarvestedBlock()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onEntityDeath(EntityDeathEvent e) {
        Player p = e.getEntity().getKiller();
        if (p == null)
            return;

        Bukkit.getPluginManager().callEvent(
                new PlayerPerformActionEvent(e.getEntity().getWorld(), p, Action.Default.KILL_MOB, e.getEntity()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onBlockPlace(BlockPlaceEvent e) {
        Block b = e.getBlockPlaced();

        if (Tag.CROPS.isTagged(b.getType()) || Tag.SAPLINGS.isTagged(b.getType()))
            Bukkit.getPluginManager().callEvent(
                    new PlayerPerformActionEvent(e.getPlayer(), Action.Default.SOW_PLANT, b));
        else
            Bukkit.getPluginManager().callEvent(
                    new PlayerPerformActionEvent(e.getPlayer(), Action.Default.PLACE_BLOCK, b));
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
}
