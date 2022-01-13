package com.tyrengard.unbound.jobs.quests.impl;

import com.tyrengard.unbound.jobs.UnboundJobs;
import com.tyrengard.unbound.jobs.quests.JobQuestReward;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class CommandQuestReward implements JobQuestReward {
    public static String key = "command";
    private final String command;

    public CommandQuestReward(String command) {
        this.command = command;
    }

    @Override
    public void awardToPlayer(Player p) {
        try {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("@player", p.getName()));
        } catch (CommandException e) {
            UnboundJobs.getInstance().getLogger().log(Level.SEVERE, "An exception was raised in trying to send a command:");
            UnboundJobs.getInstance().getLogger().log(Level.SEVERE, "\t" + command);
            e.printStackTrace();
        }
    }
}
