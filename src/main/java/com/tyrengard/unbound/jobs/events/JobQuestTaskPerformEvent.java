package com.tyrengard.unbound.jobs.events;

import com.tyrengard.unbound.jobs.tasks.JobQuestTask;
import com.tyrengard.unbound.jobs.workers.Worker;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class JobQuestTaskPerformEvent extends Event {
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

    private final Worker worker;
    private final JobQuestTask task;

    public JobQuestTaskPerformEvent(Worker worker, JobQuestTask task) {
        this.worker = worker;
        this.task = task;
    }

    public Worker getWorker() {
        return worker;
    }

    public JobQuestTask getTask() {
        return task;
    }
}
