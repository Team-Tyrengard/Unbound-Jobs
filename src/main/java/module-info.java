module com.tyrengard.unbound.jobs {
    requires org.bukkit;
    requires org.jetbrains.annotations;
    requires VaultAPI;
    requires com.tyrengard.aureycore.foundation;
    requires com.tyrengard.aureycore.customguis;
    requires javaluator;
    requires morphia.core;
    requires bungeecord.chat;
    requires java.logging;

    exports com.tyrengard.unbound.jobs;
    exports com.tyrengard.unbound.jobs.events;
    exports com.tyrengard.unbound.jobs.exceptions;
    exports com.tyrengard.unbound.jobs.actions;
    exports com.tyrengard.unbound.jobs.quests;
}