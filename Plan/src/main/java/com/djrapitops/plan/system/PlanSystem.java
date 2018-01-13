/*
 * Licence is provided in the jar as license.yml also here:
 * https://github.com/Rsl1122/Plan-PlayerAnalytics/blob/master/Plan/src/main/resources/license.yml
 */
package com.djrapitops.plan.system;

import com.djrapitops.plan.api.exceptions.EnableException;
import com.djrapitops.plan.system.database.DBSystem;
import com.djrapitops.plan.system.file.FileSystem;
import com.djrapitops.plan.system.listeners.ListenerSystem;
import com.djrapitops.plan.system.processing.ProcessingQueue;
import com.djrapitops.plan.system.settings.config.ConfigSystem;
import com.djrapitops.plan.system.update.VersionCheckSystem;
import com.djrapitops.plan.utilities.NullCheck;
import com.djrapitops.plugin.api.Check;

/**
 * PlanSystem contains everything Plan needs to run.
 * <p>
 * This is an abstraction layer on top of Plugin instances so that tests can be run with less mocks.
 *
 * @author Rsl1122
 */
public abstract class PlanSystem implements SubSystem {

    // Initialized in this class
    protected final ProcessingQueue processingQueue;

    // These need to be initialized in the sub class.
    protected VersionCheckSystem versionCheckSystem;
    protected FileSystem fileSystem;
    protected ConfigSystem configSystem;
    protected DBSystem databaseSystem;

    protected ListenerSystem listenerSystem;

    public PlanSystem() {
        processingQueue = new ProcessingQueue();
    }

    @Override
    public void enable() throws EnableException {
        checkSubSystemInitialization();

        versionCheckSystem.enable();
        fileSystem.enable();
        configSystem.enable();
        databaseSystem.enable();
        processingQueue.enable();
        listenerSystem.enable();
    }

    @Override
    public void disable() {
        processingQueue.disable();
        databaseSystem.disable();
        configSystem.disable();
        fileSystem.disable();
        versionCheckSystem.disable();
    }

    public void reload() throws EnableException {
        checkSubSystemInitialization();
        configSystem.reload();
    }

    private void checkSubSystemInitialization() throws EnableException {
        try {
            NullCheck.check(versionCheckSystem, new IllegalStateException("Version Check system was not initialized."));
            NullCheck.check(fileSystem, new IllegalStateException("File system was not initialized."));
            NullCheck.check(configSystem, new IllegalStateException("Config system was not initialized."));
            NullCheck.check(databaseSystem, new IllegalStateException("Database system was not initialized."));
            NullCheck.check(listenerSystem, new IllegalStateException("Listener system was not initialized."));
        } catch (Exception e) {
            throw new EnableException("One of the subsystems is not initialized on enable for " + this.getClass().getSimpleName() + ".", e);
        }
    }

    public static PlanSystem getInstance() {
        boolean bukkitAvailable = Check.isBukkitAvailable();
        boolean bungeeAvailable = Check.isBungeeAvailable();
        if (bukkitAvailable && bungeeAvailable) {
            // TODO test system.
        } else if (bungeeAvailable) {
            return BungeeSystem.getInstance();
        } else {
            return BukkitSystem.getInstance();
        }
        throw new IllegalAccessError("PlanSystem is not available on this platform.");
    }

    // Accessor methods.

    public ProcessingQueue getProcessingQueue() {
        return processingQueue;
    }

    public VersionCheckSystem getVersionCheckSystem() {
        return versionCheckSystem;
    }

    public ConfigSystem getConfigSystem() {
        return configSystem;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public DBSystem getDatabaseSystem() {
        return databaseSystem;
    }

    public ListenerSystem getListenerSystem() {
        return listenerSystem;
    }
}