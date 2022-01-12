package com.tyrengard.unbound.jobs.events;

import com.tyrengard.unbound.jobs.tasks.TaskType;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Called when a player performs a possible task.
 */
public class TaskPerformEvent extends Event {
    // region Base event components
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
    // endregion

    private final Player player;
    private final TaskType taskType;
    private final ItemStack itemStack;
    private final Block block;
    private final Entity entity;

    public TaskPerformEvent(Player player, TaskType taskType, ItemStack itemStack) {
        this(player, taskType, itemStack, null, null);
    }

    public TaskPerformEvent(Player player, TaskType taskType, Block block) {
        this(player, taskType, null, block, null);
    }

    public TaskPerformEvent(Player player, TaskType taskType, Entity entity) {
        this(player, taskType, null, null, entity);
    }

    protected TaskPerformEvent(Player player, TaskType taskType, ItemStack itemStack, Block block, Entity entity) {
        this.player = player;
        this.taskType = taskType;
        this.itemStack = itemStack;
        this.block = block;
        this.entity = entity;
    }

    public Player getPlayer() {
        return player;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Block getBlock() {
        return block;
    }

    public Entity getEntity() {
        return entity;
    }
}
