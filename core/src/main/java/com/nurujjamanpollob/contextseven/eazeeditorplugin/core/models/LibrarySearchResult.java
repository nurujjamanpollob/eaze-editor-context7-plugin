package com.nurujjamanpollob.contextseven.eazeeditorplugin.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LibrarySearchResult {
    private String id;
    private String title;
    private String description;
    private String version;
    private String provider;
    private String url;
}
