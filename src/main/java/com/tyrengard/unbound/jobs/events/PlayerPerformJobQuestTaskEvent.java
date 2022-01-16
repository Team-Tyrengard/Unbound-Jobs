package com.tyrengard.unbound.jobs.events;

import com.tyrengard.unbound.jobs.tasks.JobQuestTask;
import com.tyrengard.unbound.jobs.workers.Worker;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerPerformJobQuestTaskEvent extends PlayerEvent {
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

    private final JobQuestTask task;

    public PlayerPerformJobQuestTaskEvent(Player who, JobQuestTask task) {
        super(who);
        this.task = task;
    }

    public JobQuestTask getTask() {
        return task;
    }
}
