package com.tyrengard.unbound.jobs.tasks.impl;

import com.tyrengard.aureycore.common.utils.StringUtils;
import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.quests.internal.JobQuest;
import com.tyrengard.unbound.jobs.tasks.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Set;
import java.util.stream.Collectors;

public class BreedAnimalTask {
    public static class JOB_TASK extends JobTask.Base implements EntityBased {
        protected final Set<EntityType> entityTypes;

        public JOB_TASK(TaskType taskType, Set<EntityType> entityTypes, Job source, double basePay, int baseExp) {
            super(taskType, source, basePay, baseExp);
            this.entityTypes = entityTypes;
        }

        @Override
        public boolean acceptsEntity(Entity e) {
            return entityTypes.contains(e.getType());
        }
    }

    public static class JOB_QUEST_TASK extends JobQuestTask.Base implements EntityBased {
        protected final Set<EntityType> entityTypes;

        public JOB_QUEST_TASK(int jobQuestTaskId, TaskType taskType, Set<EntityType> entityTypes, JobQuest source, int amount) {
            super(jobQuestTaskId, taskType, source, amount);
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
