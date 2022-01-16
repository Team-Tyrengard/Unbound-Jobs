package com.tyrengard.unbound.jobs.actions.impl;

import com.tyrengard.aureycore.foundation.common.utils.StringUtils;
import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.actions.Action;
import com.tyrengard.unbound.jobs.actions.ItemStackBased;
import com.tyrengard.unbound.jobs.quests.JobQuest;
import com.tyrengard.unbound.jobs.tasks.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

public class BrewPotionAction {
    public static class JOB_TASK extends JobTask.Base implements ItemStackBased {
        protected final Set<PotionType> potionTypes;

        public JOB_TASK(Action action, Set<PotionType> potionTypes, Job source, double basePay, int baseExp) {
            super(action, source, basePay, baseExp);
            this.potionTypes = potionTypes;
        }

        @Override
        public boolean acceptsItemStack(ItemStack is) {
            return is.getItemMeta() instanceof PotionMeta potionMeta &&
                    potionTypes.contains(potionMeta.getBasePotionData().getType());
        }
    }

    public static class JOB_QUEST_TASK extends JobQuestTask.Base implements ItemStackBased {
        protected final Set<PotionType> potionTypes;

        public JOB_QUEST_TASK(int jobQuestTaskId, Action action, Set<PotionType> potionTypes, JobQuest source, int amount) {
            super(jobQuestTaskId, action, source, amount);
            this.potionTypes = potionTypes;
        }

        @Override
        public boolean acceptsItemStack(ItemStack is) {
            return is.getItemMeta() instanceof PotionMeta potionMeta &&
                    potionTypes.contains(potionMeta.getBasePotionData().getType());
        }

        @Override
        public String getStatusString(int currentProgress) {
            return "Brew " + currentProgress + " / " + getProgressRequired() + " " +
                    (potionTypes.size() > 0 ? "Potions of " : "Potion of ") + potionTypes.stream()
                    .map(BrewPotionAction::getPotionNameFromPotionType).map(StringUtils::toTitleCase)
                    .collect(Collectors.joining(" OR "));
        }
    }

    private static @Nullable String getPotionNameFromPotionType(PotionType potionType) {
        return switch (potionType) {
            case UNCRAFTABLE, MUNDANE, THICK, AWKWARD, WATER, LUCK -> null;
            case TURTLE_MASTER -> "the " + StringUtils.toTitleCase(potionType.toString());
            case INSTANT_HEAL -> "Healing";
            case REGEN -> "Regeneration";
            case JUMP -> "Leaping";
            case SPEED -> "Swiftness";
            case INSTANT_DAMAGE -> "Harming";
            default -> StringUtils.toTitleCase(potionType.toString());
        };
    }
}