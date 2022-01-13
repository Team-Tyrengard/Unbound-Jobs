package com.tyrengard.unbound.jobs.tasks;

import com.tyrengard.unbound.jobs.actions.Action;

public interface Task<S extends TaskSource> {
    Action getAction();
    S getSource();
}
