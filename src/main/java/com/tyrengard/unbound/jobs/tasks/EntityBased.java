package com.tyrengard.unbound.jobs.tasks;

import org.bukkit.entity.Entity;

public interface EntityBased {
    boolean acceptsEntity(Entity e);
}
