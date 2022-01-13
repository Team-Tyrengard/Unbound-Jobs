package com.tyrengard.unbound.jobs.gui;

import com.tyrengard.aureycore.customguis.ACustomChestGUI;
import com.tyrengard.aureycore.customguis.Button;
import com.tyrengard.aureycore.customguis.CustomGUIManager;
import com.tyrengard.aureycore.customguis.CustomGUIUtil;
import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.JobManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class JobListGUI extends ACustomChestGUI {
    private final boolean joinJobsViaGUI;
    private final Collection<Job> jobs;

    public JobListGUI(boolean joinJobsViaGUI) {
        super(54);
        this.joinJobsViaGUI = joinJobsViaGUI;
        this.jobs = JobManager.getJobs();
    }

    @Override
    protected String getName(int page) {
        return "Job List";
    }

    @Override
    protected void onAssign() {

    }

    @Override
    protected ItemStack[] getContents(int page) {
        ItemStack[] content = CustomGUIUtil.getSpacerContent(54);

        Iterator<Job> jobIterator = jobs.iterator();
        for (int index = 10; index < 45; index++) {
            if (index % 9 == 0 || index % 9 == 8)
                continue;

            if (jobIterator.hasNext())
                content[index] = getButtonForJob(jobIterator.next());
        }
        // TODO: create pagination for jobs and remove this (who the fuck would add more than 28 jobs to a server?)

        return content;
    }

    private Button<JobListGUI> getButtonForJob(Job job) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(job.getShortDescription());
        lore.add("");
        lore.add(ChatColor.WHITE + "LEFT-CLICK" + ChatColor.GRAY + " to see tasks, quests, and skills");

        return new Button<>(job.getIcon(), ChatColor.YELLOW + job.getName(), lore, (gui, e, b) ->
                CustomGUIManager.openGUI(new JobInfoGUI(job), (Player) e.getWhoClicked()));
    }
}
