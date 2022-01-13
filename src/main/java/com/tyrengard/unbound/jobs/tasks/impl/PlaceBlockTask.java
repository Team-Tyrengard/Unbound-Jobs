package com.tyrengard.unbound.jobs.tasks.impl;

import com.tyrengard.aureycore.foundation.common.utils.StringUtils;
import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.quests.internal.JobQuest;
import com.tyrengard.unbound.jobs.tasks.*;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Set;
import java.util.stream.Collectors;

public class PlaceBlockTask {
    public static class JOB_TASK extends JobTask.Base implements BlockBased {
        protected final Set<Material> materials;

        public JOB_TASK(TaskType taskType, Set<Material> materials, Job source, double basePay, int baseExp) {
            super(taskType, source, basePay, baseExp);
            this.materials = materials;
        }

        @Override
        public boolean acceptsBlock(Block b) {
            return materials.contains(b.getType());
        }
    }

    public static class JOB_QUEST_TASK extends JobQuestTask.Base implements BlockBased {
        protected final Set<Material> materials;

        public JOB_QUEST_TASK(int jobQuestTaskId, TaskType taskType, Set<Material> materials, JobQuest source, int amount) {
            super(jobQuestTaskId, taskType, source, amount);
            this.materials = materials;
        }

        @Override
        public boolean acceptsBlock(Block b) {
            return materials.contains(b.getType());
        }

        @Override
        public String getStatusString(int currentProgress) {
            return "Place " + currentProgress + " / " + getProgressRequired() + " " + materials.stream()
                    .map(Material::toString).map(StringUtils::toTitleCase).collect(Collectors.joining(" OR "));
        }
    }
}
