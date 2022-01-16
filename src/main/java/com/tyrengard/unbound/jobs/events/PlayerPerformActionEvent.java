package com.tyrengard.unbound.jobs.events;

import com.tyrengard.unbound.jobs.actions.Action;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player performs a possible task.
 */
public class PlayerPerformActionEvent extends PlayerEvent {
    // region Base event components
    private static final HandlerList handlers = new HandlerList();
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
    // endregion

    private final Action action;
    private final ItemStack itemStack;
    private final Block block;
    private final Entity entity;

    public PlayerPerformActionEvent(Player player, Action action, ItemStack itemStack) {
        this(player, action, itemStack, null, null);
    }

    public PlayerPerformActionEvent(Player player, Action action, Block block) {
        this(player, action, null, block, null);
    }

    public PlayerPerformActionEvent(Player player, Action action, Entity entity) {
        this(player, action, null, null, entity);
    }

    protected PlayerPerformActionEvent(Player player, Action action, ItemStack itemStack, Block block, Entity entity) {
        super(player);
        this.action = action;
        this.itemStack = itemStack;
        this.block = block;
        this.entity = entity;
    }

    public Action getAction() {
        return action;
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
