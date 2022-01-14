package com.tyrengard.unbound.jobs.quests.impl;

import com.tyrengard.unbound.jobs.quests.JobQuestReward;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PayQuestReward implements JobQuestReward {
    private final double payAmount;

    public PayQuestReward(double payAmount) {
        this.payAmount = payAmount;
    }

    @Override
    public void awardToPlayer(Player p) {
        RegisteredServiceProvider<Economy> ecoRSP = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (ecoRSP != null)
            ecoRSP.getProvider().depositPlayer(p, payAmount);
    }
}
