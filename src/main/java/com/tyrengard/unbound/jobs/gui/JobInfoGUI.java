package com.tyrengard.unbound.jobs.gui;

import com.tyrengard.aureycore.customguis.ACustomChestGUI;
import com.tyrengard.aureycore.customguis.CustomGUIUtil;
import com.tyrengard.unbound.jobs.Job;
import org.bukkit.inventory.ItemStack;

public final class JobInfoGUI extends ACustomChestGUI {
    private final Job job;

    public JobInfoGUI(Job job) {
        super(54);
        this.job = job;
    }

    @Override
    protected String getName(int page) {
        return "Job Info - " + job.getName();
    }

    @Override
    protected void onAssign() {

    }

    @Override
    protected ItemStack[] getContents(int page) {
        ItemStack[] content = CustomGUIUtil.getSpacerContent(54);

        return content;
    }
}
