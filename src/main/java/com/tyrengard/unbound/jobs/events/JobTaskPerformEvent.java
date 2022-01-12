package com.tyrengard.unbound.jobs.events;

import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.tasks.JobTask;
import com.tyrengard.unbound.jobs.workers.Worker;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JobTaskPerformEvent extends Event {
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

    private final Worker worker;
    private final JobTask task;

    public JobTaskPerformEvent(Worker worker, JobTask task) {
        this.worker = worker;
        this.task = task;
    }

    public Worker getWorker() {
        return worker;
    }

    public JobTask getTask() {
        return task;
    }
}
