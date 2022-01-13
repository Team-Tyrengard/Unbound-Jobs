package com.tyrengard.unbound.jobs.tasks;

import org.bukkit.inventory.ItemStack;

public interface ItemStackBased {
    boolean acceptsItemStack(ItemStack is);
}
