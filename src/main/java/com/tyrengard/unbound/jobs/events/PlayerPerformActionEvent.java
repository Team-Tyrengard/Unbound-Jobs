package com.tyrengard.unbound.jobs.events;

import com.tyrengard.unbound.jobs.actions.Action;
import org.bukkit.World;
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

    private final World world;
    private final Action action;
    private final ItemStack itemStack;
    private final Block block;
    private final Entity entity;

    public PlayerPerformActionEvent(Player player, Action action, ItemStack itemStack) {
        this(player.getWorld(), player, action, itemStack, null, null);
    }

    public PlayerPerformActionEvent(Player player, Action action, Block block) {
        this(player.getWorld(), player, action, null, block, null);
    }

    public PlayerPerformActionEvent(Player player, Action action, Entity entity) {
        this(player.getWorld(), player, action, null, null, entity);
    }

    public PlayerPerformActionEvent(World world, Player player, Action action, ItemStack itemStack) {
        this(world, player, action, itemStack, null, null);
    }

    public PlayerPerformActionEvent(World world, Player player, Action action, Block block) {
        this(world, player, action, null, block, null);
    }

    public PlayerPerformActionEvent(World world, Player player, Action action, Entity entity) {
        this(world, player, action, null, null, entity);
    }

    protected PlayerPerformActionEvent(World world, Player player, Action action, ItemStack itemStack, Block block, Entity entity) {
        super(player);
        this.world = world;
        this.action = action;
        this.itemStack = itemStack;
        this.block = block;
        this.entity = entity;
    }

    public @NotNull World getWorld() {
        return world;
    }

    public @NotNull Action getAction() {
        return action;
    }

    public @NotNull ItemStack getItemStack() {
        return itemStack;
    }

    public @NotNull Block getBlock() {
        return block;
    }

    public @NotNull Entity getEntity() {
        return entity;
    }
}
