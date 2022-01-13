package com.tyrengard.unbound.jobs.gui;

import com.tyrengard.aureycore.customguis.Button;
import com.tyrengard.aureycore.customguis.Information;
import com.tyrengard.aureycore.foundation.common.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum JobProfileTab {
    JOBS(Material.CARTOGRAPHY_TABLE),
    QUESTS(Material.KNOWLEDGE_BOOK),
    NEWS(Material.LECTERN),
    SETTINGS(Material.COMPARATOR);

    private final Material icon;
    private final String title;

    JobProfileTab(Material icon) {
        this.icon = icon;
        this.title = StringUtils.toTitleCase(this.name());
    }

    public ItemStack getTabItem(boolean active) {
        if (active) {
            return new Information(icon, true, ChatColor.WHITE + title);
        } else {
            return new Button<JobProfileGUI>(icon,
                    ChatColor.YELLOW + "Click to view " + ChatColor.WHITE + title, (gui, e, b) -> {
                gui.navigateToTab(this);
            });
        }
    }
}
