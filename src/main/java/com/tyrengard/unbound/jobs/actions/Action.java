package com.tyrengard.unbound.jobs.actions;

import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.quests.internal.JobQuest;
import com.tyrengard.unbound.jobs.tasks.JobQuestTask;
import com.tyrengard.unbound.jobs.tasks.JobTask;
import com.tyrengard.unbound.jobs.tasks.impl.*;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface Action {
    String getId();
    JobTask getJobTask(Job source, String shortFormString);
    JobTask getJobTask(Job source, Map<?, ?> expandedFormMap);
    JobQuestTask getJobQuestTask(int jobQuestTaskId, JobQuest source, String shortFormString);

    enum Default implements Action {
        BREAK_BLOCK, BREED_ANIMAL, BREW_POTION, CATCH_FISH, CRAFT_ITEM, GATHER_ANIMAL, HARVEST_BLOCK, KILL_MOB,
        PLACE_BLOCK, REPAIR_ITEM, SMELT_ITEM, SOW_PLANT;

        @Override
        public String getId() {
            return name().toLowerCase();
        }

        @Override
        public JobTask getJobTask(Job source, String str) {
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
                case BREAK_BLOCK -> new BreakBlockTask.JOB_TASK(this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case BREED_ANIMAL -> new BreedAnimalTask.JOB_TASK(this, Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
                        .map(EntityType::valueOf).collect(Collectors.toSet()), source, basePay, baseExp);
//                case "brew_potion" -> new BrewPotionTask(Arrays.stream(taskAcceptedThings))
                case CATCH_FISH -> new CatchFishTask.JOB_TASK(this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case CRAFT_ITEM -> new CraftItemStackTask.JOB_TASK(this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
//                case "gather_animal" -> new GatherFromAnimalTask(Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
//                        .map(Material::matchMaterial).collect(Collectors.toSet()), this, basePay, baseExp);
                case HARVEST_BLOCK -> new HarvestBlockTask.JOB_TASK(this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case KILL_MOB -> new KillMobTask.JOB_TASK(this, Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
                        .map(EntityType::valueOf).collect(Collectors.toSet()), source, basePay, baseExp);
                case PLACE_BLOCK -> new PlaceBlockTask.JOB_TASK(this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
//            case REPAIR_ITEM ->
//            case SMELT_ITEM -> new PlaceBlockTask(Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
//                    .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
//            case SOW_PLANT -> new PlaceBlockTask(Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
//                    .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                default -> null;
            };
        }

        @Override
        @SuppressWarnings("unchecked")
        public JobTask getJobTask(Job source, Map<?, ?> map) {
            double basePay = (Double) map.get("base-pay");
            int baseExp = (Integer) map.get("base-exp");
            // TODO: exp-level-cap, pay-level-cap
            List<String> acceptsList = (List<String>) map.get("accepts");
            return switch (this) {
                case BREAK_BLOCK -> new BreakBlockTask.JOB_TASK(this, acceptsList.stream()
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case BREED_ANIMAL -> new BreedAnimalTask.JOB_TASK(this, acceptsList.stream().map(String::toUpperCase)
                        .map(EntityType::valueOf).collect(Collectors.toSet()), source, basePay, baseExp);
//                    case BREW_POTION -> {
//                    }
                case CATCH_FISH -> new CatchFishTask.JOB_TASK(this, acceptsList.stream()
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case CRAFT_ITEM -> new CraftItemStackTask.JOB_TASK(this, acceptsList.stream()
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
//                    case GATHER_ANIMAL -> {
//                    }
                case HARVEST_BLOCK -> new HarvestBlockTask.JOB_TASK(this, acceptsList.stream()
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                case KILL_MOB -> new KillMobTask.JOB_TASK(this, acceptsList.stream().map(String::toUpperCase)
                        .map(EntityType::valueOf).collect(Collectors.toSet()), source, basePay, baseExp);
                case PLACE_BLOCK -> new PlaceBlockTask.JOB_TASK(this, acceptsList.stream()
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
//                    case REPAIR_ITEM -> {
//                    }
//                    case SMELT_ITEM -> {
//                    }
//                    case SOW_PLANT -> {
//                    }
                default -> null;
            };
        }

        @Override
        public JobQuestTask getJobQuestTask(int jobQuestTaskId, JobQuest source, String str) {
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
                case BREAK_BLOCK -> new BreakBlockTask.JOB_QUEST_TASK(jobQuestTaskId, this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, amount);
                case BREED_ANIMAL -> new BreedAnimalTask.JOB_QUEST_TASK(jobQuestTaskId, this, Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
                        .map(EntityType::valueOf).collect(Collectors.toSet()), source, amount);
//                case "brew_potion" -> new BrewPotionTask(Arrays.stream(taskAcceptedThings))
                case CATCH_FISH -> new CatchFishTask.JOB_QUEST_TASK(jobQuestTaskId, this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, amount);
                case CRAFT_ITEM -> new CraftItemStackTask.JOB_QUEST_TASK(jobQuestTaskId, this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, amount);
//                case "gather_animal" -> new GatherFromAnimalTask(Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
//                        .map(Material::matchMaterial).collect(Collectors.toSet()), this, basePay, baseExp);
                case HARVEST_BLOCK -> new HarvestBlockTask.JOB_QUEST_TASK(jobQuestTaskId, this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, amount);
                case KILL_MOB -> new KillMobTask.JOB_QUEST_TASK(jobQuestTaskId, this, Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
                        .map(EntityType::valueOf).collect(Collectors.toSet()), source, amount);
                case PLACE_BLOCK -> new PlaceBlockTask.JOB_QUEST_TASK(jobQuestTaskId, this, Arrays.stream(taskAcceptedThings)
                        .map(Material::matchMaterial).collect(Collectors.toSet()), source, amount);
//            case REPAIR_ITEM ->
//            case SMELT_ITEM -> new PlaceBlockTask(Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
//                    .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
//            case SOW_PLANT -> new PlaceBlockTask(Arrays.stream(taskAcceptedThings).map(String::toUpperCase)
//                    .map(Material::matchMaterial).collect(Collectors.toSet()), source, basePay, baseExp);
                default -> null;
            };
        }
    }
}
