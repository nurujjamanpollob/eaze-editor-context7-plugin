package com.nurujjamanpollob.contextseven.eazeeditorplugin;

/**
 * Global constants and configuration variables for the Context7 Documentation plugin.
 * 
 * @author Nurujjaman Pollob
 * @version 1.0
 */
public final class Vars {
    
    // Plugin Information
    public static final String PLUGIN_NAME = "context7";
    public static final String PLUGIN_DISPLAY_NAME = "Context7 Documentation";
    public static final String PLUGIN_VERSION = "1.0.0";
    public static final String PLUGIN_AUTHOR = "Nurujjaman Pollob";
    public static final String PLUGIN_DESCRIPTION = 
        "Context7 documentation lookup and search integration for AI-assisted development";
    
    // Resource Paths
    public static final String RESOURCE_BASE_PATH = "context7/";
    public static final String SETTINGS_HTML_PATH = RESOURCE_BASE_PATH + "settings.html";
    public static final String ABOUT_HTML_PATH = RESOURCE_BASE_PATH + "about.html";
    
    // Settings Keys
    public static final String SETTING_ENABLED = PLUGIN_NAME + ".enabled";
    public static final String SETTING_API_KEY = PLUGIN_NAME + ".api.key";
    public static final String SETTING_BASE_URL = PLUGIN_NAME + ".api.base_url";
    public static final String SETTING_DEFAULT_LIBRARY = PLUGIN_NAME + ".default_library";
    public static final String SETTING_SEARCH_TIMEOUT = PLUGIN_NAME + ".search_timeout";
    public static final String SETTING_MAX_RESULTS = PLUGIN_NAME + ".maxResults";
    
    // Default Settings
    public static final boolean DEFAULT_ENABLED = true;
    public static final String DEFAULT_API_KEY = "";
    public static final String DEFAULT_BASE_URL = "https://context7.com/api/v2";
    public static final String DEFAULT_LIBRARY = "open-source-libraries";
    public static final int DEFAULT_SEARCH_TIMEOUT = 30;
    public static final int DEFAULT_MAX_RESULTS = 10;
    
    private Vars() {
        // Prevent instantiation
    }
}
