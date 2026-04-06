package com.nurujjamanpollob.contextseven.eazeeditorplugin.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LibrarySearchResponse {
    private List<LibrarySearchResult> results;
}
