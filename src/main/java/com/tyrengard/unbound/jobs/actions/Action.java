package com.tyrengard.unbound.jobs.actions;

import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.quests.JobQuest;
import com.tyrengard.unbound.jobs.tasks.JobQuestTask;
import com.tyrengard.unbound.jobs.tasks.JobTask;
import com.tyrengard.unbound.jobs.actions.impl.*;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
                case BREAK_BLOCK -> new BreakBlockAction.JOB_TASK(this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case BREED_ANIMAL -> new BreedAnimalAction.JOB_TASK(this, Arrays.stream(taskAcceptedThings)
                        .map(String::toUpperCase).map(EntityType::valueOf).collect(Collectors.toSet()), source, basePay, baseExp);
//                case BREW_POTION -> new BrewPotionAction.JOB_TASK(this, Arrays.stream(taskAcceptedThings)
//                        .map(String::toUpperCase).map(PotionType::valueOf).collect(Collectors.toSet()), source, basePay, baseExp);
                case CATCH_FISH -> new CatchFishAction.JOB_TASK(this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case CRAFT_ITEM -> new CraftItemAction.JOB_TASK(this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
//                case "gather_animal" -> new GatherFromAnimalTask(Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
//                        .map(Material::matchMaterial).collect(Collectors.toSet()), this, basePay, baseExp);
                case HARVEST_PLANT -> new HarvestPlantAction.JOB_TASK(this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case KILL_MOB -> new KillMobAction.JOB_TASK(this, Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
                        .map(EntityType::valueOf).collect(Collectors.toSet()), source, basePay, baseExp);
                case PLACE_BLOCK -> new PlaceBlockAction.JOB_TASK(this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
//            case REPAIR_ITEM ->
//            case SMELT_ITEM -> new PlaceBlockTask(Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
//                    .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
            case SOW_PLANT -> new SowPlantAction.JOB_TASK(this, Arrays.stream(taskAcceptedThings)
                    .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
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
                case BREAK_BLOCK -> new BreakBlockAction.JOB_TASK(this, acceptsList.stream()
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case BREED_ANIMAL -> new BreedAnimalAction.JOB_TASK(this, acceptsList.stream().map(String::toUpperCase)
                        .map(EntityType::valueOf).collect(Collectors.toSet()), source, basePay, baseExp);
//                case BREW_POTION -> new BrewPotionAction.JOB_TASK(this, acceptsList.stream()
//                        .map(String::toUpperCase).map(PotionType::valueOf).collect(Collectors.toSet()), source, basePay, baseExp);
                case CATCH_FISH -> new CatchFishAction.JOB_TASK(this, acceptsList.stream()
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case CRAFT_ITEM -> new CraftItemAction.JOB_TASK(this, acceptsList.stream()
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
//                    case GATHER_ANIMAL -> {
//                    }
                case HARVEST_PLANT -> new HarvestPlantAction.JOB_TASK(this, acceptsList.stream()
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case KILL_MOB -> new KillMobAction.JOB_TASK(this, acceptsList.stream().map(String::toUpperCase)
                        .map(EntityType::valueOf).collect(Collectors.toSet()), source, basePay, baseExp);
                case PLACE_BLOCK -> new PlaceBlockAction.JOB_TASK(this, acceptsList.stream()
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
//                    case REPAIR_ITEM -> {
//                    }
//                    case SMELT_ITEM -> {
//                    }
                case SOW_PLANT -> new SowPlantAction.JOB_TASK(this, acceptsList.stream()
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
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
                case BREAK_BLOCK -> new BreakBlockAction.JOB_QUEST_TASK(jobQuestTaskId, this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, amount);
                case BREED_ANIMAL -> new BreedAnimalAction.JOB_QUEST_TASK(jobQuestTaskId, this, Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
                        .map(EntityType::valueOf).collect(Collectors.toSet()), source, amount);
//                case BREW_POTION -> new BrewPotionAction.JOB_QUEST_TASK(jobQuestTaskId, this, Arrays.stream(taskAcceptedThings)
//                        .map(String::toUpperCase).map(PotionType::valueOf).collect(Collectors.toSet()), source, amount);
                case CATCH_FISH -> new CatchFishAction.JOB_QUEST_TASK(jobQuestTaskId, this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, amount);
                case CRAFT_ITEM -> new CraftItemAction.JOB_QUEST_TASK(jobQuestTaskId, this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, amount);
//                case "gather_animal" -> new GatherFromAnimalTask(Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
//                        .map(Material::matchMaterial).collect(Collectors.toSet()), this, basePay, baseExp);
                case HARVEST_PLANT -> new HarvestPlantAction.JOB_QUEST_TASK(jobQuestTaskId, this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, amount);
                case KILL_MOB -> new KillMobAction.JOB_QUEST_TASK(jobQuestTaskId, this, Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
                        .map(EntityType::valueOf).collect(Collectors.toSet()), source, amount);
                case PLACE_BLOCK -> new PlaceBlockAction.JOB_QUEST_TASK(jobQuestTaskId, this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, amount);
//            case REPAIR_ITEM ->
//            case SMELT_ITEM -> new PlaceBlockTask(Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
//                    .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case SOW_PLANT -> new SowPlantAction.JOB_QUEST_TASK(jobQuestTaskId, this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, amount);
                default -> null;
            };
        }
    }
}
