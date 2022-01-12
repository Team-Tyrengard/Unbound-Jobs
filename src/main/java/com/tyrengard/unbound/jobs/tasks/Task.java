package com.tyrengard.unbound.jobs.tasks;

public interface Task<S extends TaskSource> {
    TaskType getType();
    S getSource();
}
