package com.nurujjamanpollob.contextseven.eazeeditorplugin.utilities;

import com.eazeeditor.core.pluginmodule.api.PluginApi;

/**
 * Centralized logging utility for the Context7 Documentation Finder plugin.
 * Provides logging through the PluginApi when SDK is available, with fallback to
 * standard logging when running in standalone mode.
 * 
 * @author Nurujjaman Pollob
 * @version 1.0
 */
public final class PluginLogger {

    private static boolean sdkAvailable = false;
    
    static {
        try {
            // Check if SDK is available at runtime
            Class.forName("com.eazeeditor.core.pluginmodule.api.PluginApi");
            sdkAvailable = true;
        } catch (ClassNotFoundException e) {
            sdkAvailable = false;
        }
    }

    private PluginLogger() {
        // Prevent instantiation
    }

    /**
     * Logs a debug message.
     * 
     * @param message The message to log
     * @param clazz The class from which the message originates
     */
    public static void debug(String message, Class<?> clazz) {
        if (sdkAvailable) {
            PluginApi.debug(message, clazz);
        } else {
            System.out.println("[DEBUG] [" + getSimpleClassName(clazz) + "] " + message);
        }
    }

    /**
     * Logs an info message.
     * 
     * @param message The message to log
     * @param clazz The class from which the message originates
     */
    public static void info(String message, Class<?> clazz) {
        if (sdkAvailable) {
            PluginApi.info(message, clazz);
            PluginApi.notifyHost(message, clazz);
        } else {
            System.out.println("[INFO] [" + getSimpleClassName(clazz) + "] " + message);
        }
    }

    /**
     * Logs a warning message.
     * 
     * @param message The message to log
     * @param clazz The class from which the message originates
     */
    public static void warn(String message, Class<?> clazz) {
        if (sdkAvailable) {
            PluginApi.warn(message, clazz);
            PluginApi.notifyHost(message, clazz);
        } else {
            System.out.println("[WARN] [" + getSimpleClassName(clazz) + "] " + message);
        }
    }

    /**
     * Logs an error message with an exception.
     * 
     * @param message The error message
     * @param throwable The exception that occurred
     * @param clazz The class from which the error originates
     */
    public static void error(String message, Throwable throwable, Class<?> clazz) {
        if (sdkAvailable) {
            PluginApi.error(message, clazz, throwable);
            PluginApi.notifyHost(message, clazz);
        } else {
            System.err.println("[ERROR] [" + getSimpleClassName(clazz) + "] " + message);
            if (throwable != null) {
                throwable.printStackTrace(System.err);
            }
        }
    }

    /**
     * Logs an error message without exception.
     * 
     * @param message The error message
     * @param clazz The class from which the error originates
     */
    public static void error(String message, Class<?> clazz) {
        error(message, null, clazz);
    }

    /**
     * Checks if the SDK logging is available.
     * 
     * @return true if SDK is available, false otherwise
     */
    public static boolean isSdkAvailable() {
        return sdkAvailable;
    }

    /**
     * Gets a simplified class name without package.
     */
    private static String getSimpleClassName(Class<?> clazz) {
        if (clazz == null) return "Unknown";
        String name = clazz.getName();
        int lastDot = name.lastIndexOf('.');
        return lastDot > 0 ? name.substring(lastDot + 1) : name;
    }
}
