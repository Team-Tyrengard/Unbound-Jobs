package com.tyrengard.unbound.jobs.actions;

import com.tyrengard.aureycore.foundation.common.utils.StringUtils;
import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.quests.JobQuest;
import com.tyrengard.unbound.jobs.tasks.JobQuestTask;
import com.tyrengard.unbound.jobs.tasks.JobTask;
import com.tyrengard.unbound.jobs.actions.impl.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface Action {
    @NotNull String getId();
    @Nullable JobTask getJobTask(@NotNull Job source, @NotNull String shortFormString);
    @Nullable JobTask getJobTask(@NotNull Job source, @NotNull Map<?, ?> expandedFormMap);
    @Nullable JobQuestTask getJobQuestTask(int jobQuestTaskId, @NotNull JobQuest source, @NotNull String shortFormString);

    enum Default implements Action {
        BREAK_BLOCK,
        BREED_ANIMAL,
//        BREW_POTION,
        CATCH_FISH,
        CRAFT_ITEM,
        GATHER_FROM_ANIMAL,
        SHEAR_ANIMAL,
        HARVEST_PLANT,
        KILL_MOB,
        PLACE_BLOCK,
        REPAIR_ITEM,
        SMELT_ITEM,
        SOW_PLANT;

        @Override
        public @NotNull String getId() {
            return name().toLowerCase();
        }

        @Override
        public JobTask getJobTask(@NotNull Job source, @NotNull String str) {
            String[] taskAcceptedThings;
            String[] taskStringArray;
            if (str.contains("[") && str.contains("]")) {
                String taskAcceptedThingsString = str.toLowerCase().substring(str.indexOf('['), str.indexOf(']') + 1);
                taskStringArray = str.replace(taskAcceptedThingsString, "").toLowerCase().split(" ");
                taskAcceptedThings = taskAcceptedThingsString.replaceAll("[\\[\\]\\s]", "").split(",");
            } else {
                taskStringArray = str.toLowerCase().split(" ");
                taskAcceptedThings = new String[] { taskStringArray[1] };
            }

            double basePay = Double.parseDouble(taskStringArray[2]);
            int baseExp = Integer.parseInt(taskStringArray[3]);

            return switch (this) {
                case BREAK_BLOCK, PLACE_BLOCK, SOW_PLANT -> new BlockBased.BaseJobTask(this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case CATCH_FISH, CRAFT_ITEM, HARVEST_PLANT -> new ItemStackBased.BaseJobTask(this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case BREED_ANIMAL, KILL_MOB -> new EntityBased.BaseJobTask(this, Arrays.stream(taskAcceptedThings)
                        .map(String::toUpperCase).map(EntityType::valueOf).collect(Collectors.toSet()), source, basePay, baseExp);
//                case BREW_POTION -> new BrewPotionAction.JOB_TASK(this, Arrays.stream(taskAcceptedThings)
//                        .map(String::toUpperCase).map(PotionType::valueOf).collect(Collectors.toSet()), source, basePay, baseExp);
//                case GATHER_FROM_ANIMAL -> new GatherFromAnimalTask(Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
//                        .map(Material::matchMaterial).collect(Collectors.toSet()), this, basePay, baseExp);
//            case REPAIR_ITEM ->
//            case SMELT_ITEM -> new PlaceBlockTask(Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
//                    .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                default -> null;
            };
        }

        @Override
        @SuppressWarnings("unchecked")
        public @Nullable JobTask getJobTask(@NotNull Job source, @NotNull Map<?, ?> map) {
            double basePay = (Double) map.get("base-pay");
            int baseExp = (Integer) map.get("base-exp");
            List<String> acceptsList = (List<String>) map.get("accepts");
            return switch (this) {
                case BREAK_BLOCK, PLACE_BLOCK, SOW_PLANT -> new BlockBased.BaseJobTask(this, acceptsList.stream()
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case CATCH_FISH, CRAFT_ITEM, HARVEST_PLANT -> new ItemStackBased.BaseJobTask(this, acceptsList.stream()
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case BREED_ANIMAL, KILL_MOB -> new EntityBased.BaseJobTask(this, acceptsList.stream().map(String::toUpperCase)
                        .map(EntityType::valueOf).collect(Collectors.toSet()), source, basePay, baseExp);
//                case BREW_POTION -> new BrewPotionAction.JOB_TASK(this, acceptsList.stream()
//                        .map(String::toUpperCase).map(PotionType::valueOf).collect(Collectors.toSet()), source, basePay, baseExp);
//                    case GATHER_ANIMAL -> {
//                    }
//                    case REPAIR_ITEM -> {
//                    }
//                    case SMELT_ITEM -> {
//                    }
                default -> null;
            };
        }

        @Override
        public JobQuestTask getJobQuestTask(int jobQuestTaskId, @NotNull JobQuest source, @NotNull String str) {
            String[] taskAcceptedThings;
            String[] taskStringArray;
            if (str.contains("[") && str.contains("]")) {
                String taskAcceptedThingsString = str.toLowerCase().substring(str.indexOf('['), str.indexOf(']') + 1);
                taskStringArray = str.replace(taskAcceptedThingsString, "").toLowerCase().split(" ");
                taskAcceptedThings = taskAcceptedThingsString.replaceAll("[\\[\\]\\s]", "").split(",");
            } else {
                taskStringArray = str.toLowerCase().split(" ");
                taskAcceptedThings = new String[] { taskStringArray[1] };
            }

            int amount = Integer.parseInt(taskStringArray[2]);

            return switch (this) {
                case BREAK_BLOCK -> new BlockBased.BaseJobQuestTask(jobQuestTaskId, this,
                        Arrays.stream(taskAcceptedThings).map(Material::matchMaterial).collect(Collectors.toSet()),
                        source, amount) {
                    @Override
                    public String getStatusString(int currentProgress) {
                        return "Break " + currentProgress + " / " + getProgressRequired() + " " + materials.stream()
                                .map(Material::toString).map(StringUtils::toTitleCase).collect(Collectors.joining(" OR "));
                    }
                };
                case BREED_ANIMAL -> new EntityBased.BaseJobQuestTask(jobQuestTaskId, this,
                        Arrays.stream(taskAcceptedThings).map(String::toUpperCase).map(EntityType::valueOf)
                                .collect(Collectors.toSet()), source, amount) {
                    @Override
                    public String getStatusString(int currentProgress) {
                        return "Breed " + currentProgress + " / " + getProgressRequired() + " " + entityTypes.stream()
                                .map(EntityType::toString).map(StringUtils::toTitleCase).collect(Collectors.joining(" OR "));
                    }
                };
//                case BREW_POTION -> new BrewPotionAction.JOB_QUEST_TASK(jobQuestTaskId, this, Arrays.stream(taskAcceptedThings)
//                        .map(String::toUpperCase).map(PotionType::valueOf).collect(Collectors.toSet()), source, amount);
                case CATCH_FISH -> new ItemStackBased.BaseJobQuestTask(jobQuestTaskId, this,
                        Arrays.stream(taskAcceptedThings).map(Material::matchMaterial).collect(Collectors.toSet()),
                        source, amount) {
                    @Override
                    public String getStatusString(int currentProgress) {
                        return "Catch " + currentProgress + " / " + getProgressRequired() + " " + materials.stream()
                                .map(Material::toString).map(StringUtils::toTitleCase).collect(Collectors.joining(" OR "));
                    }
                };
                case CRAFT_ITEM -> new ItemStackBased.BaseJobQuestTask(jobQuestTaskId, this,
                        Arrays.stream(taskAcceptedThings).map(Material::matchMaterial).collect(Collectors.toSet()),
                        source, amount) {
                    @Override
                    public String getStatusString(int currentProgress) {
                        return "Craft " + currentProgress + " / " + getProgressRequired() + " " + materials.stream()
                                .map(Material::toString).map(StringUtils::toTitleCase).collect(Collectors.joining(" OR "));
                    }
                };
//                case "gather_animal" -> new GatherFromAnimalTask(Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
//                        .map(Material::matchMaterial).collect(Collectors.toSet()), this, basePay, baseExp);
                case HARVEST_PLANT -> new BlockBased.BaseJobQuestTask(jobQuestTaskId, this,
                        Arrays.stream(taskAcceptedThings).map(Material::matchMaterial).collect(Collectors.toSet()),
                        source, amount) {
                    @Override
                    public String getStatusString(int currentProgress) {
                        return "Harvest " + currentProgress + " / " + getProgressRequired() + " " + materials.stream()
                                .map(Material::toString).map(StringUtils::toTitleCase).collect(Collectors.joining(" OR "));
                    }
                };
                case KILL_MOB -> new EntityBased.BaseJobQuestTask(jobQuestTaskId, this,
                        Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
                                .map(EntityType::valueOf).collect(Collectors.toSet()), source, amount) {
                    @Override
                    public String getStatusString(int currentProgress) {
                        return "Kill " + currentProgress + " / " + getProgressRequired() + " " + entityTypes.stream()
                                .map(EntityType::toString).map(StringUtils::toTitleCase).collect(Collectors.joining(" OR "));
                    }
                };
                case PLACE_BLOCK -> new BlockBased.BaseJobQuestTask(jobQuestTaskId, this,
                        Arrays.stream(taskAcceptedThings).map(Material::matchMaterial).collect(Collectors.toSet()),
                        source, amount) {
                    @Override
                    public String getStatusString(int currentProgress) {
                        return "Place " + currentProgress + " / " + getProgressRequired() + " " + materials.stream()
                                .map(Material::toString).map(StringUtils::toTitleCase).collect(Collectors.joining(" OR "));
                    }
                };
//            case REPAIR_ITEM ->
//            case SMELT_ITEM -> new PlaceBlockTask(Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
//                    .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case SOW_PLANT -> new BlockBased.BaseJobQuestTask(jobQuestTaskId, this,
                        Arrays.stream(taskAcceptedThings).map(Material::matchMaterial).collect(Collectors.toSet()),
                        source, amount) {
                    @Override
                    public String getStatusString(int currentProgress) {
                        return "Plant " + currentProgress + " / " + getProgressRequired() + " " + materials.stream()
                                .map(Material::toString).map(StringUtils::toTitleCase).collect(Collectors.joining(" OR "));
                    }
                };
                default -> null;
            };
        }
    }
}
