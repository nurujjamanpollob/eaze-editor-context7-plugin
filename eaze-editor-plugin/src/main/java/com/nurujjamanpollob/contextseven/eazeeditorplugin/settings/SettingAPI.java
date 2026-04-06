package com.nurujjamanpollob.contextseven.eazeeditorplugin.settings;

import com.eazeeditor.core.pluginmodule.core.settingssdk.DefaultPluginSettings;
import com.eazeeditor.core.pluginmodule.util.EncryptionService;

import java.util.Optional;

/**
 * Provides API for plugin settings. It calls the SDK to get and save setting data securely.
 */
public class SettingAPI {

    /**
     * Get a setting value for a plugin by key securely.
     * @param pluginName the plugin name
     * @param key the setting key
     * @return the setting value or null if not found
     */
    public static String getSettingValue(String pluginName, String key) {
        EncryptionService encryptionService = new EncryptionService();
        DefaultPluginSettings defaultPluginSettings = new DefaultPluginSettings(pluginName, encryptionService);
        Optional<String> settingValue = defaultPluginSettings.getSetting(key);
        return settingValue.orElse(null);
    }

    /**
     * Put a setting value for a plugin by key. The value is encrypted when stored.
     * @param pluginName the plugin name
     * @param key the setting key
     * @param value the setting value
     */
    public static void putSettingValue(String pluginName, String key, String value) {
        EncryptionService encryptionService = new EncryptionService();
        DefaultPluginSettings defaultPluginSettings = new DefaultPluginSettings(pluginName, encryptionService);
        defaultPluginSettings.saveSetting(key, value);
    }
}

