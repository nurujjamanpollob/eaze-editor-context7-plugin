package com.nurujjamanpollob.contextseven.eazeeditorplugin.core.documentation;

import com.nurujjamanpollob.contextseven.eazeeditorplugin.core.Context7Client;
import com.nurujjamanpollob.contextseven.eazeeditorplugin.core.models.LibrarySearchResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Documentation search service that provides semantic search capabilities
 * across library documentation using the Context7 API.
 * 
 * <p>This class encapsulates all documentation search operations, including:
 * <ul>
 *   <li>Searching for library documentation by query</li>
 *   <li>Filtering results by library name, version, or provider</li>
 *   <li>Retrieving documentation with context snippets</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * DocumentationSearcher searcher = new DocumentationSearcher(context7Client);
 * List<LibrarySearchResult> results = searcher.searchDocumentation("React hooks");
 * }</pre>
 *
 * @author Nurujjaman Pollob
 * @since 1.0.0
 */
@Slf4j
public class DocumentationSearcher {

    private final Context7Client context7Client;
    
    /**
     * Creates a new DocumentationSearcher with the specified Context7 client.
     *
     * @param context7Client the Context7 API client to use for searches
     * @throws IllegalArgumentException if context7Client is null
     */
    public DocumentationSearcher(Context7Client context7Client) {
        if (context7Client == null) {
            throw new IllegalArgumentException("Context7Client cannot be null");
        }
        this.context7Client = context7Client;
    }
    
    /**
     * Searches for library documentation matching the given query.
     *
     * <p>The search is performed against the Context7 library database and returns
     * results ranked by relevance. Each result contains the library identifier,
     * title, description, version, provider, and URL.
     *
     * @param query the search query (e.g., "React useState", "Python async/await")
     * @return a list of matching library results, never null (may be empty)
     */
    public List<LibrarySearchResult> searchDocumentation(String query) {
        if (query == null || query.trim().isEmpty()) {
            log.warn("Empty or null search query provided");
            return Collections.emptyList();
        }
        
        String trimmedQuery = query.trim();
        log.info("Searching documentation for query: {}", trimmedQuery);
        
        try {
            List<LibrarySearchResult> results = context7Client.searchLibraries(trimmedQuery);
            
            if (results == null) {
                log.warn("Context7 client returned null results for query: {}", trimmedQuery);
                return Collections.emptyList();
            }
            
            log.info("Found {} results for query: {}", results.size(), trimmedQuery);
            return results;
            
        } catch (Exception e) {
            log.error("Error searching documentation for query '{}': {}", trimmedQuery, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Searches for documentation and filters results by library name.
     *
     * @param query the search query
     * @param libraryNameFilter the library name to filter results (case-insensitive)
     * @return filtered list of results matching both query and library name
     */
    public List<LibrarySearchResult> searchDocumentationWithLibraryFilter(String query, String libraryNameFilter) {
        List<LibrarySearchResult> results = searchDocumentation(query);
        
        if (libraryNameFilter == null || libraryNameFilter.trim().isEmpty()) {
            return results;
        }
        
        String filter = libraryNameFilter.toLowerCase().trim();
        return results.stream()
                .filter(result -> {
                    String title = result.getTitle();
                    return title != null && title.toLowerCase().contains(filter);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Searches for documentation and filters results by provider.
     *
     * @param query the search query
     * @param providerFilter the provider name to filter results (case-insensitive)
     * @return filtered list of results matching both query and provider
     */
    public List<LibrarySearchResult> searchDocumentationWithProviderFilter(String query, String providerFilter) {
        List<LibrarySearchResult> results = searchDocumentation(query);
        
        if (providerFilter == null || providerFilter.trim().isEmpty()) {
            return results;
        }
        
        String filter = providerFilter.toLowerCase().trim();
        return results.stream()
                .filter(result -> {
                    String provider = result.getProvider();
                    return provider != null && provider.toLowerCase().contains(filter);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Searches for documentation and returns the top N results.
     *
     * @param query the search query
     * @param limit the maximum number of results to return
     * @return list of up to 'limit' results, ordered by relevance
     */
    public List<LibrarySearchResult> searchDocumentationWithLimit(String query, int limit) {
        if (limit <= 0) {
            log.warn("Invalid limit {} provided, returning empty list", limit);
            return Collections.emptyList();
        }
        
        List<LibrarySearchResult> results = searchDocumentation(query);
        
        if (results.size() <= limit) {
            return results;
        }
        
        return results.subList(0, limit);
    }
    
    /**
     * Finds a specific library by its exact ID.
     *
     * @param libraryId the unique identifier of the library
     * @return an Optional containing the library if found, empty otherwise
     */
    public Optional<LibrarySearchResult> findLibraryById(String libraryId) {
        if (libraryId == null || libraryId.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return searchDocumentation(libraryId).stream()
                .filter(result -> libraryId.equals(result.getId()))
                .findFirst();
    }
    
    /**
     * Searches for documentation and formats results as a human-readable string.
     *
     * @param query the search query
     * @return formatted string containing all matching results
     */
    public String searchDocumentationFormatted(String query) {
        List<LibrarySearchResult> results = searchDocumentation(query);
        
        if (results.isEmpty()) {
            return "No documentation found for query: " + query;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(results.size()).append(" result(s) for \"").append(query).append("\":\n\n");
        
        for (int i = 0; i < results.size(); i++) {
            LibrarySearchResult result = results.get(i);
            sb.append(i + 1).append(". ").append(result.getTitle());
            if (result.getVersion() != null) {
                sb.append(" (v").append(result.getVersion()).append(")");
            }
            sb.append("\n");
            
            if (result.getDescription() != null) {
                sb.append("   Description: ").append(result.getDescription()).append("\n");
            }
            if (result.getProvider() != null) {
                sb.append("   Provider: ").append(result.getProvider()).append("\n");
            }
            if (result.getUrl() != null) {
                sb.append("   URL: ").append(result.getUrl()).append("\n");
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Gets a summary of the search results containing only essential information.
     *
     * @param query the search query
     * @return list of summarized result information
     */
    public List<DocumentationSummary> getSearchSummary(String query) {
        List<LibrarySearchResult> results = searchDocumentation(query);
        
        return results.stream()
                .map(result -> new DocumentationSummary(
                        result.getId(),
                        result.getTitle(),
                        result.getVersion(),
                        result.getProvider()
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * Immutable data class representing a summarized documentation result.
     */
    public record DocumentationSummary(
            String id,
            String title,
            String version,
            String provider
    ) {}
}
