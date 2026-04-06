package com.nurujjamanpollob.contextseven.eazeeditorplugin.lifecycle;

import com.eazeeditor.core.pluginmodule.annotation.PluginLifeCycle;
import com.eazeeditor.core.pluginmodule.annotation.PluginSettingsDiscovery;
import com.eazeeditor.core.pluginmodule.api.LifecyclePhase;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.Context7Plugin;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.Vars;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.settings.Context7SettingsHandler;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.utilities.PluginLogger;

/**
 * Handles plugin lifecycle events for the Context7 Documentation Plugin.
 * 
 * @author Nurujjaman Pollob
 * @version 1.0
 */
public class Context7LifecycleObserver {

    @PluginLifeCycle(LifecyclePhase.ON_LOAD)
    public void onPluginStart() {
        PluginLogger.info("Context7 Documentation Plugin starting...", Context7LifecycleObserver.class);

        //new Context7SettingsHandler(); // important for setting screen creation
        // Initialize the plugin singleton
        Context7Plugin.getInstance().initialize();
        
        PluginLogger.info("Context7 Documentation Plugin started successfully.", Context7LifecycleObserver.class);
    }

    @PluginLifeCycle(LifecyclePhase.ON_CLOSE)
    public void onPluginStop() {
        PluginLogger.info("Context7 Documentation Plugin stopping...", Context7LifecycleObserver.class);
        // Perform any cleanup if needed
    }

    @PluginLifeCycle(LifecyclePhase.ON_ERROR)
    public void onPluginError() {
        PluginLogger.error("An error occurred in the Context7 Documentation Plugin lifecycle.", Context7LifecycleObserver.class);
    }
}
