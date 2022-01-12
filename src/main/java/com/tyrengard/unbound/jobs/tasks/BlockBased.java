package com.tyrengard.unbound.jobs.tasks;

import org.bukkit.block.Block;

public interface BlockBased {
    boolean acceptsBlock(Block b);
}
