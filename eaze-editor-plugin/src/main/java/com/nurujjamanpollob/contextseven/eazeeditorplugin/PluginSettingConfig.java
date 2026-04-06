package com.nurujjamanpollob.contextseven.eazeeditorplugin;

import com.eazeeditor.core.pluginmodule.annotation.PluginSettingsDiscovery;

@PluginSettingsDiscovery(
        pluginName = Vars.PLUGIN_NAME,
        description = Vars.PLUGIN_DESCRIPTION,
        displayName = Vars.PLUGIN_DISPLAY_NAME,
        href = "" // Let the SDK handle the URL generation for settings
)
public class PluginSettingConfig {
    // This class is intentionally left empty as the @PluginSettingsDiscovery annotation
    // is used to register the plugin settings with the editor. The actual settings
    // handling is done in Context7SettingsHandler.
}
