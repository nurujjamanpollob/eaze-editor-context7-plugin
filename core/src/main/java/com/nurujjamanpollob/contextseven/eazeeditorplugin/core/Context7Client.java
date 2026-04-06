package com.nurujjamanpollob.contextseven.eazeeditorplugin.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.core.config.Config;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.core.exceptions.Context7ApiException;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.core.models.ContextResponse;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.core.models.ErrorResponse;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.core.models.LibrarySearchResponse;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.core.models.LibrarySearchResult;
import com.eazeeditor.core.pluginmodule.configtool.ConfigManager;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Context7 API Client.
 * Handles authentication and communication with the Context7 API.
 * 
 * <p>This client provides methods to:
 * <ul>
 *   <li>Search for library documentation</li>
 *   <li>Retrieve context snippets for specific libraries</li>
 *   <li>Check API configuration status</li>
 * </ul>
 *
 * @author Nurujjaman Pollob
 * @since 1.0.0
 */
@Slf4j
public class Context7Client {
    
    /** The API key for authentication */
    private final String apiKey;
    
    /** The base URL for the Context7 API */
    private final String baseUrl;
    
    /** The default library to search in */
    private final String defaultLibrary;
    
    /** HTTP client for making requests */
    private final OkHttpClient httpClient;
    
    /** JSON object mapper */
    private final ObjectMapper objectMapper;

    /**
     * Initializes the client using the default configuration (loads from .env, context7.conf, or env variables).
     */
    public Context7Client() {
        this.apiKey = ConfigManager.getProperty("context7.api.key", null);
        this.baseUrl = ConfigManager.getProperty("context7.api.base_url", "https://context7.com/api/v2");
        this.defaultLibrary = ConfigManager.getProperty("context7.default_library", "open-source-libraries");
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        log.info("Context7Client initialized with ConfigManager");
    }

    /**
     * Initializes the client with a programmatically provided API key.
     * If the provided key is null, it falls back to the default configuration.
     * 
     * @param apiKey The API key to use for authentication.
     */
    public Context7Client(String apiKey) {
        this.apiKey = apiKey != null ? apiKey : ConfigManager.getProperty("context7.api.key", null);
        this.baseUrl = ConfigManager.getProperty("context7.api.base_url", "https://context7.com/api/v2");
        this.defaultLibrary = ConfigManager.getProperty("context7.default_library", "open-source-libraries");
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Checks if the API key is properly configured.
     * 
     * @return true if API key is configured and valid, false otherwise
     */
    public boolean isApiKeyConfigured() {
        return apiKey != null 
            && !apiKey.isEmpty() 
            && !apiKey.equals("YOUR_API_KEY_HERE");
    }
    
    /**
     * Gets the base URL for the Context7 API.
     * 
     * @return the base URL string
     */
    public String getBaseUrl() {
        return baseUrl;
    }
    
    /**
     * Gets the default library name.
     * 
     * @return the default library name
     */
    public String getDefaultLibrary() {
        return defaultLibrary;
    }
    
    /**
     * Gets the configured API key.
     * 
     * @return the API key, or null if not configured
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Searches for libraries matching the given query using the default library.
     * This is a convenience method that uses the configured default library.
     * 
     * @param query The search query.
     * @return List of matching LibrarySearchResult objects.
     */
    public List<LibrarySearchResult> searchLibraries(String query) {
        return searchLibraries(defaultLibrary, query);
    }

    /**
     * Searches for libraries matching the given name and query.
     * 
     * @param libraryName The name of the library to search for.
     * @param query The search query.
     * @return LibrarySearchResponse containing the search results.
     * @throws IOException If an error occurs during the request.
     */
    public LibrarySearchResponse searchLibrariesResponse(String libraryName, String query) throws IOException {
        validateApiKey();
        
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(baseUrl + "/libs/search"))
                .newBuilder()
                .addQueryParameter("libraryName", libraryName)
                .addQueryParameter("query", query)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return handleResponse(response, LibrarySearchResponse.class);
        }
    }
    
    /**
     * Searches for libraries matching the given name and query.
     * 
     * @param libraryName The name of the library to search for.
     * @param query The search query.
     * @return List of matching LibrarySearchResult objects.
     */
    public List<LibrarySearchResult> searchLibraries(String libraryName, String query) {
        try {
            LibrarySearchResponse response = searchLibrariesResponse(libraryName, query);
            if (response != null && response.getResults() != null) {
                return response.getResults();
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error searching libraries: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves context snippets for a specific library and query.
     * 
     * @param libraryId The ID of the library.
     * @param query The search query.
     * @return ContextResponse containing the context snippets.
     * @throws IOException If an error occurs during the request.
     */
    public ContextResponse getContext(String libraryId, String query) throws IOException {
        validateApiKey();

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(baseUrl + "/context"))
                .newBuilder()
                .addQueryParameter("libraryId", libraryId)
                .addQueryParameter("query", query)
                .addQueryParameter("type", "json")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return handleResponse(response, ContextResponse.class);
        }
    }
    
    /**
     * Retrieves context snippets for a specific library and query, returning formatted context.
     * 
     * @param libraryId The ID of the library.
     * @param query The search query.
     * @return Formatted string of context snippets, or null if not found.
     */
    public String getContextAsString(String libraryId, String query) {
        try {
            ContextResponse response = getContext(libraryId, query);
            if (response != null) {
                return response.getContext();
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting context: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Validates that the API key is properly configured.
     * 
     * @throws IllegalStateException if API key is not configured
     */
    private void validateApiKey() {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_API_KEY_HERE")) {
            throw new IllegalStateException("Context7 API key is not configured. Please provide it programmatically, in .env, or in context7.conf.");
        }
    }

    /**
     * Handles the HTTP response and parses it into the expected type.
     * 
     * @param response the HTTP response
     * @param responseType the class type to parse the response into
     * @return the parsed response object
     * @throws IOException if parsing fails or response is not successful
     */
    private <T> T handleResponse(Response response, Class<T> responseType) throws IOException {
        String body = response.body() != null ? response.body().string() : "";
        
        if (!response.isSuccessful()) {
            ErrorResponse errorResponse = null;
            try {
                errorResponse = objectMapper.readValue(body, ErrorResponse.class);
            } catch (Exception e) {
                log.error("Failed to parse error response: {}", body);
            }
            
            String error = errorResponse != null ? errorResponse.getError() : "Unknown Error";
            String message = errorResponse != null ? errorResponse.getMessage() : "No message provided";
            
            throw new Context7ApiException(response.code(), error, message);
        }

        return objectMapper.readValue(body, responseType);
    }
}
