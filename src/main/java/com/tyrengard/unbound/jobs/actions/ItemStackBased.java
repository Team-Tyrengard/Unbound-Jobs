package com.tyrengard.unbound.jobs.actions;

import org.bukkit.inventory.ItemStack;

public interface ItemStackBased {
    boolean acceptsItemStack(ItemStack is);
}
