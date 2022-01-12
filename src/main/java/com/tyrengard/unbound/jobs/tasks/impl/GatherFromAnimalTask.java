//package com.tyrengard.unbound.jobs.tasks.impl;
//
//import com.tyrengard.unbound.jobs.Job;
//import com.tyrengard.unbound.jobs.tasks.ItemStackBased;
//import com.tyrengard.unbound.jobs.tasks.Task;
//import org.bukkit.Material;
//import org.bukkit.inventory.ItemStack;
//
//import java.util.Set;
//
//public class GatherFromAnimalTask extends Task.Base implements ItemStackBased {
//    protected final Set<Material> materials;
//    public GatherFromAnimalTask(Set<Material> materials, Job source, double basePay, int baseExp) {
//        super(source, basePay, baseExp);
//        this.materials = materials;
//    }
//
//    @Override
//    public boolean acceptsItemStack(ItemStack is) {
//        return materials.contains(is.getType());
//    }
//}
