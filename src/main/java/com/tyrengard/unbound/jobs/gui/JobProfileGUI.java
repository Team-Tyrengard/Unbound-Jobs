package com.tyrengard.unbound.jobs.gui;

import com.tyrengard.aureycore.customguis.ACustomChestGUI;
import com.tyrengard.aureycore.customguis.Button;
import com.tyrengard.aureycore.customguis.CustomGUIUtil;
import com.tyrengard.aureycore.customguis.Information;
import com.tyrengard.aureycore.foundation.common.utils.StringUtils;
import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.JobData;
import com.tyrengard.unbound.jobs.JobManager;
import com.tyrengard.unbound.jobs.quests.internal.JobQuest;
import com.tyrengard.unbound.jobs.quests.internal.JobQuestData;
import com.tyrengard.unbound.jobs.quests.internal.JobQuestInstance;
import com.tyrengard.unbound.jobs.tasks.JobQuestTask;
import com.tyrengard.unbound.jobs.workers.Worker;
import com.tyrengard.unbound.jobs.workers.WorkerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

import static com.tyrengard.unbound.jobs.gui.JobProfileTab.*;

public class JobProfileGUI extends ACustomChestGUI {

    private final Player target;
    private JobProfileTab tab;

    public JobProfileGUI(Player target, JobProfileTab tab) {
        super(54, Integer.MAX_VALUE);
        this.target = target;
        this.tab = tab;
    }

    @Override
    protected String getName(int page) {
        return (target == null ? player : target).getName() + " - Job Profile";
    }

    @Override
    protected void onAssign() {

    }

    @Override
    protected ItemStack[] getContents(int page) {
        Player playerToShow = target == null ? player : target;
        Worker worker = WorkerManager.obtainWorker(playerToShow.getUniqueId());
        ItemStack[] contents = CustomGUIUtil.getSpacerContent(54);

        //region Tab row
        Information playerHeadInfo = new Information(Material.PLAYER_HEAD, false, ChatColor.YELLOW + playerToShow.getName());
        SkullMeta meta = (SkullMeta) playerHeadInfo.getItemMeta();
        Objects.requireNonNull(meta).setOwningPlayer(playerToShow);
        playerHeadInfo.setItemMeta(meta);

        contents[2] = playerHeadInfo;
        contents[3] = JOBS.getTabItem(tab == JOBS);
        contents[4] = QUESTS.getTabItem(tab == QUESTS);
        contents[5] = NEWS.getTabItem(tab == NEWS);
        contents[6] = SETTINGS.getTabItem(tab == SETTINGS);
        //endregion
        //region Content for Jobs tab
        switch (tab) {
            case JOBS -> {
                int jobSlots = JobManager.getMaxJobsPerPlayer();
                Iterator<Job> jobIterator = worker.getJobs().iterator();
                for (int index = 10; index < 45; index++) {
                    if (index % 9 == 0 || index % 9 == 8)
                        continue;

                    if (jobIterator.hasNext()) {
                        Job job = jobIterator.next();
                        contents[index] = getButtonForJob(job, worker.getJobData(job));
                    } else if (jobSlots == -1) {
                        continue;
                    } else if (jobSlots > 0) {
                        contents[index] = null;
                    }
                    jobSlots--;
                }
                // TODO: create pagination for jobs and remove this (who the fuck would join more than 28 jobs?)
            }
            case QUESTS -> {
                int jobSkipCount = (page - 1) * 4, row = 0, column = 1;
                boolean jobsOverflowed = false;
                for (Job job : worker.getJobs()) {
                    if (jobSkipCount > 0) {
                        jobSkipCount--;
                        continue;
                    } else if (row < 4)
                        row++;
                    else {
                        jobsOverflowed = true;
                        break;
                    }
                    contents[getIndex(row, column)] = new Information(job.getIcon(), false,
                            ChatColor.WHITE + StringUtils.toTitleCase(job.getName()));

                    JobQuestData jobQuestData = worker.getJobQuestData(job);
                    for (int c = 0; c < 6; c++) {
                        JobQuestInstance jobQuestInstance = null;
                        Button<JobProfileGUI> jobQuestButton = null;
                        if (c < 2) { // weekly
                            if (jobQuestData.getWeeklyQuestSlots() > c)
                                jobQuestInstance = jobQuestData.getWeeklyQuest(c);
                            else
                                jobQuestButton = getButtonForLockedJobQuestSlot();
                        } else { // daily
                            if (jobQuestData.getDailyQuestSlots() > c - 2)
                                jobQuestInstance = jobQuestData.getDailyQuest(c - 2);
                            else
                                jobQuestButton = getButtonForLockedJobQuestSlot();
                        }

                        if (jobQuestInstance != null) {
                            JobQuest jobQuest = job.getJobQuest(jobQuestInstance.getQuestId());
                            if (jobQuest != null)
                                jobQuestButton = getButtonForJobQuestData(jobQuest, jobQuestInstance);
                        } else
                            jobQuestButton = getButtonForUnlockedJobQuestSlot();
                        contents[getIndex(row, column + 1 + c)] = jobQuestButton;
                    }
                }

                if (page > 1)
                    contents[45] = new Button<JobProfileGUI>(Material.ARROW, "Previous",
                            (gui, e, b) -> gui.navigateToPage(page - 1));
                if (jobsOverflowed)
                    contents[53] = new Button<JobProfileGUI>(Material.ARROW, "Next",
                            (gui, e, b) -> gui.navigateToPage(page + 1));
            }
        }
        //endregion

        return contents;
    }

    @SuppressWarnings("unused")
    public void navigateToTab(JobProfileTab tab) {
        this.tab = tab;
        this.update();
    }

    private Button<JobProfileGUI> getButtonForJob(Job job, JobData jobData) {
        Material icon = job.getIcon();
        return new Button<>(icon, ChatColor.YELLOW + job.getName(), Arrays.asList(
                "",
                ChatColor.WHITE + "Level " + jobData.level(),
                ChatColor.WHITE + "EXP: " + jobData.exp() + " / " + JobManager.getExperienceForNextLevel(jobData.level()),
                "",
                ChatColor.GRAY + job.getShortDescription()
        ), (gui, e, b) -> {

        });
    }

    private Button<JobProfileGUI> getButtonForJobQuestData(JobQuest jobQuest, JobQuestInstance jobQuestInstance) {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        List<JobQuestTask> tasks = jobQuest.getTasks();

        if (tasks.size() > 1)
            lore.add(ChatColor.YELLOW + "Complete " + ChatColor.WHITE + jobQuest.getListType() +
                    ChatColor.YELLOW + " of the following:");

        for (JobQuestTask task : tasks)
            lore.add((jobQuestInstance.isActive() ? ChatColor.WHITE : "" + ChatColor.STRIKETHROUGH + ChatColor.GRAY) +
                    task.getStatusString(jobQuestInstance.getProgress(task.getId())));

        return new Button<>(Material.PAPER, ChatColor.YELLOW + jobQuest.getTitle(), lore,
                jobQuestInstance.isActive(),
                (gui, e, b) -> {

        });
    }

    private Button<JobProfileGUI> getButtonForUnlockedJobQuestSlot() {
        return null;
    }

    private Button<JobProfileGUI> getButtonForLockedJobQuestSlot() {
        return new Button<>(Material.GRAY_STAINED_GLASS_PANE, "Locked quest slot",
                Collections.singletonList("Unlock to receive more quests!"), (gui, e, b) -> {

        });
    }

    private int getIndex(int row, int column) {
        return (row * 9) + column;
    }
}
