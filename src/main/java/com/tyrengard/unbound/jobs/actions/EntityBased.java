package com.tyrengard.unbound.jobs.actions;

import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.quests.JobQuest;
import com.tyrengard.unbound.jobs.tasks.JobQuestTask;
import com.tyrengard.unbound.jobs.tasks.JobTask;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Set;

public interface EntityBased {
    boolean acceptsEntity(Entity e);

    class BaseJobTask extends JobTask.Base implements EntityBased {
        protected final Set<EntityType> entityTypes;

        public BaseJobTask(Action action, Set<EntityType> entityTypes, Job source, double basePay, int baseExp) {
            super(action, source, basePay, baseExp);
            this.entityTypes = entityTypes;
        }

        @Override
        public boolean acceptsEntity(Entity e) {
            return entityTypes.contains(e.getType());
        }
    }

    abstract class BaseJobQuestTask extends JobQuestTask.Base implements EntityBased {
        protected final Set<EntityType> entityTypes;

        public BaseJobQuestTask(int jobQuestTaskId, Action action, Set<EntityType> entityTypes, JobQuest source, int amount) {
            super(jobQuestTaskId, action, source, amount);
            this.entityTypes = entityTypes;
        }

        @Override
        public boolean acceptsEntity(Entity e) {
            return entityTypes.contains(e.getType());
        }
    }
}
