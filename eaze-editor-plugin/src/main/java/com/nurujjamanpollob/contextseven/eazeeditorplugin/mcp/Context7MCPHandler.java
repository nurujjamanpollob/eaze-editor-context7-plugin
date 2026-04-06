package com.nurujjamanpollob.contextseven.eazeeditorplugin.mcp;

import com.eazeeditor.core.pluginmodule.annotation.EazeAiComponent;

import com.eazeeditor.core.pluginmodule.api.PluginApi;
import com.eazeeditor.core.pluginmodule.api.PluginSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.nurujjamanpollob.contextseven.eazeeditorplugin.settings.SettingAPI;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.Vars;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.core.Context7Client;
 import com.nurujjamanpollob.contextseven.eazeeditorplugin.core.models.ContextResponse;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.core.documentation.DocumentationSearcher;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.core.models.LibrarySearchResult;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.utilities.PluginLogger;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MCP (Model Context Protocol) handler for the Context7 Documentation Plugin.
 * Exposes documentation search and retrieval tools to the AI agent.
 */
@EazeAiComponent
public class Context7MCPHandler {

    private final ObjectMapper objectMapper = createObjectMapper();
    private DocumentationSearcher searcher;

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * Initializes the MCP endpoints.
     */
    public void initializeEndpoints() {
        PluginLogger.info("[context7] MCP endpoints registered via annotations", Context7MCPHandler.class);
    }

    private String getSettingValue(String key, String defaultValue) {
        String val = SettingAPI.getSettingValue(Vars.PLUGIN_NAME, key);
        if (val != null) {
            return val;
        }
        return defaultValue;
    }

    private boolean getBooleanSetting(String key, boolean defaultValue) {
        String val = SettingAPI.getSettingValue(Vars.PLUGIN_NAME, key);
        if (val != null) {
            return Boolean.parseBoolean(val);
        }
        return defaultValue;
    }

    /**
     * Verifies if Context7 Documentation is active and enabled in settings.
     * @return An error message if not active/enabled, null otherwise.
     */
    private String verifyPluginActive() {
        if (!getBooleanSetting(Vars.SETTING_ENABLED, Vars.DEFAULT_ENABLED)) {
            return formatError("Context7 Documentation is disabled in settings. Please enable it in the plugin settings to use this tool.");
        }
        String apiKey = getSettingValue(Vars.SETTING_API_KEY, Vars.DEFAULT_API_KEY);
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.equals("YOUR_API_KEY_HERE")) {
            return formatError("Context7 API key is not configured. Please open plugin settings and provide a valid API key to use this tool.");
        }
        return null;
    }

    /**
     * Searches for documentation based on a query.
     *
     * @param query The search query
     * @param library Optional library name to filter by
     * @return JSON string containing search results
     */
    @Tool(
            name = "search_documentation",
            value = "Use this tool to search for documentation, code examples, API references, or tutorials across various libraries and frameworks. It returns a list of candidate library items alongside their required unqiue 'id' values. Call this first when discovering syntaxes or packages."
    )
    public String searchDocumentation(
            @P("The exact search query to find relevant documentation (e.g., 'useEffect', 'axios get', 'pandas dataframe')") String query,
            @P("Optional but highly recommended library or framework name to filter by (e.g., 'react', 'python', 'spring-boot')") String library
    ) {
        try {
            String verifyError = verifyPluginActive();
            if (verifyError != null) {
                PluginLogger.info("Documentation search attempted but plugin is not active/enabled: " + verifyError, Context7MCPHandler.class);
                return verifyError;
            }

            String targetLibrary = (library != null && !library.trim().isEmpty()) 
                    ? library.trim() 
                    : getSettingValue(Vars.SETTING_DEFAULT_LIBRARY, Vars.DEFAULT_LIBRARY);

            PluginLogger.info("Searching documentation for query: " + query + " in library: " + targetLibrary, Context7MCPHandler.class);

            String apiKey = getSettingValue(Vars.SETTING_API_KEY, Vars.DEFAULT_API_KEY);
            Context7Client client = new Context7Client(apiKey);
            
            List<LibrarySearchResult> results = client.searchLibraries(targetLibrary, query);

            Map<String, Object> response = new HashMap<>();
            response.put("query", query);
            response.put("library", targetLibrary);
            response.put("results", results);
            response.put("count", results != null ? results.size() : 0);
            response.put("hint", "Use get_documentation_details with the returned item 'id' and your original 'query' to fetch the actual readable documentation content snippet.");

            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            e.printStackTrace();
            return formatError("Error searching documentation: " + e.getMessage());
        }
    }

    /**
     * Retrieves full documentation for a specific item.
     *
     * @param id The unique ID of the documentation item
     * @param query The search query
     * @return JSON string containing the full documentation
     */
    @Tool(
            name = "get_documentation_details",
            value = "Use this tool to retrieve the complete, full documentation code snippets and references for a specific library item using its unique ID and the context query. Only call this tool AFTER you have obtained a library ID from the results of the search_documentation tool."
    )
    public String getDocumentationDetails(
            @P("The unique ID of the documentation library item to retrieve, exactly as returned by search_documentation") String id,
            @P("The detailed search query terms used to fetch the specific context snippets (e.g., 'useEffect best practices')") String query
    ) {
        try {
            String verifyError = verifyPluginActive();
            if (verifyError != null) {
                PluginLogger.info("Documentation details retrieval attempted but plugin is not active/enabled.", Context7MCPHandler.class);
                return verifyError;
            }

            PluginLogger.info("Retrieving documentation details for ID: " + id + " with query: " + query, Context7MCPHandler.class);

            String apiKey = getSettingValue(Vars.SETTING_API_KEY, Vars.DEFAULT_API_KEY);
            Context7Client client = new Context7Client(apiKey);
            
            ContextResponse contextResponse = client.getContext(id, query);

            if (contextResponse == null || !contextResponse.hasContext()) {
                PluginLogger.info("Documentation snippets not found for ID: " + id + " and query: " + query, Context7MCPHandler.class);
                return formatError("No context snippets found for ID: " + id + " with query: " + query + ". Try tweaking the search parameters or using a different library ID.");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("id", id);
            response.put("query", query);
            response.put("readableContext", contextResponse.getContext());

            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            e.printStackTrace();
            return formatError("Error retrieving documentation context: " + e.getMessage());
        }
    }

    /**
     * Lists available libraries for documentation search.
     *
     * @return JSON string containing the list of libraries
     */
    @Tool(
            name = "list_libraries",
            value = "Use this tool to get information about the libraries and frameworks whose documentation can be searched via Context7. Call this if the user asks what libraries, frameworks, or languages are supported."
    )
    public String listLibraries() {
        String verifyError = verifyPluginActive();
        if (verifyError != null) {
            PluginLogger.info("Library list retrieval attempted but plugin is not active/enabled.", Context7MCPHandler.class);
            return verifyError;
        }

        PluginLogger.info("Listing available libraries", Context7MCPHandler.class);
        return "{\"message\": \"Supported libraries are dynamically sourced and automatically queried.\"}";
    }

    private String formatError(String message) {
        Map<String, String> error = Map.of("status", "error", "message", message);
        try {
            return objectMapper.writeValueAsString(error);
        } catch (Exception e) {
            return "{\"status\":\"error\",\"message\":\"" + message + "\"}";
        }
    }
}
