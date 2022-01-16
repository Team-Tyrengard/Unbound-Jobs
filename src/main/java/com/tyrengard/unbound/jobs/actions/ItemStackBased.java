package com.tyrengard.unbound.jobs.actions;

import com.tyrengard.aureycore.foundation.common.utils.StringUtils;
import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.quests.JobQuest;
import com.tyrengard.unbound.jobs.tasks.JobQuestTask;
import com.tyrengard.unbound.jobs.tasks.JobTask;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.stream.Collectors;

public interface ItemStackBased {
    boolean acceptsItemStack(ItemStack is);

    class BaseJobTask extends JobTask.Base implements ItemStackBased {
        protected final Set<Material> materials;

        public BaseJobTask(Action action, Set<Material> materials, Job source, double basePay, int baseExp) {
            super(action, source, basePay, baseExp);
            this.materials = materials;
        }

        @Override
        public boolean acceptsItemStack(ItemStack is) {
            return materials.contains(is.getType());
        }
    }

    abstract class BaseJobQuestTask extends JobQuestTask.Base implements ItemStackBased {
        protected final Set<Material> materials;

        public BaseJobQuestTask(int jobQuestTaskId, Action action, Set<Material> materials, JobQuest source, int amount) {
            super(jobQuestTaskId, action, source, amount);
            this.materials = materials;
        }

        @Override
        public boolean acceptsItemStack(ItemStack is) {
            return materials.contains(is.getType());
        }
    }
}
