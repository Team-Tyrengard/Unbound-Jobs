package com.tyrengard.unbound.jobs.tasks.impl;

import com.tyrengard.aureycore.foundation.common.utils.StringUtils;
import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.actions.Action;
import com.tyrengard.unbound.jobs.quests.internal.JobQuest;
import com.tyrengard.unbound.jobs.tasks.EntityBased;
import com.tyrengard.unbound.jobs.tasks.JobQuestTask;
import com.tyrengard.unbound.jobs.tasks.JobTask;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Set;
import java.util.stream.Collectors;

public class BreedAnimalTask {
    public static class JOB_TASK extends JobTask.Base implements EntityBased {
        protected final Set<EntityType> entityTypes;

        public JOB_TASK(Action action, Set<EntityType> entityTypes, Job source, double basePay, int baseExp) {
            super(action, source, basePay, baseExp);
            this.entityTypes = entityTypes;
        }

        @Override
        public boolean acceptsEntity(Entity e) {
            return entityTypes.contains(e.getType());
        }
    }

    public static class JOB_QUEST_TASK extends JobQuestTask.Base implements EntityBased {
        protected final Set<EntityType> entityTypes;

        public JOB_QUEST_TASK(int jobQuestTaskId, Action action, Set<EntityType> entityTypes, JobQuest source, int amount) {
            super(jobQuestTaskId, action, source, amount);
            this.entityTypes = entityTypes;
        }

        @Override
        public boolean acceptsEntity(Entity e) {
            return entityTypes.contains(e.getType());
        }

        @Override
        public String getStatusString(int currentProgress) {
            return "Breed " + currentProgress + " / " + getProgressRequired() + " " + entityTypes.stream()
                    .map(EntityType::toString).map(StringUtils::toTitleCase).collect(Collectors.joining(" OR "));
        }
    }
}
