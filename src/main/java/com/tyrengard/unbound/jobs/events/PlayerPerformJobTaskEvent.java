package com.tyrengard.unbound.jobs.events;

import com.tyrengard.unbound.jobs.tasks.JobTask;
import com.tyrengard.unbound.jobs.workers.Worker;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerPerformJobTaskEvent extends PlayerEvent {
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

    private final short playerLevel;
    private final JobTask task;

    public PlayerPerformJobTaskEvent(Player who, short playerLevel, JobTask task) {
        super(who);
        this.playerLevel = playerLevel;
        this.task = task;
    }

    public JobTask getTask() {
        return task;
    }

    public short getPlayerLevel() { return playerLevel; }
}
