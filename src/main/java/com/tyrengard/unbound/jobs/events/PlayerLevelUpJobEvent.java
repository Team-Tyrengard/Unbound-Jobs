package com.tyrengard.unbound.jobs.events;

import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.workers.Worker;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerLevelUpJobEvent extends PlayerEvent implements Cancellable {
    // region Base event components
    private static final HandlerList handlers = new HandlerList();
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

    private boolean cancelled = false;
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    // endregion

    private final Job job;
    private final short newLevel;

    public PlayerLevelUpJobEvent(Player who, Job job, short newLevel) {
        super(who);
        this.job = job;
        this.newLevel = newLevel;
    }

    public Job getJob() {
        return job;
    }

    public short getNewLevel() {
        return newLevel;
    }
}
