package com.nurujjamanpollob.contextseven.eazeeditorplugin;

import com.nurujjamanpollob.contextseven.eazeeditorplugin.mcp.Context7MCPHandler;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.settings.Context7SettingsHandler;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.utilities.PluginLogger;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main plugin class for the Context7 Documentation Finder.
 * Coordinates between documentation search, settings management, and tool components.
 * 
 * @author Nurujjaman Pollob
 * @version 1.0
 */
public class Context7Plugin {

    private static Context7Plugin instance;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private boolean active = false;

    private Context7MCPHandler mcpHandler;

    public Context7Plugin() {
        instance = this;
    }
    private Context7SettingsHandler settingsHandler;

    /**
     * Gets the singleton instance of the plugin.
     * 
     * @return The plugin instance
     */
    public static Context7Plugin getInstance() {
        if (instance == null) {
            instance = new Context7Plugin();
        }
        return instance;
    }

    /**
     * Initializes the plugin.
     * This is called by the editor when the plugin is loaded.
     */
    public void initialize() {
        initialize(false);
    }

    /**
     * Initializes the plugin with optional standalone mode.
     * 
     * @param standaloneMode If true, the plugin will run without editor integration
     */
    public void initialize(boolean standaloneMode) {
        if (initialized.compareAndSet(false, true)) {
            PluginLogger.info("Initializing Context7 Documentation Plugin", Context7Plugin.class);
            
            if (!standaloneMode) {


                settingsHandler = new Context7SettingsHandler();
                // Initialize MCP handler
                this.mcpHandler = new Context7MCPHandler();

                // Initialize MCP endpoints
                this.mcpHandler.initializeEndpoints();
                
                this.active = true;
            }
            
            PluginLogger.info("Context7 Documentation Plugin initialized", Context7Plugin.class);
        }
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Get the mcp handler
     */
    public Context7MCPHandler getMCPHandler() {
        return mcpHandler;
    }
    public Context7SettingsHandler getSettingsHandler() {
        return settingsHandler;
    }
}
