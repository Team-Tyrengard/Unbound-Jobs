package com.tyrengard.unbound.jobs.tasks.impl;

import com.tyrengard.aureycore.foundation.common.utils.StringUtils;
import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.actions.Action;
import com.tyrengard.unbound.jobs.quests.internal.JobQuest;
import com.tyrengard.unbound.jobs.tasks.ItemStackBased;
import com.tyrengard.unbound.jobs.tasks.JobQuestTask;
import com.tyrengard.unbound.jobs.tasks.JobTask;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.stream.Collectors;

public class CatchFishTask {
    public static class JOB_TASK extends JobTask.Base implements ItemStackBased {
        protected final Set<Material> materials;

        public JOB_TASK(Action action, Set<Material> materials, Job source, double basePay, int baseExp) {
            super(action, source, basePay, baseExp);
            this.materials = materials;
        }

        @Override
        public boolean acceptsItemStack(ItemStack is) {
            return materials.contains(is.getType());
        }
    }

    public static class JOB_QUEST_TASK extends JobQuestTask.Base implements ItemStackBased {
        protected final Set<Material> materials;

        public JOB_QUEST_TASK(int jobQuestTaskId, Action action, Set<Material> materials, JobQuest source, int amount) {
            super(jobQuestTaskId, action, source, amount);
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
