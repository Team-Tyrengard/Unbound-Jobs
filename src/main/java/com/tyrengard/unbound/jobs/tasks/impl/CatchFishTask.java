package com.tyrengard.unbound.jobs.tasks.impl;

import com.tyrengard.aureycore.common.utils.StringUtils;
import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.quests.internal.JobQuest;
import com.tyrengard.unbound.jobs.tasks.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.stream.Collectors;

public class CatchFishTask {
    public static class JOB_TASK extends JobTask.Base implements ItemStackBased {
        protected final Set<Material> materials;

        public JOB_TASK(TaskType taskType, Set<Material> materials, Job source, double basePay, int baseExp) {
            super(taskType, source, basePay, baseExp);
            this.materials = materials;
        }

        @Override
        public boolean acceptsItemStack(ItemStack is) {
            return materials.contains(is.getType());
        }
    }

    public static class JOB_QUEST_TASK extends JobQuestTask.Base implements ItemStackBased {
        protected final Set<Material> materials;

        public JOB_QUEST_TASK(int jobQuestTaskId, TaskType taskType, Set<Material> materials, JobQuest source, int amount) {
            super(jobQuestTaskId, taskType, source, amount);
            this.materials = materials;
        }

        @Override
        public boolean acceptsItemStack(ItemStack is) {
            return materials.contains(is.getType());
        }

        @Override
        public String getStatusString(int currentProgress) {
            return "Catch " + currentProgress + " / " + getProgressRequired() + " " + materials.stream()
                    .map(Material::toString).map(StringUtils::toTitleCase).collect(Collectors.joining(" OR "));
        }
    }
}
