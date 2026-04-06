package com.nurujjamanpollob.contextseven.eazeeditorplugin.settings;

import com.eazeeditor.core.pluginmodule.api.PluginApi;
import com.eazeeditor.core.pluginmodule.api.PluginSettingRegistration;
import com.eazeeditor.core.pluginmodule.core.settingssdk.server.BaseSettingsHandler;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.Vars;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Settings handler for the Context7 Documentation Plugin.
 * Manages the settings UI and API endpoints for configuration.
 */
public class Context7SettingsHandler extends BaseSettingsHandler {

    public Context7SettingsHandler() {
        super(Vars.PLUGIN_NAME);
        
        // Register the settings handler with the editor
        PluginApi.registerSettingsHandler(Vars.PLUGIN_NAME, this);
        
        // Register the plugin settings in the editor's settings registry
        PluginApi.registerSettings(new PluginSettingRegistration(
                Vars.PLUGIN_NAME,
                Vars.PLUGIN_DESCRIPTION,
                Vars.PLUGIN_DISPLAY_NAME,
                null // Let the SDK handle the URL generation for settings
        ));
    }

    @Override
    protected void handleCustomRoute(HttpExchange exchange, String method, String path) throws IOException {
        if (path.equals("/settings") && "GET".equalsIgnoreCase(method)) {
            handleResourceRequest(exchange, "settings.html");
        } else if (path.equals("/about") && "GET".equalsIgnoreCase(method)) {
            handleResourceRequest(exchange, "about.html");
        } else if (path.equals("/script.js") && "GET".equalsIgnoreCase(method)) {
            handleResourceRequest(exchange, "script.js");
        } else if (path.equals("/tailwind.cdn.js") && "GET".equalsIgnoreCase(method)) {
            handleResourceRequest(exchange, "tailwind.cdn.js");
        } else if (path.equals("/api/get-settings") && "GET".equalsIgnoreCase(method)) {
            handleGetSettings(exchange);
        } else if (path.equals("/api/save-settings") && "POST".equalsIgnoreCase(method)) {
            handleSaveSettings(exchange);
        } else {
            super.handleCustomRoute(exchange, method, path);
        }
    }

    private void handleGetSettings(HttpExchange exchange) throws IOException {
        Map<String, Object> settings = new HashMap<>();

        settings.put("enabled", getBooleanSetting(Vars.SETTING_ENABLED, Vars.DEFAULT_ENABLED));
        settings.put("apiKey", getStringSetting(Vars.SETTING_API_KEY, Vars.DEFAULT_API_KEY));
        settings.put("baseUrl", getStringSetting(Vars.SETTING_BASE_URL, Vars.DEFAULT_BASE_URL));
        settings.put("defaultLibrary", getStringSetting(Vars.SETTING_DEFAULT_LIBRARY, Vars.DEFAULT_LIBRARY));
        settings.put("searchTimeout", getIntSetting(Vars.SETTING_SEARCH_TIMEOUT, Vars.DEFAULT_SEARCH_TIMEOUT));
        settings.put("maxResults", getIntSetting(Vars.SETTING_MAX_RESULTS, Vars.DEFAULT_MAX_RESULTS));

        sendJsonResponse(exchange, 200, settings);
    }

    private boolean getBooleanSetting(String key, boolean defaultValue) {
        String val = SettingAPI.getSettingValue(Vars.PLUGIN_NAME, key);
        if (val == null) return defaultValue;
        return Boolean.parseBoolean(val);
    }

    private String getStringSetting(String key, String defaultValue) {
        String val = SettingAPI.getSettingValue(Vars.PLUGIN_NAME, key);
        if (val == null) return defaultValue;
        return val;
    }

    private int getIntSetting(String key, int defaultValue) {
        String val = SettingAPI.getSettingValue(Vars.PLUGIN_NAME, key);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void handleSaveSettings(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            
            // Extract and save settings
            updateBooleanSetting(body, "enabled", Vars.SETTING_ENABLED);
            updateStringSetting(body, "apiKey", Vars.SETTING_API_KEY);
            updateStringSetting(body, "baseUrl", Vars.SETTING_BASE_URL);
            updateStringSetting(body, "defaultLibrary", Vars.SETTING_DEFAULT_LIBRARY);
            updateNumericSetting(body, "searchTimeout", Vars.SETTING_SEARCH_TIMEOUT);
            updateNumericSetting(body, "maxResults", Vars.SETTING_MAX_RESULTS);

            sendJsonResponse(exchange, 200, Map.of("status", "success"));
        } catch (Exception e) {
            sendJsonResponse(exchange, 500, Map.of("status", "error", "message", e.getMessage()));
        }
    }

    private void updateBooleanSetting(String body, String key, String configKey) {
        if (body.contains("\"" + key + "\":true")) {
            SettingAPI.putSettingValue(Vars.PLUGIN_NAME, configKey, "true");
        } else if (body.contains("\"" + key + "\":false")) {
            SettingAPI.putSettingValue(Vars.PLUGIN_NAME, configKey, "false");
        }
    }

    private void updateStringSetting(String body, String key, String configKey) {
        String pattern = "\"" + key + "\":\"";
        int start = body.indexOf(pattern);
        if (start != -1) {
            start += pattern.length();
            int end = body.indexOf("\"", start);
            if (end != -1) {
                String val = body.substring(start, end);
                // Unescape backslashes and quotes
                val = val.replace("\\\\", "\\").replace("\\\"", "\"");
                SettingAPI.putSettingValue(Vars.PLUGIN_NAME, configKey, val);
            }
        }
    }

    private void updateNumericSetting(String body, String key, String configKey) {
        String pattern = "\"" + key + "\":";
        int start = body.indexOf(pattern);
        if (start != -1) {
            start += pattern.length();
            int end = body.indexOf(",", start);
            if (end == -1) end = body.indexOf("}", start);
            if (end != -1) {
                try {
                    String valStr = body.substring(start, end).trim();
                    Integer.parseInt(valStr); // just to validate
                    SettingAPI.putSettingValue(Vars.PLUGIN_NAME, configKey, valStr);
                } catch (NumberFormatException ignored) {}
            }
        }
    }
}
