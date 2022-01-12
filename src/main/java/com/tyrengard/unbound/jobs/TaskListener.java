//package net.havengarde.unbound.jobs;
//
//import com.tyrengard.aureycore.common.anvilevents.AnvilUseEvent;
//import com.tyrengard.aureycore.common.utils.InventoryUtils;
//import net.havengarde.unbound.jobs.events.TaskPerformEvent;
//import net.havengarde.unbound.jobs.tasks.impl.*;
//import org.bukkit.Bukkit;
//import org.bukkit.block.Block;
//import org.bukkit.block.BrewingStand;
//import org.bukkit.block.data.Ageable;
//import org.bukkit.entity.Item;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.EventPriority;
//import org.bukkit.event.Listener;
//import org.bukkit.event.block.BlockBreakEvent;
//import org.bukkit.event.block.BlockPlaceEvent;
//import org.bukkit.event.entity.EntityBreedEvent;
//import org.bukkit.event.entity.EntityDeathEvent;
//import org.bukkit.event.inventory.BrewEvent;
//import org.bukkit.event.inventory.CraftItemEvent;
//import org.bukkit.event.player.*;
//import org.bukkit.inventory.BrewerInventory;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.PotionMeta;
//import org.bukkit.persistence.PersistentDataType;
//
//public class TaskListener implements Listener {
//
////    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
////    private void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
////        Player p = e.getPlayer();
////        ItemStack usedItem = p.getActiveItem();
////        if (usedItem == null) return;
////        switch (usedItem.getType()) {
////            case BOWL, BUCKET, SHEARS: break;
////            default: return;
////        }
////
////        Worker w = WorkerManager.getWorker(e.getPlayer().getUniqueId());
////        for (GatherFromAnimalTask t : w.getTasks(GatherFromAnimalTask.class)) {
////            if (t.getItemMaterial() == e.getItemStack().getType()) {
////                Bukkit.getPluginManager().callEvent(new TaskPerformEvent(w, t.getSource(), t));
////                break;
////            }
////        }
////    }
//
//    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
//    private void onAnvilUse(AnvilUseEvent e) {
//
//    }
//}
