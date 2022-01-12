package com.tyrengard.unbound.jobs.events;

import com.tyrengard.unbound.jobs.workers.Worker;
import com.tyrengard.unbound.jobs.Job;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JobLevelUpEvent extends Event implements Cancellable {
    // region Base event components
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
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

    private final Worker worker;
    private final Job job;
    private final short newLevel;

    public JobLevelUpEvent(Worker worker, Job job, short newLevel) {
        this.worker = worker;
        this.job = job;
        this.newLevel = newLevel;
    }

    public Worker getWorker() {
        return worker;
    }

    public Job getJob() {
        return job;
    }

    public short getNewLevel() {
        return newLevel;
    }
}
