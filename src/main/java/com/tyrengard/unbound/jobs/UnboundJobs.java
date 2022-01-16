package com.tyrengard.unbound.jobs;

import com.tyrengard.aureycore.foundation.AManagedPlugin;
import com.tyrengard.aureycore.foundation.db.MongoDBDatabaseManager;
import com.tyrengard.unbound.jobs.actions.impl.DefaultActionListener;
import com.tyrengard.unbound.jobs.workers.WorkerManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.InvalidConfigurationException;

public class UnboundJobs extends AManagedPlugin {
    private static UnboundJobs instance;
    private Economy economy;

    public static UnboundJobs getInstance() {
        return instance;
    }

    @Override
    protected void onPluginEnable() throws InvalidConfigurationException {
        instance = this;

        setDatabaseManager(new MongoDBDatabaseManager<>(this));
        addManager(new TaskManager(this));
        addManager(new JobManager(this));
        addManager(new WorkerManager(this));
        registerListener(new DefaultActionListener());

        addACommandExecutor(new UnboundJobsCommands(getConfig()), "unbound-jobs", "unbound-jobs-admin");
    }

    @Override
    protected void onPostEnable() {
//        setDebugLogging(false);
    }

    @Override
    protected void onPluginDisable() {
        
    }

    public void reloadPlugin() {
        this.reload();
    }
}
