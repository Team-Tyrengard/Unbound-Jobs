//package com.tyrengard.unbound.jobs.tasks.impl;
//
//import com.tyrengard.unbound.jobs.Job;
//import com.tyrengard.unbound.jobs.tasks.PotionTypeBased;
//import com.tyrengard.unbound.jobs.tasks.Task;
//import org.bukkit.potion.PotionType;
//
//import java.util.Set;
//
//public class BrewPotionTask extends Task.Base implements PotionTypeBased {
//    protected final Set<PotionType> potionTypes;
//
//    public BrewPotionTask(Set<PotionType> potionTypes, Job source, double basePay, int baseExp) {
//        super(source, basePay, baseExp);
//        this.potionTypes = potionTypes;
//    }
//
//    @Override
//    public boolean acceptsPotionType(PotionType pt) {
//        return potionTypes.contains(pt);
//    }
//}
