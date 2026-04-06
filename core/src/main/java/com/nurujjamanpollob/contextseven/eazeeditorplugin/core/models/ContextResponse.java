package com.nurujjamanpollob.contextseven.eazeeditorplugin.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContextResponse {
    private List<CodeSnippet> codeSnippets;
    private List<InfoSnippet> infoSnippets;

    /**
     * Returns a formatted string representation of all context snippets.
     * Combines both code snippets and info snippets into a single readable format.
     *
     * @return A string containing all context snippets, or null if no snippets are available.
     */
    public String getContext() {
        StringBuilder contextBuilder = new StringBuilder();

        if (codeSnippets != null && !codeSnippets.isEmpty()) {
            for (CodeSnippet snippet : codeSnippets) {
                if (snippet.getCodeTitle() != null) {
                    contextBuilder.append("## ").append(snippet.getCodeTitle()).append("\n\n");
                }
                if (snippet.getCodeList() != null) {
                    for (CodeItem item : snippet.getCodeList()) {
                        if (item.getCode() != null) {
                            contextBuilder.append("```\n").append(item.getCode()).append("\n```\n\n");
                        }
                    }
                }
            }
        }

        if (infoSnippets != null && !infoSnippets.isEmpty()) {
            for (InfoSnippet snippet : infoSnippets) {
                if (snippet.getContent() != null) {
                    contextBuilder.append(snippet.getContent()).append("\n\n");
                }
            }
        }

        return contextBuilder.length() > 0 ? contextBuilder.toString().trim() : null;
    }

    /**
     * Checks if the response contains any context snippets.
     *
     * @return true if there are code snippets or info snippets, false otherwise.
     */
    public boolean hasContext() {
        return (codeSnippets != null && !codeSnippets.isEmpty()) 
            || (infoSnippets != null && !infoSnippets.isEmpty());
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CodeSnippet {
        private String codeTitle;
        private List<CodeItem> codeList;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CodeItem {
        private String code;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InfoSnippet {
        private String content;
    }
}
