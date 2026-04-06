package com.nurujjamanpollob.contextseven.eazeeditorplugin.core.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class for Context7 API.
 * Handles loading the API key from multiple sources with the following priority:
 * 1. Programmatic (passed to the client)
 * 2. .env file (CONTEXT7_API_KEY)
 * 3. context7.conf file in resources (context7.api.key)
 * 4. System Environment Variables (CONTEXT7_API_KEY)
 */
@Slf4j
public class Config {

    @Getter
    private String apiKey;
    
    @Getter
    private String baseUrl = "https://context7.com/api";

    private static final String CONF_FILE = "context7.conf";
    private static final String ENV_KEY = "CONTEXT7_API_KEY";
    private static final String ENV_BASE_URL_KEY = "CONTEXT7_API_BASE_URL";
    private static final String CONF_KEY = "context7.api.key";
    private static final String CONF_BASE_URL_KEY = "context7.api.base_url";

    public Config() {
        this.apiKey = loadApiKey();
        this.baseUrl = loadBaseUrl();
    }

    public Config(String programmaticApiKey) {
        this.apiKey = programmaticApiKey != null ? programmaticApiKey : loadApiKey();
        this.baseUrl = loadBaseUrl();
    }

    private String loadApiKey() {
        // 1. Try .env file
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
            String envKey = dotenv.get(ENV_KEY);
            if (envKey != null && !envKey.isEmpty() && !envKey.equals("YOUR_API_KEY_HERE")) {
                log.debug("API key loaded from .env file.");
                return envKey;
            }
        } catch (Exception e) {
            log.warn("Failed to load .env file: {}", e.getMessage());
        }

        // 2. Try context7.conf from resources
        Properties props = loadProperties();
        String confKey = props.getProperty(CONF_KEY);
        if (confKey != null && !confKey.isEmpty() && !confKey.equals("YOUR_API_KEY_HERE")) {
            log.debug("API key loaded from {}.", CONF_FILE);
            return confKey;
        }

        // 3. Try System Environment Variables
        String sysEnvKey = System.getenv(ENV_KEY);
        if (sysEnvKey != null && !sysEnvKey.isEmpty()) {
            log.debug("API key loaded from system environment variables.");
            return sysEnvKey;
        }

        log.warn("No API key found in .env, {}, or environment variables.", CONF_FILE);
        return null;
    }

    private String loadBaseUrl() {
        // 1. Try .env file
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
            String envUrl = dotenv.get(ENV_BASE_URL_KEY);
            if (envUrl != null && !envUrl.isEmpty()) {
                return envUrl;
            }
        } catch (Exception ignored) {}

        // 2. Try context7.conf
        Properties props = loadProperties();
        String confUrl = props.getProperty(CONF_BASE_URL_KEY);
        if (confUrl != null && !confUrl.isEmpty()) {
            return confUrl;
        }

        return baseUrl; // Default
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONF_FILE)) {
            if (input != null) {
                props.load(input);
            } else {
                log.debug("{} not found in resources.", CONF_FILE);
            }
        } catch (IOException ex) {
            log.error("Error loading {}: {}", CONF_FILE, ex.getMessage());
        }
        return props;
    }
}
