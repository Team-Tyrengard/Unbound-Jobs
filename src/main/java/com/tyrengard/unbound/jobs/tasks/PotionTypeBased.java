package com.tyrengard.unbound.jobs.tasks;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

public interface PotionTypeBased {
    boolean acceptsPotionType(PotionType pt);
}
