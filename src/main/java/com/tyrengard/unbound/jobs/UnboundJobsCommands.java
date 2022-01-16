package com.tyrengard.unbound.jobs;

import com.tyrengard.aureycore.guis.CustomGUIManager;
import com.tyrengard.aureycore.foundation.ACommandExecutor;
import com.tyrengard.aureycore.foundation.CommandDeclaration;
import com.tyrengard.aureycore.foundation.common.stringformat.ChatFormat;
import com.tyrengard.aureycore.foundation.common.utils.PlayerUtils;
import com.tyrengard.aureycore.foundation.common.utils.StringUtils;
import com.tyrengard.unbound.jobs.gui.JobListGUI;
import com.tyrengard.unbound.jobs.gui.JobProfileGUI;
import com.tyrengard.unbound.jobs.gui.JobProfileTab;
import com.tyrengard.unbound.jobs.quests.JobQuestType;
import com.tyrengard.unbound.jobs.workers.Worker;
import com.tyrengard.unbound.jobs.workers.WorkerManager;
import com.tyrengard.unbound.jobs.workers.enums.BossBarExpIndicatorSetting;
import com.tyrengard.unbound.jobs.workers.enums.ProfileVisibility;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class UnboundJobsCommands extends ACommandExecutor {
    static final String adminBaseCommandString = "unbound-jobs-admin";
    static final String baseCommandString = "unbound-jobs";

    private static final String SEPARATOR_LEFT = "\u297C", SEPARATOR_RIGHT = "\u297D";
    private static final String UNBOUND_HELP_HEADER = ChatColor.DARK_PURPLE + SEPARATOR_LEFT
            + StringUtils.padString(ChatColor.AQUA + "Unbound Jobs - Commands" + ChatColor.DARK_PURPLE, '-',
            StringUtils.MAX_CHAT_SIZE, StringUtils.StringPaddingOptions.CENTER) + SEPARATOR_RIGHT;

    public UnboundJobsCommands(FileConfiguration config) {
        super();

        Bukkit.getLogger().info("Adding /unbound-jobs-admin commands...");
        addAdminCommands();
        Bukkit.getLogger().info("Adding /unbound-jobs commands...");

        boolean jobListGUIEnabled = true, playersCanJoinJobsViaListGUI = true,
                jobProfileGUIEnabled = true, allowPublicProfiles = true;
        ConfigurationSection commandsConfig = config.getConfigurationSection("commands");
        if (commandsConfig != null) {
            // region /unbound-jobs list configs
            ConfigurationSection jobListConfig = commandsConfig.getConfigurationSection("job-list");
            if (jobListConfig != null) {
                jobListGUIEnabled = jobListConfig.getBoolean("enabled", true);
                playersCanJoinJobsViaListGUI = jobListConfig.getBoolean("join-jobs", true);
            }
            // endregion
            // region /unbound-jobs profile|quests|news|settings configs
            ConfigurationSection jobProfileConfig = commandsConfig.getConfigurationSection("job-profile");
            if (jobProfileConfig != null) {
                jobProfileGUIEnabled = jobProfileConfig.getBoolean("enabled", true);
                allowPublicProfiles = jobProfileConfig.getBoolean("allow-public-profiles", true);
            }
            // endregion
        }

        addJobProfileCommands(jobProfileGUIEnabled, allowPublicProfiles);
        addJobListCommands(jobListGUIEnabled, playersCanJoinJobsViaListGUI);
        addNonGUICommands();

        createHelpCommands(UNBOUND_HELP_HEADER, ChatColor.DARK_PURPLE, ChatColor.AQUA);
    }

    public void addAdminCommands() {
        // /unbound-jobs-admin reload
        addRegularCommand(new CommandDeclaration<>(false, adminBaseCommandString,
                "reload", "Reload all UnboundJobs configs", new String[0], (sender, args) -> {
            sender.sendMessage("[Unbound Jobs] Reloading plugin...");
            UnboundJobs.getInstance().reloadPlugin();
            return true;
        }));

        // /unbound-jobs-admin jobs
        addRegularCommand(new CommandDeclaration<>(adminBaseCommandString,
                "jobs", null, new String[0]));

        // /unbound-jobs-admin jobs reload
        addRegularCommand(new CommandDeclaration<>(false, adminBaseCommandString,
                "jobs reload", "Reload jobs", new String[0], (sender, args) -> {
            sender.sendMessage("[Unbound Jobs] Reloading jobs...");
            JobManager.loadJobConfigFiles();
            sender.sendMessage("[Unbound Jobs] Jobs reloaded.");
            return true;
        }));

        // /unbound-jobs-admin reset-player <player-name>
        addRegularCommand(new CommandDeclaration<>(true, adminBaseCommandString,
                "reset-player", "Reset a player", new String[0], (sender, args) -> {
            if (args.length == 3 && args[2].equalsIgnoreCase("confirm")) {
                String playerName = args[1];
                OfflinePlayer offlinePlayer = PlayerUtils.getOfflinePlayer(playerName);
                if (offlinePlayer == null)
                    sender.sendMessage("The player " + playerName + " was not found.");
                else
                    WorkerManager.createNewWorker(offlinePlayer.getUniqueId());

                sender.sendMessage("Unbound Jobs player data for " + playerName + " was successfully reset.");
                return true;
            } else if (args.length == 2) {
                String playerName = args[1];
                String confirmCommand = "/unbound-jobs-admin reset-player " + playerName + " confirm";
                OfflinePlayer offlinePlayer = PlayerUtils.getOfflinePlayer(playerName);
                if (offlinePlayer == null)
                    sender.sendMessage("The player " + playerName + " was not found.");
                else if (sender instanceof Player playerSender) {
                    TextComponent clickable = new TextComponent("here");
                    clickable.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
                    clickable.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/unbound-jobs-admin reset-player " + playerName + " confirm"));
                    clickable.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new Text("/unbound-jobs-admin reset-player " + playerName + " confirm")));

                    TextComponent tc = new TextComponent("Click ");
                    tc.addExtra(clickable);
                    tc.addExtra(" to confirm your command.");

                    playerSender.spigot().sendMessage(tc);
                } else
                    sender.sendMessage("Type \"" + confirmCommand + "\" to confirm your command.");

                return true;
            }
            else
                return false;
        }));
    }

    private void addJobProfileCommands(boolean jobProfileGUIEnabled, boolean allowPublicProfiles) {
        // region Job profile GUI
        // /unbound-jobs profile
        if (jobProfileGUIEnabled) {
            addPlayerCommand(new CommandDeclaration<>(true, baseCommandString,
                    "profile", "Open job profile", new String[0], (p, args) -> {
                Player target = p;
                if (args.length > 1) {
                    String targetName = args[1];
                    target = Bukkit.getPlayerExact(targetName);
                    if (target != p) {
                        if (target == null) {
                            p.sendMessage(ChatColor.RED + "There is no player named " + targetName + " who is online.");
                            return true;
                        } else if (!allowPublicProfiles) {
                            p.sendMessage(ChatColor.RED + "Public profiles are disabled for this server.");
                            return true;
                        } else if (WorkerManager.obtainWorker(target.getUniqueId()).getProfileVisibility() == ProfileVisibility.PRIVATE) {
                            p.sendMessage(ChatColor.RED + "That player's profile is private.");
                            return true;
                        }
                    }
                }
                CustomGUIManager.openGUI(new JobProfileGUI(target, JobProfileTab.JOBS), p);
                return true;
            }));

            // /unbound-jobs quests
            addPlayerCommand(new CommandDeclaration<>(true, baseCommandString,
                    "quests", "Open job quests", new String[0], (p, args) -> {
                CustomGUIManager.openGUI(new JobProfileGUI(p, JobProfileTab.QUESTS), p);
                return true;
            }));

            // /unbound-jobs news
            addPlayerCommand(new CommandDeclaration<>(true, baseCommandString,
                    "news", "Open job news", new String[0], (p, args) -> {
                CustomGUIManager.openGUI(new JobProfileGUI(p, JobProfileTab.NEWS), p);
                return true;
            }));

            // /unbound-jobs settings
            addPlayerCommand(new CommandDeclaration<>(true, baseCommandString,
                    "settings", "Open job settings", new String[0], (p, args) -> {
                CustomGUIManager.openGUI(new JobProfileGUI(p, JobProfileTab.SETTINGS), p);
                return true;
            }));
        }
        // endregion
    }

    private void addJobListCommands(boolean jobListGUIEnabled, boolean playersCanJoinJobsViaListGUI) {
        // /unbound-jobs list
        if (jobListGUIEnabled)
            addPlayerCommand(new CommandDeclaration<>(false, baseCommandString,
                    "list", "Open up a list of available jobs", new String[0], (p, args) -> {
                CustomGUIManager.openGUI(new JobListGUI(playersCanJoinJobsViaListGUI), p);
                return true;
            }));
    }

    private void addNonGUICommands() {
        // TEMPORARY REROLL QUESTS
        addPlayerCommand(new CommandDeclaration<>(true, baseCommandString,
                "reroll-quests", null, new String[0], (p, args) -> {
            Worker worker = WorkerManager.obtainWorker(p.getUniqueId());
            WorkerManager.refreshQuests(worker, JobQuestType.DAILY);
            WorkerManager.refreshQuests(worker, JobQuestType.WEEKLY);
            p.sendMessage("Quests refreshed.");
            return true;
        }));

        // /unbound-jobs boss-bar-exp
        addPlayerCommand(new CommandDeclaration<>(baseCommandString,
                "boss-bar-exp", "Change boss bar experience indicator behavior", new String[] {
                ChatFormat.color(ChatColor.YELLOW, "default") + "  - Show the boss bar experience indicator every time experience is gained",
                ChatFormat.color(ChatColor.YELLOW, "hidden") + "   - Never show the boss bar experience indicator",
                ChatFormat.color(ChatColor.YELLOW, "leveling") + "  - Show the boss bar experience indicator whenever job experience reaches " +
                        "80% or higher of required experience for next level"
        }));
        for (BossBarExpIndicatorSetting setting : BossBarExpIndicatorSetting.values()) {
            addPlayerCommand(new CommandDeclaration<>(true, baseCommandString,
                    "settings boss-bar-exp " + setting.toString().toLowerCase(), (p, args) -> {
                Worker w = WorkerManager.obtainWorker(p.getUniqueId());
                w.setBossBarExpIndicatorSetting(setting);
                p.sendMessage("Set boss bar experience indicator behavior to " + setting.toString().toLowerCase());
                return true;
            }));
        }

        // /unbound-jobs join <job-name>
        addPlayerCommand(new CommandDeclaration<>(true, baseCommandString, "join",
                "Join a job", new String[0],
                (p, args) -> {
            if (!p.hasPermission("unbound.jobs.join"))
                return false;

            Job job = JobManager.getJob(args[1]);
            if (job == null)
                p.sendMessage(ChatColor.RED + "That job doesn't exist.");
            else {
                Worker worker = WorkerManager.obtainWorker(p.getUniqueId());
                if (worker.hasJob(job))
                    p.sendMessage(ChatColor.RED + "You already have that job.");
                else
                    WorkerManager.addJobToPlayer(p, job);
            }
            return true;
        }));
    }
}
