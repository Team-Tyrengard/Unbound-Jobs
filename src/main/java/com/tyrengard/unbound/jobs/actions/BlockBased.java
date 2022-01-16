package com.tyrengard.unbound.jobs.actions;

import com.tyrengard.aureycore.foundation.common.utils.StringUtils;
import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.quests.JobQuest;
import com.tyrengard.unbound.jobs.tasks.JobQuestTask;
import com.tyrengard.unbound.jobs.tasks.JobTask;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Set;
import java.util.stream.Collectors;

public interface BlockBased {
    boolean acceptsBlock(Block b);

    class BaseJobTask extends JobTask.Base implements BlockBased {
        protected final Set<Material> materials;

        public BaseJobTask(Action action, Set<Material> materials, Job source, double basePay, int baseExp) {
            super(action, source, basePay, baseExp);
            this.materials = materials;
        }

        @Override
        public boolean acceptsBlock(Block b) {
            return materials.contains(b.getType());
        }
    }

    abstract class BaseJobQuestTask extends JobQuestTask.Base implements BlockBased {
        protected final Set<Material> materials;

        public BaseJobQuestTask(int jobQuestTaskId, Action action, Set<Material> materials, JobQuest source, int amount) {
            super(jobQuestTaskId, action, source, amount);
            this.materials = materials;
        }

        @Override
        public boolean acceptsBlock(Block b) {
            return materials.contains(b.getType());
        }
    }
}
